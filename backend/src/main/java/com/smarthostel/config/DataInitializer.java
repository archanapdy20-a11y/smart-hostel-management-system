package com.smarthostel.config;

import com.smarthostel.model.entity.Student;
import com.smarthostel.model.entity.User;
import com.smarthostel.model.entity.Warden;
import com.smarthostel.model.enums.GenderEnum;
import com.smarthostel.model.enums.RoleEnum;
import com.smarthostel.repository.StudentRepository;
import com.smarthostel.repository.UserRepository;
import com.smarthostel.repository.WardenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final WardenRepository wardenRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, StudentRepository studentRepository, WardenRepository wardenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.wardenRepository = wardenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Seed Admin
            User admin = User.builder()
                    .username("admin")
                    .email("admin@hostel.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(RoleEnum.ROLE_ADMIN)
                    .firstName("System")
                    .lastName("Administrator")
                    .phoneNumber("9998887770")
                    .build();
            userRepository.save(admin);

            // Seed Warden
            User wardenUser = User.builder()
                    .username("warden")
                    .email("warden@hostel.com")
                    .password(passwordEncoder.encode("warden123"))
                    .role(RoleEnum.ROLE_WARDEN)
                    .firstName("Robert")
                    .lastName("Vance")
                    .phoneNumber("9998887771")
                    .build();
            wardenUser = userRepository.save(wardenUser);

            Warden warden = Warden.builder()
                    .user(wardenUser)
                    .employeeId("WRD-001")
                    .qualification("M.Tech, Chief Hostel Administrator")
                    .build();
            wardenRepository.save(warden);

            // Seed Student
            User studentUser = User.builder()
                    .username("student")
                    .email("student@hostel.com")
                    .password(passwordEncoder.encode("student123"))
                    .role(RoleEnum.ROLE_STUDENT)
                    .firstName("Alex")
                    .lastName("Morgan")
                    .phoneNumber("9998887772")
                    .build();
            studentUser = userRepository.save(studentUser);

            Student student = Student.builder()
                    .user(studentUser)
                    .rollNumber("STU-2024-001")
                    .department("Computer Science")
                    .academicYear(3)
                    .gender(GenderEnum.MALE)
                    .guardianName("David Morgan")
                    .guardianPhone("9876543210")
                    .address("123 Tech Campus Road, Block B")
                    .build();
            studentRepository.save(student);

            System.out.println("✅ DataInitializer: Default accounts seeded successfully!");
        }
    }
}
