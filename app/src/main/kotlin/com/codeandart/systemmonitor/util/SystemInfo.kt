package com.codeandart.systemmonitor.util

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.os.Environment
import android.os.Process
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.roundToInt

data class SystemStats(
    val cpuUsage: Float = 0f,
    val ramUsage: RamInfo = RamInfo(),
    val storageInfo: StorageInfo = StorageInfo(),
    val batteryInfo: BatteryInfo = BatteryInfo(),
    val thermalInfo: ThermalInfo = ThermalInfo(),
    val networkInfo: NetworkInfo = NetworkInfo(),
    val kernelInfo: KernelInfo = KernelInfo(),
    val selinuxStatus: String = "N/A",
    val processCount: Int = 0
)

data class RamInfo(
    val total: Long = 0L,
    val available: Long = 0L,
    val used: Long = 0L,
    val cached: Long = 0L,
    val buffers: Long = 0L,
    val percentUsed: Float = 0f
)

data class StorageInfo(
    val internalTotal: Long = 0L,
    val internalFree: Long = 0L,
    val internalUsed: Long = 0L,
    val externalTotal: Long = 0L,
    val externalFree: Long = 0L,
    val externalUsed: Long = 0L
)

data class BatteryInfo(
    val level: Int = 0,
    val temperature: Int = 0,
    val voltage: Int = 0,
    val health: String = "Unknown",
    val status: String = "Unknown",
    val technology: String = "Unknown"
)

data class ThermalInfo(
    val cpuTemp: Float = 0f,
    val batteryTemp: Float = 0f,
    val gpuTemp: Float = 0f,
    val systemTemp: Float = 0f
)

data class NetworkInfo(
    val wifiSignal: Int = 0,
    val mobileSignal: Int = 0,
    val rxBytes: Long = 0L,
    val txBytes: Long = 0L
)

data class KernelInfo(
    val version: String = "Unknown",
    val uptime: Long = 0L,
    val loadAverage: String = "N/A",
    val cpuCores: Int = 0,
    val cpuFreq: String = "N/A"
)

object SystemInfoHelper {
    
    fun getCpuUsage(): Float {
        return try {
            val reader = BufferedReader(InputStreamReader(Runtime.getRuntime().exec("cat /proc/stat").inputStream))
            val statLine = reader.readLine() ?: return 0f
            reader.close()
            
            val stat = statLine.split("\\s+".toRegex()).drop(1).take(4).map { it.toLongOrNull() ?: 0L }
            if (stat.size < 4) return 0f
            
            val user = stat[0]
            val nice = stat[1]
            val system = stat[2]
            val idle = stat[3]
            
            val total = user + nice + system + idle
            if (total == 0L) return 0f
            
            val usage = ((user + system) * 100f) / total
            usage.coerceIn(0f, 100f)
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }
    }
    
    fun getRamInfo(context: Context): RamInfo {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memInfo)
            
            val reader = BufferedReader(InputStreamReader(Runtime.getRuntime().exec("cat /proc/meminfo").inputStream))
            var line: String?
            val memMap = mutableMapOf<String, Long>()
            
            while (reader.readLine().also { line = it } != null) {
                val parts = line!!.split("\\s+".toRegex())
                if (parts.size >= 2) {
                    memMap[parts[0].removeSuffix(":")] = parts[1].toLongOrNull()?.times(1024) ?: 0L
                }
            }
            reader.close()
            
            val total = memMap["MemTotal"] ?: memInfo.totalMemory
            val available = memMap["MemAvailable"] ?: memInfo.availMem
            val cached = memMap["Cached"] ?: 0L
            val buffers = memMap["Buffers"] ?: 0L
            val used = total - available
            val percentUsed = if (total > 0) (used * 100f) / total else 0f
            
            RamInfo(
                total = total,
                available = available,
                used = used,
                cached = cached,
                buffers = buffers,
                percentUsed = percentUsed
            )
        } catch (e: Exception) {
            e.printStackTrace()
            RamInfo()
        }
    }
    
    fun getStorageInfo(): StorageInfo {
        return try {
            val internalDir = Environment.getDataDirectory()
            val externalDir = Environment.getExternalStorageDirectory()
            
            val internalTotal = internalDir.totalSpace
            val internalFree = internalDir.freeSpace
            val internalUsed = internalTotal - internalFree
            
            val externalTotal = externalDir.totalSpace
            val externalFree = externalDir.freeSpace
            val externalUsed = externalTotal - externalFree
            
            StorageInfo(
                internalTotal = internalTotal,
                internalFree = internalFree,
                internalUsed = internalUsed,
                externalTotal = externalTotal,
                externalFree = externalFree,
                externalUsed = externalUsed
            )
        } catch (e: Exception) {
            e.printStackTrace()
            StorageInfo()
        }
    }
    
    fun getThermalInfo(): ThermalInfo {
        return try {
            val cpuTemp = readThermalZone("/sys/class/thermal/thermal_zone0/temp")
            val batteryTemp = readThermalZone("/sys/class/power_supply/battery/temp")
            val gpuTemp = readThermalZone("/sys/class/thermal/thermal_zone1/temp")
            val systemTemp = readThermalZone("/sys/class/thermal/thermal_zone2/temp")
            
            ThermalInfo(
                cpuTemp = cpuTemp,
                batteryTemp = batteryTemp,
                gpuTemp = gpuTemp,
                systemTemp = systemTemp
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ThermalInfo()
        }
    }
    
    private fun readThermalZone(path: String): Float {
        return try {
            val file = File(path)
            if (!file.exists()) return 0f
            val temp = file.readText().trim().toLongOrNull() ?: return 0f
            (temp / 1000f).roundToInt().toFloat()
        } catch (e: Exception) {
            0f
        }
    }
    
    fun getKernelInfo(): KernelInfo {
        return try {
            val version = readFile("/proc/version").take(60)
            val uptime = getUptime()
            val loadAvg = readFile("/proc/loadavg").take(30)
            val cpuCores = Runtime.getRuntime().availableProcessors()
            val cpuFreq = getCpuFrequency()
            
            KernelInfo(
                version = version,
                uptime = uptime,
                loadAverage = loadAvg,
                cpuCores = cpuCores,
                cpuFreq = cpuFreq
            )
        } catch (e: Exception) {
            e.printStackTrace()
            KernelInfo()
        }
    }
    
    private fun readFile(path: String): String {
        return try {
            File(path).readText().trim()
        } catch (e: Exception) {
            "N/A"
        }
    }
    
    private fun getUptime(): Long {
        return try {
            val uptime = File("/proc/uptime").readText().split(" ")[0].toLongOrNull() ?: 0L
            uptime
        } catch (e: Exception) {
            0L
        }
    }
    
    private fun getCpuFrequency(): String {
        return try {
            val freq = File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq").readText().trim().toLongOrNull() ?: 0L
            "${freq / 1000} MHz"
        } catch (e: Exception) {
            "N/A"
        }
    }
    
    fun getSELinuxStatus(): String {
        return try {
            val process = Runtime.getRuntime().exec("getenforce")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val status = reader.readLine() ?: "Unknown"
            reader.close()
            status
        } catch (e: Exception) {
            "N/A"
        }
    }
    
    fun getProcessCount(context: Context): Int {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getRunningAppProcesses()?.size ?: 0
        } catch (e: Exception) {
            0
        }
    }
}