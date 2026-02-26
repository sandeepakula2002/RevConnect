package com.revconnect.network.repository;

import com.revconnect.network.model.Connection;
import com.revconnect.network.model.ConnectionStatus;
import com.revconnect.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // Check if connection exists between two users (either direction)
    @Query("SELECT c FROM Connection c WHERE " +
           "(c.requester.id = :userId1 AND c.addressee.id = :userId2) OR " +
           "(c.requester.id = :userId2 AND c.addressee.id = :userId1)")
    Optional<Connection> findConnectionBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Accepted connections for a user (either direction)
    @Query("SELECT c FROM Connection c WHERE " +
           "(c.requester.id = :userId OR c.addressee.id = :userId) AND c.status = 'ACCEPTED'")
    List<Connection> findAcceptedConnections(@Param("userId") Long userId);

    // Pending requests received
    List<Connection> findByAddresseeAndStatus(User addressee, ConnectionStatus status);

    // Pending requests sent
    List<Connection> findByRequesterAndStatus(User requester, ConnectionStatus status);

    // Get all connection IDs for feed
    @Query("SELECT CASE WHEN c.requester.id = :userId THEN c.addressee.id ELSE c.requester.id END " +
           "FROM Connection c WHERE (c.requester.id = :userId OR c.addressee.id = :userId) AND c.status = 'ACCEPTED'")
    List<Long> findConnectedUserIds(@Param("userId") Long userId);

    long countByRequesterAndStatus(User requester, ConnectionStatus status);
}
