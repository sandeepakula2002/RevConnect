package com.revconnect.backend.posts.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name="shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="post_id", nullable=false)
    private Long postId;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @PrePersist
    public void setTime(){
        this.createdAt = LocalDateTime.now();
    }
}