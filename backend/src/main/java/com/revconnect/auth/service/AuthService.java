package com.revconnect.auth.service;

import com.revconnect.auth.dto.AuthDtos;
import com.revconnect.common.exception.BadRequestException;
import com.revconnect.common.exception.DuplicateResourceException;
import com.revconnect.security.JwtTokenProvider;
import com.revconnect.user.model.User;
import com.revconnect.user.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))   // BCrypt encrypted
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole() != null ? request.getRole() : com.revconnect.user.model.UserRole.PERSONAL)
                .build();

        user = userRepository.save(user);
        logger.info("New user registered: {} ({})", user.getUsername(), user.getRole());

        String token = tokenProvider.generateTokenFromUsername(user.getUsername());
        return new AuthDtos.AuthResponse(
                token, user.getId(), user.getUsername(),
                user.getEmail(), user.getRole().name(), user.getProfilePicture()
        );
    }


    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsernameOrEmail(
                        request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        logger.info("User logged in: {}", user.getUsername());
        return new AuthDtos.AuthResponse(
                token, user.getId(), user.getUsername(),
                user.getEmail(), user.getRole().name(), user.getProfilePicture()
        );

    }
    // ================= FORGOT PASSWORD (DEMO RESET) =================
    @Transactional
    public void resetPasswordDirect(String email, String newPassword) {

        System.out.println("EMAIL RECEIVED FROM FRONTEND = [" + email + "]");

        String normalizedEmail =
                email == null ? "" : email.trim().toLowerCase();

        System.out.println("NORMALIZED EMAIL = [" + normalizedEmail + "]");

        User user = userRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}

