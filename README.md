# 📊 SysMonitor Pro - Android Sistem İzleme Aracı

**SysMonitor Pro**, Android cihazlarının sistem kaynaklarını derinlemesine izleyen ve gösteren profesyonel bir izleme uygulamasıdır.

## ✨ Özellikler

### Temel İzleme (Tüm Cihazlarda)
- 📈 **CPU Kullanımı** - Gerçek zamanlı CPU yüzdesi
- 💾 **RAM Durumu** - Toplam/Kullanılan/Boş bellek
- 🗂️ **Depolama** - İç ve harici bellek kullanımı
- 🌡️ **Termal Sensörler** - CPU/GPU/Sistem sıcaklığı
- ⏱️ **Sistem Çalışma Süresi** - Uptime bilgisi
- ⚡ **Pil Durumu** - Seviye, sıcaklık, voltaj
- 🌐 **Ağ Bilgisi** - İndirme/Yükleme

### Root Erişimiyle Derinlemesine İzleme
- 🔍 **Kernel Logs** - dmesg çıktısı
- 📋 **System Logs** - logcat verisi
- 📊 **/proc/meminfo** - Detaylı bellek bilgisi
- 📈 **/proc/stat** - CPU istatistikleri
- 💿 **/proc/diskstats** - I/O statistikleri
- 🔌 **/proc/interrupts** - Donanım kesmeleri
- 🧠 **/proc/vmstat** - Virtual memory istatistikleri
- 🖥️ **ps aux** - Çalışan süreçler listesi
- 🔐 **SELinux Durumu** - Security context bilgisi
- 🎮 **GPU Bilgisi** - Adreno frekansı (varsa)

### UI/UX
- 🎨 **Material 3 Tasarım** - Modern, güzel arayüz
- 🌓 **Koyu/Açık Tema** - Cihazın temasına otomatik uyum
- 📱 **Duyarlı Tasarım** - Tüm ekran boyutlarında uyumlu
- 🚀 **Hafif ve Hızlı** - Minimum kaynak tüketimi
- 🔄 **Canlı Güncelleme** - 2 saniyede bir otomatik yenileme

## 🚀 Kurulum

### Seçenek 1: APK İndir (En Kolay)
1. [Release sayfasına git](https://github.com/CodeAnd-Art/android-system-monitor/releases)
2. `SysMonitor-Pro.apk` dosyasını indir
3. Android cihazında çalıştır
4. İzinleri onayla

### Seçenek 2: Termux ile Kurulum (Geliştirmeci)

**Gereksinimler:**
- Termux uygulaması (F-Droid veya Play Store)
- Java 11+ (apt install openjdk-17)
- Android SDK

```bash
# 1. Termux'u aç ve paketleri güncelle
pkg update && pkg upgrade

# 2. Gerekli araçları yükle
pkg install git openjdk-17 android-sdk android-sdk-build-tools

# 3. Depoyu klonla
git clone https://github.com/CodeAnd-Art/android-system-monitor
cd android-system-monitor

# 4. APK oluştur
./gradlew assembleDebug

# 5. Oluşturulan APK'yı yükle
adb install app/build/outputs/apk/debug/app-debug.apk

# 6. Uygulamayı başlat
adb shell am start -n com.codeandart.systemmonitor/.MainActivity
```

### Seçenek 3: Android Studio ile Kurulum

```bash
git clone https://github.com/CodeAnd-Art/android-system-monitor
```

1. Android Studio'yu aç
2. File → Open → Proje klasörünü seç
3. Sync gradle dosyaları
4. Build → Build Bundle(s) / APK(s)
5. Cihaza bağla ve çalıştır

## 📊 Kullanım

### Ana Sayfa
- CPU, RAM, Depolama, Sıcaklık ve Uptime kartlarını görüntüle
- Canlı güncellemeler (2 saniyede bir)

### Kernel Sekmesi
- CPU çekirdek sayısı
- CPU frekansı
- Kernel versiyonu
- Sistem yükü

### Derinlemesine İzleme (Deep Info)
- **Root gerektirir**
- Kernel log çıktıları
- Detaylı bellek bilgisi
- CPU istatistikleri
- Donanım kesmeleri
- Çalışan süreçler
- I/O ve VM istatistikleri

## 🔐 İzinler

Uygulama şu izinleri kullanır:

```xml
<!-- Sistem bilgisi okuma -->
<uses-permission android:name="android.permission.GET_TASKS" />
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
<uses-permission android:name="android.permission.READ_LOGS" />
<uses-permission android:name="android.permission.DUMP" />

<!-- Depolamaya erişim -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_ALL_EXTERNAL_STORAGE" />

<!-- Ağ bilgisi -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

## ⚙️ Teknik Detaylar

### Teknoloji Stack
- **Dil:** Kotlin
- **UI Framework:** Jetpack Compose
- **API Level:** Min 24 (Android 7.0), Target 34 (Android 14)
- **Mimari:** MVVM + Coroutines

### Veri Kaynakları

**Standart Erişim (Root Yok):**
- `/proc/stat` - CPU bilgisi
- `/proc/meminfo` - Bellek bilgisi
- `ActivityManager` - RAM durumu
- `Environment` - Depolama bilgisi
- Android API - Pil, ağ, termal bilgisi

**Root Erişimi İle:**
- `dmesg` - Kernel log
- `logcat` - System log
- `ps` - Çalışan süreçler
- `/proc/*` - Tüm sistem dosyaları
- `/sys/class/thermal/*` - Termal sensörler
- SELinux getenforce
- GPU frekansı

## 🛠️ Geliştirme

### Proje Yapısı

```
android-system-monitor/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/codeandart/systemmonitor/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screen/
│   │   │   │   │   │   └── MainScreen.kt
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   ├── util/
│   │   │   │   │   ├── SystemInfo.kt      # Sistem verisi okuma
│   │   │   │   │   ├── RootHelper.kt     # Root komutları
│   │   │   │   │   └── ByteFormatter.kt  # Formatlama
│   │   │   │   └── service/
│   │   │   │       ├── SystemMonitorService.kt
│   │   │   │       └── KernelLogService.kt
│   │   │   └── res/
│   │   │       └── values/
│   │   │           └── strings.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── settings.gradle.kts
├── build.gradle.kts
└── README.md
```

### Kopyala ve Özelleştir

```bash
# Kendi uygulamanızı oluşturmak için
git clone https://github.com/CodeAnd-Art/android-system-monitor your-app
cd your-app

# Paket adını değiştir
# AndroidManifest.xml ve build.gradle.kts dosyalarını düzenle

# Uygulama adını değiştir
echo 'Your App Name' > app/src/main/res/values/strings.xml

# Build et ve çalıştır
./gradlew assembleDebug
```

## 📈 Performans

- **Bellek Kullanımı:** ~40-60 MB
- **CPU Yükü:** Minimal (<2% idle)
- **Pil Tüketimi:** Verimli polling (2s aralık)
- **Başlangıç Zamanı:** <2 saniye

## 🔄 Güncelleme Sıklığı

- **Standart Veriler:** 2 saniye
- **Derinlemesine Veriler:** 5 saniye (manual)
- **Background:** Foreground service ile devam eder

## ⚠️ Uyarılar

- **Root Olmadan:** Temel sistem bilgileri erişilebilir
- **Root İle:** Tüm `/proc` ve kernel verilerine erişebilir
- **SELinux:** Cihaza bağlı olarak veri okuma sınırlandırılabilir
- **Device-Specific:** GPU ve termal sensör verisi cihaza göre değişir

## 🤝 Katkı

Katkılar memnuniyetle karşılanır! 

1. Fork yap
2. Feature branch oluştur (`git checkout -b feature/amazing-feature`)
3. Commit yap (`git commit -m 'Add amazing feature'`)
4. Push yap (`git push origin feature/amazing-feature`)
5. Pull Request aç

## 📄 Lisans

MIT License - Detaylar için [LICENSE](LICENSE) dosyasını kontrol et

## 👨‍💻 Geliştirici

**CodeAnd-Art**
- GitHub: [@CodeAnd-Art](https://github.com/CodeAnd-Art)
- E-posta: [contact@codeandart.com]

## 🙏 Teşekkürler

- Material Design 3 ekibi
- Jetpack Compose topluluğu
- Tüm katkı yapanlara

---

**⭐ Beğendiysen projeyi star'la!**

**🐛 Hata mı buldum? [Issue aç!](https://github.com/CodeAnd-Art/android-system-monitor/issues)**