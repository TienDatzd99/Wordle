// Room Management System
class RoomManager {
    constructor() {
        this.socket = null;
        this.currentRoom = null;
        this.currentPlayer = null;
        this.isHost = false;
        this.stompClient = null;
        
        // Don't auto-initialize WebSocket, do it when needed
    }

    initializeWebSocket() {
        // Initialize STOMP WebSocket connection
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        
        return new Promise((resolve, reject) => {
            this.stompClient.connect({}, (frame) => {
                console.log('Connected to WebSocket: ' + frame);
                resolve(frame);
            }, (error) => {
                console.error('WebSocket connection error:', error);
                reject(error);
            });
        });
    }

    async createRoom(hostName) {
        try {
            // Ensure WebSocket is connected first
            if (!this.stompClient || !this.stompClient.connected) {
                await this.initializeWebSocket();
            }
            
            const response = await fetch('/api/rooms/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ hostName: hostName })
            });

            const data = await response.json();
            
            if (response.ok) {
                this.currentRoom = data.roomCode;
                this.currentPlayer = hostName;
                this.isHost = true;
                
                // Subscribe to room updates
                this.subscribeToRoom(data.roomCode);
                
                // Show room lobby
                this.showRoomLobby(data.roomCode, hostName);
                
                return data;
            } else {
                throw new Error(data.error || 'Failed to create room');
            }
        } catch (error) {
            console.error('Error creating room:', error);
            alert('Failed to create room: ' + error.message);
        }
    }

    async joinRoom(roomCode, playerName) {
        try {
            // Ensure WebSocket is connected first
            if (!this.stompClient || !this.stompClient.connected) {
                await this.initializeWebSocket();
            }
            
            const response = await fetch('/api/rooms/join', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ roomCode: roomCode, playerName: playerName })
            });

            const data = await response.json();
            
            if (response.ok) {
                this.currentRoom = roomCode;
                this.currentPlayer = playerName;
                this.isHost = false;
                
                // Subscribe to room updates
                this.subscribeToRoom(roomCode);
                
                // Show room lobby
                this.showRoomLobby(roomCode, playerName, data.room);
                
                return data;
            } else {
                throw new Error(data.error || 'Failed to join room');
            }
        } catch (error) {
            console.error('Error joining room:', error);
            alert('Failed to join room: ' + error.message);
        }
    }

    subscribeToRoom(roomCode) {
        if (!this.stompClient || !this.stompClient.connected) {
            console.error('STOMP client not connected, cannot subscribe to room');
            return;
        }

        console.log('Subscribing to room:', roomCode);

        // Subscribe to room updates
        this.stompClient.subscribe(`/topic/room/${roomCode}`, (message) => {
            const roomData = JSON.parse(message.body);
            this.updateRoomDisplay(roomData);
        });

        // Subscribe to chat messages
        this.stompClient.subscribe(`/topic/room/${roomCode}/chat`, (message) => {
            const chatMessage = JSON.parse(message.body);
            this.addChatMessage(chatMessage);
        });

        // Subscribe to game start
        this.stompClient.subscribe(`/topic/room/${roomCode}/gamestart`, (message) => {
            const gameData = JSON.parse(message.body);
            this.startMultiplayerGame(gameData);
        });
    }

    showRoomLobby(roomCode, playerName, roomData = null) {
        // Hide login area and show room lobby
        document.getElementById('loginArea').style.display = 'none';
        
        // Create room lobby container
        let roomContainer = document.getElementById('roomLobbyContainer');
        if (!roomContainer) {
            roomContainer = document.createElement('div');
            roomContainer.id = 'roomLobbyContainer';
            document.body.appendChild(roomContainer);
        }
        
        roomContainer.innerHTML = `
            <div class="room-container">
                <div class="room-modal">
                    <button class="close-btn" onclick="roomManager.leaveRoom()">&times;</button>
                    
                    <div class="room-header">
                        <h2 class="room-title">🎮 wordlecup.io</h2>
                    </div>

                    <div class="room-tabs">
                        <div class="room-tab active" data-tab="lobby">Lobby</div>
                        <div class="room-tab" data-tab="customize">Customize</div>
                        <div class="room-tab" data-tab="rules">Rules</div>
                    </div>

                    <div id="lobby-tab" class="tab-content">
                        <div class="waiting-section">
                            <div class="room-status">Waiting for players (<span id="player-count">1</span>/6)</div>
                            
                            <div class="host-info">
                                <strong id="host-name">${playerName}</strong> (host)
                            </div>

                            <div class="room-code-display">
                                <div>Share invite link with friends</div>
                                <div class="room-code-text" id="room-code">${roomCode}</div>
                                <button class="copy-button" onclick="roomManager.copyRoomCode()">📋 Copy Link</button>
                            </div>

                            <div class="players-list">
                                <div class="players-grid" id="players-grid">
                                    <!-- Players will be populated here -->
                                </div>
                            </div>

                            <div class="game-controls" id="game-controls">
                                ${this.isHost ? `
                                    <button class="start-game-btn" id="start-game-btn" onclick="roomManager.startGame()">
                                        Start Game
                                    </button>
                                ` : `
                                    <div>Only host can start the game</div>
                                `}
                            </div>

                            <div class="room-settings">
                                <div class="setting-row">
                                    <span>Check out game customization options</span>
                                </div>
                                <button class="increase-room-btn">Increase your room size</button>
                            </div>

                            <div class="premium-features">
                                <button class="premium-btn">🎖️ See Premium features</button>
                            </div>
                        </div>
                    </div>

                    <div id="customize-tab" class="tab-content" style="display: none;">
                        <h3>Game Customization</h3>
                        <p>Premium feature - Customize game rules, time limits, and more!</p>
                    </div>

                    <div id="rules-tab" class="tab-content" style="display: none;">
                        <h3>Game Rules</h3>
                        <ul>
                            <li>Guess the 5-letter word in 6 attempts</li>
                            <li>Green: Letter is correct and in right position</li>
                            <li>Yellow: Letter is in the word but wrong position</li>
                            <li>Gray: Letter is not in the word</li>
                            <li>First to solve wins the round!</li>
                        </ul>
                    </div>

                    <div class="chat-section" id="chat-section">
                        <div class="chat-header">
                            <span>Chat</span>
                            <button class="close-btn" onclick="roomManager.toggleChat()">&times;</button>
                        </div>
                        <div class="chat-messages" id="chat-messages">
                            <!-- Chat messages will appear here -->
                        </div>
                        <div class="chat-input">
                            <input type="text" id="chat-input" placeholder="Type a message..." maxlength="100">
                            <button class="chat-send" onclick="roomManager.sendChatMessage()">Send</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Initialize tab switching
        this.initializeTabs();
        
        // Initialize chat
        this.initializeChat();
        
        // Update room display if we have room data
        if (roomData) {
            this.updateRoomDisplay(roomData);
        } else {
            // Fetch current room data
            this.fetchRoomData(roomCode);
        }
    }

    initializeTabs() {
        const tabs = document.querySelectorAll('.room-tab');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                // Remove active class from all tabs
                tabs.forEach(t => t.classList.remove('active'));
                document.querySelectorAll('.tab-content').forEach(content => {
                    content.style.display = 'none';
                });

                // Add active class to clicked tab
                tab.classList.add('active');
                const tabName = tab.getAttribute('data-tab');
                document.getElementById(`${tabName}-tab`).style.display = 'block';
            });
        });
    }

    initializeChat() {
        const chatInput = document.getElementById('chat-input');
        if (chatInput) {
            chatInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.sendChatMessage();
                }
            });
        }
    }

    async fetchRoomData(roomCode) {
        try {
            const response = await fetch(`/api/rooms/${roomCode}`);
            const data = await response.json();
            
            if (response.ok) {
                this.updateRoomDisplay(data);
            }
        } catch (error) {
            console.error('Error fetching room data:', error);
        }
    }

    updateRoomDisplay(roomData) {
        // Update player count
        const playerCountElement = document.getElementById('player-count');
        if (playerCountElement) {
            playerCountElement.textContent = roomData.playerCount;
        }

        // Update host name
        const hostNameElement = document.getElementById('host-name');
        if (hostNameElement) {
            hostNameElement.textContent = roomData.host;
        }

        // Update players grid
        this.updatePlayersGrid(roomData.players, roomData.host, roomData.maxPlayers);

        // Update start button
        const startBtn = document.getElementById('start-game-btn');
        if (startBtn && this.isHost) {
            startBtn.disabled = roomData.playerCount < 2;
        }
    }

    updatePlayersGrid(players, host, maxPlayers) {
        const playersGrid = document.getElementById('players-grid');
        if (!playersGrid) return;

        playersGrid.innerHTML = '';

        // Create slots for max players
        for (let i = 0; i < maxPlayers; i++) {
            const slot = document.createElement('div');
            slot.className = 'player-slot';

            if (i < players.length) {
                const player = players[i];
                slot.classList.add('filled');
                slot.innerHTML = `
                    ${player}
                    ${player === host ? '<span class="host-crown">👑</span>' : ''}
                `;
            } else {
                slot.classList.add('empty');
                slot.textContent = 'Waiting...';
            }

            playersGrid.appendChild(slot);
        }
    }

    sendChatMessage() {
        const chatInput = document.getElementById('chat-input');
        const message = chatInput.value.trim();

        if (message && this.stompClient && this.currentRoom) {
            this.stompClient.send(`/app/room/${this.currentRoom}/chat`, {}, 
                JSON.stringify({
                    player: this.currentPlayer,
                    message: message
                })
            );
            chatInput.value = '';
        }
    }

    addChatMessage(chatMessage) {
        const chatMessages = document.getElementById('chat-messages');
        if (!chatMessages) return;

        const messageElement = document.createElement('div');
        messageElement.className = 'chat-message';
        messageElement.innerHTML = `
            <strong>${chatMessage.player}:</strong> ${chatMessage.message}
        `;

        chatMessages.appendChild(messageElement);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    copyRoomCode() {
        const roomCode = document.getElementById('room-code').textContent;
        const url = `${window.location.origin}?room=${roomCode}`;
        
        navigator.clipboard.writeText(url).then(() => {
            alert('Room link copied to clipboard!');
        }).catch(() => {
            // Fallback for older browsers
            const textArea = document.createElement('textarea');
            textArea.value = url;
            document.body.appendChild(textArea);
            textArea.select();
            document.execCommand('copy');
            document.body.removeChild(textArea);
            alert('Room link copied to clipboard!');
        });
    }

    toggleChat() {
        const chatSection = document.getElementById('chat-section');
        chatSection.classList.toggle('open');
    }

    async startGame() {
        if (!this.isHost || !this.currentRoom) return;

        try {
            const response = await fetch(`/api/rooms/${this.currentRoom}/start`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ hostName: this.currentPlayer })
            });

            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.error || 'Failed to start game');
            }
        } catch (error) {
            console.error('Error starting game:', error);
            alert('Failed to start game: ' + error.message);
        }
    }

    startMultiplayerGame(gameData) {
        // Hide room lobby and start the actual game
        alert('Game starting! Word: ' + gameData.word);
        
        // Hide room lobby
        document.getElementById('roomLobby').style.display = 'none';
        
        // Show game area
        const gameArea = document.getElementById('gameArea');
        if (gameArea) {
            gameArea.style.display = 'block';
        }
        
        // Initialize game with the target word
        if (typeof initializeWordleGame === 'function') {
            initializeWordleGame(gameData.word);
        } else {
            console.log('Starting multiplayer game with word:', gameData.word);
            // Basic game initialization
            const gameStatus = document.getElementById('gameStatus');
            if (gameStatus) {
                gameStatus.textContent = 'Multiplayer game started! Target word has ' + gameData.word.length + ' letters';
            }
        }
        
        // Store game state
        this.currentWord = gameData.word;
        this.gameStarted = true;
    }

    async leaveRoom() {
        if (!this.currentRoom || !this.currentPlayer) return;

        try {
            await fetch(`/api/rooms/${this.currentRoom}/leave`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ playerName: this.currentPlayer })
            });

            // Reset state
            this.currentRoom = null;
            this.currentPlayer = null;
            this.isHost = false;

            // Remove room lobby and show login area
            const roomContainer = document.getElementById('roomLobbyContainer');
            if (roomContainer) {
                roomContainer.remove();
            }
            document.getElementById('loginArea').style.display = 'block';
        } catch (error) {
            console.error('Error leaving room:', error);
        }
    }
}

// Global room manager instance
let roomManager = new RoomManager();

// Check for room code in URL on page load
window.addEventListener('load', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const roomCode = urlParams.get('room');
    
    if (roomCode) {
        // Show join room dialog
        const playerName = prompt('Enter your name to join the room:');
        if (playerName) {
            roomManager.joinRoom(roomCode, playerName);
        }
    }
});

// Export for use in other scripts
window.RoomManager = RoomManager;
window.roomManager = roomManager;
