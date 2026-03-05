package com.revconnect.notification.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.notification.model.Notification;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired private NotificationService notificationService;
    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {

        User user =
                userService.getUserByUsername(principal.getName());

        Page<Notification> notificationPage =
                notificationService.getNotifications(user, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(notificationPage.getContent())
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            Principal principal) {

        User user =
                userService.getUserByUsername(principal.getName());

        long count =
                notificationService.getUnreadCount(user);

        return ResponseEntity.ok(
                ApiResponse.success(Map.of("count", count))
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            Principal principal) {

        User user =
                userService.getUserByUsername(principal.getName());

        notificationService.markAsRead(id, user.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Marked as read", null)
        );
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            Principal principal) {

        User user =
                userService.getUserByUsername(principal.getName());

        notificationService.markAllAsRead(user.getId());

        return ResponseEntity.ok(
                ApiResponse.success("All notifications marked as read", null)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long id) {

        notificationService.deleteNotification(id);

        return ResponseEntity.ok(
                ApiResponse.success("Notification deleted", null)
        );
    }
}