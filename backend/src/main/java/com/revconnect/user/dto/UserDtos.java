package com.revconnect.user.dto;

import com.revconnect.user.model.PrivacyType;
import com.revconnect.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String fullName;
        private String bio;
        private String profilePicture;
        private String location;
        private String website;
        private String role;
        private PrivacyType privacy;
        private String businessName;
        private String category;
        private String contactEmail;
        private String contactPhone;
        private String businessAddress;
        private String businessHours;
        private long followerCount;
        private long followingCount;
        private long connectionCount;
        private long postCount;
        private boolean isFollowing;
        private boolean isConnected;
        private LocalDateTime createdAt;

        public static UserResponse from(User user) {
            return UserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .fullName(user.getFullName())
                    .bio(user.getBio())
                    .profilePicture(user.getProfilePicture())
                    .location(user.getLocation())
                    .website(user.getWebsite())
                    .role(user.getRole().name())
                    .privacy(user.getPrivacy())
                    .businessName(user.getBusinessName())
                    .category(user.getCategory())
                    .contactEmail(user.getContactEmail())
                    .contactPhone(user.getContactPhone())
                    .businessAddress(user.getBusinessAddress())
                    .businessHours(user.getBusinessHours())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }

    @Data
    public static class ProfileUpdateRequest {
        private String firstName;
        private String lastName;
        private String bio;
        private String location;
        private String website;
        private PrivacyType privacy;
        private String businessName;
        private String category;
        private String contactEmail;
        private String contactPhone;
        private String businessAddress;
        private String businessHours;
        private String externalLinks;
    }
}
