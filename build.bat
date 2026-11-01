@echo off
title Smart-Hostel-Management-System - Build Executable JAR
color 0A
echo =====================================================================
echo    Building Smart-Hostel-Management-System (Executable Fat JAR)
echo =====================================================================
echo.

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
        echo [ERROR] Maven not found. Please install Apache Maven and add it to PATH.
        pause
        exit /b 1
    )
)

call %MVN_CMD% clean package -DskipTests

if %errorlevel% equ 0 (
    echo.
    echo =====================================================================
    echo [SUCCESS] Build completed successfully!
    echo Executable JAR created at:
    echo target\Smart-Hostel-Management-System-jar-with-dependencies.jar
    echo.
    echo You can run the application anytime using:
    echo java -jar target\Smart-Hostel-Management-System-jar-with-dependencies.jar
    echo or double-click run.bat
    echo =====================================================================
) else (
    color 0C
    echo.
    echo [ERROR] Build failed. Please check the maven logs above.
)

pause
