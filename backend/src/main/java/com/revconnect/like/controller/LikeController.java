package com.revconnect.like.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.like.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser) {
        Map<String, Object> result = likeService.likePost(postId, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Post liked", result));
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser) {
        Map<String, Object> result = likeService.unlikePost(postId, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Post unliked", result));
    }
}
