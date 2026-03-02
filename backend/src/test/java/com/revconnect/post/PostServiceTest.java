package com.revconnect.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.revconnect.comment.repository.CommentRepository;
import com.revconnect.like.repository.LikeRepository;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.post.model.Post;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.post.service.PostService;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NotificationService notificationService;

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
        post.setContent("Hello World");
        post.setUser(user);
        post.setViewCount(10);
    }

    @Test
    void testGetPost() {

        when(userService.getUserByUsername("vasanth"))
                .thenReturn(user);

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(likeRepository.countByPost(post))
                .thenReturn(5L);

        when(commentRepository.countByPost(post))
                .thenReturn(2L);

        when(likeRepository.existsByPostAndUser(post, user))
                .thenReturn(true);

        PostDtos.PostResponse response =
                postService.getPost(1L, "vasanth");

        assertEquals("Hello World", response.getContent());
        assertEquals(5, response.getLikeCount());
        assertEquals(2, response.getCommentCount());
    }
}