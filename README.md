# Game Ô Chữ Nhiều Người Chơi (Multiplayer Wordle)

## Mô tả
Game ô chữ nhiều người chơi với tính năng:
- Server cung cấp từ điển các từ vựng
- Người chơi điền từ vào bảng ô chữ từ các chữ cái có sẵn
- Hiển thị số ô điền đúng của mình và đối thủ theo thời gian thực
- Thời gian chơi giới hạn (5 phút mỗi game)

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
