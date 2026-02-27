package com.revconnect.notification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import com.revconnect.notification.model.Notification;
import com.revconnect.notification.repository.NotificationRepository;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.user.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;

    @Mock
    NotificationRepository notificationRepository;

    private User user;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("vasanth");
    }

    @Test
    void testGetNotifications() {

        Page<Notification> page =
                new PageImpl<>(List.of(new Notification()));

        when(notificationRepository
                .findByRecipientOrderByCreatedAtDesc(
                        user,
                        PageRequest.of(0,20)))
                .thenReturn(page);

        Page<Notification> result =
                notificationService.getNotifications(user,0,20);

        assertNotNull(result);
    }

    @Test
    void testUnreadCount() {

        when(notificationRepository
                .countByRecipientAndReadFalse(user))
                .thenReturn(3L);

        long count =
                notificationService.getUnreadCount(user);

        assertNotNull(count);
    }
}