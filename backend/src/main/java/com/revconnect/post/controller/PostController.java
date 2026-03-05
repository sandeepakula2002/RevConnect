package com.revconnect.post.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> createPost(
            @Valid @RequestBody PostDtos.CreatePostRequest request,
            Principal principal) {

        String username = principal.getName();

        PostDtos.PostResponse post =
                postService.createPost(request, username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Post created", post));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> getPost(
            @PathVariable Long id,
            Principal principal) {

        return ResponseEntity.ok(ApiResponse.success(
                postService.getPost(id, principal.getName())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> updatePost(
            @PathVariable Long id,
            @RequestBody PostDtos.UpdatePostRequest request,
            Principal principal) {

        return ResponseEntity.ok(ApiResponse.success("Post updated",
                postService.updatePost(id, request, principal.getName())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            Principal principal) {

        postService.deletePost(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Post deleted", null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostDtos.PostResponse>>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {

        return ResponseEntity.ok(ApiResponse.success(
                postService.getUserPosts(userId, page, size, principal.getName())));
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<PostDtos.PostResponse>>> getTrending(
            Principal principal) {

        return ResponseEntity.ok(ApiResponse.success(
                postService.getTrendingPosts(principal.getName())));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostDtos.PostResponse>>> searchByHashtag(
            @RequestParam String hashtag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {

        return ResponseEntity.ok(ApiResponse.success(
                postService.searchByHashtag(hashtag, page, size, principal.getName())));
    }

    @GetMapping("/{id}/analytics")
    public ResponseEntity<ApiResponse<PostDtos.PostAnalyticsResponse>> getAnalytics(
            @PathVariable Long id,
            Principal principal) {

        return ResponseEntity.ok(ApiResponse.success(
                postService.getPostAnalytics(id, principal.getName())));
    }
}