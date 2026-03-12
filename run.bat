@echo off
REM PortScanner - Build & Run Script (Windows)
REM Author: hemanathan115

echo ============================================
echo    Java Port Scanner Tool - Build ^& Run
echo ============================================

cd /d "%~dp0src"

echo [*] Compiling PortScanner.java...
javac PortScanner.java

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed. Make sure JDK is installed and in PATH.
    pause
    exit /b 1
)

echo [OK] Compilation successful.
echo.

java PortScanner
pause
