package com.example.wordlegame.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/simple")
public class SimpleGameController {

    @GetMapping("")
    public ResponseEntity<String> simpleGame() {
        String html = """
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🎯 Simple Wordle Game</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: white;
        }
        .container { 
            max-width: 600px; 
            margin: 0 auto; 
            padding: 20px;
            text-align: center;
        }
        .header { 
            margin-bottom: 30px;
            padding: 20px;
        }
        .header h1 { 
            font-size: 2.5em;
            color: #ffd700;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
        }
        .game-board {
            background: rgba(255,255,255,0.95);
            border-radius: 15px;
            padding: 30px;
            color: #333;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }
        .login-area { margin-bottom: 20px; }
        .game-area { display: none; }
        .input-group { margin: 15px 0; }
        .input-group input {
            width: 100%;
            padding: 15px;
            font-size: 16px;
            border: 2px solid #ddd;
            border-radius: 8px;
            text-align: center;
        }
        .btn {
            background: #667eea;
            color: white;
            padding: 15px 30px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            margin: 10px;
            transition: background 0.3s;
        }
        .btn:hover { background: #5a6fd8; }
        .btn-success { background: #28a745; }
        .btn-success:hover { background: #218838; }
        .btn-danger { background: #dc3545; }
        .btn-danger:hover { background: #c82333; }
        
        .wordle-grid {
            display: grid;
            grid-template-columns: repeat(5, 60px);
            gap: 8px;
            justify-content: center;
            margin: 20px auto;
        }
        .grid-cell {
            width: 60px;
            height: 60px;
            border: 2px solid #d3d6da;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            font-weight: bold;
            background: white;
            border-radius: 5px;
        }
        .grid-cell.filled { background: #f0f0f0; border-color: #999; }
        .grid-cell.correct { background: #6aaa64; color: white; }
        .grid-cell.wrong-position { background: #c9b458; color: white; }
        .grid-cell.wrong { background: #787c7e; color: white; }
        
        .status { 
            margin: 20px 0;
            padding: 15px;
            border-radius: 8px;
            font-weight: bold;
        }
        .status-info { background: #d1ecf1; color: #0c5460; }
        .status-success { background: #d4edda; color: #155724; }
        .status-error { background: #f8d7da; color: #721c24; }
        
        .word-input-section {
            margin: 20px 0;
            display: flex;
            gap: 10px;
            justify-content: center;
            align-items: center;
        }
        .word-input {
            padding: 12px;
            font-size: 18px;
            border: 2px solid #ddd;
            border-radius: 5px;
            text-transform: uppercase;
            width: 200px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🎯 Simple Wordle Game</h1>
            <p>Guess the 5-letter word!</p>
        </div>
        
        <div class="game-board">
            <!-- Login Area -->
            <div id="loginArea" class="login-area">
                <h2>Welcome to Wordle!</h2>
                <div class="input-group">
                    <input type="text" id="playerName" placeholder="Enter your name" maxlength="15">
                </div>
                <button onclick="startSingleGame()" class="btn btn-success">🎮 Start Single Player Game</button>
                <button onclick="startMultiplayerGame()" class="btn">🔥 Start Multiplayer Mode</button>
                <br><br>
                <button onclick="testGameTransition()" class="btn btn-danger">🧪 Test Game Transition</button>
            </div>
            
            <!-- Game Area -->
            <div id="gameArea" class="game-area">
                <h2>Find the Word!</h2>
                <div id="gameStatus" class="status status-info">
                    Game started! Target word: APPLE
                </div>
                
                <div class="wordle-grid" id="wordleGrid">
                    <!-- 6 rows x 5 columns -->
                </div>
                
                <div class="word-input-section">
                    <input type="text" id="wordInput" placeholder="Enter word" class="word-input" maxlength="5">
                    <button onclick="submitWord()" class="btn">Submit</button>
                </div>
                
                <div>
                    <p><strong>Target Word:</strong> <span id="targetWord">APPLE</span></p>
                    <p><strong>Attempts:</strong> <span id="attempts">0</span>/6</p>
                </div>
                
                <button onclick="resetGame()" class="btn">🔄 New Game</button>
                <button onclick="backToMenu()" class="btn">⬅️ Back to Menu</button>
            </div>
        </div>
    </div>
    
    <script>
        let targetWord = 'APPLE';
        let currentAttempt = 0;
        let gameWon = false;
        let playerName = '';
        
        // Initialize grid
        function initializeGrid() {
            const grid = document.getElementById('wordleGrid');
            grid.innerHTML = '';
            for (let row = 0; row < 6; row++) {
                for (let col = 0; col < 5; col++) {
                    const cell = document.createElement('div');
                    cell.className = 'grid-cell';
                    cell.id = `cell-${row}-${col}`;
                    grid.appendChild(cell);
                }
            }
        }
        
        // Start single player game
        function startSingleGame() {
            const nameInput = document.getElementById('playerName');
            playerName = nameInput.value.trim() || 'Player';
            
            // Random word selection
            const words = ['APPLE', 'HOUSE', 'WATER', 'PLANT', 'MUSIC', 'LIGHT', 'BREAD', 'CLOUD', 'DANCE', 'EARTH'];
            targetWord = words[Math.floor(Math.random() * words.length)];
            
            showGame();
            document.getElementById('targetWord').textContent = targetWord;
            document.getElementById('gameStatus').textContent = `Welcome ${playerName}! Find the word: ${targetWord}`;
        }
        
        // Start multiplayer game (simulation)
        function startMultiplayerGame() {
            const nameInput = document.getElementById('playerName');
            playerName = nameInput.value.trim() || 'Player';
            
            alert('Multiplayer mode! Connecting to room...');
            setTimeout(() => {
                alert('Game starting! Word: BREAD');
                targetWord = 'BREAD';
                showGame();
                document.getElementById('targetWord').textContent = targetWord;
                document.getElementById('gameStatus').textContent = `Multiplayer game started! Find the word: ${targetWord}`;
            }, 1000);
        }
        
        // Test game transition
        function testGameTransition() {
            console.log('Testing game transition...');
            playerName = 'TestPlayer';
            targetWord = 'TESTS';
            showGame();
            document.getElementById('targetWord').textContent = targetWord;
            document.getElementById('gameStatus').textContent = 'Test game started! Find the word: TESTS';
        }
        
        // Show game area
        function showGame() {
            document.getElementById('loginArea').style.display = 'none';
            document.getElementById('gameArea').style.display = 'block';
            initializeGrid();
            currentAttempt = 0;
            gameWon = false;
            document.getElementById('attempts').textContent = currentAttempt;
            document.getElementById('wordInput').value = '';
            document.getElementById('wordInput').focus();
        }
        
        // Submit word
        function submitWord() {
            if (gameWon || currentAttempt >= 6) return;
            
            const input = document.getElementById('wordInput');
            const word = input.value.toUpperCase().trim();
            
            if (word.length !== 5) {
                alert('Please enter a 5-letter word!');
                return;
            }
            
            // Check word
            checkWord(word);
            input.value = '';
            currentAttempt++;
            document.getElementById('attempts').textContent = currentAttempt;
            
            if (word === targetWord) {
                gameWon = true;
                document.getElementById('gameStatus').className = 'status status-success';
                document.getElementById('gameStatus').textContent = `🎉 Congratulations ${playerName}! You won in ${currentAttempt} attempts!`;
            } else if (currentAttempt >= 6) {
                document.getElementById('gameStatus').className = 'status status-error';
                document.getElementById('gameStatus').textContent = `💀 Game Over! The word was: ${targetWord}`;
            }
        }
        
        // Check word against target
        function checkWord(word) {
            const targetArray = targetWord.split('');
            const wordArray = word.split('');
            
            for (let i = 0; i < 5; i++) {
                const cell = document.getElementById(`cell-${currentAttempt}-${i}`);
                cell.textContent = wordArray[i];
                
                if (wordArray[i] === targetArray[i]) {
                    cell.className = 'grid-cell correct';
                } else if (targetArray.includes(wordArray[i])) {
                    cell.className = 'grid-cell wrong-position';
                } else {
                    cell.className = 'grid-cell wrong';
                }
            }
        }
        
        // Reset game
        function resetGame() {
            const words = ['APPLE', 'HOUSE', 'WATER', 'PLANT', 'MUSIC', 'LIGHT', 'BREAD', 'CLOUD', 'DANCE', 'EARTH'];
            targetWord = words[Math.floor(Math.random() * words.length)];
            showGame();
            document.getElementById('targetWord').textContent = targetWord;
            document.getElementById('gameStatus').className = 'status status-info';
            document.getElementById('gameStatus').textContent = `New game started! Find the word: ${targetWord}`;
        }
        
        // Back to menu
        function backToMenu() {
            document.getElementById('gameArea').style.display = 'none';
            document.getElementById('loginArea').style.display = 'block';
        }
        
        // Enter key support
        document.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                const gameArea = document.getElementById('gameArea');
                const loginArea = document.getElementById('loginArea');
                
                if (gameArea.style.display !== 'none') {
                    submitWord();
                } else if (loginArea.style.display !== 'none') {
                    startSingleGame();
                }
            }
        });
        
        // Initialize
        document.addEventListener('DOMContentLoaded', function() {
            initializeGrid();
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
