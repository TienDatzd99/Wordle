package com.wordle.game.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * UDP Server cho Fast Game Statistics & Leaderboard
 * Áp dụng kiến thức: UDP Protocol, Connectionless Communication
 */
@Service
public class UdpStatsServer {
    
    private static final Logger logger = LoggerFactory.getLogger(UdpStatsServer.class);
    private static final int UDP_PORT = 8082;
    private static final int BUFFER_SIZE = 512;
    
    private DatagramChannel udpChannel;
    private ExecutorService threadPool;
    private ObjectMapper objectMapper;
    private volatile boolean isRunning = false;
    
    // Statistics storage
    private final Map<String, PlayerStats> playerStats = new ConcurrentHashMap<>();
    private final Map<String, GameStatistics> gameStats = new ConcurrentHashMap<>();
    
    public UdpStatsServer() {
        this.threadPool = Executors.newFixedThreadPool(5);
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Start UDP Server for statistics
     */
    public void startServer() {
        try {
            // Create UDP channel
            udpChannel = DatagramChannel.open();
            udpChannel.configureBlocking(false);
            udpChannel.bind(new InetSocketAddress(UDP_PORT));
            
            isRunning = true;
            logger.info("UDP Stats Server started on port {}", UDP_PORT);
            
            // Start server loop
            threadPool.submit(this::serverLoop);
            
        } catch (IOException e) {
            logger.error("Failed to start UDP server", e);
        }
    }
    
    /**
     * Main UDP server loop
     */
    private void serverLoop() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        
        while (isRunning) {
            try {
                buffer.clear();
                SocketAddress clientAddress = udpChannel.receive(buffer);
                
                if (clientAddress != null) {
                    buffer.flip();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    
                    String message = new String(data, "UTF-8");
                    
                    // Process in separate thread
                    threadPool.submit(() -> processStatsMessage(clientAddress, message));
                }
                
                Thread.sleep(10); // Prevent busy waiting
                
            } catch (IOException | InterruptedException e) {
                if (isRunning) {
                    logger.error("Error in UDP server loop", e);
                }
            }
        }
    }
    
    /**
     * Process statistics messages
     */
    private void processStatsMessage(SocketAddress clientAddress, String message) {
        try {
            StatsMessage statsMessage = objectMapper.readValue(message, StatsMessage.class);
            
            switch (statsMessage.getType()) {
                case "GAME_COMPLETED":
                    handleGameCompleted(clientAddress, statsMessage);
                    break;
                case "PLAYER_MOVE":
                    handlePlayerMove(clientAddress, statsMessage);
                    break;
                case "GET_LEADERBOARD":
                    handleGetLeaderboard(clientAddress, statsMessage);
                    break;
                case "GET_PLAYER_STATS":
                    handleGetPlayerStats(clientAddress, statsMessage);
                    break;
                case "PING":
                    handlePing(clientAddress);
                    break;
                default:
                    logger.warn("Unknown stats message type: {}", statsMessage.getType());
            }
            
        } catch (Exception e) {
            logger.error("Error processing stats message", e);
        }
    }
    
    /**
     * Handle game completion statistics
     */
    private void handleGameCompleted(SocketAddress clientAddress, StatsMessage message) {
        String playerId = message.getPlayerId();
        Map<String, Object> gameData = message.getData();
        
        // Update player stats
        PlayerStats stats = playerStats.computeIfAbsent(playerId, k -> new PlayerStats(playerId));
        
        boolean won = (boolean) gameData.getOrDefault("won", false);
        int attempts = (int) gameData.getOrDefault("attempts", 6);
        long duration = (long) gameData.getOrDefault("duration", 0);
        
        stats.addGame(won, attempts, duration);
        
        // Update global game statistics
        String roomId = message.getRoomId();
        GameStatistics roomStats = gameStats.computeIfAbsent(roomId, k -> new GameStatistics(roomId));
        roomStats.addGameResult(won, attempts, duration);
        
        logger.info("Game completed stats updated for player: {}", playerId);
        
        // Send acknowledgment
        sendResponse(clientAddress, createAckMessage("GAME_STATS_UPDATED"));
    }
    
    /**
     * Handle player move statistics
     */
    private void handlePlayerMove(SocketAddress clientAddress, StatsMessage message) {
        String playerId = message.getPlayerId();
        Map<String, Object> moveData = message.getData();
        
        PlayerStats stats = playerStats.computeIfAbsent(playerId, k -> new PlayerStats(playerId));
        
        String word = (String) moveData.get("word");
        String[] colors = (String[]) moveData.get("colors");
        
        stats.addMove(word, colors);
        
        // Send response with move analysis
        Map<String, Object> analysis = analyzeMoveEfficiency(word, colors);
        sendResponse(clientAddress, createDataMessage("MOVE_ANALYSIS", analysis));
    }
    
    /**
     * Handle leaderboard request
     */
    private void handleGetLeaderboard(SocketAddress clientAddress, StatsMessage message) {
        List<Map<String, Object>> leaderboard = generateLeaderboard();
        sendResponse(clientAddress, createDataMessage("LEADERBOARD", leaderboard));
    }
    
    /**
     * Handle player stats request
     */
    private void handleGetPlayerStats(SocketAddress clientAddress, StatsMessage message) {
        String playerId = message.getPlayerId();
        PlayerStats stats = playerStats.get(playerId);
        
        if (stats != null) {
            Map<String, Object> statsData = stats.toMap();
            sendResponse(clientAddress, createDataMessage("PLAYER_STATS", statsData));
        } else {
            sendResponse(clientAddress, createErrorMessage("PLAYER_NOT_FOUND"));
        }
    }
    
    /**
     * Handle ping request
     */
    private void handlePing(SocketAddress clientAddress) {
        sendResponse(clientAddress, createAckMessage("PONG"));
    }
    
    /**
     * Generate leaderboard
     */
    private List<Map<String, Object>> generateLeaderboard() {
        return playerStats.values().stream()
                .sorted((a, b) -> Double.compare(b.getWinRate(), a.getWinRate()))
                .limit(10)
                .map(stats -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("playerId", stats.getPlayerId());
                    entry.put("winRate", stats.getWinRate());
                    entry.put("averageAttempts", stats.getAverageAttempts());
                    entry.put("totalGames", stats.getTotalGames());
                    entry.put("bestTime", stats.getBestTime());
                    return entry;
                })
                .toList();
    }
    
    /**
     * Analyze move efficiency
     */
    private Map<String, Object> analyzeMoveEfficiency(String word, String[] colors) {
        Map<String, Object> analysis = new HashMap<>();
        
        int greenCount = 0;
        int yellowCount = 0;
        int grayCount = 0;
        
        for (String color : colors) {
            switch (color) {
                case "green": greenCount++; break;
                case "yellow": yellowCount++; break;
                case "gray": grayCount++; break;
            }
        }
        
        // Calculate efficiency score
        double efficiency = (greenCount * 3 + yellowCount * 1) / 15.0; // Max possible is 15 (5 greens)
        
        analysis.put("efficiency", efficiency);
        analysis.put("greenCount", greenCount);
        analysis.put("yellowCount", yellowCount);
        analysis.put("grayCount", grayCount);
        analysis.put("suggestion", generateMoveSuggestion(efficiency));
        
        return analysis;
    }
    
    /**
     * Generate move suggestion
     */
    private String generateMoveSuggestion(double efficiency) {
        if (efficiency >= 0.8) {
            return "Excellent move! You're very close to the answer.";
        } else if (efficiency >= 0.5) {
            return "Good progress! Try to use the yellow letters in different positions.";
        } else if (efficiency >= 0.2) {
            return "Keep trying! Consider using different vowels.";
        } else {
            return "Try a word with common letters like R, S, T, L, N.";
        }
    }
    
    /**
     * Send response to client
     */
    private void sendResponse(SocketAddress clientAddress, StatsMessage response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            ByteBuffer buffer = ByteBuffer.wrap(json.getBytes("UTF-8"));
            udpChannel.send(buffer, clientAddress);
            
        } catch (Exception e) {
            logger.error("Error sending UDP response", e);
        }
    }
    
    /**
     * Create acknowledgment message
     */
    private StatsMessage createAckMessage(String content) {
        StatsMessage message = new StatsMessage();
        message.setType("ACK");
        message.setData(Map.of("message", content, "timestamp", System.currentTimeMillis()));
        return message;
    }
    
    /**
     * Create data response message
     */
    private StatsMessage createDataMessage(String type, Object data) {
        StatsMessage message = new StatsMessage();
        message.setType(type);
        message.setData(Map.of("data", data, "timestamp", System.currentTimeMillis()));
        return message;
    }
    
    /**
     * Create error message
     */
    private StatsMessage createErrorMessage(String error) {
        StatsMessage message = new StatsMessage();
        message.setType("ERROR");
        message.setData(Map.of("error", error, "timestamp", System.currentTimeMillis()));
        return message;
    }
    
    /**
     * Stop UDP server
     */
    public void stopServer() {
        isRunning = false;
        
        try {
            if (udpChannel != null) {
                udpChannel.close();
            }
            
            threadPool.shutdown();
            
            logger.info("UDP Stats Server stopped");
            
        } catch (IOException e) {
            logger.error("Error stopping UDP server", e);
        }
    }
    
    /**
     * Get server statistics
     */
    public Map<String, Object> getServerStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlayers", playerStats.size());
        stats.put("totalGames", gameStats.values().stream().mapToInt(GameStatistics::getTotalGames).sum());
        stats.put("serverPort", UDP_PORT);
        stats.put("isRunning", isRunning);
        
        return stats;
    }
}
