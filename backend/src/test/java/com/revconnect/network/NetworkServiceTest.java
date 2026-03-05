package com.revconnect.network;

import com.revconnect.network.model.Connection;
import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.network.repository.FollowRepository;
import com.revconnect.network.service.NetworkService;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.user.model.User;
import com.revconnect.user.repository.UserRepository;
import com.revconnect.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NetworkServiceTest {

    @InjectMocks
    private NetworkService networkService;

    @Mock private ConnectionRepository connectionRepository;
    @Mock private FollowRepository followRepository;
    @Mock private UserService userService;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = User.builder().id(1L).username("user1").build();
        user2 = User.builder().id(2L).username("user2").build();
    }

    @Test
    void testSendConnectionRequest() {

        when(userService.getUserByUsername("user1")).thenReturn(user1);
        when(userService.getUserById(2L)).thenReturn(user2);
        when(connectionRepository.findConnectionBetween(1L, 2L))
                .thenReturn(Optional.empty());

        networkService.sendConnectionRequest(2L, "user1");

        verify(connectionRepository).save(any(Connection.class));
        verify(notificationService).notifyConnectionRequest(user1, user2);
    }
}