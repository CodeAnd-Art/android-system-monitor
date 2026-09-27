# 🚀 SysMonitor Pro - Build Komutları

## Termux'ta Derleme (Android Telefon)

### Kurulum (İlk Defa)
```bash
pkg update -y
pkg install -y git openjdk-17 gradle
```

### Projeyi İndir
```bash
cd $HOME
git clone https://github.com/CodeAnd-Art/android-system-monitor
cd android-system-monitor
```

### İzin Ver (İlk Defa)
```bash
termux-setup-storage
```

Browser'da izin penceresinde **İzin Ver** seç.

### Debug APK Oluştur (Hızlı Test)
```bash
chmod +x build.sh
./build.sh debug
```

**Sonuç:** APK, Android'ın `Download` klasörüne kaydedilir.

### Release APK Oluştur (Optimize, Daha Küçük)
```bash
./build.sh release
```

**Sonuç:** Optimized APK, Android'ın `Download` klasörüne kaydedilir.

---

## Linux/macOS'ta Derleme

### Kurulum (İlk Defa)

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get update
sudo apt-get install -y git openjdk-17-jdk gradle
```

**macOS:**
```bash
brew install git openjdk@17 gradle
```

### Projeyi İndir
```bash
git clone https://github.com/CodeAnd-Art/android-system-monitor
cd android-system-monitor
```

### Debug APK Oluştur
```bash
chmod +x build.sh
./build.sh debug
```

**Sonuç:** APK, proje klasöründe `app/build/outputs/apk/debug/app-debug.apk` konumunda oluşturulur.

### Release APK Oluştur
```bash
./build.sh release
```

**Sonuç:** APK, proje klasöründe `app/build/outputs/apk/release/app-release.apk` konumunda oluşturulur.

---

## Windows'ta Derleme

### Kurulum (İlk Defa)

1. **Java 17+ İndir:**
   - https://adoptopenjdk.net veya Oracle'dan
   - JAVA_HOME ortam değişkenini ayarla

2. **Gradle İndir:**
   - https://gradle.org/install/
   - PATH'e ekle

3. **Git İndir:**
   - https://git-scm.com/

### Projeyi İndir
```bash
git clone https://github.com/CodeAnd-Art/android-system-monitor
cd android-system-monitor
```

### Debug APK Oluştur
```cmd
build.bat debug
```

**Sonuç:** APK, `app\build\outputs\apk\debug\app-debug.apk` konumunda oluşturulur.

### Release APK Oluştur
```cmd
build.bat release
```

**Sonuç:** APK, `app\build\outputs\apk\release\app-release.apk` konumunda oluşturulur.

---

## 📱 APK'yı Cihaza Kur

### Seçenek 1: Dosya Yöneticisiyle (En Kolay)
1. Android Dosya Yöneticisini aç
2. `Download` klasörüne git
3. `SysMonitor-Pro-v1.0.0-debug.apk` dosyasını tap
4. Yükle

### Seçenek 2: ADB ile (Termux/Linux/macOS)
```bash
# ADB'yi Termux'ta kur (opsiyonel)
pkg install android-tools

# APK'yı kur
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Seçenek 3: GitHub Release'den İndir
1. https://github.com/CodeAnd-Art/android-system-monitor/releases
2. APK dosyasını indir
3. Cihazda aç

---

## ⏱️ Beklenen Süreler

| İşlem | Süre |
|-------|------|
| Gradle wrapper oluştur | 1-2 dakika |
| Debug APK | 3-8 dakika |
| Release APK | 5-10 dakika |

---

## 🐛 Sorun Giderme

### "Java not found"
```bash
# Termux
pkg install openjdk-17

# Linux
sudo apt-get install openjdk-17-jdk

# macOS
brew install openjdk@17
```

### "Gradle not found"
```bash
# Termux
pkg install gradle

# Linux
sudo apt-get install gradle

# macOS
brew install gradle
```

### "Build failed"
```bash
# Cache temizle ve yeniden dene
./gradlew clean build
```

### "Out of memory"
```bash
# Gradle bellek ayarlarını düşür
echo "org.gradle.jvmargs=-Xmx512m" >> gradle.properties
./build.sh debug
```

---

## ✅ Kontrol Listesi

- [ ] Java 17+ kurulu
- [ ] Gradle kurulu
- [ ] Git kurulu
- [ ] Proje klonlandı
- [ ] Projeye gidildi (`cd android-system-monitor`)
- [ ] `build.sh` veya `build.bat` çalıştırıldı
- [ ] APK oluşturuldu
- [ ] APK cihazda kuruldu
- [ ] Uygulama açılıyor

---

**Başarıyla derlediniz!** 🎉

Herhangi sorununuz olursa GitHub Issues'ta bildirin:
https://github.com/CodeAnd-Art/android-system-monitor/issues
