package com.revconnect.backend.users.controller;

import com.revconnect.backend.users.dto.ProfileRequestDto;
import com.revconnect.backend.users.model.Profile;
import com.revconnect.backend.users.model.User;
import com.revconnect.backend.users.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{userId}/profile")
    public Profile viewProfile(@PathVariable Long userId) {
        return service.getProfile(userId);
    }

    @PutMapping("/profile")
    public Profile editProfile(@RequestBody ProfileRequestDto dto) {
        return service.updateProfile(dto);
    }

    @GetMapping("/search")
    public List<User> search(@RequestParam String q) {
        return service.searchUsers(q);
    }

    @PutMapping("/{userId}/privacy")
    public void updatePrivacy(
            @PathVariable Long userId,
            @RequestParam boolean isPrivate) {
        service.updatePrivacy(userId, isPrivate);
    }
}