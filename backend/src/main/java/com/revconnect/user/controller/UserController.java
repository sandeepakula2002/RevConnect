package com.revconnect.user.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.user.dto.UserDtos;
import com.revconnect.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDtos.UserResponse>> getUserProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        UserDtos.UserResponse profile = userService.getUserProfile(id, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDtos.UserResponse>> updateProfile(
            @PathVariable Long id,
            @RequestBody UserDtos.ProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        UserDtos.UserResponse updated = userService.updateProfile(id, request, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDtos.UserResponse>>> searchUsers(
            @RequestParam String q,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<UserDtos.UserResponse> results = userService.searchUsers(q,
                currentUser != null ? currentUser.getUsername() : null);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDtos.UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails currentUser) {
        UserDtos.UserResponse user = userService.getUserProfile(
                userService.getUserByUsername(currentUser.getUsername()).getId(),
                currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
