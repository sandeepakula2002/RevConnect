package com.revconnect.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.revconnect.auth.dto.AuthDtos;
import com.revconnect.auth.service.AuthService;
import com.revconnect.security.JwtTokenProvider;
import com.revconnect.user.model.User;
import com.revconnect.user.model.UserRole;
import com.revconnect.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("vasanth")
                .email("vasanth@test.com")
                .password("encoded")
                .role(UserRole.PERSONAL)
                .build();
    }

    @Test
    void testRegister() {

        AuthDtos.RegisterRequest request =
                new AuthDtos.RegisterRequest();

        request.setUsername("vasanth");
        request.setEmail("vasanth@test.com");
        request.setPassword("1234");

        when(userRepository.existsByUsername("vasanth"))
                .thenReturn(false);

        when(userRepository.existsByEmail("vasanth@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("1234"))
                .thenReturn("encoded");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        when(tokenProvider.generateTokenFromUsername("vasanth"))
                .thenReturn("token123");

        AuthDtos.AuthResponse response =
                authService.register(request);

        assertEquals("vasanth", response.getUsername());
        assertEquals("token123", response.getToken());
    }
}
