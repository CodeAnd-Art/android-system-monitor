package com.codeandart.systemmonitor.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object AdbHelper {
    
    private const val GITHUB_RELEASE_URL = "https://github.com/CodeAnd-Art/android-system-monitor/releases"
    private const val GITHUB_REPO_URL = "https://github.com/CodeAnd-Art/android-system-monitor"
    private const val GITHUB_RAW_APK = "https://github.com/CodeAnd-Art/android-system-monitor/releases/download/v1.0.0/SysMonitor-Pro-v1.0.0.apk"
    
    /**
     * ADB üzerinden root erişimi talep et
     */
    fun requestRootViaAdb(context: Context): String {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val writer = process.outputStream.bufferedWriter()
            writer.write("id\n")
            writer.flush()
            writer.close()
            
            val reader = process.inputStream.bufferedReader()
            val result = reader.readLine() ?: "Access Denied"
            reader.close()
            process.waitFor()
            
            result
        } catch (e: Exception) {
            "Root erişim reddedildi: ${e.message}"
        }
    }
    
    /**
     * ADB komutu çalıştır
     */
    fun executeAdbCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            reader.close()
            output.trim()
        } catch (e: Exception) {
            "ADB komutu başarısız: ${e.message}"
        }
    }
    
    /**
     * GitHub'dan APK indirme linkini aç
     */
    fun openGithubReleases(context: Context) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setToolbarColor(android.graphics.Color.parseColor("#1A1F3A"))
                .build()
            customTabsIntent.launchUrl(context, Uri.parse(GITHUB_RELEASE_URL))
        } catch (e: Exception) {
            // Fallback
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_RELEASE_URL))
            context.startActivity(intent)
        }
    }
    
    /**
     * Doğrudan APK indir
     */
    fun downloadApkDirect(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_RAW_APK))
            intent.setPackage("com.android.chrome")
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_RAW_APK))
            context.startActivity(intent)
        }
    }
    
    /**
     * GitHub repo'yu aç
     */
    fun openGithubRepo(context: Context) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setToolbarColor(android.graphics.Color.parseColor("#1A1F3A"))
                .build()
            customTabsIntent.launchUrl(context, Uri.parse(GITHUB_REPO_URL))
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_REPO_URL))
            context.startActivity(intent)
        }
    }
    
    /**
     * ADB ile Termux'ta derin izleme başlat
     */
    fun startTermuxMonitoring(context: Context): String {
        return try {
            val commands = arrayOf(
                "su -c 'dmesg | tail -20'",
                "su -c 'cat /proc/meminfo'",
                "su -c 'cat /proc/stat'",
                "su -c 'getenforce'"
            )
            
            val output = StringBuilder()
            for (cmd in commands) {
                val process = Runtime.getRuntime().exec(cmd)
                val reader = process.inputStream.bufferedReader()
                output.append("\n>>> $cmd\n").append(reader.readText())
                reader.close()
            }
            
            output.toString()
        } catch (e: Exception) {
            "Termux komutu başarısız: ${e.message}"
        }
    }
    
    /**
     * Root izinleri kontrol et ve iste
     */
    fun checkAndRequestRootPermission(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -v")
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * ADB Shell komutu (Superuser gerektirebilir)
     */
    fun executeShellCommand(cmd: String): String {
        return try {
            val fullCmd = "su -c '$cmd'"
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", fullCmd))
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            reader.close()
            process.waitFor()
            output.trim()
        } catch (e: Exception) {
            "Shell komutu başarısız: ${e.message}"
        }
    }
    
    /**
     * Kullanıcıya root talep dialog'u göster
     */
    fun showRootRequestDialog(context: Context, onGranted: () -> Unit, onDenied: () -> Unit) {
        try {
            val process = Runtime.getRuntime().exec("su")
            val writer = process.outputStream.bufferedWriter()
            writer.write("exit\n")
            writer.flush()
            writer.close()
            
            val exitValue = process.waitFor()
            if (exitValue == 0) {
                onGranted()
            } else {
                onDenied()
            }
        } catch (e: Exception) {
            onDenied()
        }
    }
}