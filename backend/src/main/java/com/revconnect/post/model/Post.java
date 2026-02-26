package com.revconnect.post.model;

import com.revconnect.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank
    private String content;

    // For reposts — points to original
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_post_id")
    private Post originalPost;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PostType type = PostType.TEXT;

    // Business/Creator features
    @Column(length = 50)
    private String callToActionLabel;   // "Shop Now", "Learn More"

    @Column(length = 300)
    private String callToActionUrl;

    @Builder.Default
    private boolean pinned = false;

    // Scheduling
    private LocalDateTime scheduledAt;

    @Builder.Default
    private boolean published = true;

    // Analytics
    @Builder.Default
    private int viewCount = 0;

    // Hashtags (stored as comma-separated for simplicity, queried via LIKE)
    @Column(length = 500)
    private String hashtags;

    @Column(length = 500)
    private String imageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
