package com.revconnect.comment;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

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

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        post = Post.builder()
                .id(1L)
                .build();
    }

    @Test
    void testAddComment() {

        when(userService.getUserByUsername("testuser"))
                .thenReturn(user);

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Comment comment = commentService.addComment(1L, "Nice", "testuser");

        assertEquals("Nice", comment.getContent());
        verify(notificationService).notifyComment(user, post);
    }

    @Test
    void testGetComments() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(commentRepository.findByPostOrderByCreatedAtAsc(any(), any()))
                .thenReturn(new PageImpl<>(java.util.List.of()));

        commentService.getComments(1L, 0, 10);

        verify(commentRepository).findByPostOrderByCreatedAtAsc(any(), any());
    }

    @Test
    void testDeleteComment() {

        Comment comment = Comment.builder()
                .id(1L)
                .user(user)
                .build();

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, "testuser");

        verify(commentRepository).delete(comment);
    }
}