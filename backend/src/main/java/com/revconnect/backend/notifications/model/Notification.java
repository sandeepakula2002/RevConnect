package com.revconnect.backend.notifications.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data                          // Lombok: auto-generates getters, setters, toString
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;           // Who receives the notification

    @Column(name = "type", nullable = false)
    private String type;           // e.g. "LIKE", "COMMENT", "FOLLOW"

    @Column(name = "source_id")
    private Long sourceId;         // ID of the post/comment that triggered it

    @Column(name = "message")
    private String message;        // e.g. "John liked your post"

    @Column(name = "is_read")
    private boolean isRead = false; // Has the user seen this notification?

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // This runs automatically BEFORE saving a new record to DB
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
}