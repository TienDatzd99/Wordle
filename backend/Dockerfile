# Sử dụng Java 17 base image
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy file pom.xml và download dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Chạy application
CMD ["java", "-jar", "target/wordle-game-0.0.1-SNAPSHOT.jar"]
