package com.wordle.game.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.wordle.game.model.Game;
import com.wordle.game.model.GameState;
import com.wordle.game.model.GameStatus;
import com.wordle.game.model.Player;
import com.wordle.game.model.WordSubmission;

@Service
public class GameService {
    
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final Map<String, List<String>> gamePlayerMap = new ConcurrentHashMap<>();
    
    @Autowired
    private EnhancedDictionaryService dictionaryService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public Game createGame(String playerId, String playerName) {
        String gameId = UUID.randomUUID().toString();
        
        // Tạo game mới
        String targetWord = dictionaryService.getRandomTargetWord();
        List<Character> availableLetters = dictionaryService.generateRandomLetters(25); // 5x5 grid
        
        Game game = new Game(gameId, targetWord, availableLetters);
        game.setPlayers(new ArrayList<>());
        
        // Tạo player
        Player player = new Player(playerId, playerName);
        player.setFoundWords(new ArrayList<>());
        
        // Lưu vào maps
        games.put(gameId, game);
        players.put(playerId, player);
        gamePlayerMap.put(gameId, new ArrayList<>(Arrays.asList(playerId)));
        
        game.getPlayers().add(playerId);
        
        return game;
    }
    
    public boolean joinGame(String gameId, String playerId, String playerName) {
        Game game = games.get(gameId);
        if (game == null || game.getPlayers().size() >= 2) {
            return false;
        }
        
        Player player = new Player(playerId, playerName);
        player.setFoundWords(new ArrayList<>());
        
        players.put(playerId, player);
        game.getPlayers().add(playerId);
        gamePlayerMap.get(gameId).add(playerId);
        
        // Nếu đủ 2 người chơi, bắt đầu game
        if (game.getPlayers().size() == 2) {
            game.setStatus(GameStatus.IN_PROGRESS);
            game.setStartTime(System.currentTimeMillis());
            broadcastGameState(gameId);
        }
        
        return true;
    }
    
    public boolean submitWord(WordSubmission submission) {
        Game game = games.get(submission.getGameId());
        Player player = players.get(submission.getPlayerId());
        
        if (game == null || player == null || game.getStatus() != GameStatus.IN_PROGRESS) {
            return false;
        }
        
        String word = submission.getWord().toUpperCase();
        
        // Kiểm tra từ có hợp lệ không
        if (!dictionaryService.isValidWord(word)) {
            return false;
        }
        
        // Kiểm tra từ đã được tìm chưa
        if (player.getFoundWords().contains(word)) {
            return false;
        }
        
        // Kiểm tra xem từ có thể tạo từ các chữ cái có sẵn không
        if (!canFormWord(word, game.getAvailableLetters())) {
            return false;
        }
        
        // Thêm từ vào danh sách đã tìm và cập nhật điểm
        player.getFoundWords().add(word);
        player.setScore(player.getScore() + word.length());
        player.setLastMoveTime(System.currentTimeMillis());
        
        // Broadcast trạng thái game mới
        broadcastGameState(submission.getGameId());
        
        return true;
    }
    
    private boolean canFormWord(String word, List<Character> availableLetters) {
        Map<Character, Integer> letterCount = new HashMap<>();
        for (Character letter : availableLetters) {
            letterCount.put(letter, letterCount.getOrDefault(letter, 0) + 1);
        }
        
        for (char c : word.toCharArray()) {
            if (letterCount.getOrDefault(c, 0) == 0) {
                return false;
            }
            letterCount.put(c, letterCount.get(c) - 1);
        }
        
        return true;
    }
    
    public GameState getGameState(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            return null;
        }
        
        GameState state = new GameState();
        state.setGameId(gameId);
        state.setStatus(game.getStatus());
        
        Map<String, Integer> scores = new HashMap<>();
        Map<String, Integer> wordsFound = new HashMap<>();
        
        for (String playerId : game.getPlayers()) {
            Player player = players.get(playerId);
            if (player != null) {
                scores.put(player.getPlayerName(), player.getScore());
                wordsFound.put(player.getPlayerName(), player.getFoundWords().size());
            }
        }
        
        state.setPlayerScores(scores);
        state.setPlayerWordsFound(wordsFound);
        
        // Tính thời gian còn lại (5 phút mỗi game)
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            long elapsed = System.currentTimeMillis() - game.getStartTime();
            long timeLimit = 5 * 60 * 1000; // 5 phút
            state.setTimeRemaining(Math.max(0, timeLimit - elapsed));
            
            // Kết thúc game nếu hết thời gian
            if (state.getTimeRemaining() == 0) {
                game.setStatus(GameStatus.FINISHED);
            }
        }
        
        return state;
    }
    
    public void broadcastGameState(String gameId) {
        GameState state = getGameState(gameId);
        if (state != null) {
            messagingTemplate.convertAndSend("/topic/game/" + gameId, state);
        }
    }
    
    public Game getGame(String gameId) {
        return games.get(gameId);
    }
    
    public List<Game> getWaitingGames() {
        return games.values().stream()
                .filter(game -> game.getStatus() == GameStatus.WAITING && game.getPlayers().size() < 2)
                .toList();
    }
}
