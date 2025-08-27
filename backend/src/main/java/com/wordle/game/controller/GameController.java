package com.wordle.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.wordle.game.model.Game;
import com.wordle.game.model.GameState;
import com.wordle.game.model.GameStatus;
import com.wordle.game.model.Player;
import com.wordle.game.model.WordSubmission;
import com.wordle.game.service.GameService;

@Controller
public class GameController {
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/game/create")
    @SendTo("/topic/game/created")
    public Game createGame(Player player) {
        return gameService.createGame(player.getPlayerId(), player.getPlayerName());
    }
    
    @MessageMapping("/game/join")
    public void joinGame(JoinGameRequest request) {
        boolean success = gameService.joinGame(request.getGameId(), request.getPlayerId(), request.getPlayerName());
        
        if (success) {
            Game game = gameService.getGame(request.getGameId());
            messagingTemplate.convertAndSend("/topic/game/" + request.getGameId() + "/joined", game);
            
            // Nếu game bắt đầu, broadcast trạng thái
            if (game.getStatus() == GameStatus.IN_PROGRESS) {
                gameService.broadcastGameState(request.getGameId());
            }
        }
    }
    
    @MessageMapping("/game/submit")
    public void submitWord(WordSubmission submission) {
        boolean success = gameService.submitWord(submission);
        
        if (success) {
            // Trạng thái đã được broadcast trong service
        } else {
            // Gửi thông báo lỗi cho player
            messagingTemplate.convertAndSendToUser(
                submission.getPlayerId(), 
                "/queue/error", 
                "Invalid word or word already found"
            );
        }
    }
    
    @MessageMapping("/game/state")
    public void getGameState(String gameId) {
        GameState state = gameService.getGameState(gameId);
        if (state != null) {
            messagingTemplate.convertAndSend("/topic/game/" + gameId, state);
        }
    }
    
    // Inner class cho join game request
    public static class JoinGameRequest {
        private String gameId;
        private String playerId;
        private String playerName;
        
        public JoinGameRequest() {}
        
        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        
        public String getPlayerId() { return playerId; }
        public void setPlayerId(String playerId) { this.playerId = playerId; }
        
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
    }
}
