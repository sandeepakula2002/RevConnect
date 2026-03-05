package com.revconnect.comment.repository;

import com.revconnect.comment.model.Comment;
import com.revconnect.post.model.Post;
import com.revconnect.user.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
        SELECT c
        FROM Comment c
        JOIN FETCH c.user
        JOIN FETCH c.post
        WHERE c.post = :post
        ORDER BY c.createdAt ASC
    """)
    Page<Comment> findByPostOrderByCreatedAtAsc(Post post, Pageable pageable);

    long countByPost(Post post);

    long countByUser(User user);

    void deleteByIdAndUser(Long id, User user);
}