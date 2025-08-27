package com.wordle.game.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Game Statistics Management
 * Áp dụng kiến thức: Statistical Analysis, Game Analytics
 */
public class GameStatistics {
    private final String roomId;
    private int totalGames;
    private int totalWins;
    private long totalDuration;
    private final Map<Integer, Integer> attemptDistribution; // attempts -> count
    private final List<Long> gameDurations;
    private final Map<String, Integer> popularWords;
    
    public GameStatistics(String roomId) {
        this.roomId = roomId;
        this.totalGames = 0;
        this.totalWins = 0;
        this.totalDuration = 0;
        this.attemptDistribution = new HashMap<>();
        this.gameDurations = new ArrayList<>();
        this.popularWords = new HashMap<>();
    }
    
    public void addGameResult(boolean won, int attempts, long duration) {
        totalGames++;
        
        if (won) {
            totalWins++;
        }
        
        totalDuration += duration;
        
        // Track attempt distribution
        attemptDistribution.merge(attempts, 1, Integer::sum);
        
        // Track game durations
        gameDurations.add(duration);
        
        // Keep only last 1000 game durations
        if (gameDurations.size() > 1000) {
            gameDurations.remove(0);
        }
    }
    
    public void addWordUsage(String word) {
        popularWords.merge(word.toUpperCase(), 1, Integer::sum);
    }
    
    public double getWinRate() {
        return totalGames > 0 ? (double) totalWins / totalGames : 0.0;
    }
    
    public double getAverageDuration() {
        return totalGames > 0 ? (double) totalDuration / totalGames : 0.0;
    }
    
    public double getAverageAttempts() {
        if (attemptDistribution.isEmpty()) return 0.0;
        
        int totalAttempts = attemptDistribution.entrySet().stream()
                .mapToInt(entry -> entry.getKey() * entry.getValue())
                .sum();
        
        return (double) totalAttempts / totalGames;
    }
    
    public Map<String, Object> getDetailedStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("roomId", roomId);
        stats.put("totalGames", totalGames);
        stats.put("totalWins", totalWins);
        stats.put("winRate", getWinRate());
        stats.put("averageDuration", getAverageDuration());
        stats.put("averageAttempts", getAverageAttempts());
        stats.put("attemptDistribution", attemptDistribution);
        
        // Calculate percentiles for duration
        if (!gameDurations.isEmpty()) {
            List<Long> sortedDurations = new ArrayList<>(gameDurations);
            Collections.sort(sortedDurations);
            
            Map<String, Long> percentiles = new HashMap<>();
            percentiles.put("p50", getPercentile(sortedDurations, 0.5));
            percentiles.put("p75", getPercentile(sortedDurations, 0.75));
            percentiles.put("p90", getPercentile(sortedDurations, 0.9));
            percentiles.put("p95", getPercentile(sortedDurations, 0.95));
            
            stats.put("durationPercentiles", percentiles);
        }
        
        // Top 10 popular words
        List<Map.Entry<String, Integer>> topWords = popularWords.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();
        
        stats.put("popularWords", topWords);
        
        return stats;
    }
    
    private Long getPercentile(List<Long> sortedData, double percentile) {
        if (sortedData.isEmpty()) return 0L;
        
        int index = (int) Math.ceil(percentile * sortedData.size()) - 1;
        index = Math.max(0, Math.min(index, sortedData.size() - 1));
        
        return sortedData.get(index);
    }
    
    // Getters
    public String getRoomId() {
        return roomId;
    }
    
    public int getTotalGames() {
        return totalGames;
    }
    
    public int getTotalWins() {
        return totalWins;
    }
}
