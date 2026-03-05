package com.revconnect.notification;

import com.revconnect.notification.controller.NotificationController;
import com.revconnect.notification.model.Notification;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private User mockUser;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(notificationController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("demoUser");
    }

    @Test
    void shouldReturnNotifications() throws Exception {

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setRecipient(mockUser); // 🔥 IMPORTANT

        when(userService.getUserByUsername(anyString()))
                .thenReturn(mockUser);

        when(notificationService.getNotifications(any(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        mockMvc.perform(get("/notifications")
                        .param("page","0")
                        .param("size","20")
                        .principal(() -> "demoUser"))
                .andExpect(status().isOk());
    }
}