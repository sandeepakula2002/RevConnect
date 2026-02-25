package com.revconnect.backend.posts.dto;

import java.util.List;
import lombok.Data;

@Data
public class CreatePostRequest {

    private Long userId;

    private String content;

    private List<String> hashtags;

}