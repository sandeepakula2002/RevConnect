package com.revconnect.network;

import com.revconnect.network.dto.ConnectionResponse;
import com.revconnect.network.model.Connection;
import com.revconnect.network.model.ConnectionStatus;
import com.revconnect.network.repository.ConnectionRepository;
import com.revconnect.notification.service.NotificationService;
import com.revconnect.user.model.User;
import com.revconnect.user.service.UserService;
import com.revconnect.network.service.NetworkService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkServiceTest {

    @InjectMocks
    private NetworkService networkService;

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setup() {

        sender = User.builder()
                .id(1L)
                .username("sender")
                .build();

        receiver = User.builder()
                .id(2L)
                .username("receiver")
                .build();
    }

    @Test
    void testSendConnectionRequest() {

        when(userService.getUserByUsername("sender"))
                .thenReturn(sender);

        when(userService.getUserById(2L))
                .thenReturn(receiver);

        when(connectionRepository.findConnectionBetween(1L, 2L))
                .thenReturn(Optional.empty());

        Connection connection = Connection.builder()
                .id(1L)
                .requester(sender)
                .addressee(receiver)
                .status(ConnectionStatus.PENDING)
                .build();

        when(connectionRepository.save(any(Connection.class)))
                .thenReturn(connection);

        ConnectionResponse result =
                networkService.sendConnectionRequest(2L, "sender");

        assertNotNull(result);
    }


}
