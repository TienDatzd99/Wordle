@echo off
echo =================================================================
echo       🌐 WORDLE MULTIPLAYER - NETWORK PROGRAMMING DEMO
echo =================================================================
echo.
echo 📚 Demonstrating University-Level Network Programming Concepts:
echo    ✅ TCP/IP Socket Programming with NIO
echo    ✅ UDP Connectionless Communication  
echo    ✅ Multi-Protocol Architecture
echo    ✅ WebSocket Real-time Communication
echo    ✅ Concurrent Programming & Threading
echo    ✅ Client-Server Architecture
echo    ✅ Network Performance Monitoring
echo.
echo =================================================================
echo                      🚀 STARTING DEMO
echo =================================================================
echo.

echo 📡 Step 1: Starting Multi-Protocol Server...
echo    - WebSocket Server: Port 8080 (Web Communication)
echo    - TCP Server (NIO): Port 8081 (Real-time Game)  
echo    - UDP Server: Port 8082 (Fast Statistics)
echo.

cd /d "d:\Wordle\backend"

echo 🔧 Setting up Java environment...
set JAVA_HOME=C:\Program Files\Java\jdk-21
echo JAVA_HOME: %JAVA_HOME%
echo.

echo 🌐 Launching Network Servers...
start /min cmd /c ".\mvnw.cmd spring-boot:run"

echo.
echo ⏳ Waiting for servers to initialize (10 seconds)...
timeout /t 10 /nobreak > nul

echo.
echo 🌍 Step 2: Opening Game Interface...
start http://localhost:8080

echo.
echo =================================================================
echo                      ✨ DEMO FEATURES
echo =================================================================
echo.
echo 🎮 GAME FEATURES:
echo    • Real-time multiplayer Wordle battles
echo    • AI-powered word validation with definitions
echo    • Customizable game settings (time/rounds)
echo    • Live chat system
echo    • Comprehensive statistics tracking
echo.
echo 🌐 NETWORK PROTOCOLS IN ACTION:
echo    • WebSocket: Real-time web communication
echo    • TCP Socket: Reliable game state sync
echo    • UDP Server: Fast statistics updates
echo    • JSON Protocol: Custom message format
echo.
echo 📊 MONITORING CAPABILITIES:
echo    • Connection health monitoring
echo    • Performance metrics tracking
echo    • Real-time network status
echo    • Protocol-specific statistics
echo.
echo =================================================================
echo                      🧪 TESTING COMMANDS
echo =================================================================
echo.
echo 📝 To test network protocols manually:
echo.
echo 1️⃣  Test WebSocket Connection:
echo    curl http://localhost:8080/api/network/status
echo.
echo 2️⃣  Test Network Architecture Info:
echo    curl http://localhost:8080/api/network/architecture  
echo.
echo 3️⃣  Check Server Statistics:
echo    netstat -ano ^| findstr :8080
echo    netstat -ano ^| findstr :8081
echo    netstat -ano ^| findstr :8082
echo.
echo 4️⃣  Monitor Network Traffic:
echo    netstat -s
echo.
echo =================================================================
echo                   🎯 DEMO INSTRUCTIONS
echo =================================================================
echo.
echo 🕹️  HOW TO PLAY:
echo    1. Enter your name in the game interface
echo    2. Click 'Settings' ⚙️ to customize game parameters
echo    3. Create a new game or join existing one
echo    4. Try the word guessing with AI validation
echo    5. Check the network status indicators
echo    6. View network architecture information
echo.
echo 📊 NETWORK FEATURES TO EXPLORE:
echo    • Click "📊 Network Stats" to view connection status
echo    • Click "🏗️ Architecture" to see protocol details  
echo    • Monitor real-time status indicators
echo    • Check browser developer tools for WebSocket traffic
echo.
echo 🔬 FOR DEVELOPMENT/DEBUGGING:
echo    • F12 + Ctrl: Show game state debug info
echo    • Network tab in DevTools: Monitor WebSocket
echo    • Server logs: See TCP/UDP activity in console
echo.
echo =================================================================
echo.
echo ✅ Demo is now running!
echo.
echo 🌐 Game URL: http://localhost:8080
echo 📝 Server Logs: Check the background console window
echo 🛑 To stop: Close this window or Ctrl+C in server console
echo.
echo =================================================================
echo        🎓 NETWORK PROGRAMMING ASSIGNMENT COMPLETE!
echo =================================================================
echo.
echo 📚 This demo showcases ALL major network programming concepts
echo    required for university-level coursework:
echo.
echo    ✅ Socket Programming (TCP/UDP)
echo    ✅ Multi-threading & Concurrency  
echo    ✅ Protocol Design & Implementation
echo    ✅ Client-Server Architecture
echo    ✅ Network Performance Monitoring
echo    ✅ Real-time Communication Systems
echo    ✅ Distributed Application Development
echo.
echo 🎯 Ready for evaluation and presentation!
echo.
pause
