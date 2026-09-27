#!/bin/bash
# SysMonitor Pro - Termux Complete Build Guide
# Bu script Termux'ta çalışır ve APK oluşturur

set -e

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║   🚀 SysMonitor Pro - Termux Build System                       ║"
echo "║   Android APK Derleyici (Termux üzerinde)                      ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Renk tanımları
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}[1/5]${NC} Sistem kontrolü yapılıyor..."
echo ""

# Termux kontrolü
if [ ! -d "$PREFIX" ]; then
    echo -e "${RED}❌ Hata: Bu script sadece Termux'ta çalışır!${NC}"
    echo "Termux'u şuradan indir: https://f-droid.org/packages/com.termux/"
    exit 1
fi

echo -e "${GREEN}✓${NC} Termux tespit edildi"

# Java kontrolü
if ! command -v java &> /dev/null; then
    echo -e "${YELLOW}⚠ Java bulunamadı. Yükleniyor...${NC}"
    pkg install -y openjdk-17
else
    echo -e "${GREEN}✓${NC} Java zaten yüklü: $(java -version 2>&1 | head -1)"
fi

# Git kontrolü
if ! command -v git &> /dev/null; then
    echo -e "${YELLOW}⚠ Git bulunamadı. Yükleniyor...${NC}"
    pkg install -y git
else
    echo -e "${GREEN}✓${NC} Git zaten yüklü"
fi

echo ""
echo -e "${BLUE}[2/5]${NC} Proje klonlanıyor..."
echo ""

# Dizin oluştur
BUILD_DIR="$HOME/SysMonitor-Pro"
if [ -d "$BUILD_DIR" ]; then
    echo -e "${YELLOW}⚠ Proje dizini zaten var. Güncelleniyor...${NC}"
    cd "$BUILD_DIR"
    git pull origin main
else
    git clone https://github.com/CodeAnd-Art/android-system-monitor "$BUILD_DIR"
    cd "$BUILD_DIR"
fi

echo -e "${GREEN}✓${NC} Proje hazırlandı: $BUILD_DIR"

echo ""
echo -e "${BLUE}[3/5]${NC} Gradle bağımlılıkları indiriliyor..."
echo ""

chmod +x gradlew
./gradlew --version

echo ""
echo -e "${BLUE}[4/5]${NC} APK derleniliyor..."
echo ""

# Build type seçimi
BUILD_TYPE="debug"
if [ "$1" = "release" ]; then
    BUILD_TYPE="release"
    echo -e "${YELLOW}📦 Release APK oluşturulacak...${NC}"
else
    echo -e "${YELLOW}🐛 Debug APK oluşturulacak...${NC}"
fi

echo ""
echo -e "${YELLOW}⏳ Derleme başlanıyor (2-10 dakika sürebilir)...${NC}"
echo ""

if [ "$BUILD_TYPE" = "release" ]; then
    ./gradlew clean assembleRelease -x lint
else
    ./gradlew clean assembleDebug -x lint
fi

echo ""
echo -e "${BLUE}[5/5]${NC} APK dosyası işleniyor..."
echo ""

# APK yolunu belirle
if [ "$BUILD_TYPE" = "release" ]; then
    APK_PATH="$BUILD_DIR/app/build/outputs/apk/release/app-release.apk"
    APK_NAME="SysMonitor-Pro-v1.0.0-release.apk"
else
    APK_PATH="$BUILD_DIR/app/build/outputs/apk/debug/app-debug.apk"
    APK_NAME="SysMonitor-Pro-v1.0.0-debug.apk"
fi

# APK kontrol et
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║          ✅ APK BAŞARIYLA OLUŞTURULDU!                        ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${GREEN}📊 Bilgiler:${NC}"
    echo -e "   📁 Dosya: $APK_NAME"
    echo -e "   📍 Konum: $APK_PATH"
    echo -e "   💾 Boyut: $APK_SIZE"
    echo -e "   🔧 Versiyon: 1.0.0"
    echo -e "   📦 Tür: $BUILD_TYPE"
    echo ""
    
    # Output dizinine kopyala
    OUTPUT_DIR="$HOME/SysMonitor-Output"
    mkdir -p "$OUTPUT_DIR"
    cp "$APK_PATH" "$OUTPUT_DIR/$APK_NAME"
    
    echo -e "${GREEN}📥 İndirme Klasörü:${NC}"
    echo -e "   $OUTPUT_DIR/$APK_NAME"
    echo ""
    
    echo -e "${BLUE}📱 Kurulum Seçenekleri:${NC}"
    echo ""
    echo "Seçenek 1: ADB ile kur (Tavsiye)"
    echo -e "${YELLOW}  adb install -r \"$APK_PATH\"${NC}"
    echo ""
    
    echo "Seçenek 2: Termux'ta doğrudan aç"
    echo -e "${YELLOW}  am start -a android.intent.action.VIEW -d \"file://$APK_PATH\" -t application/vnd.android.package-archive${NC}"
    echo ""
    
    echo "Seçenek 3: Dosya Yöneticisi ile aç"
    echo -e "${YELLOW}  Termux Files > $OUTPUT_DIR > $APK_NAME > Aç${NC}"
    echo ""
    
    echo -e "${BLUE}🔗 GitHub'a Yükle:${NC}"
    echo ""
    echo "1. GitHub Release sayfasını aç:"
    echo "   https://github.com/CodeAnd-Art/android-system-monitor/releases/new"
    echo ""
    echo "2. Tag: v1.0.0"
    echo "3. Dosyayı yükle: $APK_NAME"
    echo ""
    echo -e "${GREEN}✅ Tamamlandı!${NC}"
    echo ""
    
else
    echo -e "${RED}❌ APK oluşturulamadı!${NC}"
    echo "Hata mesajları için yukarı bakın."
    exit 1
fi
