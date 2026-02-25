package com.revconnect.backend.network.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime; // Added import

@Entity
@Table(name = "connections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    private Long receiverId;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Added this field to match your Service and Database
    private LocalDateTime createdAt;

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}