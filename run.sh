#!/bin/bash
# PortScanner - Build & Run Script (Linux/Mac)
# Author: hemanathan115

echo "============================================"
echo "   Java Port Scanner Tool - Build & Run"
echo "============================================"

# Navigate to src directory
cd "$(dirname "$0")/src"

# Compile
echo "[*] Compiling PortScanner.java..."
javac PortScanner.java

if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed. Make sure JDK is installed."
    exit 1
fi

echo "[OK] Compilation successful."
echo ""

# Run
java PortScanner
