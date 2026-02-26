package com.revconnect.network.repository;

import com.revconnect.network.model.Follow;
import com.revconnect.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    List<Follow> findByFollower(User follower);

    List<Follow> findByFollowing(User following);

    void deleteByFollowerAndFollowing(User follower, User following);

    long countByFollowing(User following);   // follower count

    long countByFollower(User follower);     // following count

    @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId")
    List<Long> findFollowingIds(@Param("userId") Long userId);
}
