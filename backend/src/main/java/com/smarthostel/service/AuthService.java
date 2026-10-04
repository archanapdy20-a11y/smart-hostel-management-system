package com.smarthostel.service;

import com.smarthostel.dto.AuthResponse;
import com.smarthostel.dto.LoginRequest;
import com.smarthostel.dto.RefreshTokenRequest;
import com.smarthostel.dto.RegisterRequest;
import com.smarthostel.dto.UserDto;
import com.smarthostel.model.entity.RefreshToken;
import com.smarthostel.model.entity.Student;
import com.smarthostel.model.entity.User;
import com.smarthostel.model.entity.Warden;
import com.smarthostel.model.enums.RoleEnum;
import com.smarthostel.repository.StudentRepository;
import com.smarthostel.repository.UserRepository;
import com.smarthostel.repository.WardenRepository;
import com.smarthostel.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final WardenRepository wardenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, StudentRepository studentRepository, WardenRepository wardenRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.wardenRepository = wardenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByUsernameOrEmail(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getUsernameOrEmail()
        ).orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(mapToUserDto(user))
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .build();

        user = userRepository.save(user);

        if (registerRequest.getRole() == RoleEnum.ROLE_STUDENT) {
            String rollNum = registerRequest.getRollNumber() != null ? registerRequest.getRollNumber() : "STU-" + System.currentTimeMillis();
            Student student = Student.builder()
                    .user(user)
                    .rollNumber(rollNum)
                    .department(registerRequest.getDepartment())
                    .academicYear(registerRequest.getAcademicYear() != null ? registerRequest.getAcademicYear() : 1)
                    .gender(registerRequest.getGender())
                    .guardianName(registerRequest.getGuardianName())
                    .guardianPhone(registerRequest.getGuardianPhone())
                    .address(registerRequest.getAddress())
                    .build();
            studentRepository.save(student);
        } else if (registerRequest.getRole() == RoleEnum.ROLE_WARDEN) {
            String empId = registerRequest.getEmployeeId() != null ? registerRequest.getEmployeeId() : "WRD-" + System.currentTimeMillis();
            Warden warden = Warden.builder()
                    .user(user)
                    .employeeId(empId)
                    .qualification(registerRequest.getQualification())
                    .build();
            wardenRepository.save(warden);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword())
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(mapToUserDto(user))
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user.getUsername(), null, user.getRole() != null ? 
                            java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole().name())) : 
                            java.util.Collections.emptySet()
                    );
                    String accessToken = jwtTokenProvider.generateToken(authentication);
                    return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(request.getRefreshToken())
                            .user(mapToUserDto(user))
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    public UserDto getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        return mapToUserDto(user);
    }

    public UserDto mapToUserDto(User user) {
        UserDto.UserDtoBuilder builder = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber());

        if (user.getRole() == RoleEnum.ROLE_STUDENT) {
            studentRepository.findByUserId(user.getId()).ifPresent(student -> {
                builder.studentId(student.getId())
                        .rollNumber(student.getRollNumber())
                        .department(student.getDepartment())
                        .academicYear(student.getAcademicYear())
                        .gender(student.getGender());
            });
        } else if (user.getRole() == RoleEnum.ROLE_WARDEN) {
            wardenRepository.findByUserId(user.getId()).ifPresent(warden -> {
                builder.wardenId(warden.getId())
                        .employeeId(warden.getEmployeeId());
            });
        }

        return builder.build();
    }
}
