package com.revconnect.network.service;

import com.revconnect.common.exception.BadRequestException;
import com.revconnect.common.exception.ResourceNotFoundException;
import com.revconnect.common.exception.UnauthorizedException;
import com.revconnect.network.dto.ConnectionResponse;
import com.revconnect.network.model.Connection;
import com.revconnect.network.model.ConnectionStatus;
import com.revconnect.network.model.Follow;
import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.network.repository.FollowRepository;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.user.dto.UserDtos;
import com.revconnect.user.model.User;
import com.revconnect.user.repository.UserRepository;
import com.revconnect.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NetworkService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;


    // ─────────────────────────────
    // Send Connection Request
    // ─────────────────────────────

    @Transactional
    public ConnectionResponse sendConnectionRequest(Long targetUserId, String username) {

        User currentUser = userService.getUserByUsername(username);
        User targetUser = userService.getUserById(targetUserId);

        if (currentUser.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot connect with yourself");
        }

        connectionRepository
                .findConnectionBetween(currentUser.getId(), targetUserId)
                .ifPresent(c -> {
                    throw new BadRequestException("Connection already exists or pending");
                });

        Connection connection = Connection.builder()
                .requester(currentUser)
                .addressee(targetUser)
                .status(ConnectionStatus.PENDING)
                .build();

        connection = connectionRepository.save(connection);

        notificationService.notifyConnectionRequest(currentUser, targetUser);

        return ConnectionResponse.from(connection);
    }


    // ─────────────────────────────
    // Accept / Reject Request
    // ─────────────────────────────

    @Transactional
    public ConnectionResponse respondToRequest(Long connectionId, boolean accept, String username) {

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        if (!connection.getAddressee().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only respond to your own requests");
        }

        connection.setStatus(
                accept ? ConnectionStatus.ACCEPTED : ConnectionStatus.REJECTED
        );

        connection = connectionRepository.save(connection);

        return ConnectionResponse.from(connection);
    }


    // ─────────────────────────────
    // Remove Connection
    // ─────────────────────────────

    @Transactional
    public void removeConnection(Long targetUserId, String username) {

        User currentUser = userService.getUserByUsername(username);

        Connection connection = connectionRepository
                .findConnectionBetween(currentUser.getId(), targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found"));

        connectionRepository.delete(connection);
    }


    // ─────────────────────────────
    // Get Accepted Connections
    // ─────────────────────────────

    @Transactional(readOnly = true)
    public List<ConnectionResponse> getConnections(String username) {

        User user = userService.getUserByUsername(username);

        return connectionRepository.findAcceptedConnections(user.getId())
                .stream()
                .map(ConnectionResponse::from)
                .collect(Collectors.toList());
    }


    // ─────────────────────────────
    // Get Pending Requests (Received)
    // ─────────────────────────────

    @Transactional(readOnly = true)
    public List<ConnectionResponse> getPendingRequests(String username) {

        User user = userService.getUserByUsername(username);

        return connectionRepository
                .findReceivedRequests(user, ConnectionStatus.PENDING)
                .stream()
                .map(ConnectionResponse::from)
                .collect(Collectors.toList());
    }


    // ─────────────────────────────
    // Get Sent Requests
    // ─────────────────────────────

    @Transactional(readOnly = true)
    public List<ConnectionResponse> getSentRequests(String username) {

        User user = userService.getUserByUsername(username);

        return connectionRepository
                .findSentRequests(user, ConnectionStatus.PENDING)
                .stream()
                .map(ConnectionResponse::from)
                .collect(Collectors.toList());
    }


    // ─────────────────────────────
    // Suggested Connections
    // ─────────────────────────────

    @Transactional(readOnly = true)
    public List<UserDtos.UserResponse> getSuggestedConnections(String username, int limit) {

        User currentUser = userService.getUserByUsername(username);

        List<Long> connectedIds =
                connectionRepository.findConnectedUserIds(currentUser.getId());

        return userRepository.findAll()
                .stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .filter(u -> !connectedIds.contains(u.getId()))
                .limit(limit)
                .map(UserDtos.UserResponse::from)
                .collect(Collectors.toList());
    }


    // ─────────────────────────────
    // Follow User
    // ─────────────────────────────

    @Transactional
    public Follow followUser(Long targetUserId, String username) {

        User follower = userService.getUserByUsername(username);
        User following = userService.getUserById(targetUserId);

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BadRequestException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        follow = followRepository.save(follow);

        notificationService.notifyNewFollower(follower, following);

        return follow;
    }


    // ─────────────────────────────
    // Unfollow
    // ─────────────────────────────

    @Transactional
    public void unfollowUser(Long targetUserId, String username) {

        User follower = userService.getUserByUsername(username);
        User following = userService.getUserById(targetUserId);

        followRepository.deleteByFollowerAndFollowing(follower, following);
    }


    // ─────────────────────────────
    // Followers
    // ─────────────────────────────

    @Transactional(readOnly = true)
    public List<Follow> getFollowers(Long userId) {

        User user = userService.getUserById(userId);

        return followRepository.findByFollowing(user);
    }


    // ─────────────────────────────
    // Following
    // ─────────────────────────────

    @Transactional(readOnly = true)
    public List<Follow> getFollowing(Long userId) {

        User user = userService.getUserById(userId);

        return followRepository.findByFollower(user);
    }
}