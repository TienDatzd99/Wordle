# 🎯 WordleCup Multiplayer Game

A sophisticated multiplayer Wordle game built with Spring Boot and real-time WebSocket communication, featuring room management, AI-powered word validation, and multi-protocol networking architecture.

## 🌟 Features

### Core Game Features
- **Multiplayer Wordle Game**: Classic 5-letter word guessing with multiplayer support
- **Room Management System**: Create and join game rooms with custom room codes
- **Real-time Synchronization**: Live game updates via WebSocket/STOMP messaging
- **AI Word Validation**: Intelligent word validation with definitions and suggestions
- **Multiple Game Modes**: Single player and multiplayer modes
- **Responsive UI**: Modern web interface with smooth animations

### Technical Features
- **Multi-Protocol Architecture**: WebSocket (8080), TCP (8081), UDP (8082)
- **Spring Boot 3.1.0**: Modern Java framework with WebSocket support
- **Java 21**: Latest LTS Java version with enhanced performance
- **Enhanced Dictionary**: Comprehensive word database with AI validation
- **Network Monitoring**: Real-time network statistics and monitoring
- **Public Access**: Ngrok integration for external multiplayer access

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+ (included via wrapper)
- Modern web browser
- Internet connection (for AI features)

### Running the Game

1. **Clone the repository**:
   ```bash
   git clone https://github.com/TienDatzd99/Wordle.git
   cd Wordle
   ```

2. **Start the server**:
   ```bash
   # Windows
   START_SERVER.bat
   
   # Linux/Mac
   chmod +x backend/run-server.sh
   ./backend/run-server.sh
   ```

3. **Access the game**:
   - Local: http://localhost:8080
   - Public (via ngrok): Run `START_PUBLIC_MULTIPLAYER.bat`

## 🏗️ Architecture

### Backend Structure
```
backend/
├── src/main/java/com/wordle/game/
│   ├── controller/          # REST and WebSocket controllers
│   ├── service/            # Business logic and AI services
│   ├── model/              # Data models
│   ├── network/            # Multi-protocol networking
│   └── config/             # Configuration classes
└── src/main/resources/
    ├── static/             # Web assets
    └── application.properties
```

### Frontend Structure
```
frontend/
├── css/                    # Stylesheets
├── js/                     # JavaScript modules
│   ├── room-manager.js     # Room management logic
│   └── network-client.js   # Network communication
└── index.html              # Main game interface
```

### Network Architecture
- **Port 8080**: WebSocket server for web client communication
- **Port 8081**: TCP server for real-time game communication  
- **Port 8082**: UDP server for fast statistics and leaderboard

## 🎮 How to Play

### Single Player Mode
1. Enter your name
2. Click "Create New Game"
3. Start guessing 5-letter words
4. Try to find the target word in 6 attempts

### Multiplayer Mode
1. **Create Room**: Click "Create New Game" to host
2. **Join Room**: Click "Join Game" and enter room code
3. **Start Game**: Host clicks "Start Game" when ready
4. **Compete**: Race to find the word faster than opponents

### Game Controls
- **Keyboard**: Type letters directly
- **On-screen Keyboard**: Click letters on virtual keyboard
- **Enter**: Submit your guess
- **Backspace**: Delete last letter

## 🔧 Development

### Project Setup
```bash
# Backend development
cd backend
./mvnw spring-boot:run

# Frontend development (serve static files)
# Files are served from backend/src/main/resources/static/
```

### Configuration
- **Server Port**: `application.properties` → `server.port=8080`
- **AI Features**: Enable/disable in `AIWordValidationService`
- **Network Ports**: Configure in `NetworkManager`

### Building
```bash
# Build JAR file
cd backend
./mvnw clean package

# Run JAR
java -jar target/wordle-game-0.0.1-SNAPSHOT.jar
```

## 🌐 Network Programming Concepts

This project demonstrates various network programming concepts:

### Protocols Implemented
- **WebSocket**: Bidirectional real-time communication
- **TCP Sockets**: Reliable connection-oriented communication
- **UDP Sockets**: Fast connectionless communication
- **HTTP REST**: Traditional request-response API

### Concepts Covered
- Multi-threading and concurrent programming
- Protocol design and message serialization
- Client-server architecture patterns
- Network performance monitoring
- Real-time data synchronization

## 🤖 AI Integration

### Word Validation
- **Intelligent Validation**: AI-powered word checking
- **Definitions**: Get word meanings and usage
- **Suggestions**: Helpful word suggestions for invalid inputs
- **Caching**: Efficient caching for improved performance

### API Endpoints
- `GET /api/dictionary/validate-ai/{word}` - AI word validation
- `GET /api/dictionary/suggestions/{prefix}` - Word suggestions
- `POST /api/dictionary/cache/clear` - Clear AI cache

## 📊 Monitoring

### Network Statistics
- Real-time connection monitoring
- Protocol-specific metrics
- Performance analytics
- Connection health checks

### Game Analytics
- Player statistics
- Game completion rates
- Word difficulty metrics
- Performance monitoring

## 🔧 Troubleshooting

### Common Issues

1. **Port Already in Use**:
   ```bash
   # Kill existing Java processes
   taskkill /F /IM java.exe  # Windows
   pkill java                # Linux/Mac
   ```

2. **Network Connection Issues**:
   - Check firewall settings
   - Verify port availability
   - Test with `telnet localhost 8080`

3. **WebSocket Connection Failed**:
   - Ensure server is running
   - Check browser console for errors
   - Verify CORS configuration

## 🚀 Deployment

### Local Deployment
- Use `START_SERVER.bat` for local testing
- Access via `http://localhost:8080`

### Public Deployment
- Use `START_PUBLIC_MULTIPLAYER.bat` for public access
- Ngrok provides public URL for external players
- Share the generated URL with friends

### Cloud Deployment
- Docker support included (`Dockerfile`)
- Deploy to Heroku, AWS, or Google Cloud
- Configure environment variables for production

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Wordle game concept inspired by Josh Wardle's original game
- Spring Boot framework for robust backend development
- WebSocket technology for real-time communication
- AI services for enhanced word validation

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Email: tiendatzd99@gmail.com
- Check the [Wiki](https://github.com/TienDatzd99/Wordle/wiki) for detailed documentation

---

**Built with ❤️ using Spring Boot, WebSocket, and modern web technologies**

## Công nghệ sử dụng
- **Backend:** Java Spring Boot + WebSocket
- **Frontend:** HTML/CSS/JavaScript với SockJS + STOMP
- **Database:** In-memory (có thể mở rộng với MySQL/PostgreSQL)

## Cấu trúc thư mục
```
Wordle/
├── backend/                 # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/wordle/game/
│   │       ├── WordleGameApplication.java
│   │       ├── config/
│   │       │   └── WebSocketConfig.java
│   │       ├── controller/
│   │       │   ├── GameController.java
│   │       │   └── RestApiController.java
│   │       ├── model/
│   │       │   ├── Game.java
│   │       │   ├── Player.java
│   │       │   ├── GameState.java
│   │       │   ├── GameStatus.java
│   │       │   └── WordSubmission.java
│   │       └── service/
│   │           ├── DictionaryService.java
│   │           └── GameService.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
└── frontend/                # Web frontend
    └── index.html
```

## Cách chạy

### 1. Chạy Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```
Hoặc nếu chưa có Maven:
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Server sẽ chạy tại: http://localhost:8080

### 2. Chạy Frontend
Mở file `frontend/index.html` trong trình duyệt hoặc sử dụng Live Server.

## Cách chơi

### Tạo game mới:
1. Nhập tên của bạn
2. Nhấn "Tạo Game Mới"
3. Chia sẻ Game ID cho người chơi khác

### Tham gia game:
1. Nhập tên của bạn
2. Nhấn "Tham Gia Game"
3. Nhập Game ID
4. Nhấn "Tham Gia"

### Chơi game:
1. Game bắt đầu khi có đủ 2 người chơi
2. Nhìn vào bảng 5x5 chữ cái có sẵn
3. Nhập từ bạn tìm được từ các chữ cái đó
4. Nhấn "Gửi từ" hoặc Enter
5. Điểm của bạn tăng theo độ dài của từ
6. Theo dõi điểm số và số từ tìm được của đối thủ

## API Endpoints

### REST API:
- `GET /api/health` - Kiểm tra trạng thái server
- `GET /api/games/waiting` - Lấy danh sách game đang chờ
- `GET /api/game/{gameId}` - Lấy thông tin game

### WebSocket:
- `/ws` - Endpoint kết nối WebSocket
- `/app/game/create` - Tạo game mới
- `/app/game/join` - Tham gia game
- `/app/game/submit` - Gửi từ
- `/topic/game/{gameId}` - Nhận cập nhật trạng thái game

## Tính năng

### Đã hoàn thành:
✅ Tạo và tham gia game  
✅ Kết nối WebSocket nhiều người chơi  
✅ Kiểm tra từ hợp lệ từ từ điển  
✅ Cập nhật điểm số theo thời gian thực  
✅ Hiển thị trạng thái của cả hai người chơi  
✅ Đồng hồ đếm ngược thời gian  
✅ Giao diện web responsive  

### Có thể mở rộng:
🔄 Lưu trữ từ điển từ file hoặc database  
🔄 Thêm âm thanh và hiệu ứng  
🔄 Hỗ trợ nhiều hơn 2 người chơi  
🔄 Lưu lịch sử game và thống kê  
🔄 Chat trong game  
🔄 Xếp hạng người chơi  

## Troubleshooting

### Lỗi kết nối WebSocket:
- Kiểm tra server đang chạy tại port 8080
- Đảm bảo không có firewall chặn

### Lỗi Maven:
```bash
# Cài đặt dependencies
mvn clean install

# Chạy với profile dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Lỗi CORS:
- Server đã cấu hình CORS cho tất cả origins
- Nếu vẫn lỗi, thử chạy browser với `--disable-web-security`

## Tác giả
Dự án bài tập lớn Lập trình mạng - Game ô chữ nhiều người chơi
