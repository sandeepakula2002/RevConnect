package com.revconnect.backend.profile.service;

import com.revconnect.backend.auth.model.User;
import com.revconnect.backend.auth.repository.UserRepository;
import com.revconnect.backend.profile.model.Profile;
import com.revconnect.backend.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    // 🔐 Get logged-in user from JWT (FIXED VERSION)
    private User getLoggedInUser() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email;

        // 🔥 FIX: handle both UserDetails and String
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Create Profile
    public Profile createProfile(Profile profile) {

        User user = getLoggedInUser();

        if (profileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Profile already exists");
        }

        profile.setUser(user);

        return profileRepository.save(profile);
    }

    // ✅ Get My Profile
    public Profile getMyProfile() {

        User user = getLoggedInUser();

        return profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    // ✅ Update Profile
    public Profile updateProfile(Profile updatedProfile) {

        User user = getLoggedInUser();

        Profile existing = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        existing.setName(updatedProfile.getName());
        existing.setBio(updatedProfile.getBio());
        existing.setProfilePicturePath(updatedProfile.getProfilePicturePath());
        existing.setLocation(updatedProfile.getLocation());
        existing.setWebsite(updatedProfile.getWebsite());

        return profileRepository.save(existing);
    }

    // ✅ Delete Profile by ID (SECURE)
    public String deleteProfileById(Long id) {

        User loggedInUser = getLoggedInUser();

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // 🔒 Only owner can delete
        if (!profile.getUser().getId().equals(loggedInUser.getId())) {
            throw new RuntimeException("You can delete only your own profile");
        }

        profileRepository.delete(profile);

        return "Profile deleted successfully";
    }

    // ✅ Get All Profiles
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    // ✅ Search Profiles
    public List<Profile> searchByName(String name) {
        return profileRepository.findByNameContainingIgnoreCase(name);
    }
}