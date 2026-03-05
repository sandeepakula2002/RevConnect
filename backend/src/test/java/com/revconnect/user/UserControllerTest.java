package com.revconnect.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.user.controller.UserController;
import com.revconnect.user.dto.UserDtos;
import com.revconnect.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    void testGetUserProfile() throws Exception {

        when(userService.getUserProfile(1L, "demoUser"))
                .thenReturn(UserDtos.UserResponse.builder().id(1L).build());

        mockMvc.perform(get("/users/1")
                        .principal(() -> "demoUser"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProfile() throws Exception {

        when(userService.updateProfile(eq(1L), any(), eq("demoUser")))
                .thenReturn(UserDtos.UserResponse.builder().id(1L).build());

        UserDtos.ProfileUpdateRequest request =
                new UserDtos.ProfileUpdateRequest();

        mockMvc.perform(put("/users/1")
                        .principal(() -> "demoUser")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchUsers() throws Exception {

        when(userService.searchUsers(eq("john"), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/users/search")
                        .param("q", "john")
                        .principal(() -> "demoUser"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCurrentUser() throws Exception {

        when(userService.getUserByUsername("demoUser"))
                .thenReturn(com.revconnect.user.model.User.builder()
                        .id(1L).username("demoUser").build());

        when(userService.getUserProfile(1L, "demoUser"))
                .thenReturn(UserDtos.UserResponse.builder().id(1L).build());

        mockMvc.perform(get("/users/me")
                        .principal(() -> "demoUser"))
                .andExpect(status().isOk());
    }
}