package com.revconnect.backend.posts.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.backend.posts.model.PostHashtag;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    
    

}
