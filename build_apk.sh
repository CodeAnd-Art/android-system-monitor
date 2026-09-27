#!/bin/bash

# SysMonitor Pro - APK Build Script for Linux/Mac
# Usage: ./build_apk.sh [debug|release]

set -e

BUILD_TYPE=${1:-debug}
PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

echo "🔨 SysMonitor Pro APK Builder"
echo "================================"
echo "Build Type: $BUILD_TYPE"
echo "Project Directory: $PROJECT_DIR"
echo ""

# Clean
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build
if [ "$BUILD_TYPE" = "release" ]; then
    echo "📦 Building Release APK..."
    ./gradlew assembleRelease
    APK_PATH="$PROJECT_DIR/app/build/outputs/apk/release/app-release.apk"
else
    echo "🐛 Building Debug APK..."
    ./gradlew assembleDebug
    APK_PATH="$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk"
fi

# Check if build successful
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo ""
    echo "✅ Build Successful!"
    echo "📁 APK Path: $APK_PATH"
    echo "📊 APK Size: $APK_SIZE"
    echo ""
    echo "📱 Install with:"
    echo "   adb install -r $APK_PATH"
    echo ""
else
    echo "❌ Build Failed!"
    exit 1
fi
