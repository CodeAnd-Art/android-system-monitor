@echo off
REM SysMonitor Pro - Build Script for Windows
REM Gradle wrapper oluşturur, APK derler ve Downloads klasörüne kaydeder

setlocal enabledelayedexpansion

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║  🚀 SysMonitor Pro - APK Build Script (Windows)                ║
echo ║  Build via Gradle                                              ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Build type seçimi
set BUILD_TYPE=%1
if "%BUILD_TYPE%"==" " set BUILD_TYPE=debug
if not "%BUILD_TYPE%"=="debug" if not "%BUILD_TYPE%"=="release" (
    echo Hata: Geçersiz build tipi. debug veya release seç.
    echo Kullanım: build.bat [debug^|release]
    exit /b 1
)

echo [1/6] Sistem kontrolü yapılıyor...
echo.

REM Java kontrol
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java bulunamadı!
    echo Https://adoptopenjdk.net veya Oracle'dan Java 17+ indir
    exit /b 1
)
echo ✓ Java kurulu

REM Gradle kontrol
gradle --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Gradle bulunamadı!
    echo https://gradle.org/install/ adresinden Gradle indir
    exit /b 1
)
echo ✓ Gradle kurulu

echo.
echo [2/6] Proje klasörü hazırlanıyor...
echo.

if not exist "settings.gradle.kts" (
    echo ❌ Hata: settings.gradle.kts bulunamadı!
    echo Bu script proje root klasöründe çalıştırılmalı.
    exit /b 1
)

echo ✓ Proje dizini: %CD%

echo.
echo [3/6] Gradle wrapper oluşturuluyor...
echo.

if not exist "gradlew.bat" (
    echo ⏳ gradle wrapper oluşturuluyor...
    call gradle wrapper --gradle-version 8.1
    echo ✓ Gradle wrapper oluşturuldu
) else (
    echo ✓ Gradle wrapper zaten mevcut
)

echo.
echo [4/6] Gradle bağımlılıkları kontrol ediliyor...
echo.

call gradlew.bat --version

echo.
echo [5/6] APK derleniliyor (%BUILD_TYPE%)...
echo.

if "%BUILD_TYPE%"=="release" (
    echo 📦 Release APK oluşturuluyor (optimized)...
    echo ⏳ Bu 5-10 dakika sürebilir...
    call gradlew.bat clean assembleRelease -x lint
    set APK_SRC=app\build\outputs\apk\release\app-release.apk
    set APK_NAME=SysMonitor-Pro-v1.0.0-release.apk
) else (
    echo 🐛 Debug APK oluşturuluyor...
    echo ⏳ Bu 3-8 dakika sürebilir...
    call gradlew.bat clean assembleDebug -x lint
    set APK_SRC=app\build\outputs\apk\debug\app-debug.apk
    set APK_NAME=SysMonitor-Pro-v1.0.0-debug.apk
)

echo.
echo [6/6] APK dosyası işleniyor...
echo.

if not exist "%APK_SRC%" (
    echo ❌ APK oluşturulamadı!
    echo Yukarıdaki hata mesajlarını kontrol et.
    exit /b 1
)

echo ╔════════════════════════════════════════════════════════════════╗
echo ║        ✅ APK BAŞARIYLA OLUŞTURULDU!                           ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

echo 📊 Bilgiler:
echo    📁 Dosya: %APK_NAME%
echo    📍 Konum: %CD%\%APK_SRC%
echo    📦 Tür: %BUILD_TYPE%
echo.

echo 📱 Kurulum Seçenekleri:
echo.
echo Seçenek 1: Dosya Yöneticisiyle aç (Kolay)
echo    Telefon > Download > %APK_NAME% > Aç
echo.

echo Seçenek 2: ADB ile (Windows)
echo    adb install -r "%CD%\%APK_SRC%"
echo.

echo Seçenek 3: GitHub Release'e yükle
echo    https://github.com/CodeAnd-Art/android-system-monitor/releases
echo.

echo ✅ Tamamlandı!
echo.
