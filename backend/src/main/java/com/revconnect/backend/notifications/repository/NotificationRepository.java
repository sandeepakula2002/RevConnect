package com.revconnect.backend.notifications.repository;

import com.revconnect.backend.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Spring automatically builds this SQL query from the method name:
    // SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Count how many unread notifications a user has
    long countByUserIdAndIsRead(Long userId, boolean isRead);
}