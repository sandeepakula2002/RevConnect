package com.revconnect.auth;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.auth.controller.AuthController;
import com.revconnect.auth.dto.AuthDtos;
import com.revconnect.auth.service.AuthService;
import com.revconnect.security.JwtAuthenticationFilter;
import com.revconnect.security.JwtTokenProvider;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testRegister() throws Exception {

        AuthDtos.RegisterRequest request =
                new AuthDtos.RegisterRequest();

        request.setUsername("vasanth");
        request.setEmail("vasanth@test.com");
        request.setPassword("12345678");

        AuthDtos.AuthResponse response =
                new AuthDtos.AuthResponse(
                        "token",
                        1L,
                        "vasanth",
                        "vasanth@test.com",
                        "PERSONAL",
                        null
                );

        when(authService.register(request))
                .thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
