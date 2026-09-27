# 📱 Android APK Hazırlama Rehberi

## Seçenek 1: Android Studio ile (En Kolay)

### Adım 1: Projeyi Aç
```bash
git clone https://github.com/CodeAnd-Art/android-system-monitor
cd android-system-monitor
```
1. Android Studio'yu aç
2. File > Open > Proje klasörünü seç
3. Gradle sync otomatik başlar

### Adım 2: Debug APK Oluştur (Test için)
```
Android Studio menüsü:
Build > Build Bundle(s) / APK(s) > Build APK(s)
```
Oluşturulan APK:
```
app/build/outputs/apk/debug/app-debug.apk
```

**Cihaza Kur:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Adım 3: Release APK Oluştur (Google Play için)
```
Android Studio menüsü:
Build > Build Bundle(s) / APK(s) > Build Release APK
```
Oluşturulan APK:
```
app/build/outputs/apk/release/app-release.apk
```

---

## Seçenek 2: Komut Satırı ile Gradle (Hızlı)

### Windows
```batch
cd android-system-monitor
gradlew.bat clean
gradlew.bat assembleDebug
REM APK: app\build\outputs\apk\debug\app-debug.apk

REM veya Release için:
gradlew.bat assembleRelease
REM APK: app\build\outputs\apk\release\app-release.apk
```

### Mac/Linux
```bash
cd android-system-monitor
./gradlew clean
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk

# veya Release için:
./gradlew assembleRelease
# APK: app/build/outputs/apk/release/app-release.apk
```

---

## Seçenek 3: Termux'ta Android Cihazdan Build (En İlginç)

### Kurulum
```bash
# 1. Termux'u F-Droid veya Play Store'dan indir
# 2. Termux'u aç ve:

pkg update && pkg upgrade -y
pkg install -y git openjdk-17 gradle android-tools

# JAVA_HOME ayarla
export JAVA_HOME=$PREFIX/opt/openjdk
export PATH=$PATH:$JAVA_HOME/bin
```

### Build
```bash
# 1. Depoyu klonla
git clone https://github.com/CodeAnd-Art/android-system-monitor
cd android-system-monitor

# 2. Debug APK oluştur
./gradlew assembleDebug

# 3. APK konumu
ls -la app/build/outputs/apk/debug/

# 4. Cihaza kur (ADB gerekli - Play Store'dan ADBLink veya Wireless ADB kullan)
adb install app/build/outputs/apk/debug/app-debug.apk

# 5. Uygulamayı başlat
adb shell am start -n com.codeandart.systemmonitor/.MainActivity
```

---

## 🔑 Release APK İçin İmzalama (Google Play Store)

### Keystore Oluşturma (İlk Defa)

#### Windows
```batch
keytool -genkey -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias sysmonitor
```

#### Mac/Linux
```bash
keytool -genkey -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias sysmonitor
```

**Sorulan soruların cevapları:**
```
Keystore password: sysmonitor2024
Key password: sysmonitor2024
First and last name: SysMonitor Pro
Organizational Unit: Development
Organization: CodeAnd-Art
City/Locality: Istanbul
State/Province: Istanbul
Country Code: TR
```

**Çıktı:** `keystore.jks` dosyası proje root'unda oluşur

### build.gradle.kts'de İmzalama Yapılandırması

Zaten konfigüre edilmiş:
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("keystore.jks")
        storePassword = "sysmonitor2024"
        keyAlias = "sysmonitor"
        keyPassword = "sysmonitor2024"
    }
}
```

### Release APK Build
```bash
./gradlew clean assembleRelease
# APK: app/build/outputs/apk/release/app-release.apk
```

---

## 📦 APK Dosyalarının Konumları

```
android-system-monitor/
├── app/
│   └── build/
│       └── outputs/
│           └── apk/
│               ├── debug/
│               │   ├── app-debug.apk (8-15 MB)
│               │   └── output-metadata.json
│               └── release/
│                   ├── app-release.apk (4-8 MB - optimized)
│                   └── output-metadata.json
```

---

## ✅ Kurulum Testi

### APK Bilgisi Kontrol Et
```bash
# Windows
dir /s app-debug.apk

# Mac/Linux
find . -name "app-debug.apk"
```

### Cihaza Kur
```bash
# Debug APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Release APK
adb install -r app/build/outputs/apk/release/app-release.apk
```

### Uygulamayı Başlat
```bash
adb shell am start -n com.codeandart.systemmonitor/.MainActivity
```

### Logları Görmek
```bash
adb logcat | grep -i systemmonitor
```

### Uygulamayı Sil
```bash
adb uninstall com.codeandart.systemmonitor
```

---

## 🚀 GitHub'a Release Olarak Yükle

### 1. Git Tag Oluştur
```bash
git tag -a v1.0.0 -m "SysMonitor Pro v1.0.0 Release"
git push origin v1.0.0
```

### 2. GitHub Release Sayfasında
1. https://github.com/CodeAnd-Art/android-system-monitor/releases/new
2. "Choose a tag" > v1.0.0
3. Title: "SysMonitor Pro v1.0.0"
4. Description: Release notlarını ekle (RELEASE_NOTES.md'den kopyala)
5. "Attach binaries" > app-release.apk dosyasını yükle
6. "Publish release"

---

## 📊 APK Boyut Analizi

### Debug APK (Geliştirme)
- **Boyut:** 15-20 MB
- **Optimizasyon:** Yok
- **Hata Ayıklama:** Etkin
- **Uygun:** Test ve geliştirme

### Release APK (Üretim)
- **Boyut:** 4-8 MB (ProGuard ile optimize)
- **Optimizasyon:** Kod ve kaynaklar minimize
- **Hata Ayıklama:** Devre dışı
- **Uygun:** Google Play Store, kullanıcılara dağıtım

### Boyut Küçültme
```bash
# ProGuard kuralları (proguard-rules.pro)
-keep class com.codeandart.systemmonitor.** { *; }
-keepclassmembers class * {
    public <init>(...);
}
```

---

## 🐛 Sorun Giderme

### Build Hatası: "SDK not found"
```bash
# ANDROID_HOME'u ayarla
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

### Gradle Sync Hatası
```bash
# Cache temizle
./gradlew clean
./gradlew assembleDebug --refresh-dependencies
```

### APK Kurulum Başarısız
```bash
# Önceki versiyonu kaldır
adb uninstall com.codeandart.systemmonitor

# Tekrar kur
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Keystore Şifresi Unutuldu
```bash
# Yeni keystore oluştur
keytool -genkey -v -keystore keystore_new.jks -keyalg RSA -keysize 2048 -validity 10000 -alias sysmonitor
```

---

## 💡 İpuçları

1. **Hızlı Build:**
   ```bash
   ./gradlew assembleDebug --parallel --daemon
   ```

2. **Boyut Raporu:**
   ```bash
   ./gradlew assembleDebug --analyze-size
   ```

3. **Lint Kontrolü:**
   ```bash
   ./gradlew lint
   ```

4. **Bağımlılıkları Görüntüle:**
   ```bash
   ./gradlew androidDependencies
   ```

5. **Clean Build:**
   ```bash
   ./gradlew clean build
   ```

---

## 📱 Cihaz Yönetimi

### Bağlı Cihazları Listele
```bash
adb devices
```

### Dragging (Dosya Aktarımı)
```bash
# Cihaza gönder
adb push app-debug.apk /sdcard/

# Cihazdan al
adb pull /sdcard/systemmonitor-data.txt ./
```

### Shell Komutları
```bash
# Cihaz info
adb shell getprop

# Root kontrolü
adb shell su -v

# Sistem verisi
adb shell cat /proc/meminfo
```

---

## 📚 Ek Kaynaklar

- [Android Developer - Build](https://developer.android.com/studio/build)
- [Gradle for Android](https://developer.android.com/build)
- [APK Signing](https://developer.android.com/studio/publish/app-signing)
- [Play Store Deployment](https://developer.android.com/studio/publish)
- [ADB Documentation](https://developer.android.com/studio/command-line/adb)

---

**Not:** Tüm APK dosyaları GitHub Releases sayfasında yayınlanabilir ve kullanıcılar doğrudan indirebilir.
