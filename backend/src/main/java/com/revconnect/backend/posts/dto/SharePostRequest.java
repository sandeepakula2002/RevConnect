package com.revconnect.backend.posts.dto;

import lombok.Data;

@Data
public class SharePostRequest {

    private Long userId;
    private Long postId;

}