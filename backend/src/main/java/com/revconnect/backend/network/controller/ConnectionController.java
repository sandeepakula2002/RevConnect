package com.revconnect.backend.network.controller;

import com.revconnect.backend.network.model.Connection;
import com.revconnect.backend.network.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", 
             allowedHeaders = "*", 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS},
             allowCredentials = "true",
             maxAge = 3600)
@RestController
@RequestMapping("/api/network")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService service;

    @PostMapping("/request")
    public ResponseEntity<?> sendRequest(
            @RequestParam("senderId") Long senderId, 
            @RequestParam("receiverId") Long receiverId
    ) {
        try {
            System.out.println("📨 POST Request received: senderId=" + senderId + ", receiverId=" + receiverId);
            Connection connection = service.sendRequest(senderId, receiverId);
            System.out.println("✅ Request processed successfully. Connection ID: " + connection.getId());
            return ResponseEntity.ok(connection);
        } catch (RuntimeException e) {
            System.err.println("❌ Business error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Explicit OPTIONS handler for preflight requests
    @RequestMapping(value = "/request", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        System.out.println("🔧 OPTIONS preflight request received");
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "http://localhost:4200")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<?> acceptRequest(@PathVariable("id") Long id) {
        try {
            System.out.println("📨 PUT Accept request: id=" + id);
            Connection connection = service.acceptRequest(id);
            return ResponseEntity.ok(connection);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectRequest(@PathVariable("id") Long id) {
        try {
            System.out.println("📨 PUT Reject request: id=" + id);
            Connection connection = service.rejectRequest(id);
            return ResponseEntity.ok(connection);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<?> getPending(@PathVariable("userId") Long userId) {
        try {
            System.out.println("📨 GET Pending requests for user: " + userId);
            List<Connection> connections = service.getPendingRequests(userId);
            return ResponseEntity.ok(connections);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/connections/{userId}")
    public ResponseEntity<?> getConnections(@PathVariable("userId") Long userId) {
        try {
            System.out.println("📨 GET Connections for user: " + userId);
            List<Connection> connections = service.getConnections(userId);
            return ResponseEntity.ok(connections);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}