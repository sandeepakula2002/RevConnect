package com.revconnect.comment.controller;

import com.revconnect.comment.dto.CommentResponse;
import com.revconnect.comment.service.CommentService;
import com.revconnect.common.dto.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;


    // Add comment
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> body,
            Principal principal) {

        String content = body.get("content");

        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Comment content cannot be empty", 400));
        }

        String username = principal.getName();

        CommentResponse comment =
                commentService.addComment(postId, content, username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Comment added", comment));
    }


    // Get comments
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<CommentResponse> commentPage =
                commentService.getComments(postId, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(commentPage.getContent())
        );
    }


    // Delete comment
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            Principal principal) {

        String username = principal.getName();

        commentService.deleteComment(id, username);

        return ResponseEntity.ok(
                ApiResponse.success("Comment deleted", null)
        );
    }
}