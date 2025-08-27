# Hướng dẫn setup IDE cho Game Ô Chữ Multiplayer

## 1. IntelliJ IDEA

### Import Project:
1. Mở IntelliJ IDEA
2. File → Open → Chọn thư mục `backend`
3. IntelliJ sẽ tự động nhận diện Maven project
4. Chờ IDE download dependencies

### Cấu hình Run Configuration:
1. Run → Edit Configurations
2. Add New → Application
3. Main class: `com.wordle.game.WordleGameApplication`
4. VM Options (optional): `-Dspring.profiles.active=dev`
5. Working directory: `$MODULE_WORKING_DIR$`

### Chạy Application:
- Click nút Run hoặc Shift+F10
- Server sẽ chạy tại http://localhost:8080

## 2. Eclipse

### Import Project:
1. File → Import → Existing Maven Projects
2. Browse đến thư mục `backend`
3. Select `pom.xml`
4. Finish

### Cấu hình Run Configuration:
1. Right-click project → Run As → Java Application
2. Search for `WordleGameApplication`
3. Run

## 3. Visual Studio Code

### Cài đặt Extensions cần thiết:
- Extension Pack for Java
- Spring Boot Extension Pack

### Mở Project:
1. File → Open Folder → Chọn thư mục `backend`
2. VS Code sẽ tự động setup Java project

### Chạy Application:
1. Mở file `WordleGameApplication.java`
2. Click "Run" trên đầu main method
3. Hoặc sử dụng Command Palette: `Java: Run`

## 4. Troubleshooting

### Lỗi Java Version:
- Đảm bảo sử dụng Java 17 hoặc cao hơn
- Cấu hình JAVA_HOME trong system environment

### Lỗi Maven Dependencies:
```bash
mvn clean install
```

### Lỗi Port đã được sử dụng:
- Thay đổi port trong `application.properties`:
```properties
server.port=8081
```

### Lỗi WebSocket Connection:
- Kiểm tra firewall settings
- Đảm bảo frontend kết nối đúng port

## 5. Testing

### Chạy Unit Tests:
```bash
mvn test
```

### Test WebSocket Connection:
1. Chạy backend server
2. Mở `frontend/index.html` trong browser
3. Tạo game mới
4. Mở tab browser khác và join game

### Test REST API:
```bash
# Health check
curl http://localhost:8080/api/health

# Get waiting games
curl http://localhost:8080/api/games/waiting
```

## 6. Debugging

### Enable Debug Mode:
- Add VM option: `-Dlogging.level.com.wordle.game=DEBUG`
- Hoặc set profile: `-Dspring.profiles.active=dev`

### WebSocket Debug:
- Check browser console for WebSocket messages
- Monitor network tab for connection status

### Common Issues:
1. **CORS Error**: Đảm bảo frontend URL trong allowed origins
2. **Connection Failed**: Kiểm tra server đang chạy và port đúng
3. **Game Not Starting**: Cần đủ 2 người chơi để bắt đầu
