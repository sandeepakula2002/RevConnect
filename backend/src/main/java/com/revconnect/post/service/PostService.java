package com.revconnect.post.service;

import com.revconnect.common.exception.ResourceNotFoundException;
import com.revconnect.common.exception.UnauthorizedException;
import com.revconnect.like.repository.LikeRepository;
import com.revconnect.comment.repository.CommentRepository;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.post.model.Post;
import com.revconnect.post.model.PostType;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.user.model.User;
import com.revconnect.user.repository.UserRepository;
import com.revconnect.user.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private static final Logger logger = LogManager.getLogger(PostService.class);

    @Autowired private PostRepository postRepository;
    @Autowired private UserService userService;
    @Autowired private LikeRepository likeRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private NotificationService notificationService;

    @Transactional
    public PostDtos.PostResponse createPost(PostDtos.CreatePostRequest request, String username) {
        User user = userService.getUserByUsername(username);

        Post.PostBuilder builder = Post.builder()
                .user(user)
                .content(request.getContent())
                .hashtags(request.getHashtags())
                .imageUrl(request.getImageUrl())
                .type(request.getType() != null ? request.getType() : PostType.TEXT)
                .callToActionLabel(request.getCallToActionLabel())
                .callToActionUrl(request.getCallToActionUrl())
                .scheduledAt(request.getScheduledAt())
                .published(request.getScheduledAt() == null);

        // Handle repost//
        if (request.getOriginalPostId() != null) {
            Post original = postRepository.findById(request.getOriginalPostId())
                    .orElseThrow(() -> new ResourceNotFoundException("Original post", request.getOriginalPostId()));
            builder.originalPost(original).type(PostType.REPOST);
            notificationService.notifyShare(user, original);
        }

        Post saved = postRepository.save(builder.build());
        logger.info("Post created by user: {}, postId: {}", username, saved.getId());
        return buildPostResponse(saved, user);
    }

    public PostDtos.PostResponse getPost(Long postId, String username) {
        User user = userService.getUserByUsername(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
        postRepository.incrementViewCount(postId);
        return buildPostResponse(post, user);
    }

    @Transactional
    public PostDtos.PostResponse updatePost(Long postId, PostDtos.UpdatePostRequest request, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        if (!post.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only edit your own posts");
        }

        if (request.getContent() != null)   post.setContent(request.getContent());
        if (request.getHashtags() != null)  post.setHashtags(request.getHashtags());
        post.setPinned(request.isPinned());

        Post updated = postRepository.save(post);
        return buildPostResponse(updated, post.getUser());
    }

    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        if (!post.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only delete your own posts");
        }
        postRepository.delete(post);
        logger.info("Post {} deleted by {}", postId, username);
    }

    public Page<PostDtos.PostResponse> getUserPosts(Long userId, int page, int size, String currentUsername) {
        User user = userService.getUserById(userId);
        User currentUser = userService.getUserByUsername(currentUsername);
        return postRepository.findByUserAndPublishedTrueOrderByPinnedDescCreatedAtDesc(
                user, PageRequest.of(page, size))
                .map(p -> buildPostResponse(p, currentUser));
    }

    public List<PostDtos.PostResponse> getTrendingPosts(String username) {
        User user = userService.getUserByUsername(username);
        return postRepository.findTrendingPosts(PageRequest.of(0, 20)).stream()
                .map(p -> buildPostResponse(p, user))
                .collect(Collectors.toList());
    }

    public Page<PostDtos.PostResponse> searchByHashtag(String hashtag, int page, int size, String username) {
        User user = userService.getUserByUsername(username);
        return postRepository.findByHashtag(hashtag, PageRequest.of(page, size))
                .map(p -> buildPostResponse(p, user));
    }

    // ─── Analytics ───────────────────────────────────────────────────
    public PostDtos.PostAnalyticsResponse getPostAnalytics(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
        if (!post.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("Analytics only available for your own posts");
        }
        long likes = likeRepository.countByPost(post);
        long comments = commentRepository.countByPost(post);

        double engagementRate = post.getViewCount() > 0
                ? ((double)(likes + comments) / post.getViewCount()) * 100 : 0;

        return PostDtos.PostAnalyticsResponse.builder()
                .postId(postId)
                .viewCount(post.getViewCount())
                .likeCount(likes)
                .commentCount(comments)
                .engagementRate(Math.round(engagementRate * 100.0) / 100.0)
                .build();
    }

    // ─── Private helper ──────────────────────────────────────────────
    private PostDtos.PostResponse buildPostResponse(Post post, User currentUser) {
        PostDtos.PostResponse response = PostDtos.PostResponse.from(post);
        response.setLikeCount(likeRepository.countByPost(post));
        response.setCommentCount(commentRepository.countByPost(post));
        response.setLikedByCurrentUser(likeRepository.existsByPostAndUser(post, currentUser));

        if (post.getOriginalPost() != null) {
            response.setOriginalPost(PostDtos.PostResponse.from(post.getOriginalPost()));
        }
        return response;
    }
}
