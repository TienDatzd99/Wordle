# 🌍 CLOUD DEPLOYMENT - WORDLE MULTIPLAYER

## 🚀 Deploy Options for Global Access

### 1. **Heroku Deployment** (Free Tier)

```bash
# Install Heroku CLI
# Create Procfile
echo "web: java -jar target/wordle-game-0.0.1-SNAPSHOT.jar --server.port=$PORT" > Procfile

# Deploy
heroku create your-wordle-game
git add .
git commit -m "Deploy multiplayer Wordle"
git push heroku main
```

### 2. **Railway Deployment** (Modern, Easy)

```bash
# Install Railway CLI
npm install -g @railway/cli

# Deploy
railway login
railway init
railway up
```

### 3. **Google Cloud Run** (Scalable)

```dockerfile
# Dockerfile
FROM openjdk:21-jre-slim
COPY target/wordle-game-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080 8081 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 4. **AWS EC2** (Full Control)

```bash
# User data script for EC2
#!/bin/bash
yum update -y
yum install -y java-21-openjdk
# Upload and run your jar file
```

### 5. **Ngrok (Instant)**
- Free tier: 1 concurrent tunnel
- Pro tier: Multiple tunnels, custom domains
- Perfect for testing and demos

## 🔧 Configuration for Cloud

### Application Properties for Cloud:
```properties
# application-cloud.properties
server.port=${PORT:8080}
server.address=0.0.0.0

# Enable all IPs
spring.websocket.allowed-origins=*
```

### Environment Variables:
```bash
export PORT=8080
export JAVA_OPTS="-Xmx512m"
export SPRING_PROFILES_ACTIVE=cloud
```

## 📊 Performance Considerations

### For Internet Play:
- **WebSocket**: Primary for web clients
- **TCP Socket**: May need proxy for browser clients
- **UDP**: Best for statistics, works well over internet

### Optimization:
- Enable compression
- Use CDN for static files
- Implement connection pooling
- Add rate limiting

## 🛡️ Security for Public Access

```java
// Add CORS configuration
@CrossOrigin(origins = "*", maxAge = 3600)

// Rate limiting
@Component
public class RateLimitingFilter implements Filter {
    // Implement rate limiting logic
}

// Input validation
public boolean isValidInput(String input) {
    return input.matches("^[A-Z]{5}$");
}
```

## 🎯 Recommended Setup for Friends

1. **Quick Demo**: Use Ngrok
2. **Regular Play**: Deploy to Railway/Heroku
3. **Serious Gaming**: AWS/GCP with custom domain
4. **Local Events**: LAN setup with router forwarding
