package com.revconnect.like.service;

import com.revconnect.common.exception.BadRequestException;
import com.revconnect.common.exception.ResourceNotFoundException;
import com.revconnect.like.model.Like;
import com.revconnect.like.repository.LikeRepository;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.post.model.Post;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class LikeService {

    @Autowired private LikeRepository likeRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserService userService;
    @Autowired private NotificationService notificationService;

    @Transactional
    public Map<String, Object> likePost(Long postId, String username) {
        User user = userService.getUserByUsername(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new BadRequestException("You have already liked this post");
        }

        Like like = Like.builder().post(post).user(user).build();
        likeRepository.save(like);
        notificationService.notifyLike(user, post);

        long totalLikes = likeRepository.countByPost(post);
        return Map.of("liked", true, "likeCount", totalLikes);
    }

    @Transactional
    public Map<String, Object> unlikePost(Long postId, String username) {
        User user = userService.getUserByUsername(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        if (!likeRepository.existsByPostAndUser(post, user)) {
            throw new BadRequestException("You haven't liked this post");
        }

        likeRepository.deleteByPostAndUser(post, user);
        long totalLikes = likeRepository.countByPost(post);
        return Map.of("liked", false, "likeCount", totalLikes);
    }
}
