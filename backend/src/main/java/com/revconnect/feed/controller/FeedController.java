package com.revconnect.feed.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.feed.service.FeedService;
import com.revconnect.post.dto.PostDtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostDtos.PostResponse>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {

        String username = principal.getName();

        Page<PostDtos.PostResponse> feedPage =
                feedService.getFeed(username, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(feedPage.getContent())
        );
    }
}