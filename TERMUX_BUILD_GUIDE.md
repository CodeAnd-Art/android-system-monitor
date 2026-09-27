# 🚀 Termux'ta SysMonitor Pro APK Oluşturma Rehberi

## 📋 Gereksinimler

- **Termux uygulaması** (F-Droid veya Play Store)
- **2GB+ boş alan** (/data ve /cache)
- **İnternet bağlantısı**
- **10-15 dakika zaman**

---

## ✅ Adım 1: Termux Kurulumu

### 1.1 Termux İndir
- F-Droid: https://f-droid.org/packages/com.termux/
- Play Store: https://play.google.com/store/apps/details?id=com.termux

### 1.2 Termux'u Aç
```bash
# Termux uygulamasını aç
```

---

## 🔧 Adım 2: İlk Kurulum (Otomatik)

### 2.1 Projeyi İndir
```bash
cd $HOME
git clone https://github.com/CodeAnd-Art/android-system-monitor
cd android-system-monitor
```

### 2.2 Setup Script'ini Çalıştır
```bash
chmod +x termux_setup.sh
./termux_setup.sh
```

**Bu script:**
- Paketleri günceller
- Java (openjdk-17) yükler
- Git yükler
- Environment değişkenlerini ayarlar
- Tüm kontrolleri yapır

⏳ **Süre:** 3-5 dakika

---

## 🔨 Adım 3: APK Oluşturma

### 3.1 Debug APK (Test için - Hızlı)
```bash
cd ~/android-system-monitor
chmod +x termux_build.sh
./termux_build.sh debug
```

⏳ **Süre:** 3-5 dakika
⬇️ **Boyut:** 15-20 MB

### 3.2 Release APK (Üretim için - Optimize)
```bash
cd ~/android-system-monitor
./termux_build.sh release
```

⏳ **Süre:** 5-10 dakika
⬇️ **Boyut:** 4-8 MB

---

## 📁 Adım 4: APK'yı Bul ve Kur

### 4.1 APK Konumu

Debug:
```
~/SysMonitor-Output/SysMonitor-Pro-v1.0.0-debug.apk
```

Release:
```
~/SysMonitor-Output/SysMonitor-Pro-v1.0.0-release.apk
```

### 4.2 Termux'ta Kurulum (ADB gerekli)
```bash
# ADB'yi Termux'ta kur
pkg install android-tools

# APK'yı kur
adb install -r ~/SysMonitor-Output/SysMonitor-Pro-v1.0.0-debug.apk
```

### 4.3 Dosya Yöneticisi ile Kurulum (ADB olmadan)
```bash
# APK'yı dosya yöneticisinde aç
am start -a android.intent.action.VIEW \
  -d file://$HOME/SysMonitor-Output/SysMonitor-Pro-v1.0.0-debug.apk \
  -t application/vnd.android.package-archive
```

---

## 🚀 Adım 5: GitHub'a Yükle

### 5.1 Yüklemek için Gerekli
- GitHub hesabı
- Repository erişimi

### 5.2 GitHub Release Oluştur

1. Browser'da aç: https://github.com/CodeAnd-Art/android-system-monitor/releases
2. "Create a new release" tıkla
3. Bilgileri doldur:
   - **Tag:** v1.0.0
   - **Title:** SysMonitor Pro v1.0.0 Release
   - **Description:** Aşağıdaki template kullan
4. APK dosyasını yükle
5. "Publish release" tıkla

### 5.2 Release Template

```markdown
# 🚀 SysMonitor Pro v1.0.0

## ✨ Özellikler
- 📊 CPU, RAM, Depolama izleme
- 🌡️ Termal sensör verileri
- ⚡ Pil ve ağ bilgisi
- 🔍 Derin sistem analizi (Root)
- 🎨 Modern Material 3 UI

## 📥 Kurulum
1. APK'yı indir
2. Android cihazında aç
3. İzinleri onayla
4. Uygulama başlar

## 🔧 Build Bilgisi
- **Platform:** Android
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Boyut:** 4-8 MB (Release)
- **Build:** Termux'ta oluşturuldu

## 📝 Lisans
MIT License
```

---

## 🐛 Sorun Giderme

### Problem: "Java not found"
```bash
pkg install -y openjdk-17
export JAVA_HOME=$PREFIX/opt/openjdk
export PATH=$PATH:$JAVA_HOME/bin
```

### Problem: "Gradle error"
```bash
cd ~/android-system-monitor
./gradlew clean
./gradlew --refresh-dependencies
./gradlew assembleDebug
```

### Problem: "Storage error"
```bash
# Termux'a storage erişimi ver
termux-setup-storage

# Yeniden dene
./termux_build.sh debug
```

### Problem: "Out of memory"
```bash
# Gradle bellek ayarlarını düşür
echo "org.gradle.jvmargs=-Xmx512m" >> gradle.properties
./gradlew assembleDebug
```

### Problem: "Slow build"
```bash
# Parallel build'i etkinleştir
./gradlew assembleDebug --parallel --daemon
```

---

## 📊 Build İstatistikleri

| Tür | Boyut | Süre | Amaç |
|-----|-------|------|-------|
| Debug | 15-20 MB | 3-5 dk | Test |
| Release | 4-8 MB | 5-10 dk | Üretim |

---

## 📱 Kurulum Yöntemleri

### Yöntem 1: ADB (Tavsiye)
```bash
adb install -r ~/SysMonitor-Output/SysMonitor-Pro-v1.0.0-debug.apk
```

### Yöntem 2: Dosya Yöneticisi
- Termux'ta: Dosyaları aç → APK'ya tıkla
- Veya: Başka bir dosya yöneticisinde bul ve aç

### Yöntem 3: Telegram/Discord
- APK'yı Telegram/Discord'a gönder
- Cihazda indir ve aç

### Yöntem 4: GitHub Release
- GitHub Release sayfasından indir
- Cihazda aç

---

## ✅ Kontrol Listesi

- [ ] Termux kurulu
- [ ] termux_setup.sh çalıştırıldı
- [ ] Java kurulu (`java -version` kontrol et)
- [ ] Proje klonlandı
- [ ] termux_build.sh çalıştırıldı
- [ ] APK oluşturuldu
- [ ] APK cihazda kuruldu
- [ ] Uygulama başarıyla açılıyor
- [ ] GitHub'a yüklendi

---

## 🔗 Kullanışlı Linkler

- **GitHub Repo:** https://github.com/CodeAnd-Art/android-system-monitor
- **Termux:** https://termux.com
- **Android SDK:** https://developer.android.com/
- **Gradle Docs:** https://gradle.org/
- **Kotlin:** https://kotlinlang.org/

---

**Başarıyla APK oluşturdunuz! 🎉**

Herhangi sorununuz olursa GitHub Issues'ta bildir.
