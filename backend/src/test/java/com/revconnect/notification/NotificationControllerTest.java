package com.revconnect.notification;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.revconnect.notification.controller.NotificationController;
import com.revconnect.notification.model.Notification;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.security.JwtAuthenticationFilter;
import com.revconnect.security.JwtTokenProvider;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "vasanth")
public class NotificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NotificationService notificationService;

    @MockBean
    UserService userService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void testGetNotifications() throws Exception {

        User user = new User();
        user.setUsername("vasanth");

        when(userService.getUserByUsername("vasanth"))
                .thenReturn(user);

        Page<Notification> page =
                new PageImpl<>(List.of());

        when(notificationService.getNotifications(user,0,20))
                .thenReturn(page);

        mockMvc.perform(get("/notifications")
                .with(user("vasanth")))
                .andExpect(status().isOk());
    }
}
