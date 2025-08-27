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
echo Maven không có sẵn. Sử dụng một trong các cách sau:
echo.
echo 1. Cài đặt Maven:
echo    - Tải về từ: https://maven.apache.org/download.cgi
echo    - Giải nén và thêm vào PATH
echo    - Sau đó chạy: mvn spring-boot:run
echo.
echo 2. Sử dụng IDE:
echo    - IntelliJ IDEA: Import Maven project và chạy WordleGameApplication
echo    - Eclipse: Import Existing Maven Projects
echo    - VS Code: Mở thư mục và chạy Java Application
echo.
echo 3. Tải Maven Wrapper:
echo    Chạy lệnh sau trong PowerShell:
echo    Invoke-WebRequest -Uri https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar -OutFile .mvn/wrapper/maven-wrapper.jar
echo.

pause
