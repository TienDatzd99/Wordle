package com.example.wordlegame.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/game")
    public ResponseEntity<String> testGamePage() {
        String html = """
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wordle Test Game</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            margin: 0;
            padding: 20px;
        }
        .container { 
            max-width: 600px; 
            margin: 50px auto; 
            text-align: center;
            background: rgba(255,255,255,0.1);
            padding: 40px;
            border-radius: 15px;
        }
        button { 
            background: #ff0000; 
            color: white; 
            padding: 15px 30px; 
            border: none; 
            border-radius: 8px; 
            cursor: pointer; 
            font-size: 18px;
            font-weight: bold;
            margin: 10px;
        }
        button:hover { background: #cc0000; }
        #gameArea { 
            display: none; 
            background: white;
            color: black;
            padding: 20px;
            border-radius: 10px;
            margin-top: 20px;
        }
        .grid { 
            display: grid; 
            grid-template-columns: repeat(5, 50px); 
            gap: 5px; 
            justify-content: center;
            margin: 20px 0;
        }
        .cell { 
            width: 50px; 
            height: 50px; 
            border: 2px solid #ccc; 
            display: flex; 
            align-items: center; 
            justify-content: center;
            font-weight: bold;
            font-size: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎮 Wordle Test Game</h1>
        <p>This is a direct test to verify game transition works</p>
        
        <div id="loginArea">
            <button onclick="startGame()">🚀 START GAME TEST</button>
            <button onclick="testMultiplayer()">🔧 TEST MULTIPLAYER FLOW</button>
        </div>
        
        <div id="gameArea">
            <h2>Game Started! Target: APPLE</h2>
            <div class="grid" id="grid">
                <!-- 6 rows x 5 cols -->
                <div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div>
                <div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div>
                <div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div>
                <div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div>
                <div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div>
                <div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div><div class="cell"></div>
            </div>
            <input type="text" id="wordInput" placeholder="Enter 5-letter word" maxlength="5" style="padding: 10px; font-size: 16px;">
            <button onclick="submitWord()">Submit</button>
            <div id="status" style="margin-top: 20px; font-weight: bold;"></div>
        </div>
    </div>
    
    <script>
        function startGame() {
            console.log('Starting game...');
            document.getElementById('loginArea').style.display = 'none';
            document.getElementById('gameArea').style.display = 'block';
            document.getElementById('status').textContent = 'Game started! Find the word: APPLE';
        }
        
        function testMultiplayer() {
            // Simulate multiplayer flow
            alert('Game starting! Word: APPLE');
            startGame();
        }
        
        function submitWord() {
            const word = document.getElementById('wordInput').value.toUpperCase();
            if (word.length === 5) {
                if (word === 'APPLE') {
                    document.getElementById('status').textContent = '🎉 Correct! You won!';
                    document.getElementById('status').style.color = 'green';
                } else {
                    document.getElementById('status').textContent = '❌ Wrong! Try again. Target: APPLE';
                    document.getElementById('status').style.color = 'red';
                }
                document.getElementById('wordInput').value = '';
            } else {
                alert('Please enter a 5-letter word!');
            }
        }
        
        // Auto-start if URL has parameters
        window.addEventListener('load', function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('autostart') === 'true') {
                setTimeout(startGame, 500);
            }
        });
    </script>
</body>
</html>
        """;
        
        return ResponseEntity.ok()
            .header("Content-Type", "text/html; charset=UTF-8")
            .header("Cache-Control", "no-cache, no-store, must-revalidate")
            .header("Pragma", "no-cache")
            .header("Expires", "0")
            .body(html);
    }
}
