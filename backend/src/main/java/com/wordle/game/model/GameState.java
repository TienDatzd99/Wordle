package com.wordle.game.model;

import java.util.Map;

public class GameState {
    private String gameId;
    private Map<String, Integer> playerScores;
    private Map<String, Integer> playerWordsFound;
    private char[][] grid;
    private GameStatus status;
    private long timeRemaining;
    
    public GameState() {}
    
    // Getters và Setters
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public Map<String, Integer> getPlayerScores() { return playerScores; }
    public void setPlayerScores(Map<String, Integer> playerScores) { this.playerScores = playerScores; }
    
    public Map<String, Integer> getPlayerWordsFound() { return playerWordsFound; }
    public void setPlayerWordsFound(Map<String, Integer> playerWordsFound) { this.playerWordsFound = playerWordsFound; }
    
    public char[][] getGrid() { return grid; }
    public void setGrid(char[][] grid) { this.grid = grid; }
    
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
    
    public long getTimeRemaining() { return timeRemaining; }
    public void setTimeRemaining(long timeRemaining) { this.timeRemaining = timeRemaining; }
}
