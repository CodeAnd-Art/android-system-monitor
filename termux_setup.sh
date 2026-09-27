#!/bin/bash
# SysMonitor Pro - Termux İlk Kurulum Scripti
# Bu script Termux ortamını hazırlar

echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║   🔧 SysMonitor Pro - Termux Setup                            ║"
echo "║   Termux ortamını hazırlama scripti                           ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

echo "[1/4] Paketler güncelleniyse..."
pkg update -y
pkg upgrade -y

echo ""
echo "[2/4] Gerekli paketler yükleniyor..."
pkg install -y \
    openjdk-17 \
    git \
    wget \
    curl \
    zip \
    unzip \
    nano \
    less

echo ""
echo "[3/4] Environment değişkenleri ayarlanıyor..."

# .bashrc veya .profile'a ekle
BASHRC="$HOME/.bashrc"
if [ ! -f "$BASHRC" ]; then
    touch "$BASHRC"
fi

# JAVA_HOME kontrolü
if ! grep -q "JAVA_HOME" "$BASHRC"; then
    cat >> "$BASHRC" << 'EOF'

# SysMonitor Pro Build Environment
export JAVA_HOME=$PREFIX/opt/openjdk
export PATH=$PATH:$JAVA_HOME/bin
export GRADLE_USER_HOME=$HOME/.gradle

echo "✓ SysMonitor Pro build ortamı hazırlandı"
EOF
    echo "✓ Environment değişkenleri eklendi"
else
    echo "✓ Environment değişkenleri zaten ayarlı"
fi

# Şu anki shell'e uygulamak için
export JAVA_HOME=$PREFIX/opt/openjdk
export PATH=$PATH:$JAVA_HOME/bin

echo ""
echo "[4/4] Kontroller yapılıyor..."
echo ""

# Java kontrol
echo "Java sürümü:"
java -version
echo ""

# Git kontrol
echo "Git sürümü:"
git --version
echo ""

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║              ✅ SETUP TAMAMLANDI!                             ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

echo "Sonraki adım: APK oluşturmak için"
echo ""
echo "  chmod +x termux_build.sh"
echo "  ./termux_build.sh"
echo ""
echo "Veya release APK için:"
echo ""
echo "  ./termux_build.sh release"
echo ""
