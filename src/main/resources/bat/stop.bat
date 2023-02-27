@echo off
setlocal enabledelayedexpansion
for /f "eol=* tokens=*" %%i in ('netstat -an -o ^| findstr "9090"') do (
set a=%%i
set a=!a:~69,10!
echo !a!
taskkill /F /PID !a!
)
pause>nul