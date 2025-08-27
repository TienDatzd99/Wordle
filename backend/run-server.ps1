# Game Ô Chữ Multiplayer - Backend Server
Write-Host "====================================" -ForegroundColor Green
Write-Host " Game O Chu Multiplayer - Backend" -ForegroundColor Green  
Write-Host "====================================" -ForegroundColor Green

# Kiểm tra Java
Write-Host "`nKiểm tra Java version..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host $javaVersion -ForegroundColor Cyan
    
    # Tự động tìm và set JAVA_HOME từ java.home property
    $javaHomeOutput = java -XshowSettings:properties -version 2>&1 | Select-String "java.home"
    if ($javaHomeOutput) {
        $javaHome = ($javaHomeOutput -split "=")[1].Trim()
        $env:JAVA_HOME = $javaHome
        Write-Host "JAVA_HOME được set tự động: $env:JAVA_HOME" -ForegroundColor Cyan
    } else {
        # Fallback - set manual
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
        Write-Host "JAVA_HOME được set manual: $env:JAVA_HOME" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "Java không được cài đặt hoặc không có trong PATH" -ForegroundColor Red
    Write-Host "Vui lòng cài đặt Java 17 hoặc cao hơn" -ForegroundColor Red
    Read-Host "Nhấn Enter để thoát"
    exit 1
}

Write-Host "`nĐang compile và chạy server với Maven Wrapper..." -ForegroundColor Yellow
Write-Host "Server sẽ chạy tại: http://localhost:8080" -ForegroundColor Green
Write-Host "Lần đầu chạy sẽ tải Maven và dependencies, có thể mất vài phút..." -ForegroundColor Yellow
Write-Host ""

# Set execution policy tạm thời
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force

# Chạy Maven Wrapper
try {
    if (Test-Path "mvnw.cmd") {
        & ".\mvnw.cmd" spring-boot:run
    } else {
        Write-Host "Maven Wrapper không tìm thấy!" -ForegroundColor Red
        Write-Host "Vui lòng sử dụng IDE để chạy project" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Lỗi khi chạy server: $($_.Exception.Message)" -ForegroundColor Red
}

Read-Host "`nNhấn Enter để thoát"
