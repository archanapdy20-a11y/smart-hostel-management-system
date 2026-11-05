@echo off
REM Builds fat JAR and packages native Windows application (.exe) with jpackage
setlocal enabledelayedexpansion

set "BASE_DIR=%~dp0"
cd /d "%BASE_DIR%"

REM Locate Maven executable
set "MVN_CMD=mvn"
where mvn >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\apache-maven-3.10.0\bin\mvn.cmd" (
        set "MVN_CMD=C:\apache-maven-3.10.0\bin\mvn.cmd"
    ) else if exist "C:\apache-maven\apache-maven-3.10.0\bin\mvn.cmd" (
        set "MVN_CMD=C:\apache-maven\apache-maven-3.10.0\bin\mvn.cmd"
    ) else (
        echo [ERROR] Maven not found! Please ensure Maven is installed.
        pause
        exit /b 1
    )
)

REM Locate jpackage executable
set "JPACKAGE=jpackage"
where jpackage >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\Program Files\Java\jdk-24\bin\jpackage.exe" (
        set "JPACKAGE=C:\Program Files\Java\jdk-24\bin\jpackage.exe"
    ) else if exist "%JAVA_HOME%\bin\jpackage.exe" (
        set "JPACKAGE=%JAVA_HOME%\bin\jpackage.exe"
    ) else (
        echo [ERROR] jpackage not found! JDK 17+ is required.
        pause
        exit /b 1
    )
)

echo [1/3] Compiling and packaging application JAR...
call "%MVN_CMD%" clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven build failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/3] Generating native Windows application bundle (.exe)...
if exist "%BASE_DIR%dist" rmdir /s /q "%BASE_DIR%dist"

"%JPACKAGE%" --type app-image -n "Smart-Hostel-Management-System" -i "%BASE_DIR%target" --main-jar "Smart-Hostel-Management-System-jar-with-dependencies.jar" --main-class "com.smarthostel.Main" --icon "%BASE_DIR%resources\app.ico" -d "%BASE_DIR%dist"

if %ERRORLEVEL% neq 0 (
    echo [ERROR] jpackage failed to generate native Windows app!
    pause
    exit /b %ERRORLEVEL%
)

echo [3/3] Copying runtime configuration...
if not exist "%BASE_DIR%dist\Smart-Hostel-Management-System\config" (
    mkdir "%BASE_DIR%dist\Smart-Hostel-Management-System\config"
)
xcopy /y /e "%BASE_DIR%config\*" "%BASE_DIR%dist\Smart-Hostel-Management-System\config\" >nul

echo =========================================================================
echo  [SUCCESS] Standalone Windows Desktop App generated:
echo  %BASE_DIR%dist\Smart-Hostel-Management-System\Smart-Hostel-Management-System.exe
echo =========================================================================
pause
