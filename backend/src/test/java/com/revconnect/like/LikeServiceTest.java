package com.revconnect.like;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import com.revconnect.like.service.LikeService;
import com.revconnect.like.repository.LikeRepository;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.post.model.Post;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LikeServiceTest {

    @InjectMocks
    LikeService likeService;

    @Mock
    LikeRepository likeRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    UserService userService;

    @Mock
    NotificationService notificationService;

    private User user;
    private Post post;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("vasanth");

        post = new Post();
        post.setId(1L);
    }

    @Test
    void testLikePost() {

        when(userService.getUserByUsername("vasanth"))
                .thenReturn(user);

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(likeRepository.existsByPostAndUser(post,user))
                .thenReturn(false);

        when(likeRepository.countByPost(post))
                .thenReturn(1L);

        Map<String,Object> result =
                likeService.likePost(1L,"vasanth");

        assertEquals(true,result.get("liked"));
        assertEquals(1L,result.get("likeCount"));
    }
}