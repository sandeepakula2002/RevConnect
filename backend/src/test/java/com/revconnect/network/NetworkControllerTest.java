package com.revconnect.network;

import com.revconnect.network.controller.NetworkController;
import com.revconnect.network.service.NetworkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NetworkControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NetworkService networkService;

    @InjectMocks
    private NetworkController networkController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(networkController)
                .build();
    }

    @Test
    void testGetConnections() throws Exception {

        when(networkService.getConnections("testuser"))
                .thenReturn(List.of());

        mockMvc.perform(get("/network/connections")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk());
    }
}