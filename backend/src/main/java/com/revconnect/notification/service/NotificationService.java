package com.revconnect.notification.service;

import com.revconnect.notification.model.Notification;
import com.revconnect.notification.model.NotificationType;
import com.revconnect.notification.repository.NotificationRepository;
import com.revconnect.post.model.Post;
import com.revconnect.user.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    // ─── Trigger methods ─────────────────────────────────────────────

    public void notifyConnectionRequest(User sender, User recipient) {
        save(sender, recipient, NotificationType.CONNECTION_REQUEST, null,
             sender.getUsername() + " sent you a connection request");
    }

    public void notifyConnectionAccepted(User sender, User recipient) {
        save(sender, recipient, NotificationType.CONNECTION_ACCEPTED, null,
             sender.getUsername() + " accepted your connection request");
    }

    public void notifyNewFollower(User follower, User following) {
        save(follower, following, NotificationType.NEW_FOLLOWER, null,
             follower.getUsername() + " started following you");
    }

    public void notifyLike(User liker, Post post) {
        if (!liker.getId().equals(post.getUser().getId())) {  // Don't notify self
            save(liker, post.getUser(), NotificationType.POST_LIKED, post.getId(),
                 liker.getUsername() + " liked your post");
        }
    }

    public void notifyComment(User commenter, Post post) {
        if (!commenter.getId().equals(post.getUser().getId())) {
            save(commenter, post.getUser(), NotificationType.POST_COMMENTED, post.getId(),
                 commenter.getUsername() + " commented on your post");
        }
    }

    public void notifyShare(User sharer, Post post) {
        if (!sharer.getId().equals(post.getUser().getId())) {
            save(sharer, post.getUser(), NotificationType.POST_SHARED, post.getId(),
                 sharer.getUsername() + " shared your post");
        }
    }

    // ─── Query methods ───────────────────────────────────────────────

    public Page<Notification> getNotifications(User user, int page, int size) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(
                user, PageRequest.of(page, size));
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.markAsRead(notificationId, userId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    // ─── Private helper ──────────────────────────────────────────────
    private void save(User sender, User recipient, NotificationType type,
                      Long referenceId, String message) {
        Notification notification = Notification.builder()
                .sender(sender)
                .recipient(recipient)
                .type(type)
                .referenceId(referenceId)
                .message(message)
                .build();
        notificationRepository.save(notification);
        logger.debug("Notification created: {} -> {}: {}", sender.getUsername(),
                     recipient.getUsername(), type);
    }
}
