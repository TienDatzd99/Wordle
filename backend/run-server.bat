@echo off
echo ====================================
echo  Game O Chu Multiplayer - Backend
echo ====================================

REM Kiểm tra Java
java -version
if %errorlevel% neq 0 (
    echo Java không được cài đặt hoặc không có trong PATH
    echo Vui lòng cài đặt Java 17 hoặc cao hơn
    pause
    exit /b 1
)

echo.
echo Đang compile và chạy server với Maven Wrapper...
echo Server sẽ chạy tại: http://localhost:8080
echo Lần đầu chạy sẽ tải Maven và dependencies, có thể mất vài phút...
echo.

REM Sử dụng Maven Wrapper để tự động tải Maven nếu chưa có
mvnw.cmd spring-boot:run

pause
