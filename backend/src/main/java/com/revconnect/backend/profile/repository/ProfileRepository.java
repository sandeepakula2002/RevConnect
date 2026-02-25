package com.revconnect.backend.profile.repository;

import com.revconnect.backend.profile.model.Profile;
import com.revconnect.backend.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);

    List<Profile> findByNameContainingIgnoreCase(String name);
}