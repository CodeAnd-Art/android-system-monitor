#!/bin/bash
# SysMonitor Pro - Complete Build Script for Termux and Linux/macOS
# Gradle wrapper oluşturur, APK derler ve Download klasörüne kaydeder

set -e

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  🚀 SysMonitor Pro - APK Build Script                          ║"
echo "║  (Termux, Linux, macOS uyumlu)                                  ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Renk tanımları
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Build type seçimi
BUILD_TYPE="${1:-debug}"

if [ "$BUILD_TYPE" != "debug" ] && [ "$BUILD_TYPE" != "release" ]; then
  echo -e "${RED}Hata: Geçersiz build tipi. debug veya release seç.${NC}"
  echo "Kullanım: $0 [debug|release]"
  exit 1
fi

echo -e "${BLUE}[1/6]${NC} Sistem kontrolü yapılıyor..."
echo ""

# Java kontrol
if ! command -v java &> /dev/null; then
  echo -e "${RED}❌ Java bulunamadı!${NC}"
  echo "Termux: pkg install openjdk-17"
  echo "Linux/macOS: sudo apt-get install openjdk-17-jdk (veya brew install openjdk@17)"
  exit 1
fi
echo -e "${GREEN}✓${NC} Java: $(java -version 2>&1 | head -1 | grep -oE '[0-9]+' | head -1)"

# Git kontrol
if ! command -v git &> /dev/null; then
  echo -e "${RED}❌ Git bulunamadı!${NC}"
  exit 1
fi
echo -e "${GREEN}✓${NC} Git kurulu"

# Gradle kontrol
if ! command -v gradle &> /dev/null; then
  echo -e "${RED}❌ Gradle bulunamadı!${NC}"
  echo "Termux: pkg install gradle"
  echo "Linux: sudo apt-get install gradle"
  echo "macOS: brew install gradle"
  exit 1
fi
echo -e "${GREEN}✓${NC} Gradle: $(gradle --version | head -1)"

echo ""
echo -e "${BLUE}[2/6]${NC} Proje klasörü hazırlanıyor..."
echo ""

# Proje dizini
PROJECT_DIR="${PWD}"

if [ ! -f "settings.gradle.kts" ]; then
  echo -e "${RED}❌ Hata: settings.gradle.kts bulunamadı!${NC}"
  echo "Bu script proje root klasöründe çalıştırılmalı."
  exit 1
fi

echo -e "${GREEN}✓${NC} Proje dizini: $PROJECT_DIR"

echo ""
echo -e "${BLUE}[3/6]${NC} Gradle wrapper oluşturuluyor..."
echo ""

if [ ! -f "gradlew" ]; then
  echo -e "${YELLOW}⏳ gradle wrapper oluşturuluyor...${NC}"
  gradle wrapper --gradle-version 8.1
  chmod +x gradlew
  echo -e "${GREEN}✓${NC} Gradle wrapper oluşturuldu"
else
  echo -e "${GREEN}✓${NC} Gradle wrapper zaten mevcut"
fi

echo ""
echo -e "${BLUE}[4/6]${NC} Gradle bağımlılıkları kontrol ediliyor..."
echo ""

chmod +x "./gradlew"
./gradlew --version

echo ""
echo -e "${BLUE}[5/6]${NC} APK derleniliyor ($BUILD_TYPE)..."
echo ""

if [ "$BUILD_TYPE" = "release" ]; then
  echo -e "${YELLOW}📦 Release APK oluşturuluyor (optimized)...${NC}"
  echo -e "${YELLOW}⏳ Bu 5-10 dakika sürebilir...${NC}"
  ./gradlew clean assembleRelease -x lint
  APK_SRC="app/build/outputs/apk/release/app-release.apk"
  APK_NAME="SysMonitor-Pro-v1.0.0-release.apk"
else
  echo -e "${YELLOW}🐛 Debug APK oluşturuluyor...${NC}"
  echo -e "${YELLOW}⏳ Bu 3-8 dakika sürebilir...${NC}"
  ./gradlew clean assembleDebug -x lint
  APK_SRC="app/build/outputs/apk/debug/app-debug.apk"
  APK_NAME="SysMonitor-Pro-v1.0.0-debug.apk"
fi

echo ""
echo -e "${BLUE}[6/6]${NC} APK dosyası işleniyor..."
echo ""

# APK kontrol et
if [ ! -f "$APK_SRC" ]; then
  echo -e "${RED}❌ APK oluşturulamadı!${NC}"
  echo "Yukarıdaki hata mesajlarını kontrol et."
  exit 1
fi

APK_SIZE=$(du -h "$APK_SRC" | cut -f1)

echo -e "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║        ✅ APK BAŞARIYLA OLUŞTURULDU!                           ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${GREEN}📊 Bilgiler:${NC}"
echo -e "   📁 Dosya: $APK_NAME"
echo -e "   📍 Konum: $PROJECT_DIR/$APK_SRC"
echo -e "   💾 Boyut: $APK_SIZE"
echo -e "   🔧 Versiyon: 1.0.0"
echo -e "   📦 Tür: $BUILD_TYPE"
echo ""

# Download klasörüne kopyala (Termux için)
if [ -d "$HOME/storage/downloads" ]; then
  echo -e "${BLUE}📥 Download klasörüne kopyalanıyor...${NC}"
  cp -f "$APK_SRC" "$HOME/storage/downloads/$APK_NAME"
  echo -e "${GREEN}✓${NC} Kaydedildi: ~/storage/downloads/$APK_NAME"
  echo ""
  echo -e "${GREEN}📌 Dosya Android Download klasöründe:${NC}"
  echo -e "   ${YELLOW}Telefon > Download > $APK_NAME${NC}"
  echo ""
else
  echo -e "${YELLOW}⚠ Download klasörü bulunamadı.${NC}"
  echo -e "${YELLOW}Termux'ta ilk kez: termux-setup-storage${NC}"
  echo ""
fi

echo -e "${BLUE}📱 Kurulum Seçenekleri:${NC}"
echo ""
echo "Seçenek 1: Dosya Yöneticisiyle aç (Kolay)"
echo -e "   ${YELLOW}Telefon > Download > $APK_NAME > Aç${NC}"
echo ""

echo "Seçenek 2: ADB ile (Termux/Linux)"
echo -e "   ${YELLOW}adb install -r \"$PROJECT_DIR/$APK_SRC\"${NC}"
echo ""

echo "Seçenek 3: GitHub Release'e yükle"
echo -e "   ${YELLOW}https://github.com/CodeAnd-Art/android-system-monitor/releases${NC}"
echo ""

echo -e "${GREEN}✅ Tamamlandı!${NC}"
echo ""
