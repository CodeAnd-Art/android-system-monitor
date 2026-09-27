#!/bin/bash
# Hızlı test: Termux paketlerini kontrol et

echo "SysMonitor Pro - Termux Compatibility Check"
echo "=========================================="
echo ""

echo "Platform:"
uname -a
echo ""

echo "Java:"
if command -v java &> /dev/null; then
    java -version 2>&1
else
    echo "❌ Java yüklü değil"
fi
echo ""

echo "Git:"
if command -v git &> /dev/null; then
    git --version
else
    echo "❌ Git yüklü değil"
fi
echo ""

echo "Gradle:"
if [ -f "./gradlew" ]; then
    echo "✓ Gradle wrapper var"
else
    echo "❌ Gradle wrapper bulunamadı"
fi
echo ""

echo "Depolama:"
df -h $HOME | tail -1
echo ""

echo "Bellek:"
free -h 2>/dev/null || echo "Bilgi alınamadı"
echo ""

echo "Kontrol tamamlandı!"
