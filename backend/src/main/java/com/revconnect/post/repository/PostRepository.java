package com.revconnect.post.repository;

import com.revconnect.post.model.Post;
import com.revconnect.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Posts by user (profile page)
    Page<Post> findByUserAndPublishedTrueOrderByPinnedDescCreatedAtDesc(User user, Pageable pageable);

    // Feed: posts from connections + following + own
    @Query("SELECT p FROM Post p WHERE p.published = true AND p.scheduledAt IS NULL AND " +
           "(p.user.id IN :userIds) ORDER BY p.pinned DESC, p.createdAt DESC")
    Page<Post> findFeedPosts(@Param("userIds") List<Long> userIds, Pageable pageable);

    // Trending: most liked in last 24h
    @Query("SELECT p FROM Post p WHERE p.published = true AND " +
           "p.createdAt >= CURRENT_TIMESTAMP - 1 DAY " +
           "ORDER BY p.viewCount DESC")
    List<Post> findTrendingPosts(Pageable pageable);

    // Search by hashtag
    @Query("SELECT p FROM Post p WHERE p.published = true AND " +
           "LOWER(p.hashtags) LIKE LOWER(CONCAT('%', :hashtag, '%')) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findByHashtag(@Param("hashtag") String hashtag, Pageable pageable);

    // Increment view count
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    // Count posts by user (analytics)
    long countByUser(User user);
}
