package com.wordle.game.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Player Connection Management
 * Áp dụng kiến thức: Connection State Management, Buffer Management
 */
public class PlayerConnection {
    private final SocketChannel channel;
    private String playerId;
    private String currentRoom;
    private long lastHeartbeat;
    private final ConcurrentLinkedQueue<String> messageQueue;
    private ByteBuffer writeBuffer;
    
    public PlayerConnection(SocketChannel channel) {
        this.channel = channel;
        this.lastHeartbeat = System.currentTimeMillis();
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.writeBuffer = ByteBuffer.allocate(1024);
    }
    
    public SocketChannel getChannel() {
        return channel;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }
    
    public void updateLastHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }
    
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public void queueMessage(String message) {
        messageQueue.offer(message);
    }
    
    public boolean hasDataToWrite() {
        return !messageQueue.isEmpty() || writeBuffer.hasRemaining();
    }
    
    public void writeData() throws IOException {
        // If buffer is empty, load next message
        if (!writeBuffer.hasRemaining()) {
            String nextMessage = messageQueue.poll();
            if (nextMessage != null) {
                writeBuffer.clear();
                writeBuffer.put((nextMessage + "\n").getBytes("UTF-8"));
                writeBuffer.flip();
            }
        }
        
        // Write buffer to channel
        if (writeBuffer.hasRemaining()) {
            channel.write(writeBuffer);
        }
    }
    
    public boolean isConnectionAlive() {
        return System.currentTimeMillis() - lastHeartbeat < 30000; // 30 seconds timeout
    }
}
