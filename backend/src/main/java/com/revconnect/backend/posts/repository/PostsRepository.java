package com.revconnect.backend.posts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.backend.posts.model.Posts;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    
      List<Posts> findByUserId(Long userId);

}
