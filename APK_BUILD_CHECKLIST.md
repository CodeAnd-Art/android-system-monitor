# SysMonitor Pro v1.0.0 - Android APK Build Checklist

## Pre-Build Checklist
- [ ] Android SDK 34 installed
- [ ] Java JDK 11+ installed
- [ ] Git repository cloned
- [ ] Network connection available
- [ ] At least 2GB free disk space

## Build Process

### Debug APK (Development)
```bash
# Linux/Mac
cd android-system-monitor
./gradlew assembleDebug

# Windows
cd android-system-monitor
gradlew.bat assembleDebug
```

**Result:**
- APK: `app/build/outputs/apk/debug/app-debug.apk`
- Size: ~15-20 MB
- Time: 2-5 minutes
- Install: `adb install -r app/build/outputs/apk/debug/app-debug.apk`

### Release APK (Production)
```bash
# Linux/Mac
cd android-system-monitor
./gradlew assembleRelease

# Windows
cd android-system-monitor
gradlew.bat assembleRelease
```

**Result:**
- APK: `app/build/outputs/apk/release/app-release.apk`
- Size: ~4-8 MB (ProGuard optimized)
- Time: 3-7 minutes
- Install: `adb install -r app/build/outputs/apk/release/app-release.apk`

## Automated Build Scripts

### Linux/Mac
```bash
chmod +x build_apk.sh
./build_apk.sh debug    # Debug build
./build_apk.sh release  # Release build
```

### Windows
```batch
build_apk.bat debug    REM Debug build
build_apk.bat release  REM Release build
```

## Post-Build Verification

- [ ] APK file exists
- [ ] APK size is reasonable
- [ ] No build warnings
- [ ] APK installs on device
- [ ] App launches successfully
- [ ] App functionality works

## Distribution

### GitHub Release
1. Create tag: `git tag -a v1.0.0 -m "Release v1.0.0"`
2. Push: `git push origin v1.0.0`
3. Upload APK to GitHub Releases
4. Users can download directly

### Direct Installation
```bash
# Via ADB
adb install app-release.apk

# Via File Transfer
adb push app-release.apk /sdcard/
# Then install from Files app
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "SDK not found" | Set ANDROID_HOME environment variable |
| "Gradle sync failed" | Run `./gradlew clean --refresh-dependencies` |
| "APK not found" | Check build output, ensure no errors |
| "Installation failed" | Run `adb uninstall com.codeandart.systemmonitor` first |
| "Permission denied" | Run `chmod +x build_apk.sh` on Linux/Mac |

---

**Latest Build:** 2026-09-27 v1.0.0
**Status:** Ready for distribution
