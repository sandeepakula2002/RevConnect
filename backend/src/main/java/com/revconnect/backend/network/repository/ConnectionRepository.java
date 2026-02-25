package com.revconnect.backend.network.repository;

import com.revconnect.backend.network.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    
    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Connection> findByReceiverIdAndStatus(Long receiverId, Connection.Status status);
    
    // Improved method name to ensure correct boolean logic for status and IDs
    List<Connection> findByStatusAndSenderIdOrStatusAndReceiverId(
            Connection.Status status1, Long senderId, 
            Connection.Status status2, Long receiverId
    );
}