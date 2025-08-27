@echo off
echo =================================================================
echo         🌍 WORDLE MULTIPLAYER - PUBLIC INTERNET ACCESS
echo =================================================================
echo.
echo 🚀 Making your game accessible worldwide via Ngrok tunneling
echo.

echo 📥 Step 1: Download Ngrok (if not installed)...
if not exist "ngrok.exe" (
    echo Downloading Ngrok...
    powershell -Command "Invoke-WebRequest -Uri 'https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-windows-amd64.zip' -OutFile 'ngrok.zip'"
    powershell -Command "Expand-Archive -Path 'ngrok.zip' -DestinationPath '.'"
    del ngrok.zip
    echo ✅ Ngrok downloaded successfully!
) else (
    echo ✅ Ngrok already exists!
)

echo.
echo 🔗 Step 2: Creating public tunnel for port 8080...
echo.
echo 📝 Instructions:
echo    1. Sign up at https://ngrok.com (free account)
echo    2. Get your auth token from dashboard
echo    3. Run: ngrok config add-authtoken YOUR_TOKEN
echo    4. Then restart this script
echo.

echo 🌐 Starting Ngrok tunnel...
start "Ngrok Tunnel" ngrok http 8080

echo.
echo ⏳ Waiting for tunnel to establish (5 seconds)...
timeout /t 5 /nobreak > nul

echo.
echo 🔍 Getting public URL...
powershell -Command "try { $response = Invoke-RestMethod -Uri 'http://localhost:4040/api/tunnels' -Method Get; $url = $response.tunnels[0].public_url; Write-Host '🌍 PUBLIC URL: ' $url -ForegroundColor Green; Write-Host '📱 Share this URL with friends to play together!' -ForegroundColor Yellow } catch { Write-Host '❌ Please setup Ngrok auth token first' -ForegroundColor Red }"

echo.
echo =================================================================
echo                    🎮 MULTIPLAYER ACCESS READY
echo =================================================================
echo.
echo 📤 SHARE WITH FRIENDS:
echo    • Get the public URL shown above (starts with https://...)
echo    • Send this URL to anyone worldwide
echo    • They can join your game directly!
echo.
echo 📊 MONITOR CONNECTIONS:
echo    • Ngrok Dashboard: http://localhost:4040
echo    • Game Server Logs: Check server console
echo    • Real-time connections: In game interface
echo.
echo 🔧 TROUBLESHOOTING:
echo    • If no URL shown: Setup Ngrok auth token
echo    • Connection issues: Check Windows Firewall
echo    • Performance: Ngrok free tier has limits
echo.
echo =================================================================
pause
