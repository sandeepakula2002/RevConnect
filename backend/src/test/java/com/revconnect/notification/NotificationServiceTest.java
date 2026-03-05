package com.revconnect.notification;

import com.revconnect.notification.model.Notification;
import com.revconnect.notification.model.NotificationType;
import com.revconnect.notification.repository.NotificationRepository;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.post.model.Post;
import com.revconnect.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    private User sender;
    private User recipient;
    private Post post;

    @BeforeEach
    void setup() {
        sender = User.builder().id(1L).username("sender").build();
        recipient = User.builder().id(2L).username("recipient").build();
        post = Post.builder().id(10L).user(recipient).build();
    }

    @Test
    void testNotifyConnectionRequest() {
        notificationService.notifyConnectionRequest(sender, recipient);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testNotifyConnectionAccepted() {
        notificationService.notifyConnectionAccepted(sender, recipient);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testNotifyNewFollower() {
        notificationService.notifyNewFollower(sender, recipient);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testNotifyLike_NotSelf() {
        notificationService.notifyLike(sender, post);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testNotifyLike_SelfLike_NoSave() {
        Post selfPost = Post.builder().id(10L).user(sender).build();
        notificationService.notifyLike(sender, selfPost);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testNotifyComment() {
        notificationService.notifyComment(sender, post);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testNotifyShare() {
        notificationService.notifyShare(sender, post);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testGetNotifications() {
        when(notificationRepository.findByRecipientOrderByCreatedAtDesc(eq(recipient), any()))
                .thenReturn(new PageImpl<>(List.of()));
        notificationService.getNotifications(recipient, 0, 10);
        verify(notificationRepository).findByRecipientOrderByCreatedAtDesc(eq(recipient), any());
    }

    @Test
    void testGetUnreadCount() {
        when(notificationRepository.countByRecipientAndReadFalse(recipient))
                .thenReturn(3L);
        notificationService.getUnreadCount(recipient);
        verify(notificationRepository).countByRecipientAndReadFalse(recipient);
    }

    @Test
    void testDeleteNotification() {
        notificationService.deleteNotification(1L);
        verify(notificationRepository).deleteById(1L);
    }

    @Test
    void testMarkAsRead() {
        notificationService.markAsRead(1L, 2L);
        verify(notificationRepository).markAsRead(1L, 2L);
    }

    @Test
    void testMarkAllAsRead() {
        notificationService.markAllAsRead(2L);
        verify(notificationRepository).markAllAsRead(2L);
    }
}