package com.codeandart.systemmonitor.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File

object DeepSystemMonitor {
    
    /**
     * Detaylı bellek analizi
     */
    fun getDetailedMemoryAnalysis(): String {
        return try {
            val output = StringBuilder()
            output.append("=== BELLEK ANALİZİ (DETAYLI) ===\n\n")
            
            val meminfo = File("/proc/meminfo").readText()
            output.append("MemInfo:\n").append(meminfo).append("\n\n")
            
            val smaps = File("/proc/self/smaps_rollup").readText()
            output.append("SMAPS Rollup:\n").append(smaps.take(1000)).append("\n\n")
            
            val pagetypeinfo = File("/proc/pagetypeinfo").readText()
            output.append("Page Type Info (İlk 500):\n").append(pagetypeinfo.take(500))
            
            output.toString()
        } catch (e: Exception) {
            "Bellek analizi kullanılamaz: ${e.message}"
        }
    }
    
    /**
     * CPU thermal throttling analizi
     */
    fun getThermalThrottlingAnalysis(): String {
        return try {
            val output = StringBuilder()
            output.append("=== THERMAL THROTTLING ANALİZİ ===\n\n")
            
            val thermalZones = File("/sys/class/thermal").listFiles() ?: emptyArray()
            for (zone in thermalZones.take(10)) {
                if (zone.isDirectory) {
                    val tempFile = File(zone, "temp")
                    val typeFile = File(zone, "type")
                    
                    if (tempFile.exists() && typeFile.exists()) {
                        val temp = tempFile.readText().trim().toLongOrNull()?.let { it / 1000 } ?: "N/A"
                        val type = typeFile.readText().trim()
                        output.append("$type: ${temp}°C\n")
                    }
                }
            }
            
            output.toString()
        } catch (e: Exception) {
            "Thermal analizi kullanılamaz: ${e.message}"
        }
    }
    
    /**
     * Detaylı I/O analizi
     */
    fun getDetailedIOAnalysis(): String {
        return try {
            val output = StringBuilder()
            output.append("=== I/O ANALİZİ (DETAYLI) ===\n\n")
            
            // Disk istatistikleri
            val diskstats = File("/proc/diskstats").readText()
            output.append("Disk Stats:\n").append(diskstats).append("\n\n")
            
            // I/O wait süresi
            val stat = File("/proc/stat").readText()
            output.append("CPU/IO Stats:\n").append(stat.take(500))
            
            output.toString()
        } catch (e: Exception) {
            "I/O analizi kullanılamaz: ${e.message}"
        }
    }
    
    /**
     * Detaylı işlem bilgisi (ps detaylı)
     */
    fun getDetailedProcessInfo(): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", "ps -eo pid,ppid,%cpu,%mem,vsz,rss,stat,cmd | head -40"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            output.append("=== İŞLEM BİLGİSİ (DETAYLI) ===\n\n")
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            reader.close()
            output.toString()
        } catch (e: Exception) {
            "İşlem bilgisi kullanılamaz: ${e.message}"
        }
    }
    
    /**
     * Sistem call analizi
     */
    fun getSystemCallAnalysis(): String {
        return try {
            val syscalls = File("/proc/sys/kernel").listFiles() ?: emptyArray()
            val output = StringBuilder()
            output.append("=== SISTEM AYARLARI (KERNEL) ===\n\n")
            
            for (file in syscalls.take(20)) {
                if (file.isFile) {
                    output.append("${file.name}: ").append(file.readText().trim().take(50)).append("\n")
                }
            }
            
            output.toString()
        } catch (e: Exception) {
            "Sistem çağrı analizi kullanılamaz: ${e.message}"
        }
    }
    
    /**
     * Ağ ayrıntılı analizi
     */
    fun getDetailedNetworkAnalysis(): String {
        return try {
            val output = StringBuilder()
            output.append("=== AĞ ANALİZİ (DETAYLI) ===\n\n")
            
            // TCP bağlantıları
            val tcp = File("/proc/net/tcp").readText()
            output.append("TCP Bağlantıları (İlk 20):\n")
            output.append(tcp.split("\n").take(20).joinToString("\n")).append("\n\n")
            
            // UDP bağlantıları
            val udp = File("/proc/net/udp").readText()
            output.append("UDP Bağlantıları (İlk 20):\n")
            output.append(udp.split("\n").take(20).joinToString("\n")).append("\n\n")
            
            // Ağ arayüzleri
            val dev = File("/proc/net/dev").readText()
            output.append("Ağ Arayüzleri:\n").append(dev)
            
            output.toString()
        } catch (e: Exception) {
            "Ağ analizi kullanılamaz: ${e.message}"
        }
    }
    
    /**
     * Kernel panic ve oops logları
     */
    fun getKernelPanicLogs(): String {
        return try {
            val process = Runtime.getRuntime().exec("dmesg")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            output.append("=== KERNEL PANIC/OOPS LOGS ===\n\n")
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.contains("panic", ignoreCase = true) || 
                    line!!.contains("oops", ignoreCase = true) ||
                    line!!.contains("error", ignoreCase = true)) {
                    output.append(line).append("\n")
                }
            }
            reader.close()
            
            if (output.length > 50) output.toString() else "Panic/Oops kaydı bulunamadı"
        } catch (e: Exception) {
            "Kernel logları okunamaz: ${e.message}"
        }
    }
    
    /**
     * Cihaz güvenlik durumu
     */
    fun getSecurityStatus(context: android.content.Context): String {
        return try {
            val output = StringBuilder()
            output.append("=== GÜVENLİK DURUMU ===\n\n")
            
            // SELinux
            try {
                val selinux = Runtime.getRuntime().exec("getenforce")
                val reader = BufferedReader(InputStreamReader(selinux.inputStream))
                output.append("SELinux: ").append(reader.readLine() ?: "Unknown").append("\n")
                reader.close()
            } catch (e: Exception) {
                output.append("SELinux: Erişilemez\n")
            }
            
            // Root check
            output.append("Root Erişimi: ").append(checkRoot()).append("\n")
            
            // Bootloader
            output.append("Bootloader: ${android.os.Build.BOOTLOADER}\n")
            
            // Etiketleri kontrol et
            output.append("Build Tags: ${android.os.Build.TAGS}\n")
            output.append("Türü: ${android.os.Build.TYPE}\n")
            output.append("Güvenlik Yamaaları: ${getSecurityPatch()}\n")
            
            output.toString()
        } catch (e: Exception) {
            "Güvenlik durumu kontrol edilemez: ${e.message}"
        }
    }
    
    private fun checkRoot(): String {
        return try {
            Runtime.getRuntime().exec("su -v").waitFor()
            "EVET (ROOT AÇIK)"
        } catch (e: Exception) {
            "HAYIR"
        }
    }
    
    private fun getSecurityPatch(): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                android.os.Build.VERSION.SECURITY_PATCH
            } else {
                "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
    
    /**
     * Tüm systemi detaylı tarayıp rapor oluştur
     */
    fun generateFullSystemReport(context: android.content.Context): String {
        return try {
            val output = StringBuilder()
            output.append("\n╔════════════════════════════════════════════════════════════╗\n")
            output.append("║         SysMonitor Pro - TAM SİSTEM RAPORU                  ║\n")
            output.append("║                                                            ║\n")
            output.append("║  Oluşturma Zamanı: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(java.util.Date())}  ║\n")
            output.append("╚════════════════════════════════════════════════════════════╝\n\n")
            
            output.append(getDetailedMemoryAnalysis()).append("\n\n")
            output.append(getThermalThrottlingAnalysis()).append("\n\n")
            output.append(getDetailedIOAnalysis()).append("\n\n")
            output.append(getDetailedProcessInfo()).append("\n\n")
            output.append(getDetailedNetworkAnalysis()).append("\n\n")
            output.append(getSystemCallAnalysis()).append("\n\n")
            output.append(getKernelPanicLogs()).append("\n\n")
            output.append(getSecurityStatus(context))
            
            output.toString()
        } catch (e: Exception) {
            "Rapor oluşturulamaz: ${e.message}"
        }
    }
}