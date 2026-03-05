package com.revconnect.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.auth.controller.AuthController;
import com.revconnect.auth.dto.AuthDtos;
import com.revconnect.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    void testRegister() throws Exception {

        AuthDtos.AuthResponse response =
                new AuthDtos.AuthResponse(
                        "token123",
                        1L,
                        "vasanth",
                        "vasanth@test.com",
                        "PERSONAL",
                        null
                );

        when(authService.register(any(AuthDtos.RegisterRequest.class)))
                .thenReturn(response);

        AuthDtos.RegisterRequest request =
                new AuthDtos.RegisterRequest();
        request.setUsername("vasanth");
        request.setEmail("vasanth@test.com");
        request.setPassword("12345678");

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }


}