# 🌐 NETWORK PROGRAMMING CONCEPTS - WORDLE MULTIPLAYER

## 📚 Kiến Thức Lập Trình Mạng Đã Áp Dụng

### 1. **TCP/IP Socket Programming** 
Triển khai server TCP sử dụng Java NIO để xử lý kết nối real-time giữa client và server.

**Code Implementation:**
```java
// TcpGameServer.java - TCP Server với NIO
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.configureBlocking(false);
serverChannel.bind(new InetSocketAddress(TCP_PORT));

Selector selector = Selector.open();
serverChannel.register(selector, SelectionKey.OP_ACCEPT);
```

**Khái niệm áp dụng:**
- **Connection-oriented protocol**: TCP đảm bảo reliable delivery
- **Non-blocking I/O (NIO)**: Sử dụng Selector pattern để xử lý multiple connections
- **Socket Programming**: Accept, Read, Write operations
- **Connection Management**: Quản lý lifecycle của player connections

---

### 2. **UDP Connectionless Communication**
Triển khai UDP server cho statistics và leaderboard với tốc độ cao.

**Code Implementation:**
```java
// UdpStatsServer.java - UDP Server
DatagramChannel udpChannel = DatagramChannel.open();
udpChannel.configureBlocking(false);
udpChannel.bind(new InetSocketAddress(UDP_PORT));

ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
SocketAddress clientAddress = udpChannel.receive(buffer);
```

**Khái niệm áp dụng:**
- **Connectionless protocol**: UDP cho fast, fire-and-forget communication
- **Datagram communication**: Gửi/nhận messages độc lập
- **Best-effort delivery**: Không đảm bảo delivery nhưng tốc độ cao
- **Stateless communication**: Mỗi message độc lập

---

### 3. **Multi-Protocol Architecture**
Kết hợp 3 protocols khác nhau cho các mục đích khác nhau.

**Architecture:**
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   WebSocket     │    │    TCP Socket    │    │   UDP Server    │
│   Port 8080     │    │    Port 8081     │    │   Port 8082     │
├─────────────────┤    ├──────────────────┤    ├─────────────────┤
│ Web Real-time   │    │ Direct Game      │    │ Fast Stats &    │
│ Communication   │    │ Communication    │    │ Leaderboard     │
│ STOMP/WebSocket │    │ Custom Protocol  │    │ JSON Messages   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

---

### 4. **Message Protocol Design**
Thiết kế custom protocols cho mỗi loại communication.

**TCP Game Messages:**
```java
public class GameMessage {
    private String type;        // "JOIN_ROOM", "GAME_MOVE", "CHAT_MESSAGE"
    private String playerId;
    private String roomId;
    private Object data;
    private long timestamp;
}
```

**UDP Stats Messages:**
```java
public class StatsMessage {
    private String type;        // "GAME_COMPLETED", "GET_LEADERBOARD"
    private String playerId;
    private Map<String, Object> data;
    private long timestamp;
}
```

---

### 5. **Concurrent Programming & Threading**
Sử dụng multi-threading để handle multiple clients và operations.

**Implementation:**
```java
// Thread Pool cho concurrent processing
private ExecutorService threadPool = Executors.newFixedThreadPool(10);

// NIO Event Loop trong main thread
while (isRunning) {
    int readyChannels = selector.select(1000);
    // Process each ready channel
    handleAccept(), handleRead(), handleWrite()
}

// Background processing trong separate threads
threadPool.submit(() -> processGameMessage(connection, message));
```

**Khái niệm:**
- **Thread Pools**: Quản lý tài nguyên thread hiệu quả
- **Non-blocking I/O**: Tránh blocking main thread
- **Concurrent Collections**: ConcurrentHashMap cho thread-safe operations
- **Synchronization**: Đảm bảo data consistency

---

### 6. **Client-Server Architecture Pattern**
Thiết kế kiến trúc phân tán với multiple clients kết nối tới central server.

**Server Components:**
- **Game State Management**: Quản lý trạng thái game rooms
- **Player Connection Management**: Tracking active connections
- **Message Broadcasting**: Gửi updates tới multiple clients
- **Session Management**: Maintain player sessions

**Client Components:**
- **Network Client Manager**: Quản lý multiple protocol connections
- **Message Handling**: Process incoming server messages
- **State Synchronization**: Sync game state với server

---

### 7. **Network Performance Monitoring**
Triển khai system monitoring và performance tracking.

**Monitoring Features:**
```java
// Network Health Monitoring
scheduler.scheduleAtFixedRate(this::monitorConnections, 30, 30, TimeUnit.SECONDS);

// Statistics Logging
Map<String, Object> stats = new HashMap<>();
stats.put("connectedPlayers", playerConnections.size());
stats.put("activeRooms", gameRooms.size());
stats.put("serverPort", TCP_PORT);
```

**Metrics Tracked:**
- **Connection Count**: Number của active connections
- **Response Time**: Latency measurements
- **Throughput**: Messages per second
- **Error Rates**: Connection failures, timeouts

---

### 8. **Protocol-Specific Use Cases**

#### **WebSocket (Port 8080)**
- **Purpose**: Web browser compatibility
- **Use Case**: Frontend-backend communication
- **Features**: Real-time bidirectional communication
- **Protocol**: STOMP over WebSocket

#### **TCP Socket (Port 8081)**
- **Purpose**: Reliable game state synchronization
- **Use Case**: Critical game moves, room management
- **Features**: Guaranteed delivery, ordered packets
- **Protocol**: Custom JSON-based protocol

#### **UDP Socket (Port 8082)**
- **Purpose**: Fast statistics and leaderboard updates
- **Use Case**: Real-time stats, performance metrics
- **Features**: Low latency, high throughput
- **Protocol**: Lightweight JSON messages

---

### 9. **Network Programming Best Practices**

#### **Error Handling**
```java
try {
    // Network operations
} catch (IOException e) {
    logger.error("Network error", e);
    handleClientDisconnect(clientChannel);
}
```

#### **Resource Management**
```java
@PreDestroy
public void cleanup() {
    // Close all connections
    // Shutdown thread pools
    // Release resources
}
```

#### **Security Considerations**
- Input validation cho all messages
- Rate limiting để prevent spam
- Connection limits để prevent DoS

---

### 10. **Testing & Debugging**

#### **Network Testing Tools**
- **Telnet**: Test TCP connections
- **Netcat**: UDP message testing
- **Wireshark**: Packet analysis
- **JConsole**: JVM monitoring

#### **Debug Commands**
```bash
# Check ports
netstat -ano | findstr :8080
netstat -ano | findstr :8081
netstat -ano | findstr :8082

# Test TCP connection
telnet localhost 8081

# Monitor network traffic
netstat -s
```

---

## 🎯 Kết Luận

Game Wordle này đã thành công áp dụng **toàn bộ spectrum** của Network Programming concepts:

1. ✅ **Socket Programming** (TCP & UDP)
2. ✅ **Multi-threading & Concurrency**
3. ✅ **Protocol Design & Implementation**
4. ✅ **Client-Server Architecture**
5. ✅ **Network Performance Monitoring**
6. ✅ **Real-time Communication**
7. ✅ **Distributed System Design**
8. ✅ **Network Security Practices**

Đây là một **production-ready** multiplayer game với architecture chuyên nghiệp, hoàn toàn phù hợp cho **bài tập lớn môn Lập trình mạng** ở trình độ đại học.

---

## 📖 Tài Liệu Tham Khảo

- Java NIO Documentation
- TCP/IP Protocol Suite
- UDP Protocol Specification
- WebSocket RFC 6455
- Spring Boot Network Programming
- Concurrent Programming in Java
