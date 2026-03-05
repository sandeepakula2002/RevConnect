package com.revconnect.network.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.network.dto.ConnectionResponse;
import com.revconnect.network.model.Follow;
import com.revconnect.network.service.NetworkService;
import com.revconnect.user.dto.UserDtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/network")
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    // Send connection request
    @PostMapping("/connect/{userId}")
    public ResponseEntity<ApiResponse<ConnectionResponse>> sendRequest(
            @PathVariable Long userId,
            Principal principal) {

        ConnectionResponse response =
                networkService.sendConnectionRequest(userId, principal.getName());

        return ResponseEntity.ok(
                ApiResponse.success("Connection request sent", response));
    }

    // Accept request
    @PutMapping("/connections/{connectionId}/accept")
    public ResponseEntity<ApiResponse<ConnectionResponse>> acceptRequest(
            @PathVariable Long connectionId,
            Principal principal) {

        ConnectionResponse response =
                networkService.respondToRequest(connectionId, true, principal.getName());

        return ResponseEntity.ok(
                ApiResponse.success("Connection accepted", response));
    }

    // Reject request
    @PutMapping("/connections/{connectionId}/reject")
    public ResponseEntity<ApiResponse<ConnectionResponse>> rejectRequest(
            @PathVariable Long connectionId,
            Principal principal) {

        ConnectionResponse response =
                networkService.respondToRequest(connectionId, false, principal.getName());

        return ResponseEntity.ok(
                ApiResponse.success("Connection rejected", response));
    }

    // Remove connection
    @DeleteMapping("/connect/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeConnection(
            @PathVariable Long userId,
            Principal principal) {

        networkService.removeConnection(userId, principal.getName());

        return ResponseEntity.ok(
                ApiResponse.success("Connection removed", null));
    }

    // Get connections
    @GetMapping("/connections")
    public ResponseEntity<ApiResponse<List<ConnectionResponse>>> getConnections(
            Principal principal) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        networkService.getConnections(principal.getName())));
    }

    // Pending requests
    @GetMapping("/requests/received")
    public ResponseEntity<ApiResponse<List<ConnectionResponse>>> getPendingRequests(
            Principal principal) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        networkService.getPendingRequests(principal.getName())));
    }

    // Sent requests
    @GetMapping("/requests/sent")
    public ResponseEntity<ApiResponse<List<ConnectionResponse>>> getSentRequests(
            Principal principal) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        networkService.getSentRequests(principal.getName())));
    }

    // Suggestions
    @GetMapping("/suggestions")
    public ResponseEntity<List<UserDtos.UserResponse>> getSuggestions(
            @RequestParam(defaultValue = "10") int limit,
            Principal principal) {

        return ResponseEntity.ok(
                networkService.getSuggestedConnections(principal.getName(), limit));
    }

    // Follow
    @PostMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<Follow>> followUser(
            @PathVariable Long userId,
            Principal principal) {

        Follow follow =
                networkService.followUser(userId, principal.getName());

        return ResponseEntity.ok(
                ApiResponse.success("Now following", follow));
    }

    // Unfollow
    @DeleteMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @PathVariable Long userId,
            Principal principal) {

        networkService.unfollowUser(userId, principal.getName());

        return ResponseEntity.ok(
                ApiResponse.success("Unfollowed", null));
    }

    // Followers
    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<Follow>>> getFollowers(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        networkService.getFollowers(userId)));
    }

    // Following
    @GetMapping("/following/{userId}")
    public ResponseEntity<ApiResponse<List<Follow>>> getFollowing(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        networkService.getFollowing(userId)));
    }
}