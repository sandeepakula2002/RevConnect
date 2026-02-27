package com.revconnect.feed;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import com.revconnect.feed.service.FeedService;
import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.network.repository.FollowRepository;
import com.revconnect.post.model.Post;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class FeedServiceTest {

    @InjectMocks
    FeedService feedService;

    @Mock
    PostRepository postRepository;

    @Mock
    ConnectionRepository connectionRepository;

    @Mock
    FollowRepository followRepository;

    @Mock
    UserService userService;

    private User user;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("vasanth");
    }

    @Test
    void testGetFeed() {

        when(userService.getUserByUsername("vasanth"))
                .thenReturn(user);

        when(connectionRepository.findConnectedUserIds(1L))
                .thenReturn(List.of(2L,3L));

        when(followRepository.findFollowingIds(1L))
                .thenReturn(List.of(4L));

        Page<Post> page =
                new PageImpl<>(List.of(new Post()));

        when(postRepository.findFeedPosts(
                List.of(1L,2L,3L,4L),
                PageRequest.of(0,10)))
                .thenReturn(page);

        Page<?> result =
                feedService.getFeed("vasanth",0,10);

        assertNotNull(result);
    }
}