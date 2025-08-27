package com.wordle.game.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TCP Socket Server cho Real-time Game Communication
 * Áp dụng kiến thức: TCP/IP, Multi-threading, NIO, Socket Programming
 */
@Service
public class TcpGameServer {
    
    private static final Logger logger = LoggerFactory.getLogger(TcpGameServer.class);
    private static final int TCP_PORT = 8081;
    private static final int BUFFER_SIZE = 1024;
    
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ExecutorService threadPool;
    private ObjectMapper objectMapper;
    private volatile boolean isRunning = false;
    
    // Game rooms management
    private final Map<String, GameRoom> gameRooms = new ConcurrentHashMap<>();
    private final Map<SocketChannel, PlayerConnection> playerConnections = new ConcurrentHashMap<>();
    
    public TcpGameServer() {
        this.threadPool = Executors.newFixedThreadPool(10);
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Start TCP Server với NIO (Non-blocking I/O)
     */
    public void startServer() {
        try {
            // Tạo selector cho NIO
            selector = Selector.open();
            
            // Tạo server socket channel
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(TCP_PORT));
            
            // Register accept operation
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            isRunning = true;
            logger.info("TCP Game Server started on port {}", TCP_PORT);
            
            // Main server loop
            threadPool.submit(this::serverLoop);
            
        } catch (IOException e) {
            logger.error("Failed to start TCP server", e);
        }
    }
    
    /**
     * Main server loop sử dụng NIO Selector
     */
    private void serverLoop() {
        while (isRunning) {
            try {
                // Wait for events
                int readyChannels = selector.select(1000);
                
                if (readyChannels == 0) continue;
                
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    
                    if (!key.isValid()) continue;
                    
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    } else if (key.isWritable()) {
                        handleWrite(key);
                    }
                }
                
            } catch (IOException e) {
                logger.error("Error in server loop", e);
            }
        }
    }
    
    /**
     * Handle new client connections
     */
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        
        if (clientChannel != null) {
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            
            // Create player connection
            PlayerConnection connection = new PlayerConnection(clientChannel);
            playerConnections.put(clientChannel, connection);
            
            logger.info("New client connected: {}", clientChannel.getRemoteAddress());
            
            // Send welcome message
            sendWelcomeMessage(clientChannel);
        }
    }
    
    /**
     * Handle reading data from clients
     */
    private void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        PlayerConnection connection = playerConnections.get(clientChannel);
        
        if (connection == null) return;
        
        try {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            int bytesRead = clientChannel.read(buffer);
            
            if (bytesRead == -1) {
                // Client disconnected
                handleClientDisconnect(clientChannel);
                return;
            }
            
            if (bytesRead > 0) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                
                String message = new String(data, "UTF-8");
                processGameMessage(connection, message);
            }
            
        } catch (IOException e) {
            logger.error("Error reading from client", e);
            handleClientDisconnect(clientChannel);
        }
    }
    
    /**
     * Handle writing data to clients
     */
    private void handleWrite(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        PlayerConnection connection = playerConnections.get(clientChannel);
        
        if (connection != null && connection.hasDataToWrite()) {
            try {
                connection.writeData();
                if (!connection.hasDataToWrite()) {
                    key.interestOps(SelectionKey.OP_READ);
                }
            } catch (IOException e) {
                logger.error("Error writing to client", e);
                handleClientDisconnect(clientChannel);
            }
        }
    }
    
    /**
     * Process game messages from clients
     */
    private void processGameMessage(PlayerConnection connection, String message) {
        try {
            GameMessage gameMessage = objectMapper.readValue(message, GameMessage.class);
            
            switch (gameMessage.getType()) {
                case "JOIN_ROOM":
                    handleJoinRoom(connection, gameMessage);
                    break;
                case "GAME_MOVE":
                    handleGameMove(connection, gameMessage);
                    break;
                case "CHAT_MESSAGE":
                    handleChatMessage(connection, gameMessage);
                    break;
                case "HEARTBEAT":
                    handleHeartbeat(connection);
                    break;
                default:
                    logger.warn("Unknown message type: {}", gameMessage.getType());
            }
            
        } catch (Exception e) {
            logger.error("Error processing game message", e);
        }
    }
    
    /**
     * Handle client joining a game room
     */
    private void handleJoinRoom(PlayerConnection connection, GameMessage message) {
        String roomId = message.getRoomId();
        String playerId = message.getPlayerId();
        
        GameRoom room = gameRooms.computeIfAbsent(roomId, k -> new GameRoom(roomId));
        room.addPlayer(connection);
        connection.setCurrentRoom(roomId);
        connection.setPlayerId(playerId);
        
        // Broadcast to room that player joined
        broadcastToRoom(roomId, createSystemMessage("Player " + playerId + " joined the room"));
        
        logger.info("Player {} joined room {}", playerId, roomId);
    }
    
    /**
     * Handle game moves (word submissions)
     */
    private void handleGameMove(PlayerConnection connection, GameMessage message) {
        String roomId = connection.getCurrentRoom();
        if (roomId != null) {
            GameRoom room = gameRooms.get(roomId);
            if (room != null) {
                // Process game logic
                room.processGameMove(connection, message);
                
                // Broadcast move to other players
                broadcastToRoom(roomId, message, connection);
            }
        }
    }
    
    /**
     * Handle chat messages
     */
    private void handleChatMessage(PlayerConnection connection, GameMessage message) {
        String roomId = connection.getCurrentRoom();
        if (roomId != null) {
            broadcastToRoom(roomId, message);
        }
    }
    
    /**
     * Handle heartbeat for connection monitoring
     */
    private void handleHeartbeat(PlayerConnection connection) {
        connection.updateLastHeartbeat();
        
        // Send heartbeat response
        GameMessage response = new GameMessage();
        response.setType("HEARTBEAT_ACK");
        response.setTimestamp(System.currentTimeMillis());
        
        sendToConnection(connection, response);
    }
    
    /**
     * Broadcast message to all players in a room
     */
    private void broadcastToRoom(String roomId, GameMessage message) {
        broadcastToRoom(roomId, message, null);
    }
    
    private void broadcastToRoom(String roomId, GameMessage message, PlayerConnection excludeConnection) {
        GameRoom room = gameRooms.get(roomId);
        if (room != null) {
            for (PlayerConnection connection : room.getPlayers()) {
                if (connection != excludeConnection) {
                    sendToConnection(connection, message);
                }
            }
        }
    }
    
    /**
     * Send message to specific connection
     */
    private void sendToConnection(PlayerConnection connection, GameMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            connection.queueMessage(json);
            
            // Register for write operation
            SelectionKey key = connection.getChannel().keyFor(selector);
            if (key != null) {
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                selector.wakeup();
            }
            
        } catch (Exception e) {
            logger.error("Error sending message to connection", e);
        }
    }
    
    /**
     * Send welcome message to new clients
     */
    private void sendWelcomeMessage(SocketChannel clientChannel) {
        PlayerConnection connection = playerConnections.get(clientChannel);
        if (connection != null) {
            GameMessage welcome = createSystemMessage("Welcome to Wordle TCP Server!");
            sendToConnection(connection, welcome);
        }
    }
    
    /**
     * Handle client disconnect
     */
    private void handleClientDisconnect(SocketChannel clientChannel) {
        try {
            PlayerConnection connection = playerConnections.remove(clientChannel);
            if (connection != null) {
                String roomId = connection.getCurrentRoom();
                if (roomId != null) {
                    GameRoom room = gameRooms.get(roomId);
                    if (room != null) {
                        room.removePlayer(connection);
                        broadcastToRoom(roomId, createSystemMessage("Player " + connection.getPlayerId() + " left the room"));
                        
                        // Remove empty rooms
                        if (room.isEmpty()) {
                            gameRooms.remove(roomId);
                        }
                    }
                }
                
                logger.info("Client disconnected: {}", connection.getPlayerId());
            }
            
            clientChannel.close();
            
        } catch (IOException e) {
            logger.error("Error handling client disconnect", e);
        }
    }
    
    /**
     * Create system message
     */
    private GameMessage createSystemMessage(String content) {
        GameMessage message = new GameMessage();
        message.setType("SYSTEM_MESSAGE");
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());
        return message;
    }
    
    /**
     * Stop the TCP server
     */
    public void stopServer() {
        isRunning = false;
        
        try {
            if (selector != null) {
                selector.close();
            }
            if (serverChannel != null) {
                serverChannel.close();
            }
            
            threadPool.shutdown();
            
            logger.info("TCP Game Server stopped");
            
        } catch (IOException e) {
            logger.error("Error stopping TCP server", e);
        }
    }
    
    /**
     * Get server statistics
     */
    public Map<String, Object> getServerStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("connectedPlayers", playerConnections.size());
        stats.put("activeRooms", gameRooms.size());
        stats.put("serverPort", TCP_PORT);
        stats.put("isRunning", isRunning);
        
        return stats;
    }
}
