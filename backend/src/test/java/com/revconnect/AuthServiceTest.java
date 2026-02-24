package com.revconnect;

import com.revconnect.auth.dto.AuthDtos;
import com.revconnect.auth.service.AuthService;
import com.revconnect.common.exception.DuplicateResourceException;
import com.revconnect.security.JwtTokenProvider;
import com.revconnect.user.model.User;
import com.revconnect.user.model.UserRole;
import com.revconnect.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private AuthDtos.RegisterRequest registerRequest;

    @Before
    public void setUp() {
        registerRequest = new AuthDtos.RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@email.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(UserRole.PERSONAL);
    }

    @Test
    public void testRegister_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("$2a$12$encoded");

        User savedUser = User.builder()
                .id(1L).username("testuser").email("test@email.com")
                .role(UserRole.PERSONAL).build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenProvider.generateTokenFromUsername(anyString())).thenReturn("jwt-token-123");

        AuthDtos.AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("PERSONAL", response.getRole());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test(expected = DuplicateResourceException.class)
    public void testRegister_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        authService.register(registerRequest);
    }

    @Test(expected = DuplicateResourceException.class)
    public void testRegister_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);
        authService.register(registerRequest);
    }

    @Test
    public void testLogin_Success() {
        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(tokenProvider.generateToken(mockAuth)).thenReturn("jwt-login-token");

        User user = User.builder()
                .id(1L).username("testuser").email("test@email.com")
                .role(UserRole.PERSONAL).build();
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(user));

        AuthDtos.AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-login-token", response.getToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    public void testPasswordIsEncoded() {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$hashed");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            assertEquals("$2a$12$hashed", u.getPassword());
            u = User.builder().id(1L).username("testuser").role(UserRole.PERSONAL).build();
            return u;
        });
        when(tokenProvider.generateTokenFromUsername(any())).thenReturn("token");
        authService.register(registerRequest);
        verify(passwordEncoder, times(1)).encode("password123");
    }
}
