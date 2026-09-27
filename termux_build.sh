#!/bin/bash
# SysMonitor Pro - Termux APK build script
# APK oluşturur ve yalnızca Download klasörüne kopyalar; kurulum yapmaz.

set -e

BUILD_TYPE="debug"
if [ "${1:-}" = "release" ]; then
  BUILD_TYPE="release"
fi

if [ -z "${PREFIX:-}" ]; then
  echo "Hata: Bu script Termux içinde çalıştırılmalıdır."
  exit 1
fi

command -v java >/dev/null 2>&1 || {
  echo "Java bulunamadı. Önce çalıştır: pkg install openjdk-17"
  exit 1
}

PROJECT_DIR="$HOME/SysMonitor-Pro"
DOWNLOAD_DIR="$HOME/storage/downloads"

if [ ! -d "$PROJECT_DIR" ]; then
  git clone https://github.com/CodeAnd-Art/android-system-monitor "$PROJECT_DIR"
else
  cd "$PROJECT_DIR"
  git pull --ff-only origin main
fi

cd "$PROJECT_DIR"
chmod +x gradlew

if [ "$BUILD_TYPE" = "release" ]; then
  ./gradlew clean assembleRelease -x lint
  APK_PATH="$PROJECT_DIR/app/build/outputs/apk/release/app-release.apk"
  APK_NAME="SysMonitor-Pro-v1.0.0-release.apk"
else
  ./gradlew clean assembleDebug -x lint
  APK_PATH="$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk"
  APK_NAME="SysMonitor-Pro-v1.0.0-debug.apk"
fi

if [ ! -f "$APK_PATH" ]; then
  echo "Hata: APK oluşturulamadı."
  exit 1
fi

# Android Download klasörüne erişim için ilk çalıştırmada izin iste.
if [ ! -d "$HOME/storage" ]; then
  echo "Termux depolama erişimi gerekiyor. İzin isteği açılıyor..."
  termux-setup-storage || true
  echo "İzin penceresinde Allow/İzin Ver seç ve scripti tekrar çalıştır."
  exit 0
fi

mkdir -p "$DOWNLOAD_DIR"
DEST="$DOWNLOAD_DIR/$APK_NAME"
cp -f "$APK_PATH" "$DEST"

printf '\nAPK başarıyla oluşturuldu. Kurulum yapılmadı.\n'
printf 'Dosya: %s\n' "$DEST"
printf 'Boyut: %s\n' "$(du -h "$DEST" | cut -f1)"
printf '\nDosya yöneticisinde Download klasörünü açıp APK dosyasını manuel olarak kurabilirsin.\n'
