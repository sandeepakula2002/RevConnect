package com.revconnect.user;

import com.revconnect.common.exception.ResourceNotFoundException;
import com.revconnect.common.exception.UnauthorizedException;
import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.network.repository.FollowRepository;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.user.dto.UserDtos;
import com.revconnect.user.model.User;
import com.revconnect.user.repository.UserRepository;
import com.revconnect.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock private UserRepository userRepository;
    @Mock private FollowRepository followRepository;
    @Mock private ConnectionRepository connectionRepository;
    @Mock private PostRepository postRepository;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).username("demoUser").build();
    }

    @Test
    void testGetUserProfile() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("demoUser")).thenReturn(Optional.of(user));

        when(followRepository.countByFollowing(user)).thenReturn(5L);
        when(followRepository.countByFollower(user)).thenReturn(3L);
        when(postRepository.countByUser(user)).thenReturn(10L);
        when(connectionRepository.findConnectedUserIds(1L))
                .thenReturn(List.of(2L, 3L));

        when(followRepository.existsByFollowerAndFollowing(any(), any()))
                .thenReturn(false);

        UserDtos.UserResponse response =
                userService.getUserProfile(1L, "demoUser");

        assertEquals(5L, response.getFollowerCount());
        assertEquals(3L, response.getFollowingCount());
        assertEquals(10L, response.getPostCount());
        assertEquals(2, response.getConnectionCount());
    }

    @Test
    void testUpdateProfile_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("demoUser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        when(followRepository.countByFollowing(user)).thenReturn(0L);
        when(followRepository.countByFollower(user)).thenReturn(0L);
        when(postRepository.countByUser(user)).thenReturn(0L);
        when(connectionRepository.findConnectedUserIds(1L)).thenReturn(List.of());
        when(followRepository.existsByFollowerAndFollowing(any(), any()))
                .thenReturn(false);

        UserDtos.ProfileUpdateRequest request =
                new UserDtos.ProfileUpdateRequest();
        request.setFirstName("John");

        UserDtos.UserResponse response =
                userService.updateProfile(1L, request, "demoUser");

        assertNotNull(response);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateProfile_Unauthorized() {

        User other = User.builder().id(2L).username("other").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("other"))
                .thenReturn(Optional.of(other));

        UserDtos.ProfileUpdateRequest request =
                new UserDtos.ProfileUpdateRequest();

        assertThrows(UnauthorizedException.class,
                () -> userService.updateProfile(1L, request, "other"));
    }

    @Test
    void testSearchUsers() {

        when(userRepository.searchUsers("john"))
                .thenReturn(List.of(user));

        when(followRepository.countByFollowing(user)).thenReturn(5L);

        var result = userService.searchUsers("john", "demoUser");

        assertEquals(1, result.size());
    }

    @Test
    void testFindUsersExcludingIds() {

        User user2 = User.builder().id(2L).build();
        User user3 = User.builder().id(3L).build();

        when(userRepository.findAll())
                .thenReturn(List.of(user, user2, user3));

        var result = userService.findUsersExcludingIds(List.of(1L), 10);

        assertEquals(2, result.size());
    }

    @Test
    void testGetUserByUsername_NotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByUsername("unknown"));
    }
}