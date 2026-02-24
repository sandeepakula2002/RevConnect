package com.revconnect.like.repository;

import com.revconnect.like.model.Like;
import com.revconnect.post.model.Post;
import com.revconnect.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);

    long countByPost(Post post);

    long countByUser(User user);

    void deleteByPostAndUser(Post post, User user);
}
