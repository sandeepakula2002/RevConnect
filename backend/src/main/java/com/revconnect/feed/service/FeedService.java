package com.revconnect.feed.service;

import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.network.repository.FollowRepository;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.post.service.PostService;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedService {

    @Autowired private PostRepository postRepository;
    @Autowired private ConnectionRepository connectionRepository;
    @Autowired private FollowRepository followRepository;
    @Autowired private UserService userService;
    @Autowired private PostService postService;

    public Page<PostDtos.PostResponse> getFeed(String username, int page, int size) {
        User user = userService.getUserByUsername(username);

        // Gather user IDs: own + connections + following
        List<Long> feedUserIds = new ArrayList<>();
        feedUserIds.add(user.getId());
        feedUserIds.addAll(connectionRepository.findConnectedUserIds(user.getId()));
        feedUserIds.addAll(followRepository.findFollowingIds(user.getId()));

        // Deduplicate
        List<Long> uniqueIds = feedUserIds.stream().distinct().toList();

        return postRepository.findFeedPosts(uniqueIds, PageRequest.of(page, size))
                .map(p -> {
                    PostDtos.PostResponse r = PostDtos.PostResponse.from(p);
                    return r;
                });
    }
}
