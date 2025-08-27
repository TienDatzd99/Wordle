package com.wordle.game.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Player Statistics Management
 * Áp dụng kiến thức: Data Analytics, Performance Tracking
 */
public class PlayerStats {
    private final String playerId;
    private int totalGames;
    private int gamesWon;
    private int totalAttempts;
    private long totalDuration;
    private long bestTime;
    private final List<GameResult> gameHistory;
    private final Map<String, Integer> letterEfficiency;
    
    public PlayerStats(String playerId) {
        this.playerId = playerId;
        this.totalGames = 0;
        this.gamesWon = 0;
        this.totalAttempts = 0;
        this.totalDuration = 0;
        this.bestTime = Long.MAX_VALUE;
        this.gameHistory = new ArrayList<>();
        this.letterEfficiency = new HashMap<>();
    }
    
    public void addGame(boolean won, int attempts, long duration) {
        totalGames++;
        totalAttempts += attempts;
        totalDuration += duration;
        
        if (won) {
            gamesWon++;
            if (duration > 0 && duration < bestTime) {
                bestTime = duration;
            }
        }
        
        gameHistory.add(new GameResult(won, attempts, duration, System.currentTimeMillis()));
        
        // Keep only last 100 games
        if (gameHistory.size() > 100) {
            gameHistory.remove(0);
        }
    }
    
    public void addMove(String word, String[] colors) {
        // Track letter efficiency
        for (int i = 0; i < word.length() && i < colors.length; i++) {
            char letter = word.charAt(i);
            String color = colors[i];
            
            letterEfficiency.merge(letter + "_" + color, 1, Integer::sum);
        }
    }
    
    public double getWinRate() {
        return totalGames > 0 ? (double) gamesWon / totalGames : 0.0;
    }
    
    public double getAverageAttempts() {
        return totalGames > 0 ? (double) totalAttempts / totalGames : 0.0;
    }
    
    public long getAverageDuration() {
        return totalGames > 0 ? totalDuration / totalGames : 0;
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("playerId", playerId);
        map.put("totalGames", totalGames);
        map.put("gamesWon", gamesWon);
        map.put("winRate", getWinRate());
        map.put("averageAttempts", getAverageAttempts());
        map.put("averageDuration", getAverageDuration());
        map.put("bestTime", bestTime == Long.MAX_VALUE ? 0 : bestTime);
        map.put("letterEfficiency", letterEfficiency);
        map.put("recentGames", gameHistory.subList(Math.max(0, gameHistory.size() - 10), gameHistory.size()));
        
        return map;
    }
    
    // Getters
    public String getPlayerId() {
        return playerId;
    }
    
    public int getTotalGames() {
        return totalGames;
    }
    
    public int getGamesWon() {
        return gamesWon;
    }
    
    public long getBestTime() {
        return bestTime == Long.MAX_VALUE ? 0 : bestTime;
    }
    
    /**
     * Inner class for game results
     */
    public static class GameResult {
        private final boolean won;
        private final int attempts;
        private final long duration;
        private final long timestamp;
        
        public GameResult(boolean won, int attempts, long duration, long timestamp) {
            this.won = won;
            this.attempts = attempts;
            this.duration = duration;
            this.timestamp = timestamp;
        }
        
        // Getters
        public boolean isWon() { return won; }
        public int getAttempts() { return attempts; }
        public long getDuration() { return duration; }
        public long getTimestamp() { return timestamp; }
    }
}
