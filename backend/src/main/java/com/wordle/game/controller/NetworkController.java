package com.wordle.game.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordle.game.network.NetworkManager;

/**
 * Network Controller cho monitoring network services
 * Áp dụng kiến thức: RESTful API, Network Management
 */
@RestController
@RequestMapping("/api/network")
@CrossOrigin(origins = "*")
public class NetworkController {
    
    @Autowired
    private NetworkManager networkManager;
    
    /**
     * Get comprehensive network status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getNetworkStatus() {
        try {
            Map<String, Object> status = networkManager.getNetworkStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get network status", "message", e.getMessage()));
        }
    }
    
    /**
     * Restart network services
     */
    @PostMapping("/restart")
    public ResponseEntity<Map<String, Object>> restartNetworkServices() {
        try {
            networkManager.restartNetworkServices();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Network services restarted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to restart network services", "message", e.getMessage()));
        }
    }
    
    /**
     * Get network architecture information
     */
    @GetMapping("/architecture")
    public ResponseEntity<Map<String, Object>> getNetworkArchitecture() {
        Map<String, Object> architecture = Map.of(
            "title", "Wordle Multi-Protocol Network Architecture",
            "protocols", Map.of(
                "websocket", Map.of(
                    "port", 8080,
                    "protocol", "WebSocket/STOMP over HTTP",
                    "purpose", "Web client real-time communication",
                    "features", new String[]{"Real-time updates", "Bidirectional communication", "Session management"},
                    "concepts", new String[]{"WebSocket Protocol", "STOMP Messaging", "HTTP Upgrade"}
                ),
                "tcp", Map.of(
                    "port", 8081,
                    "protocol", "TCP/IP with NIO (Non-blocking I/O)",
                    "purpose", "Direct socket game communication",
                    "features", new String[]{"Reliable delivery", "Connection-oriented", "Multi-threading", "NIO Selector"},
                    "concepts", new String[]{"Socket Programming", "Multi-threading", "NIO", "Selector Pattern"}
                ),
                "udp", Map.of(
                    "port", 8082,
                    "protocol", "UDP (User Datagram Protocol)",
                    "purpose", "Fast statistics and leaderboard updates",
                    "features", new String[]{"Connectionless", "Low latency", "Fire-and-forget", "Statistical data"},
                    "concepts", new String[]{"Connectionless Protocol", "Datagram", "Best-effort delivery"}
                )
            ),
            "networkConcepts", new String[]{
                "Client-Server Architecture",
                "Multi-Protocol Communication",
                "Concurrent Programming",
                "Socket Programming",
                "Message Serialization (JSON)",
                "Connection Management",
                "Load Balancing",
                "Network Monitoring"
            },
            "technologies", Map.of(
                "backend", new String[]{"Spring Boot", "Java NIO", "WebSocket", "TCP Sockets", "UDP Sockets"},
                "communication", new String[]{"JSON Serialization", "STOMP Protocol", "Custom Message Protocol"},
                "concurrency", new String[]{"ExecutorService", "Thread Pools", "Concurrent Collections", "NIO Selector"}
            )
        );
        
        return ResponseEntity.ok(architecture);
    }
}
