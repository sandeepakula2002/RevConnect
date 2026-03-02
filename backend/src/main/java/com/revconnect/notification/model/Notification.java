package com.revconnect.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.revconnect.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)   // CHANGED: LAZY → EAGER
    @JoinColumn(name = "recipient_id", nullable = false)
    @JsonIgnoreProperties({"password", "notifications", "posts", "connections", "hibernateLazyInitializer"})
    private User recipient;

    @ManyToOne(fetch = FetchType.EAGER)   // CHANGED: LAZY → EAGER
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnoreProperties({"password", "notifications", "posts", "connections", "hibernateLazyInitializer"})
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private Long referenceId;

    @Column(length = 500)
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}