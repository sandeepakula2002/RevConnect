package com.revconnect.backend.posts.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.backend.posts.model.Share;

public interface ShareRepository extends JpaRepository<Share, Long> {
    
}
