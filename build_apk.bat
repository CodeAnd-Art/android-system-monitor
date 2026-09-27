@echo off
REM SysMonitor Pro - APK Build Script for Windows
REM Usage: build_apk.bat [debug|release]

setlocal enabledelayedexpansion

set BUILD_TYPE=%1
if "%BUILD_TYPE%"=="" set BUILD_TYPE=debug

echo.
echo 🔨 SysMonitor Pro APK Builder
echo ================================
echo Build Type: %BUILD_TYPE%
echo.

REM Clean
echo 🧹 Cleaning previous builds...
call gradlew.bat clean

REM Build
if "%BUILD_TYPE%"=="release" (
    echo 📦 Building Release APK...
    call gradlew.bat assembleRelease
    set APK_PATH=app\build\outputs\apk\release\app-release.apk
) else (
    echo 🐛 Building Debug APK...
    call gradlew.bat assembleDebug
    set APK_PATH=app\build\outputs\apk\debug\app-debug.apk
)

REM Check if build successful
if exist "%APK_PATH%" (
    echo.
    echo ✅ Build Successful!
    echo 📁 APK Path: %APK_PATH%
    echo.
    echo 📱 Install with:
    echo    adb install -r %APK_PATH%
    echo.
) else (
    echo ❌ Build Failed!
    exit /b 1
)
