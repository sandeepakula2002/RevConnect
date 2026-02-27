package com.revconnect.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.network.repository.FollowRepository;
import com.revconnect.post.repository.PostRepository;
import com.revconnect.user.model.User;
import com.revconnect.user.repository.UserRepository;
import com.revconnect.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    FollowRepository followRepository;

    @Mock
    ConnectionRepository connectionRepository;

    @Mock
    PostRepository postRepository;

    private User user;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("vasanth");
    }

    @Test
    void testGetUserByUsername() {

        when(userRepository.findByUsername("vasanth"))
                .thenReturn(Optional.of(user));

        User result =
                userService.getUserByUsername("vasanth");

        assertNotNull(result);
    }

}