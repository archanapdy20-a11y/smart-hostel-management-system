@echo off
title Smart Hostel Management System - Database Setup
color 0E
echo =====================================================================
echo    Smart Hostel Management System - MySQL Database Setup
echo =====================================================================
echo.
echo This script will import schema.sql and sample_data.sql into your local MySQL.
echo.

set /p MYSQL_USER=Enter MySQL Username [default: root]: 
if "%MYSQL_USER%"=="" set MYSQL_USER=root

echo.
echo Enter MySQL Password for '%MYSQL_USER%' (press Enter if no password):
set /p MYSQL_PASS=

set PWD_ARG=
if not "%MYSQL_PASS%"=="" set PWD_ARG=-p%MYSQL_PASS%

echo.
echo [1/2] Creating database and tables from database\schema.sql...
mysql -u %MYSQL_USER% %PWD_ARG% < database\schema.sql
if %errorlevel% neq 0 (
    color 0C
    echo [ERROR] Failed to execute schema.sql. Please check your MySQL credentials and service.
    pause
    exit /b
)

echo.
echo [2/2] Seeding sample data from database\sample_data.sql...
mysql -u %MYSQL_USER% %PWD_ARG% < database\sample_data.sql
if %errorlevel% neq 0 (
    color 0C
    echo [ERROR] Failed to execute sample_data.sql.
    pause
    exit /b
)

color 0A
echo.
echo =====================================================================
echo [SUCCESS] MySQL Database 'smart_hostel_db' setup completed successfully!
echo.
echo Default Demo Accounts:
echo - Admin:      admin      / admin123
echo - Warden:     warden     / warden123
echo - Accountant: accountant / account123
echo - Student:    student    / student123
echo =====================================================================
echo.
pause
