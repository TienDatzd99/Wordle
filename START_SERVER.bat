@echo off
cd /d "D:\Wordle\backend"
set JAVA_HOME=C:\Program Files\Java\jdk-21
echo Starting Wordle Game Server...
echo JAVA_HOME: %JAVA_HOME%
echo Current Directory: %CD%
mvnw.cmd spring-boot:run
pause
