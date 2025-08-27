package com.wordle.game.network;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

/**
 * Network Manager - Quản lý tất cả Network Protocols
 * Áp dụng kiến thức: Multi-Protocol Architecture, Service Management
 */
@Service
public class NetworkManager {
    
    private static final Logger logger = LoggerFactory.getLogger(NetworkManager.class);
    
    @Autowired
    private TcpGameServer tcpGameServer;
    
    @Autowired
    private UdpStatsServer udpStatsServer;
    
    private ScheduledExecutorService scheduler;
    private volatile boolean isRunning = false;
    
    /**
     * Khởi động tất cả network servers khi application ready
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startAllServers() {
        logger.info("Starting Network Manager - Multi-Protocol Game Server");
        
        try {
            // Start TCP Server cho game communication
            tcpGameServer.startServer();
            logger.info("✓ TCP Game Server started successfully");
            
            // Start UDP Server cho statistics
            udpStatsServer.startServer();
            logger.info("✓ UDP Stats Server started successfully");
            
            // Start monitoring scheduler
            startMonitoring();
            
            isRunning = true;
            logger.info("🚀 All Network Servers are running successfully!");
            
            // Log server information
            logServerInfo();
            
        } catch (Exception e) {
            logger.error("Failed to start network servers", e);
        }
    }
    
    /**
     * Khởi động monitoring scheduler
     */
    private void startMonitoring() {
        scheduler = Executors.newScheduledThreadPool(2);
        
        // Monitor connection health every 30 seconds
        scheduler.scheduleAtFixedRate(this::monitorConnections, 30, 30, TimeUnit.SECONDS);
        
        // Log statistics every 5 minutes
        scheduler.scheduleAtFixedRate(this::logStatistics, 300, 300, TimeUnit.SECONDS);
        
        logger.info("Network monitoring started");
    }
    
    /**
     * Monitor connection health
     */
    private void monitorConnections() {
        try {
            // Get TCP server stats
            Map<String, Object> tcpStats = tcpGameServer.getServerStats();
            int tcpConnections = (int) tcpStats.get("connectedPlayers");
            int activeRooms = (int) tcpStats.get("activeRooms");
            
            // Get UDP server stats
            Map<String, Object> udpStats = udpStatsServer.getServerStats();
            int totalPlayers = (int) udpStats.get("totalPlayers");
            int totalGames = (int) udpStats.get("totalGames");
            
            logger.info("Network Health - TCP: {} connections, {} rooms | UDP: {} players, {} games", 
                       tcpConnections, activeRooms, totalPlayers, totalGames);
            
        } catch (Exception e) {
            logger.error("Error monitoring connections", e);
        }
    }
    
    /**
     * Log detailed statistics
     */
    private void logStatistics() {
        try {
            logger.info("=== NETWORK STATISTICS ===");
            
            // TCP Statistics
            Map<String, Object> tcpStats = tcpGameServer.getServerStats();
            logger.info("TCP Server - Port: {}, Connected: {}, Rooms: {}, Status: {}", 
                       tcpStats.get("serverPort"), 
                       tcpStats.get("connectedPlayers"),
                       tcpStats.get("activeRooms"),
                       tcpStats.get("isRunning"));
            
            // UDP Statistics
            Map<String, Object> udpStats = udpStatsServer.getServerStats();
            logger.info("UDP Server - Port: {}, Players: {}, Games: {}, Status: {}", 
                       udpStats.get("serverPort"),
                       udpStats.get("totalPlayers"),
                       udpStats.get("totalGames"),
                       udpStats.get("isRunning"));
            
            logger.info("=========================");
            
        } catch (Exception e) {
            logger.error("Error logging statistics", e);
        }
    }
    
    /**
     * Log server information at startup
     */
    private void logServerInfo() {
        logger.info("=== WORDLE NETWORK ARCHITECTURE ===");
        logger.info("📡 WebSocket Server (HTTP): Port 8080 - Web client communication");
        logger.info("🔗 TCP Server (NIO): Port 8081 - Real-time game communication"); 
        logger.info("📊 UDP Server: Port 8082 - Fast statistics & leaderboard");
        logger.info("====================================");
        
        logger.info("Network Programming Concepts Applied:");
        logger.info("• TCP/IP Socket Programming with NIO");
        logger.info("• UDP Connectionless Communication");
        logger.info("• Multi-threading & Concurrent Programming");
        logger.info("• Protocol Design & Message Serialization");
        logger.info("• Client-Server Architecture");
        logger.info("• Network Performance Monitoring");
    }
    
    /**
     * Get comprehensive network status
     */
    public Map<String, Object> getNetworkStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            status.put("isRunning", isRunning);
            status.put("tcpServer", tcpGameServer.getServerStats());
            status.put("udpServer", udpStatsServer.getServerStats());
            status.put("startTime", System.currentTimeMillis());
            
            // Network protocols info
            Map<String, Object> protocols = new HashMap<>();
            protocols.put("websocket", Map.of("port", 8080, "protocol", "WebSocket/STOMP", "purpose", "Web Client Communication"));
            protocols.put("tcp", Map.of("port", 8081, "protocol", "TCP/IP with NIO", "purpose", "Real-time Game Communication"));
            protocols.put("udp", Map.of("port", 8082, "protocol", "UDP", "purpose", "Fast Statistics & Leaderboard"));
            
            status.put("protocols", protocols);
            
        } catch (Exception e) {
            logger.error("Error getting network status", e);
            status.put("error", e.getMessage());
        }
        
        return status;
    }
    
    /**
     * Restart network services
     */
    public void restartNetworkServices() {
        logger.info("Restarting network services...");
        
        try {
            // Stop all servers
            stopAllServers();
            
            // Wait a moment
            Thread.sleep(2000);
            
            // Restart all servers
            startAllServers();
            
            logger.info("Network services restarted successfully");
            
        } catch (Exception e) {
            logger.error("Error restarting network services", e);
        }
    }
    
    /**
     * Stop all servers gracefully
     */
    @PreDestroy
    public void stopAllServers() {
        logger.info("Stopping all network servers...");
        
        isRunning = false;
        
        try {
            // Stop monitoring
            if (scheduler != null) {
                scheduler.shutdown();
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
            }
            
            // Stop TCP server
            tcpGameServer.stopServer();
            logger.info("✓ TCP Game Server stopped");
            
            // Stop UDP server
            udpStatsServer.stopServer();
            logger.info("✓ UDP Stats Server stopped");
            
            logger.info("🛑 All network servers stopped successfully");
            
        } catch (Exception e) {
            logger.error("Error stopping network servers", e);
        }
    }
}
