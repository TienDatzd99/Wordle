package com.wordle.game.model;

public class WordSubmission {
    private String gameId;
    private String playerId;
    private String word;
    private int row;
    private int col;
    private String direction; // "horizontal" hoặc "vertical"
    
    public WordSubmission() {}
    
    // Getters và Setters
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }
    
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}
