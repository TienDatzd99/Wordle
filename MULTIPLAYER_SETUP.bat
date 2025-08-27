@echo off
echo =================================================================
echo       🔗 WORDLE MULTIPLAYER - NETWORK SETUP ASSISTANT
echo =================================================================
echo.

:MENU
echo 🌍 Choose your multiplayer setup:
echo.
echo 1. 🏠 Local Network (LAN/WiFi) - Friends in same network
echo 2. 🌐 Internet Public - Anyone worldwide via Ngrok
echo 3. 🔧 Advanced - Manual router configuration
echo 4. ❓ Help - Setup instructions
echo 5. 🚪 Exit
echo.
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto LOCAL_NETWORK
if "%choice%"=="2" goto PUBLIC_INTERNET
if "%choice%"=="3" goto ADVANCED_SETUP
if "%choice%"=="4" goto HELP
if "%choice%"=="5" goto EXIT
goto MENU

:LOCAL_NETWORK
echo.
echo =================================================================
echo                  🏠 LOCAL NETWORK SETUP
echo =================================================================
echo.
echo 📍 Getting your local IP address...
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr "IPv4"') do (
    set "ip=%%a"
    setlocal enabledelayedexpansion
    set "ip=!ip: =!"
    echo 🌐 Your IP Address: !ip!
    echo.
    echo 📤 SHARE WITH FRIENDS IN SAME NETWORK:
    echo    URL: http://!ip!:8080
    echo.
    echo 📱 Instructions for friends:
    echo    1. Connect to same WiFi/LAN network
    echo    2. Open browser and go to: http://!ip!:8080
    echo    3. Enter name and join your game!
    echo.
    goto CONTINUE
)

:PUBLIC_INTERNET
echo.
echo =================================================================
echo                🌍 PUBLIC INTERNET ACCESS
echo =================================================================
echo.
echo 🚀 Setting up Ngrok tunnel for worldwide access...
echo.

REM Check if ngrok exists
if not exist "ngrok.exe" (
    echo 📥 Downloading Ngrok...
    powershell -Command "try { Invoke-WebRequest -Uri 'https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-windows-amd64.zip' -OutFile 'ngrok.zip'; Expand-Archive -Path 'ngrok.zip' -DestinationPath '.'; Remove-Item 'ngrok.zip'; Write-Host '✅ Ngrok downloaded!' } catch { Write-Host '❌ Download failed. Please download manually from https://ngrok.com' }"
)

echo.
echo 🔐 Authentication Setup:
echo    1. Go to https://ngrok.com and create free account
echo    2. Copy your authtoken from dashboard
echo    3. Run: ngrok config add-authtoken YOUR_TOKEN
echo.
set /p token="Enter your Ngrok authtoken (or press Enter if already configured): "

if not "%token%"=="" (
    ngrok config add-authtoken %token%
    echo ✅ Auth token configured!
)

echo.
echo 🌐 Starting public tunnel...
start "Ngrok Tunnel" ngrok http 8080 --log=stdout

echo.
echo ⏳ Waiting for tunnel to establish...
timeout /t 8 /nobreak > nul

echo.
echo 🔍 Getting your public URL...
powershell -Command "$maxRetries = 10; $retryCount = 0; do { try { $response = Invoke-RestMethod -Uri 'http://localhost:4040/api/tunnels' -Method Get; if ($response.tunnels.Count -gt 0) { $url = $response.tunnels[0].public_url; Write-Host ''; Write-Host '🌍 PUBLIC URL: ' $url -ForegroundColor Green; Write-Host ''; Write-Host '📤 SHARE THIS URL WITH FRIENDS ANYWHERE:' -ForegroundColor Yellow; Write-Host '   ' $url -ForegroundColor Cyan; Write-Host ''; Write-Host '📱 Anyone can now access your game!' -ForegroundColor Green; Write-Host '📊 Monitor at: http://localhost:4040' -ForegroundColor Blue; break; } } catch { Start-Sleep -Seconds 2; $retryCount++; } } while ($retryCount -lt $maxRetries); if ($retryCount -ge $maxRetries) { Write-Host '❌ Could not get tunnel URL. Check Ngrok setup.' -ForegroundColor Red }"

goto CONTINUE

:ADVANCED_SETUP
echo.
echo =================================================================
echo                  🔧 ADVANCED SETUP OPTIONS
echo =================================================================
echo.
echo 🛠️ Manual Router Port Forwarding:
echo.
echo 📋 Steps to forward ports:
echo    1. Open router admin (usually 192.168.1.1 or 192.168.0.1)
echo    2. Find "Port Forwarding" or "Virtual Server" section
echo    3. Add these rules:
echo       - Port 8080 (HTTP) → Your PC IP
echo       - Port 8081 (TCP Game) → Your PC IP  
echo       - Port 8082 (UDP Stats) → Your PC IP
echo    4. Enable UPnP if available
echo.
echo 🌐 Your current local IP:
ipconfig | findstr "IPv4"
echo.
echo 🔥 Windows Firewall Rules:
echo    Adding firewall exceptions...
netsh advfirewall firewall add rule name="Wordle Game HTTP" dir=in action=allow protocol=TCP localport=8080
netsh advfirewall firewall add rule name="Wordle Game TCP" dir=in action=allow protocol=TCP localport=8081  
netsh advfirewall firewall add rule name="Wordle Game UDP" dir=in action=allow protocol=UDP localport=8082
echo    ✅ Firewall rules added!
echo.
echo 🌍 Get your public IP:
powershell -Command "try { $ip = (Invoke-WebRequest -Uri 'https://api.ipify.org' -UseBasicParsing).Content; Write-Host 'Public IP: ' $ip } catch { Write-Host 'Could not get public IP' }"
echo.
goto CONTINUE

:HELP
echo.
echo =================================================================
echo                      ❓ SETUP HELP
echo =================================================================
echo.
echo 🏠 LOCAL NETWORK (Option 1):
echo    ✅ Best for: Friends in same house/office
echo    ✅ Speed: Fastest, no lag
echo    ✅ Setup: Automatic, just share IP
echo    ❌ Limitation: Same network only
echo.
echo 🌍 INTERNET PUBLIC (Option 2):
echo    ✅ Best for: Friends anywhere in the world
echo    ✅ Setup: Easy with Ngrok (free)
echo    ✅ Access: Share one URL link
echo    ❌ Limitation: Some latency, bandwidth limits
echo.
echo 🔧 ADVANCED (Option 3):
echo    ✅ Best for: Permanent setup, full control
echo    ✅ Performance: Best for serious gaming
echo    ✅ Features: All protocols optimized
echo    ❌ Complexity: Requires router configuration
echo.
echo 📚 TECHNICAL INFO:
echo    • WebSocket: Port 8080 (Web interface)
echo    • TCP Socket: Port 8081 (Real-time game)
echo    • UDP Server: Port 8082 (Statistics)
echo    • All protocols work together for optimal experience
echo.
goto CONTINUE

:CONTINUE
echo.
echo =================================================================
echo                    🎮 READY TO PLAY!
echo =================================================================
echo.
echo 🚀 Your game server is running with these features:
echo    ✅ Real-time multiplayer battles
echo    ✅ AI word validation with definitions  
echo    ✅ Live chat system
echo    ✅ Customizable game settings
echo    ✅ Network performance monitoring
echo    ✅ Professional UI with statistics
echo.
echo 📊 Monitor your game:
echo    • Server logs: Check terminal window
echo    • Network stats: Click buttons in game
echo    • Active connections: Real-time indicators
echo.
set /p restart="Press Enter to return to menu, or 'q' to quit: "
if "%restart%"=="q" goto EXIT
cls
goto MENU

:EXIT
echo.
echo 👋 Thanks for using Wordle Multiplayer!
echo 🎓 Perfect for Network Programming assignments!
echo.
pause
exit
