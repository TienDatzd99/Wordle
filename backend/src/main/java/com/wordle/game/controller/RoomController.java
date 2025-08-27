package com.wordle.game.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordle.game.service.EnhancedDictionaryService;

@Controller
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private EnhancedDictionaryService dictionaryService;

    private Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private Map<String, String> playerRooms = new ConcurrentHashMap<>();

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRoom(@RequestBody Map<String, String> request) {
        String hostName = request.get("hostName");
        String roomCode = generateRoomCode();
        
        GameRoom room = new GameRoom(roomCode, hostName);
        rooms.put(roomCode, room);
        playerRooms.put(hostName, roomCode);

        Map<String, String> response = new HashMap<>();
        response.put("roomCode", roomCode);
        response.put("status", "created");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> joinRoom(@RequestBody Map<String, String> request) {
        String roomCode = request.get("roomCode");
        String playerName = request.get("playerName");
        
        GameRoom room = rooms.get(roomCode);
        if (room == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Room not found");
            return ResponseEntity.badRequest().body(response);
        }

        if (room.getPlayers().size() >= room.getMaxPlayers()) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Room is full");
            return ResponseEntity.badRequest().body(response);
        }

        room.addPlayer(playerName);
        playerRooms.put(playerName, roomCode);

        // Notify all players in room
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, room.toMap());

        Map<String, Object> response = new HashMap<>();
        response.put("room", room.toMap());
        response.put("status", "joined");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<Map<String, Object>> getRoomInfo(@PathVariable String roomCode) {
        GameRoom room = rooms.get(roomCode);
        if (room == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Room not found");
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(room.toMap());
    }

    @PostMapping("/{roomCode}/start")
    public ResponseEntity<Map<String, String>> startGame(@PathVariable String roomCode, @RequestBody Map<String, String> request) {
        String hostName = request.get("hostName");
        GameRoom room = rooms.get(roomCode);
        
        if (room == null || !room.getHost().equals(hostName)) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Unauthorized or room not found");
            return ResponseEntity.badRequest().body(response);
        }

        room.startGame(dictionaryService);
        
        // Notify all players that game started
        Map<String, Object> gameStartData = room.toMap();
        gameStartData.put("word", room.getCurrentWord());
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gamestart", gameStartData);

        Map<String, String> response = new HashMap<>();
        response.put("status", "started");
        response.put("word", room.getCurrentWord());
        
        return ResponseEntity.ok(response);
    }

    @MessageMapping("/room/{roomCode}/chat")
    @SendTo("/topic/room/{roomCode}/chat")
    public Map<String, Object> sendChatMessage(@DestinationVariable String roomCode, Map<String, String> message) {
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("player", message.get("player"));
        chatMessage.put("message", message.get("message"));
        chatMessage.put("timestamp", System.currentTimeMillis());
        
        return chatMessage;
    }

    @PostMapping("/{roomCode}/leave")
    public ResponseEntity<Map<String, String>> leaveRoom(@PathVariable String roomCode, @RequestBody Map<String, String> request) {
        String playerName = request.get("playerName");
        GameRoom room = rooms.get(roomCode);
        
        if (room != null) {
            room.removePlayer(playerName);
            playerRooms.remove(playerName);

            if (room.getPlayers().isEmpty()) {
                rooms.remove(roomCode);
            } else {
                // Notify remaining players
                messagingTemplate.convertAndSend("/topic/room/" + roomCode, room.toMap());
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", "left");
        
        return ResponseEntity.ok(response);
    }

    private String generateRoomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append((char) ('A' + random.nextInt(26)));
        }
        return code.toString();
    }

    // Inner class for GameRoom
    public static class GameRoom {
        private String roomCode;
        private String host;
        private List<String> players;
        private boolean gameStarted;
        private int maxPlayers;
        private String currentWord;
        private long createdAt;

        public GameRoom(String roomCode, String host) {
            this.roomCode = roomCode;
            this.host = host;
            this.players = new ArrayList<>();
            this.players.add(host);
            this.gameStarted = false;
            this.maxPlayers = 6;
            this.createdAt = System.currentTimeMillis();
        }

        public void addPlayer(String playerName) {
            if (!players.contains(playerName) && players.size() < maxPlayers) {
                players.add(playerName);
            }
        }

        public void removePlayer(String playerName) {
            players.remove(playerName);
            if (playerName.equals(host) && !players.isEmpty()) {
                host = players.get(0); // Transfer host to first player
            }
        }

        public void startGame(EnhancedDictionaryService dictionaryService) {
            gameStarted = true;
            // Get random word from dictionary service
            currentWord = dictionaryService.getRandomTargetWord();
            System.out.println("Game starting! Word: " + currentWord); // Debug log
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("roomCode", roomCode);
            map.put("host", host);
            map.put("players", players);
            map.put("playerCount", players.size());
            map.put("maxPlayers", maxPlayers);
            map.put("gameStarted", gameStarted);
            map.put("createdAt", createdAt);
            return map;
        }

        // Getters
        public String getRoomCode() { return roomCode; }
        public String getHost() { return host; }
        public List<String> getPlayers() { return players; }
        public boolean isGameStarted() { return gameStarted; }
        public int getMaxPlayers() { return maxPlayers; }
        public String getCurrentWord() { return currentWord; }
    }
}
