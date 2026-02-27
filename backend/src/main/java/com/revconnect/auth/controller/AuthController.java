package com.revconnect.auth.controller;

import com.revconnect.auth.dto.AuthDtos;
import com.revconnect.auth.service.AuthService;
import com.revconnect.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> register(
            @Valid @RequestBody AuthDtos.RegisterRequest request) {
        AuthDtos.AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> login(
            @Valid @RequestBody AuthDtos.LoginRequest request) {
        AuthDtos.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {

        String email = req.get("email");
        String password = req.get("password");

        authService.resetPasswordDirect(email, password);

        return ResponseEntity.ok("Password reset successful");
    }
}