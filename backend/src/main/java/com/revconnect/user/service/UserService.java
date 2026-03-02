package com.revconnect.user.service;

import com.revconnect.common.exception.ResourceNotFoundException;
import com.revconnect.common.exception.UnauthorizedException;
import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.network.repository.FollowRepository;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.user.dto.UserDtos;
import com.revconnect.user.model.User;
import com.revconnect.user.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired private UserRepository userRepository;
    @Autowired private FollowRepository followRepository;
    @Autowired private ConnectionRepository connectionRepository;
    @Autowired private PostRepository postRepository;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public UserDtos.UserResponse getUserProfile(Long targetUserId, String currentUsername) {
        User target = getUserById(targetUserId);
        User currentUser = getUserByUsername(currentUsername);

        UserDtos.UserResponse response = UserDtos.UserResponse.from(target);

        // Populate social counts
        response.setFollowerCount(followRepository.countByFollowing(target));
        response.setFollowingCount(followRepository.countByFollower(target));
        response.setPostCount(postRepository.countByUser(target));

        List<Long> connectedIds = connectionRepository.findConnectedUserIds(target.getId());
        response.setConnectionCount(connectedIds.size());

        // Is the current user following / connected?
        response.setFollowing(followRepository.existsByFollowerAndFollowing(currentUser, target));
        response.setConnected(connectedIds.contains(currentUser.getId()));

        return response;
    }

    @Transactional
    public UserDtos.UserResponse updateProfile(Long userId, UserDtos.ProfileUpdateRequest request,
                                               String currentUsername) {
        User user = getUserById(userId);
        User currentUser = getUserByUsername(currentUsername);

        if (!user.getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only edit your own profile");
        }

        if (request.getFirstName() != null)     user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)      user.setLastName(request.getLastName());
        if (request.getBio() != null)           user.setBio(request.getBio());
        if (request.getLocation() != null)      user.setLocation(request.getLocation());
        if (request.getWebsite() != null)       user.setWebsite(request.getWebsite());
        if (request.getPrivacy() != null)       user.setPrivacy(request.getPrivacy());
        if (request.getBusinessName() != null)  user.setBusinessName(request.getBusinessName());
        if (request.getCategory() != null)      user.setCategory(request.getCategory());
        if (request.getContactEmail() != null)  user.setContactEmail(request.getContactEmail());
        if (request.getContactPhone() != null)  user.setContactPhone(request.getContactPhone());
        if (request.getBusinessAddress() != null) user.setBusinessAddress(request.getBusinessAddress());
        if (request.getBusinessHours() != null) user.setBusinessHours(request.getBusinessHours());
        if (request.getExternalLinks() != null) user.setExternalLinks(request.getExternalLinks());

        user = userRepository.save(user);
        logger.info("Profile updated for user: {}", user.getUsername());
        return getUserProfile(user.getId(), currentUsername);
    }

    public List<UserDtos.UserResponse> searchUsers(String query, String currentUsername) {
        return userRepository.searchUsers(query).stream()
                .map(u -> {
                    UserDtos.UserResponse r = UserDtos.UserResponse.from(u);
                    r.setFollowerCount(followRepository.countByFollowing(u));
                    return r;
                })
                .collect(Collectors.toList());
    }

    // ========== NEW METHOD FOR NETWORK SUGGESTIONS ==========
    
    /**
     * Find users excluding a list of IDs (for connection suggestions)
     * @param excludedIds List of user IDs to exclude
     * @param limit Maximum number of users to return
     * @return List of users not in the excluded IDs
     */
    public List<User> findUsersExcludingIds(List<Long> excludedIds, int limit) {
        logger.debug("Finding users excluding {} IDs with limit: {}", 
                     excludedIds != null ? excludedIds.size() : 0, limit);
        
        if (excludedIds == null || excludedIds.isEmpty()) {
            // If no IDs to exclude, return random users
            return userRepository.findRandomUsers(limit);
        }
        
        // Get all users and filter in memory (simpler approach that will work)
        // This is less efficient but will work immediately
        List<User> allUsers = userRepository.findAll();
        
        return allUsers.stream()
                .filter(user -> !excludedIds.contains(user.getId()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}