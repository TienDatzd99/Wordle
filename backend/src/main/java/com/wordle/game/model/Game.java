package com.wordle.game.model;

import java.util.List;

public class Game {
    private String gameId;
    private List<String> players;
    private String targetWord;
    private GameStatus status;
    private long startTime;
    private int gridSize;
    private List<Character> availableLetters;
    
    public Game() {}
    
    public Game(String gameId, String targetWord, List<Character> availableLetters) {
        this.gameId = gameId;
        this.targetWord = targetWord;
        this.availableLetters = availableLetters;
        this.status = GameStatus.WAITING;
        this.gridSize = 5; // Mặc định 5x5
    }
    
    // Getters và Setters
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public List<String> getPlayers() { return players; }
    public void setPlayers(List<String> players) { this.players = players; }
    
    public String getTargetWord() { return targetWord; }
    public void setTargetWord(String targetWord) { this.targetWord = targetWord; }
    
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public int getGridSize() { return gridSize; }
    public void setGridSize(int gridSize) { this.gridSize = gridSize; }
    
    public List<Character> getAvailableLetters() { return availableLetters; }
    public void setAvailableLetters(List<Character> availableLetters) { this.availableLetters = availableLetters; }
}
