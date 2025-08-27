# 🎯 HƯỚNG DẪN CHẠY NHANH - Game Ô Chữ Multiplayer

## ✅ Project đã hoàn thành và READY TO RUN!

### 🚀 Cách chạy:

#### **1. Chạy Backend Server:**
```powershell
cd "d:\Wordle\backend"
powershell -ExecutionPolicy Bypass -File "d:\Wordle\backend\run-server.ps1"
```

**Hoặc đơn giản hơn:**
```powershell
cd d:\Wordle\backend
.\mvnw.cmd spring-boot:run
```

✅ **Server sẽ chạy tại:** http://localhost:8080  
✅ **Lần đầu chạy sẽ tải Maven và dependencies (vài phút)**

#### **2. Chạy Frontend:**
- Mở file `d:\Wordle\frontend\index.html` trong trình duyệt
- Hoặc mở `d:\Wordle\frontend\demo-test.html` để test trước

---

### 🎮 Demo Game (2 người chơi):

#### **Người chơi 1:**
1. Mở `d:\Wordle\frontend\index.html`
2. Nhập tên: **"Player 1"**
3. Click **"Tạo Game Mới"**
4. Copy **Game ID** hiển thị

#### **Người chơi 2:**  
1. Mở tab browser mới với `d:\Wordle\frontend\index.html`
2. Nhập tên: **"Player 2"**
3. Click **"Tham Gia Game"**
4. Nhập **Game ID** từ Player 1
5. Click **"Tham Gia"**

#### **Chơi Game:**
- Game tự động bắt đầu khi có 2 người
- Thời gian: **5 phút**
- Nhập từ từ các chữ cái có sẵn
- VD: **HELLO, WORLD, APPLE, MUSIC, HEART, SMART**
- Điểm = độ dài từ
- Xem điểm đối thủ real-time

---

### 📋 Status hiện tại:

#### ✅ **Backend (Java Spring Boot):**
- [x] WebSocket real-time communication  
- [x] Game management (tạo/join game)
- [x] Dictionary service (Tiếng Việt + Tiếng Anh)
- [x] Player scoring system
- [x] Game state broadcast
- [x] Timer (5 phút/game)
- [x] Word validation
- [x] REST API endpoints

#### ✅ **Frontend (Web Client):**
- [x] Responsive UI
- [x] Real-time score updates
- [x] Game lobby
- [x] Letter grid display
- [x] Word input system
- [x] Timer display
- [x] Score tracking

#### ✅ **Deploy & Build:**
- [x] Maven Wrapper (không cần cài Maven)
- [x] PowerShell scripts
- [x] Auto JAVA_HOME setup
- [x] Dockerfile ready

---

### 🧪 Quick Test:

#### **Test Server:**
```powershell
curl http://localhost:8080/api/health
# Kết quả: "OK"
```

#### **Test Demo:**
- Mở `d:\Wordle\frontend\demo-test.html`
- Click các nút test để kiểm tra kết nối

---

### 🎯 **Tính năng nổi bật:**

1. **Real-time Multiplayer** - 2 người chơi cùng lúc
2. **Cross-platform** - Chạy trên Windows/Linux/Mac  
3. **No Installation** - Chỉ cần Java và browser
4. **Rich Dictionary** - Hỗ trợ tiếng Việt + tiếng Anh
5. **Live Scoring** - Xem điểm đối thủ ngay lập tức
6. **Timer System** - Đồng hồ đếm ngược 5 phút
7. **Auto Game Management** - Tự động bắt đầu khi đủ người

---

### 📚 **Tài liệu đầy đủ:**
- `README.md` - Hướng dẫn chi tiết
- `SETUP_IDE.md` - Setup với IDE
- `frontend/demo-test.html` - Tool test và demo

---

## 🏆 **READY FOR SUBMISSION!**

Project hoàn toàn sẵn sàng cho bài tập lớn Lập trình mạng!  
Có thể demo ngay cho giảng viên! 🎉
