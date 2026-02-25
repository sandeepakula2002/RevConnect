package com.revconnect.backend.profile.controller;

import com.revconnect.backend.profile.model.Profile;
import com.revconnect.backend.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ✅ Create Profile
    @PostMapping
    public Profile createProfile(@RequestBody Profile profile) {
        return profileService.createProfile(profile);
    }

    // ✅ Get My Profile
    @GetMapping("/me")
    public Profile getMyProfile() {
        return profileService.getMyProfile();
    }

    // ✅ Update Profile
    @PutMapping
    public Profile updateProfile(@RequestBody Profile profile) {
        return profileService.updateProfile(profile);
    }

    // ✅ Delete by ID
    @DeleteMapping("/{id}")
    public String deleteProfileById(@PathVariable Long id) {
        return profileService.deleteProfileById(id);
    }

    // ✅ Get All Profiles
    @GetMapping
    public List<Profile> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    // ✅ Search Profiles
    @GetMapping("/search")
    public List<Profile> search(@RequestParam String name) {
        return profileService.searchByName(name);
    }
}