package com.revconnect.post.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> createPost(
            @Valid @RequestBody PostDtos.CreatePostRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        PostDtos.PostResponse post = postService.createPost(request, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Post created", post));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.getPost(id, currentUser.getUsername())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> updatePost(
            @PathVariable Long id,
            @RequestBody PostDtos.UpdatePostRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Post updated",
                postService.updatePost(id, request, currentUser.getUsername())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        postService.deletePost(id, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Post deleted", null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostDtos.PostResponse>>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.getUserPosts(userId, page, size, currentUser.getUsername())));
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<PostDtos.PostResponse>>> getTrending(
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.getTrendingPosts(currentUser.getUsername())));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostDtos.PostResponse>>> searchByHashtag(
            @RequestParam String hashtag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.searchByHashtag(hashtag, page, size, currentUser.getUsername())));
    }

    @GetMapping("/{id}/analytics")
    public ResponseEntity<ApiResponse<PostDtos.PostAnalyticsResponse>> getAnalytics(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.getPostAnalytics(id, currentUser.getUsername())));
    }
}
