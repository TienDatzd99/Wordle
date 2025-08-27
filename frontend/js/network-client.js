/**
 * Network Client Manager - Tích hợp TCP/UDP với WebSocket
 * Áp dụng kiến thức: Client-side Network Programming, Multi-Protocol Communication
 */
class NetworkClientManager {
    constructor() {
        this.websocket = null;
        this.tcpSocket = null;
        this.udpClient = null;
        this.isConnected = false;
        this.playerStats = {};
        this.networkStats = {};
        
        this.initializeNetworkClients();
    }
    
    /**
     * Initialize all network clients
     */
    async initializeNetworkClients() {
        try {
            // Initialize WebSocket connection (primary)
            await this.initWebSocket();
            
            // Initialize TCP Socket connection for real-time communication
            await this.initTcpConnection();
            
            // Initialize UDP client for statistics
            await this.initUdpClient();
            
            console.log('🌐 All network protocols initialized successfully');
            
        } catch (error) {
            console.error('Failed to initialize network clients:', error);
        }
    }
    
    /**
     * Initialize WebSocket connection (existing)
     */
    async initWebSocket() {
        return new Promise((resolve, reject) => {
            try {
                const socket = new SockJS('/ws');
                this.websocket = Stomp.over(socket);
                
                this.websocket.connect({}, (frame) => {
                    console.log('✓ WebSocket connected:', frame);
                    resolve();
                }, (error) => {
                    console.error('WebSocket connection failed:', error);
                    reject(error);
                });
                
            } catch (error) {
                reject(error);
            }
        });
    }
    
    /**
     * Initialize TCP Socket connection using WebSocket as proxy
     */
    async initTcpConnection() {
        // Since browsers can't directly use TCP sockets, we'll create a WebSocket proxy
        try {
            this.tcpSocket = {
                connected: false,
                messageQueue: [],
                
                send: (message) => {
                    const tcpMessage = {
                        type: 'TCP_PROXY',
                        destination: 'tcp://localhost:8081',
                        payload: message
                    };
                    
                    if (this.websocket && this.websocket.connected) {
                        this.websocket.send('/app/tcp-proxy', {}, JSON.stringify(tcpMessage));
                    }
                },
                
                onMessage: (callback) => {
                    // Subscribe to TCP proxy responses
                    if (this.websocket) {
                        this.websocket.subscribe('/topic/tcp-responses', (response) => {
                            const data = JSON.parse(response.body);
                            callback(data);
                        });
                    }
                }
            };
            
            this.tcpSocket.connected = true;
            console.log('✓ TCP Socket proxy initialized');
            
        } catch (error) {
            console.error('TCP connection failed:', error);
        }
    }
    
    /**
     * Initialize UDP client for statistics
     */
    async initUdpClient() {
        try {
            this.udpClient = {
                connected: false,
                
                send: async (statsMessage) => {
                    try {
                        // Use fetch API to send UDP messages via HTTP proxy
                        const response = await fetch('/api/network/udp-proxy', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify({
                                destination: 'udp://localhost:8082',
                                message: statsMessage
                            })
                        });
                        
                        if (response.ok) {
                            return await response.json();
                        }
                        
                    } catch (error) {
                        console.error('UDP send failed:', error);
                    }
                },
                
                getLeaderboard: async () => {
                    const statsMessage = {
                        type: 'GET_LEADERBOARD',
                        playerId: currentPlayer,
                        timestamp: Date.now()
                    };
                    
                    return await this.udpClient.send(statsMessage);
                },
                
                getPlayerStats: async (playerId) => {
                    const statsMessage = {
                        type: 'GET_PLAYER_STATS',
                        playerId: playerId,
                        timestamp: Date.now()
                    };
                    
                    return await this.udpClient.send(statsMessage);
                },
                
                sendGameStats: async (gameData) => {
                    const statsMessage = {
                        type: 'GAME_COMPLETED',
                        playerId: currentPlayer,
                        roomId: currentRoom,
                        data: gameData,
                        timestamp: Date.now()
                    };
                    
                    return await this.udpClient.send(statsMessage);
                },
                
                sendMoveStats: async (word, colors) => {
                    const statsMessage = {
                        type: 'PLAYER_MOVE',
                        playerId: currentPlayer,
                        roomId: currentRoom,
                        data: { word, colors },
                        timestamp: Date.now()
                    };
                    
                    return await this.udpClient.send(statsMessage);
                }
            };
            
            this.udpClient.connected = true;
            console.log('✓ UDP Client proxy initialized');
            
        } catch (error) {
            console.error('UDP client initialization failed:', error);
        }
    }
    
    /**
     * Send game move via TCP
     */
    sendGameMove(word, colors) {
        if (this.tcpSocket && this.tcpSocket.connected) {
            const moveMessage = {
                type: 'GAME_MOVE',
                playerId: currentPlayer,
                roomId: currentRoom,
                data: { word, colors },
                timestamp: Date.now()
            };
            
            this.tcpSocket.send(moveMessage);
        }
        
        // Also send stats via UDP
        if (this.udpClient && this.udpClient.connected) {
            this.udpClient.sendMoveStats(word, colors);
        }
    }
    
    /**
     * Join game room via TCP
     */
    joinGameRoom(roomId, playerId) {
        if (this.tcpSocket && this.tcpSocket.connected) {
            const joinMessage = {
                type: 'JOIN_ROOM',
                playerId: playerId,
                roomId: roomId,
                timestamp: Date.now()
            };
            
            this.tcpSocket.send(joinMessage);
        }
    }
    
    /**
     * Send heartbeat to maintain connection
     */
    sendHeartbeat() {
        if (this.tcpSocket && this.tcpSocket.connected) {
            const heartbeatMessage = {
                type: 'HEARTBEAT',
                playerId: currentPlayer,
                timestamp: Date.now()
            };
            
            this.tcpSocket.send(heartbeatMessage);
        }
    }
    
    /**
     * Get comprehensive network statistics
     */
    async getNetworkStats() {
        try {
            const response = await fetch('/api/network/status');
            if (response.ok) {
                this.networkStats = await response.json();
                return this.networkStats;
            }
        } catch (error) {
            console.error('Failed to get network stats:', error);
        }
        return {};
    }
    
    /**
     * Display network architecture information
     */
    async showNetworkArchitecture() {
        try {
            const response = await fetch('/api/network/architecture');
            if (response.ok) {
                const architecture = await response.json();
                this.displayArchitectureModal(architecture);
            }
        } catch (error) {
            console.error('Failed to get network architecture:', error);
        }
    }
    
    /**
     * Display architecture modal
     */
    displayArchitectureModal(architecture) {
        const modal = document.createElement('div');
        modal.className = 'network-modal';
        modal.innerHTML = `
            <div class="network-modal-content">
                <div class="network-modal-header">
                    <h2>${architecture.title}</h2>
                    <span class="close-modal">&times;</span>
                </div>
                <div class="network-modal-body">
                    <div class="protocol-section">
                        <h3>🌐 Network Protocols</h3>
                        ${Object.entries(architecture.protocols).map(([key, protocol]) => `
                            <div class="protocol-card">
                                <h4>${protocol.protocol} (Port ${protocol.port})</h4>
                                <p><strong>Purpose:</strong> ${protocol.purpose}</p>
                                <p><strong>Features:</strong> ${protocol.features.join(', ')}</p>
                                <p><strong>Concepts:</strong> ${protocol.concepts.join(', ')}</p>
                            </div>
                        `).join('')}
                    </div>
                    
                    <div class="concepts-section">
                        <h3>📚 Network Programming Concepts</h3>
                        <div class="concepts-grid">
                            ${architecture.networkConcepts.map(concept => `
                                <div class="concept-tag">${concept}</div>
                            `).join('')}
                        </div>
                    </div>
                    
                    <div class="tech-section">
                        <h3>🛠️ Technologies Used</h3>
                        ${Object.entries(architecture.technologies).map(([category, techs]) => `
                            <div class="tech-category">
                                <h4>${category.charAt(0).toUpperCase() + category.slice(1)}</h4>
                                <p>${techs.join(', ')}</p>
                            </div>
                        `).join('')}
                    </div>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // Close modal handlers
        modal.querySelector('.close-modal').onclick = () => modal.remove();
        modal.onclick = (e) => {
            if (e.target === modal) modal.remove();
        };
    }
    
    /**
     * Start heartbeat interval
     */
    startHeartbeat() {
        setInterval(() => {
            this.sendHeartbeat();
        }, 30000); // Every 30 seconds
    }
    
    /**
     * Disconnect all network clients
     */
    disconnect() {
        try {
            if (this.websocket && this.websocket.connected) {
                this.websocket.disconnect();
            }
            
            if (this.tcpSocket) {
                this.tcpSocket.connected = false;
            }
            
            if (this.udpClient) {
                this.udpClient.connected = false;
            }
            
            this.isConnected = false;
            console.log('🔌 All network connections disconnected');
            
        } catch (error) {
            console.error('Error disconnecting:', error);
        }
    }
}

// Global network client instance
let networkClient = null;

// Initialize network client when page loads
document.addEventListener('DOMContentLoaded', () => {
    networkClient = new NetworkClientManager();
    
    // Start heartbeat
    setTimeout(() => {
        if (networkClient) {
            networkClient.startHeartbeat();
        }
    }, 5000);
});

// Integrate with existing game functions
function integrateNetworkWithGame() {
    // Override submitWord to use network client
    const originalSubmitWord = window.submitWord;
    window.submitWord = function() {
        const result = originalSubmitWord.apply(this, arguments);
        
        // Send move via TCP and UDP
        if (networkClient && currentGuess.length === 5) {
            const colors = getCurrentRowColors();
            networkClient.sendGameMove(currentGuess, colors);
        }
        
        return result;
    };
    
    // Override joinRoom to use TCP
    const originalJoinRoom = window.joinRoom || function() {};
    window.joinRoom = function(roomId) {
        const result = originalJoinRoom.apply(this, arguments);
        
        if (networkClient) {
            networkClient.joinGameRoom(roomId, currentPlayer);
        }
        
        return result;
    };
}

// Get current row colors helper
function getCurrentRowColors() {
    const currentRowElement = document.querySelector(`.grid-row:nth-child(${currentRow + 1})`);
    if (!currentRowElement) return [];
    
    const tiles = currentRowElement.querySelectorAll('.grid-tile');
    return Array.from(tiles).map(tile => {
        if (tile.classList.contains('correct')) return 'green';
        if (tile.classList.contains('present')) return 'yellow';
        if (tile.classList.contains('absent')) return 'gray';
        return 'empty';
    });
}

// Initialize integration when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    setTimeout(integrateNetworkWithGame, 1000);
});
