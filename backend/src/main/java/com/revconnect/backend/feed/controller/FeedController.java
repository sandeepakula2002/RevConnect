package com.revconnect.feed.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.feed.service.FeedService;
import com.revconnect.post.dto.PostDtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostDtos.PostResponse>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                feedService.getFeed(currentUser.getUsername(), page, size)));
    }
}
