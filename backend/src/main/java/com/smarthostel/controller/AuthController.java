package com.smarthostel.controller;

import com.smarthostel.dto.ApiResponse;
import com.smarthostel.dto.AuthResponse;
import com.smarthostel.dto.LoginRequest;
import com.smarthostel.dto.RefreshTokenRequest;
import com.smarthostel.dto.RegisterRequest;
import com.smarthostel.dto.UserDto;
import com.smarthostel.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication APIs", description = "Endpoints for authentication, refresh token, registration, and user profile")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user and returns access token + refresh token in standardized ApiResponse")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse data = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(data, "User logged in successfully"));
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Registers user and returns access token + refresh token in standardized ApiResponse")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse data = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data, "User registered successfully"));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Access Token", description = "Generates a new access token using a valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse data = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(data, "Access token refreshed successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User Profile", description = "Returns profile details of currently authenticated user in standardized ApiResponse")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UserDto data = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(data, "User profile retrieved successfully"));
    }
}
