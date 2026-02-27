package com.revconnect.network.service;

import com.revconnect.common.exception.BadRequestException;
import com.revconnect.common.exception.ResourceNotFoundException;
import com.revconnect.common.exception.UnauthorizedException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NetworkService {

    private static final Logger logger = LogManager.getLogger(NetworkService.class);

    @Autowired private ConnectionRepository connectionRepository;
    @Autowired private FollowRepository followRepository;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;

    // ─── Connections ─────────────────────────────────────────────────

    @Transactional
    public Connection sendConnectionRequest(Long targetUserId, String currentUsername) {
        User currentUser = userService.getUserByUsername(currentUsername);
        User targetUser = userService.getUserById(targetUserId);

        if (currentUser.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot connect with yourself");
        }

        connectionRepository.findConnectionBetween(currentUser.getId(), targetUserId)
                .ifPresent(c -> { throw new BadRequestException("Connection already exists or is pending"); });

        Connection connection = Connection.builder()
                .requester(currentUser)
                .addressee(targetUser)
                .build();
        connection = connectionRepository.save(connection);
        notificationService.notifyConnectionRequest(currentUser, targetUser);
        logger.info("Connection request: {} -> {}", currentUsername, targetUser.getUsername());
        return connection;
    }

    @Transactional
    public Connection respondToRequest(Long connectionId, boolean accept, String currentUsername) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        if (!connection.getAddressee().getUsername().equals(currentUsername)) {
            throw new UnauthorizedException("You can only respond to your own requests");
        }

        connection.setStatus(accept ? ConnectionStatus.ACCEPTED : ConnectionStatus.REJECTED);
        connection = connectionRepository.save(connection);

        if (accept) {
            notificationService.notifyConnectionAccepted(
                    connection.getAddressee(), connection.getRequester());
        }
        return connection;
    }

    @Transactional
    public void removeConnection(Long targetUserId, String currentUsername) {
        User currentUser = userService.getUserByUsername(currentUsername);
        Connection connection = connectionRepository
                .findConnectionBetween(currentUser.getId(), targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found"));
        connectionRepository.delete(connection);
    }

    public List<Connection> getConnections(String username) {
        User user = userService.getUserByUsername(username);
        return connectionRepository.findAcceptedConnections(user.getId());
    }

    public List<Connection> getPendingRequests(String username) {
        User user = userService.getUserByUsername(username);
        return connectionRepository.findByAddresseeAndStatus(user, ConnectionStatus.PENDING);
    }

    public List<Connection> getSentRequests(String username) {
        User user = userService.getUserByUsername(username);
        return connectionRepository.findByRequesterAndStatus(user, ConnectionStatus.PENDING);
    }

    // ─── Suggestions ─────────────────────────────────────────────────

    public List<UserDtos.UserResponse> getSuggestedConnections(String currentUsername, int limit) {
        User currentUser = userService.getUserByUsername(currentUsername);

        List<Long> connectedIds = connectionRepository.findConnectedUserIds(currentUser.getId());

        List<Long> pendingIds = connectionRepository
                .findByRequesterAndStatus(currentUser, ConnectionStatus.PENDING)
                .stream().map(c -> c.getAddressee().getId()).collect(Collectors.toList());

        List<Long> allExcluded = new ArrayList<>();
        allExcluded.add(currentUser.getId());
        allExcluded.addAll(connectedIds);
        allExcluded.addAll(pendingIds);

        return userRepository.findAll().stream()
                .filter(u -> !allExcluded.contains(u.getId()))
                .limit(limit)
                .map(UserDtos.UserResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Follow ──────────────────────────────────────────────────────

    @Transactional
    public Follow followUser(Long targetUserId, String currentUsername) {
        User follower = userService.getUserByUsername(currentUsername);
        User following = userService.getUserById(targetUserId);

        if (follower.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself");
        }
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BadRequestException("You are already following this user");
        }

        Follow follow = Follow.builder().follower(follower).following(following).build();
        follow = followRepository.save(follow);
        notificationService.notifyNewFollower(follower, following);
        return follow;
    }

    @Transactional
    public void unfollowUser(Long targetUserId, String currentUsername) {
        User follower = userService.getUserByUsername(currentUsername);
        User following = userService.getUserById(targetUserId);
        if (!followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BadRequestException("You are not following this user");
        }
        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    public List<Follow> getFollowers(Long userId) {
        User user = userService.getUserById(userId);
        return followRepository.findByFollowing(user);
    }

    public List<Follow> getFollowing(Long userId) {
        User user = userService.getUserById(userId);
        return followRepository.findByFollower(user);
    }
}