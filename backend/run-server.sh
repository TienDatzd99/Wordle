#!/bin/bash

echo "===================================="
echo "  Game O Chu Multiplayer - Backend"
echo "===================================="

# Kiểm tra Java
if ! command -v java &> /dev/null; then
    echo "Java không được cài đặt hoặc không có trong PATH"
    echo "Vui lòng cài đặt Java 17 hoặc cao hơn"
    exit 1
fi

echo "Java version:"
java -version

# Kiểm tra Maven
if ! command -v mvn &> /dev/null; then
    echo "Maven không được cài đặt hoặc không có trong PATH"
    echo "Vui lòng cài đặt Maven từ https://maven.apache.org/download.cgi"
    echo "Hoặc sử dụng IDE như IntelliJ IDEA/Eclipse để chạy"
    exit 1
fi

echo "Maven version:"
mvn -version

echo ""
echo "Đang compile và chạy server..."
echo "Server sẽ chạy tại: http://localhost:8080"
echo ""

mvn spring-boot:run
