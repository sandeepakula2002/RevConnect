package com.revconnect.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.revconnect.comment.model.Comment;
import com.revconnect.comment.repository.CommentRepository;
import com.revconnect.comment.service.CommentService;
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

public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("vasanth");

        post = new Post();
        post.setId(1L);

        comment = Comment.builder()
                .id(1L)
                .content("Nice post")
                .user(user)
                .post(post)
                .build();
    }

    @Test
    void testAddComment() {

        when(userService.getUserByUsername("vasanth"))
                .thenReturn(user);

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        Comment result =
                commentService.addComment(1L, "Nice post", "vasanth");

        assertEquals("Nice post", result.getContent());
    }
}
