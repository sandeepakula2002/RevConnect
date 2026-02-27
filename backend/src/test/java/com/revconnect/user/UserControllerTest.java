package com.revconnect.user;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revconnect.security.JwtAuthenticationFilter;
import com.revconnect.security.JwtTokenProvider;
import com.revconnect.user.controller.UserController;
import com.revconnect.user.dto.UserDtos;
import com.revconnect.user.service.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;



import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "vasanth")
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void testGetUserProfile() throws Exception {

        UserDtos.UserResponse response =
                UserDtos.UserResponse.builder()
                .username("vasanth")
                .build();

        when(userService.getUserProfile(1L,"vasanth"))
                .thenReturn(response);

        mockMvc.perform(get("/users/1")
                .with(user("vasanth")))
                .andExpect(status().isOk());
    }
}
