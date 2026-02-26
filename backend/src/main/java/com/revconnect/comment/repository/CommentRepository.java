package com.revconnect.comment.repository;

import com.revconnect.comment.model.Comment;
import com.revconnect.post.model.Post;
import com.revconnect.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostOrderByCreatedAtAsc(Post post, Pageable pageable);

    long countByPost(Post post);

    long countByUser(User user);

    void deleteByIdAndUser(Long id, User user);
}
