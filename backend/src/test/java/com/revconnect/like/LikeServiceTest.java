package com.revconnect.like;

import com.revconnect.like.model.Like;
import com.revconnect.like.repository.LikeRepository;
import com.revconnect.like.service.LikeService;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.post.model.Post;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    private User user;
    private Post post;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).username("testuser").build();
        post = Post.builder().id(1L).build();
    }

    @Test
    void testLikePost() {

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.existsByPostAndUser(post, user)).thenReturn(false);
        when(likeRepository.countByPost(post)).thenReturn(1L);

        var result = likeService.likePost(1L, "testuser");

        assertEquals(true, result.get("liked"));
        verify(notificationService).notifyLike(user, post);
    }

    @Test
    void testUnlikePost() {

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.existsByPostAndUser(post, user)).thenReturn(true);
        when(likeRepository.countByPost(post)).thenReturn(0L);

        var result = likeService.unlikePost(1L, "testuser");

        assertEquals(false, result.get("liked"));
        verify(likeRepository).deleteByPostAndUser(post, user);
    }
}