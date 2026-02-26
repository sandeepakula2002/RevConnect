package com.revconnect.notification.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.notification.model.Notification;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired private NotificationService notificationService;
    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Notification>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.getUserByUsername(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getNotifications(user, page, size)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.getUserByUsername(currentUser.getUsername());
        long count = notificationService.getUnreadCount(user);
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.getUserByUsername(currentUser.getUsername());
        notificationService.markAsRead(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Marked as read", null));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.getUserByUsername(currentUser.getUsername());
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
