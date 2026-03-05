package com.revconnect.comment.service;

import com.revconnect.comment.dto.CommentResponse;
import com.revconnect.comment.model.Comment;
import com.revconnect.comment.repository.CommentRepository;
import com.revconnect.common.exception.ResourceNotFoundException;
import com.revconnect.common.exception.UnauthorizedException;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.post.model.Post;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;


    // Add comment
    @Transactional
    public CommentResponse addComment(Long postId, String content, String username) {

        User user = userService.getUserByUsername(username);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();

        comment = commentRepository.save(comment);

        notificationService.notifyComment(user, post);

        return CommentResponse.from(comment);
    }


    // Get comments
    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(Long postId, int page, int size) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        return commentRepository
                .findByPostOrderByCreatedAtAsc(post, PageRequest.of(page, size))
                .map(CommentResponse::from);
    }


    // Delete comment
    @Transactional
    public void deleteComment(Long commentId, String username) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }
}