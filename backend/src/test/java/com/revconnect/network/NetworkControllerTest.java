package com.revconnect.network;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revconnect.network.controller.NetworkController;
import com.revconnect.network.model.Connection;
import com.revconnect.network.service.NetworkService;
import com.revconnect.security.JwtAuthenticationFilter;
import com.revconnect.security.JwtTokenProvider;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NetworkController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "vasanth")
public class NetworkControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NetworkService networkService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void testSendConnectionRequest() throws Exception {

        Connection connection =
                Connection.builder().build();

        when(networkService.sendConnectionRequest(2L,"vasanth"))
                .thenReturn(connection);

        mockMvc.perform(post("/network/connect/2")
                .with(user("vasanth")))
                .andExpect(status().isOk());
    }
}
