package com.wordle.game.model;

import java.util.List;

public class Player {
    private String playerId;
    private String playerName;
    private int score;
    private List<String> foundWords;
    private long lastMoveTime;
    
    public Player() {}
    
    public Player(String playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.score = 0;
    }
    
    // Getters và Setters
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public List<String> getFoundWords() { return foundWords; }
    public void setFoundWords(List<String> foundWords) { this.foundWords = foundWords; }
    
    public long getLastMoveTime() { return lastMoveTime; }
    public void setLastMoveTime(long lastMoveTime) { this.lastMoveTime = lastMoveTime; }
}
