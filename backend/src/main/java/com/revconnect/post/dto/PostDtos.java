package com.revconnect.post.dto;

import com.revconnect.post.model.Post;
import com.revconnect.post.model.PostType;
import com.revconnect.user.dto.UserDtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class PostDtos {

    @Data
    public static class CreatePostRequest {
        private String content;
        private String hashtags;
        private String imageUrl;
        private PostType type = PostType.TEXT;
        private String callToActionLabel;
        private String callToActionUrl;
        private LocalDateTime scheduledAt;
        private Long originalPostId;
    }

    @Data
    public static class UpdatePostRequest {
        private String content;
        private String hashtags;
        private boolean pinned;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResponse {

        private Long id;
        private UserDtos.UserResponse author;
        private String content;
        private String hashtags;
        private String imageUrl;
        private String type;
        private String callToActionLabel;
        private String callToActionUrl;
        private boolean pinned;
        private int viewCount;
        private long likeCount;
        private long commentCount;
        private long shareCount;
        private boolean likedByCurrentUser;
        private PostResponse originalPost;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static PostResponse from(Post post) {

            UserDtos.UserResponse author = null;

            if(post.getUser() != null){
                author = UserDtos.UserResponse.from(post.getUser());
            }

            return PostResponse.builder()
                    .id(post.getId())
                    .author(author)
                    .content(post.getContent())
                    .hashtags(post.getHashtags())
                    .imageUrl(post.getImageUrl())
                    .type(post.getType() != null ? post.getType().name() : "TEXT")
                    .callToActionLabel(post.getCallToActionLabel())
                    .callToActionUrl(post.getCallToActionUrl())
                    .pinned(post.isPinned())
                    .viewCount(post.getViewCount())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostAnalyticsResponse {

        private Long postId;
        private int viewCount;
        private long likeCount;
        private long commentCount;
        private long shareCount;
        private double engagementRate;
    }
}