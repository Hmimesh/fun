@echo off
REM run.bat — build and run Dungo on Windows
REM
REM Usage: double-click run.bat, or run from Command Prompt / PowerShell

echo === Dungo - build and run ===

if not exist out mkdir out

javac -d out src\com\hmimesh\game\*.java
if errorlevel 1 (
    echo Compilation failed. Make sure Java JDK is installed and on your PATH.
    pause
    exit /b 1
)

echo Compiled OK.
java -cp out com.hmimesh.game.Main
