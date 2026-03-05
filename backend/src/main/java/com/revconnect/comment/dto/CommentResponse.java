package com.revconnect.comment.dto;

import com.revconnect.comment.model.Comment;
import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private String content;

    private Long postId;

    private Long userId;
    private String username;

    private LocalDateTime createdAt;

    public static CommentResponse from(Comment c) {

        CommentResponse r = new CommentResponse();

        r.id = c.getId();
        r.content = c.getContent();

        r.postId = c.getPost().getId();

        r.userId = c.getUser().getId();
        r.username = c.getUser().getUsername();

        r.createdAt = c.getCreatedAt();

        return r;
    }

    public Long getId() { return id; }

    public String getContent() { return content; }

    public Long getPostId() { return postId; }

    public Long getUserId() { return userId; }

    public String getUsername() { return username; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}