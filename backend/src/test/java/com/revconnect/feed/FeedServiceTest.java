package com.revconnect.feed;

import com.revconnect.feed.service.FeedService;
import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.network.repository.FollowRepository;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.post.model.Post;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.post.service.PostService;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @InjectMocks
    private FeedService feedService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("demoUser");
    }

    @Test
    void testGetFeed() {

        User user = new User();
        user.setId(1L);
        user.setUsername("demoUser");

        when(userService.getUserByUsername("demoUser"))
                .thenReturn(user);

        when(connectionRepository.findConnectedUserIds(1L))
                .thenReturn(List.of(2L,3L));

        when(followRepository.findFollowingIds(1L))
                .thenReturn(List.of(4L));

        User postUser = new User();
        postUser.setId(2L);
        postUser.setUsername("author");

        Post post = new Post();
        post.setId(10L);
        post.setUser(postUser);   // 🔥 REQUIRED
        post.setContent("Test");

        Page<Post> page = new PageImpl<>(List.of(post));

        when(postRepository.findFeedPosts(anyList(), any()))
                .thenReturn(page);

        Page<?> result = feedService.getFeed("demoUser",0,10);

        assertNotNull(result);
    }
}