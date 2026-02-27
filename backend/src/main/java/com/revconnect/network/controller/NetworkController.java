package com.revconnect.network.controller;

import com.revconnect.common.dto.ApiResponse;
import com.revconnect.network.model.Connection;
import com.revconnect.network.model.Follow;
import com.revconnect.network.service.NetworkService;
import com.revconnect.user.dto.UserDtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/network")
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    // ─── Connections ─────────────────────────────────────────────────

    @PostMapping("/connect/{userId}")
    public ResponseEntity<ApiResponse<Connection>> sendRequest(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser) {
        Connection c = networkService.sendConnectionRequest(userId, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Connection request sent", c));
    }

    @PutMapping("/connections/{connectionId}/accept")
    public ResponseEntity<ApiResponse<Connection>> acceptRequest(
            @PathVariable Long connectionId,
            @AuthenticationPrincipal UserDetails currentUser) {
        Connection c = networkService.respondToRequest(connectionId, true, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Connection accepted", c));
    }

    @PutMapping("/connections/{connectionId}/reject")
    public ResponseEntity<ApiResponse<Connection>> rejectRequest(
            @PathVariable Long connectionId,
            @AuthenticationPrincipal UserDetails currentUser) {
        Connection c = networkService.respondToRequest(connectionId, false, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Connection rejected", c));
    }

    @DeleteMapping("/connect/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeConnection(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser) {
        networkService.removeConnection(userId, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Connection removed", null));
    }

    @GetMapping("/connections")
    public ResponseEntity<ApiResponse<List<Connection>>> getConnections(
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                networkService.getConnections(currentUser.getUsername())));
    }

    @GetMapping("/requests/received")
    public ResponseEntity<ApiResponse<List<Connection>>> getPendingRequests(
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                networkService.getPendingRequests(currentUser.getUsername())));
    }

    @GetMapping("/requests/sent")
    public ResponseEntity<ApiResponse<List<Connection>>> getSentRequests(
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                networkService.getSentRequests(currentUser.getUsername())));
    }

    // ─── Suggestions ─────────────────────────────────────────────────

    @GetMapping("/suggestions")
    public ResponseEntity<List<UserDtos.UserResponse>> getSuggestions(
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<UserDtos.UserResponse> suggestions =
                networkService.getSuggestedConnections(currentUser.getUsername(), limit);
        return ResponseEntity.ok(suggestions);
    }

    // ─── Follow ──────────────────────────────────────────────────────

    @PostMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<Follow>> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser) {
        Follow follow = networkService.followUser(userId, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Now following", follow));
    }

    @DeleteMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser) {
        networkService.unfollowUser(userId, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Unfollowed", null));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<Follow>>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(networkService.getFollowers(userId)));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<ApiResponse<List<Follow>>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(networkService.getFollowing(userId)));
    }
}