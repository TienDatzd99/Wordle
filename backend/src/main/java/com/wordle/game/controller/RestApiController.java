package com.wordle.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordle.game.model.Game;
import com.wordle.game.service.GameService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RestApiController {
    
    @Autowired
    private GameService gameService;
    
    @GetMapping("/games/waiting")
    public List<Game> getWaitingGames() {
        return gameService.getWaitingGames();
    }
    
    @GetMapping("/game/{gameId}")
    public Game getGame(@PathVariable String gameId) {
        return gameService.getGame(gameId);
    }
    
    @GetMapping("/")
    public String home() {
        return "Wordle Multiplayer Game Backend Server is running! Use /api/health to check status.";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
