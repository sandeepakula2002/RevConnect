package com.revconnect.user.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.user.dto.UserDtos;
import com.revconnect.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDtos.UserResponse>> getUserProfile(
            @PathVariable Long id,
            Principal principal) {

        String username = principal.getName();

        UserDtos.UserResponse profile =
                userService.getUserProfile(id, username);

        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDtos.UserResponse>> updateProfile(
            @PathVariable Long id,
            @RequestBody UserDtos.ProfileUpdateRequest request,
            Principal principal) {

        String username = principal.getName();

        UserDtos.UserResponse updated =
                userService.updateProfile(id, request, username);

        return ResponseEntity.ok(
                ApiResponse.success("Profile updated successfully", updated));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDtos.UserResponse>>> searchUsers(
            @RequestParam String q,
            Principal principal) {

        String username =
                principal != null ? principal.getName() : null;

        List<UserDtos.UserResponse> results =
                userService.searchUsers(q, username);

        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDtos.UserResponse>> getCurrentUser(
            Principal principal) {

        String username = principal.getName();

        Long userId =
                userService.getUserByUsername(username).getId();

        UserDtos.UserResponse user =
                userService.getUserProfile(userId, username);

        return ResponseEntity.ok(ApiResponse.success(user));
    }
}