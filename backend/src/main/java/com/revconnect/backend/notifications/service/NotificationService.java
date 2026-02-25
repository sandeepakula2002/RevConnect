package com.revconnect.backend.notifications.service;

import com.revconnect.backend.notifications.model.Notification;
import com.revconnect.backend.notifications.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    // Spring automatically creates and injects this object for us
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    // ---- Create a new notification ----
    public Notification createNotification(Long userId, String type, Long sourceId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setSourceId(sourceId);
        notification.setMessage(message);
        // save() stores it in MySQL and returns the saved object (with generated ID)
        return repository.save(notification);
    }

    // ---- Get all notifications for a user (newest first) ----
    public List<Notification> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ---- Mark one notification as read ----
    public void markAsRead(Long notificationId) {
        // findById returns Optional — orElseThrow() throws error if not found
        Notification notif = repository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        notif.setRead(true);
        repository.save(notif);
    }

    // ---- Mark ALL notifications as read for a user ----
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = repository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Notification notif : notifications) {
            notif.setRead(true);
        }
        repository.saveAll(notifications);
    }

    // ---- Count unread notifications ----
    public long countUnread(Long userId) {
        return repository.countByUserIdAndIsRead(userId, false);
    }
}