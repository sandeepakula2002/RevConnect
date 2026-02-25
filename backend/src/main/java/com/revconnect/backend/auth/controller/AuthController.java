package com.revconnect.backend.auth.controller;

import com.revconnect.backend.auth.service.AuthService;
import com.revconnect.backend.common.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Map<String, String> request) {

        return ResponseEntity.ok(
                authService.login(
                        request.get("email"),
                        request.get("password")
                )
        );
    }

    // ✅ FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody Map<String, String> request) {

        return ResponseEntity.ok(
                authService.forgotPassword(
                        request.get("email"),
                        request.get("answer"),
                        request.get("newPassword")
                )
        );
    }

    // ✅ CHANGE PASSWORD
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody Map<String, String> request) {

        return ResponseEntity.ok(
                authService.changePassword(
                        request.get("email"),
                        request.get("oldPassword"),
                        request.get("newPassword")
                )
        );
    }
}