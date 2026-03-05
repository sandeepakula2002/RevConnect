package com.revconnect.network.repository;

import com.revconnect.network.model.Connection;
import com.revconnect.network.model.ConnectionStatus;
import com.revconnect.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // Check if connection exists between two users (both directions)
    @Query("""
        SELECT c
        FROM Connection c
        JOIN FETCH c.requester
        JOIN FETCH c.addressee
        WHERE
        (c.requester.id = :userId1 AND c.addressee.id = :userId2)
        OR
        (c.requester.id = :userId2 AND c.addressee.id = :userId1)
    """)
    Optional<Connection> findConnectionBetween(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );



    // Get accepted connections
    @Query("""
        SELECT c
        FROM Connection c
        JOIN FETCH c.requester
        JOIN FETCH c.addressee
        WHERE
        (c.requester.id = :userId OR c.addressee.id = :userId)
        AND c.status = 'ACCEPTED'
    """)
    List<Connection> findAcceptedConnections(@Param("userId") Long userId);



    // Pending requests received
    @Query("""
        SELECT c
        FROM Connection c
        JOIN FETCH c.requester
        JOIN FETCH c.addressee
        WHERE c.addressee = :addressee
        AND c.status = :status
    """)
    List<Connection> findReceivedRequests(
            @Param("addressee") User addressee,
            @Param("status") ConnectionStatus status
    );



    // Pending requests sent
    @Query("""
        SELECT c
        FROM Connection c
        JOIN FETCH c.requester
        JOIN FETCH c.addressee
        WHERE c.requester = :requester
        AND c.status = :status
    """)
    List<Connection> findSentRequests(
            @Param("requester") User requester,
            @Param("status") ConnectionStatus status
    );



    // Get all connected user IDs (for feed / suggestions)
    @Query("""
        SELECT
        CASE
            WHEN c.requester.id = :userId
            THEN c.addressee.id
            ELSE c.requester.id
        END
        FROM Connection c
        WHERE
        (c.requester.id = :userId OR c.addressee.id = :userId)
        AND c.status = 'ACCEPTED'
    """)
    List<Long> findConnectedUserIds(@Param("userId") Long userId);



    // Count pending requests
    long countByRequesterAndStatus(User requester, ConnectionStatus status);
}