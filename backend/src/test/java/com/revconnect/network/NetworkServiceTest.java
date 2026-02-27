package com.revconnect.network;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.revconnect.network.model.Connection;
import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.network.repository.FollowRepository;
import com.revconnect.network.service.NetworkService;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NetworkServiceTest {

    @InjectMocks
    NetworkService networkService;

    @Mock
    ConnectionRepository connectionRepository;

    @Mock
    FollowRepository followRepository;

    @Mock
    UserService userService;

    @Mock
    NotificationService notificationService;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("vasanth");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("john");
    }

    @Test
    void testSendConnectionRequest() {

        when(userService.getUserByUsername("vasanth"))
                .thenReturn(user1);

        when(userService.getUserById(2L))
                .thenReturn(user2);

        when(connectionRepository.findConnectionBetween(1L,2L))
                .thenReturn(Optional.empty());

        Connection connection =
                Connection.builder()
                .requester(user1)
                .addressee(user2)
                .build();

        when(connectionRepository.save(connection))
                .thenReturn(connection);

        Connection result =
                networkService.sendConnectionRequest(2L,"vasanth");

        assertNotNull(result);
    }
}