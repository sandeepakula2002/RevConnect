package com.revconnect.post;

import com.revconnect.comment.repository.CommentRepository;
import com.revconnect.common.exception.ResourceNotFoundException;
import com.revconnect.common.exception.UnauthorizedException;
import com.revconnect.like.repository.LikeRepository;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.post.model.Post;
import com.revconnect.post.model.PostType;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock private PostRepository postRepository;
    @Mock private UserService userService;
    @Mock private LikeRepository likeRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private NotificationService notificationService;

    private User user;
    private Post post;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).username("demoUser").build();

        post = Post.builder()
                .id(10L)
                .user(user)
                .content("Hello")
                .viewCount(100) // ✅ match entity type
                .type(PostType.TEXT)
                .build();
    }

    // ✅ CREATE POST
    @Test
    void testCreatePost() {

        when(userService.getUserByUsername("demoUser")).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(likeRepository.countByPost(any())).thenReturn(0L);
        when(commentRepository.countByPost(any())).thenReturn(0L);
        when(likeRepository.existsByPostAndUser(any(), any())).thenReturn(false);

        PostDtos.CreatePostRequest request = new PostDtos.CreatePostRequest();
        request.setContent("Hello");

        PostDtos.PostResponse response =
                postService.createPost(request, "demoUser");

        assertEquals("Hello", response.getContent());
    }

    // ✅ UPDATE UNAUTHORIZED
    @Test
    void testUpdatePost_Unauthorized() {

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        PostDtos.UpdatePostRequest request =
                new PostDtos.UpdatePostRequest();

        assertThrows(UnauthorizedException.class,
                () -> postService.updatePost(10L, request, "otherUser"));
    }

    // ✅ DELETE UNAUTHORIZED
    @Test
    void testDeletePost_Unauthorized() {

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertThrows(UnauthorizedException.class,
                () -> postService.deletePost(10L, "otherUser"));
    }

    // ✅ ANALYTICS SUCCESS
    @Test
    void testGetPostAnalytics() {

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(likeRepository.countByPost(post)).thenReturn(20L);
        when(commentRepository.countByPost(post)).thenReturn(10L);

        var analytics = postService.getPostAnalytics(10L, "demoUser");

        assertEquals(20L, analytics.getLikeCount());
        assertEquals(10L, analytics.getCommentCount());
        assertEquals(100, analytics.getViewCount());
    }

    // ✅ ANALYTICS UNAUTHORIZED
    @Test
    void testGetPostAnalytics_Unauthorized() {

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertThrows(UnauthorizedException.class,
                () -> postService.getPostAnalytics(10L, "otherUser"));
    }

    // ✅ GET POST NOT FOUND
    @Test
    void testGetPost_NotFound() {

        when(userService.getUserByUsername("demoUser")).thenReturn(user);
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> postService.getPost(99L, "demoUser"));
    }
}