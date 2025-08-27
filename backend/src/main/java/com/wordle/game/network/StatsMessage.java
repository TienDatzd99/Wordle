package com.wordle.game.network;

import java.util.Map;

/**
 * Statistics Message cho UDP Communication
 * Áp dụng kiến thức: UDP Message Protocol, Lightweight Communication
 */
public class StatsMessage {
    private String type;
    private String playerId;
    private String roomId;
    private Map<String, Object> data;
    private long timestamp;
    
    public StatsMessage() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
