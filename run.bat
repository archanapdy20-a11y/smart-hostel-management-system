@echo off
title Smart-Hostel-Management-System - Launcher
color 0B
echo =====================================================================
echo    Smart-Hostel-Management-System
echo =====================================================================
echo.

java -version >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo [ERROR] Java is not installed or not added to your system PATH.
    echo Please install JDK 17+ and try again.
    echo.
    pause
    exit /b 1
)

if exist "target\Smart-Hostel-Management-System-jar-with-dependencies.jar" (
    echo [INFO] Launching Desktop Application from compiled executable JAR...
    start javaw -jar target\Smart-Hostel-Management-System-jar-with-dependencies.jar
    exit /b 0
)

set MVN_CMD=mvn
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    if exist "C:\apache-maven-3.10.0\bin\mvn.cmd" (
        set MVN_CMD="C:\apache-maven-3.10.0\bin\mvn.cmd"
    ) else if exist "C:\apache-maven\apache-maven-3.10.0\bin\mvn.cmd" (
        set MVN_CMD="C:\apache-maven\apache-maven-3.10.0\bin\mvn.cmd"
    ) else if exist "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" (
        set MVN_CMD="C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
    ) else (
        color 0C
        echo [ERROR] Neither target JAR nor Maven was found.
        echo Please build the project first using build.bat or in your IDE.
        pause
        exit /b 1
    )
)

echo [1/2] Compiling and packaging project...
call %MVN_CMD% package -DskipTests
if %errorlevel% neq 0 (
    color 0C
    echo.
    echo [ERROR] Maven build failed.
    pause
    exit /b 1
)

echo.
echo [2/2] Launching Java Swing Desktop Application...
start javaw -jar target\Smart-Hostel-Management-System-jar-with-dependencies.jar
exit /b 0
