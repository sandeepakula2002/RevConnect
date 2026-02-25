package com.revconnect.backend.network.service;

import com.revconnect.backend.network.model.Connection;
import com.revconnect.backend.network.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; // Required for the timestamp
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository repository;

   public Connection sendRequest(Long senderId, Long receiverId) {
    // Check if the IDs actually exist to prevent a crash
    if (repository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
        throw new RuntimeException("Request already exists");
    }

    Connection connection = Connection.builder()
            .senderId(senderId)
            .receiverId(receiverId)
            .status(Connection.Status.PENDING)
            .createdAt(LocalDateTime.now()) // Fixes the NULL column
            .build();

    return repository.save(connection);
}

    public Connection acceptRequest(Long requestId) {
        Connection connection = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        connection.setStatus(Connection.Status.ACCEPTED);
        return repository.save(connection);
    }

    public Connection rejectRequest(Long requestId) {
        Connection connection = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        connection.setStatus(Connection.Status.REJECTED);
        return repository.save(connection);
    }

    public List<Connection> getPendingRequests(Long userId) {
        return repository.findByReceiverIdAndStatus(userId, Connection.Status.PENDING);
    }

    public List<Connection> getConnections(Long userId) {
        // Updated call to match the improved repository logic
        return repository.findByStatusAndSenderIdOrStatusAndReceiverId(
                Connection.Status.ACCEPTED, userId,
                Connection.Status.ACCEPTED, userId
        );
    }
}