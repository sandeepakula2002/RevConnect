package com.revconnect.backend.notifications.controller;

import com.revconnect.backend.notifications.model.Notification;
import com.revconnect.backend.notifications.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")   // All endpoints start with this base URL
@CrossOrigin(origins = "*")             // Allows frontend (React/Angular) to call this
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET /api/notifications/user/1
    // Returns all notifications for user with ID 1
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // GET /api/notifications/user/1/unread-count
    // Returns how many unread notifications user 1 has
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(count);
    }

    // PUT /api/notifications/5/read
    // Marks notification with ID 5 as read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }

    // PUT /api/notifications/user/1/read-all
    // Marks all notifications for user 1 as read
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    // POST /api/notifications
    // Creates a new notification (used internally by other services)
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification saved = notificationService.createNotification(
                notification.getUserId(),
                notification.getType(),
                notification.getSourceId(),
                notification.getMessage()
        );
        return ResponseEntity.ok(saved);
    }
}