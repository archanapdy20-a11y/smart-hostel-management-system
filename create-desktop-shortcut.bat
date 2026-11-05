@echo off
REM Creates a desktop shortcut for the Smart Hostel Management System
setlocal enabledelayedexpansion

set "BASE_DIR=%~dp0"
set "EXE_PATH=%BASE_DIR%dist\Smart-Hostel-Management-System\Smart-Hostel-Management-System.exe"
set "ICON_PATH=%BASE_DIR%resources\app.ico"

if not exist "%EXE_PATH%" (
    echo Building native application first...
    call "%BASE_DIR%package-app.bat"
)

powershell -NoProfile -ExecutionPolicy Bypass -Command "$ws = New-Object -ComObject WScript.Shell; $s = $ws.CreateShortcut([IO.Path]::Combine([Environment]::GetFolderPath('Desktop'), 'Smart Hostel Management System.lnk')); $s.TargetPath = '%EXE_PATH%'; $s.WorkingDirectory = [IO.Path]::GetDirectoryName('%EXE_PATH%'); $s.IconLocation = '%ICON_PATH%,0'; $s.Description = 'Smart Hostel Management System'; $s.Save(); Write-Host 'Shortcut created successfully on Desktop!'"

pause

