package com.revconnect.backend.users.service;

import com.revconnect.backend.users.dto.ProfileRequestDto;
import com.revconnect.backend.users.model.Profile;
import com.revconnect.backend.users.model.User;
import com.revconnect.backend.users.repository.ProfileRepository;
import com.revconnect.backend.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;

    public UserService(UserRepository userRepo, ProfileRepository profileRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    // VIEW PROFILE
    public Profile getProfile(Long userId) {
        return profileRepo.findByUserId(userId);
    }

    // CREATE / UPDATE PROFILE (FIXED)
    public Profile updateProfile(ProfileRequestDto dto) {

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepo.findByUserId(dto.getUserId());
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
        }

        profile.setName(dto.getName());
        profile.setBio(dto.getBio());
        profile.setLocation(dto.getLocation());
        profile.setWebsite(dto.getWebsite());
        profile.setCategory(dto.getCategory());
        profile.setContactInfo(dto.getContactInfo());
        profile.setSocialLinks(dto.getSocialLinks());
        profile.setBusinessAddress(dto.getBusinessAddress());
        profile.setBusinessHours(dto.getBusinessHours());

        return profileRepo.save(profile);
    }

    // SEARCH USERS
    public List<User> searchUsers(String query) {
        return userRepo.findByUsernameContainingIgnoreCase(query);
    }

    // PRIVACY SETTINGS
    public void updatePrivacy(Long userId, boolean isPrivate) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPrivate(isPrivate);
        userRepo.save(user);
    }
}