package com.revconnect;

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
import com.revconnect.user.model.UserRole;
import com.revconnect.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private UserService userService;
    @Mock private LikeRepository likeRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @Before
    public void setUp() {
        testUser = User.builder()
                .id(1L).username("john").email("john@email.com")
                .role(UserRole.PERSONAL).build();

        testPost = Post.builder()
                .id(1L).user(testUser).content("Hello World!")
                .type(PostType.TEXT).published(true).viewCount(0).build();
    }

    @Test
    public void testCreatePost_Success() {
        PostDtos.CreatePostRequest request = new PostDtos.CreatePostRequest();
        request.setContent("My first post");
        request.setType(PostType.TEXT);

        when(userService.getUserByUsername("john")).thenReturn(testUser);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(likeRepository.countByPost(any())).thenReturn(0L);
        when(commentRepository.countByPost(any())).thenReturn(0L);
        when(likeRepository.existsByPostAndUser(any(), any())).thenReturn(false);

        PostDtos.PostResponse response = postService.createPost(request, "john");

        assertNotNull(response);
        assertEquals("Hello World!", response.getContent());
        verify(postRepository).save(any(Post.class));
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeletePost_NotOwner_ThrowsException() {
        User anotherUser = User.builder().id(2L).username("jane").build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        postService.deletePost(1L, "jane");  // different user
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetPost_NotFound_ThrowsException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        when(userService.getUserByUsername("john")).thenReturn(testUser);
        postService.getPost(99L, "john");
    }

    @Test
    public void testDeletePost_Owner_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        postService.deletePost(1L, "john");
        verify(postRepository).delete(testPost);
    }

    @Test
    public void testGetPostAnalytics_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.countByPost(testPost)).thenReturn(10L);
        when(commentRepository.countByPost(testPost)).thenReturn(5L);

        PostDtos.PostAnalyticsResponse analytics = postService.getPostAnalytics(1L, "john");

        assertNotNull(analytics);
        assertEquals(10L, analytics.getLikeCount());
        assertEquals(5L, analytics.getCommentCount());
    }
}
