package com.revconnect.backend.auth.service;

import com.revconnect.backend.auth.model.Role;
import com.revconnect.backend.auth.model.User;
import com.revconnect.backend.auth.repository.RoleRepository;
import com.revconnect.backend.auth.repository.UserRepository;
import com.revconnect.backend.common.dto.RegisterRequest;
import com.revconnect.backend.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ✅ REGISTER USER
    @Transactional
    public Map<String, String> register(RegisterRequest request) {

        // -------- VALIDATION --------
        if (request.getEmail() == null || request.getUsername() == null
                || request.getPassword() == null) {
            throw new RuntimeException("Required fields missing");
        }

        // ✅ check duplicate email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // ✅ check duplicate username
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // -------- CREATE USER --------
        User user = new User();

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordHint(request.getPasswordHint());
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(request.getSecurityAnswer());

        // assign ROLE_USER
        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new RuntimeException("ROLE_USER not initialized in database"));

        user.setRoles(Set.of(role));

        userRepository.save(user);

        return Map.of(
                "message", "User Registered Successfully"
        );
    }

    // ✅ LOGIN USER
    public Map<String, String> login(String email, String password) {

        if (email == null || password == null) {
            throw new RuntimeException("Email and password required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return Map.of(
                "token", token,
                "message", "Login successful"
        );
    }

    // ✅ FORGOT PASSWORD
    @Transactional
    public Map<String, String> forgotPassword(String email,
                                              String answer,
                                              String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getSecurityAnswer() == null ||
                !user.getSecurityAnswer().equalsIgnoreCase(answer)) {
            throw new RuntimeException("Wrong security answer");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return Map.of(
                "message", "Password reset successful"
        );
    }

    // ✅ CHANGE PASSWORD
    @Transactional
    public Map<String, String> changePassword(String email,
                                              String oldPassword,
                                              String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return Map.of(
                "message", "Password changed successfully"
        );
    }
}