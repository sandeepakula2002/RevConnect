package com.revconnect.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.PERSONAL;

    // ─── Basic Profile Fields ───────────────────────────────────────────
    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 500)
    private String profilePicture;

    @Column(length = 100)
    private String location;



    @Column(length = 200)
    private String website;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PrivacyType privacy = PrivacyType.PUBLIC;

    // ─── Business / Creator Extra Fields ───────────────────────────────
    @Column(length = 150)
    private String businessName;

    @Column(length = 100)
    private String category;

    @Column(length = 200)
    private String contactEmail;

    @Column(length = 20)
    private String contactPhone;

    @Column(length = 300)
    private String businessAddress;

    @Column(length = 200)
    private String businessHours;

    @Column(columnDefinition = "TEXT")
    private String externalLinks;    // JSON-stored array of links

    // ─── Account Status ─────────────────────────────────────────────────
    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean verified = false;

    // ─── Timestamps ─────────────────────────────────────────────────────
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ─── Helper ─────────────────────────────────────────────────────────
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return username;
    }
}
