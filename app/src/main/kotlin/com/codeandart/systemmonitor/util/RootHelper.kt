package com.codeandart.systemmonitor.util

import java.io.BufferedReader
import java.io.InputStreamReader

object RootHelper {
    
    fun hasRoot(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            process.destroy()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun executeAsRoot(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            reader.close()
            process.waitFor()
            output.toString().trim()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
    
    fun readKernelLogs(): String {
        return try {
            val process = Runtime.getRuntime().exec("dmesg")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            var count = 0
            while (reader.readLine().also { line = it } != null && count < 100) {
                output.append(line).append("\n")
                count++
            }
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            "Kernel logs not available"
        }
    }
    
    fun readSystemLogs(): String {
        return try {
            val process = Runtime.getRuntime().exec("logcat -d -v threadtime -n 1 -r 100 -f /dev/null")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            var count = 0
            while (reader.readLine().also { line = it } != null && count < 100) {
                output.append(line).append("\n")
                count++
            }
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            "System logs not available"
        }
    }
    
    fun readProcessInfo(): String {
        return try {
            val process = Runtime.getRuntime().exec("ps -aux")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            var count = 0
            while (reader.readLine().also { line = it } != null && count < 50) {
                output.append(line).append("\n")
                count++
            }
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            "Process info not available"
        }
    }
    
    fun readNetworkStats(): String {
        return try {
            val process = Runtime.getRuntime().exec("cat /proc/net/dev")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            "Network stats not available"
        }
    }
    
    fun readMemoryDetails(): String {
        return try {
            val process = Runtime.getRuntime().exec("cat /proc/meminfo")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            "Memory details not available"
        }
    }
    
    fun readCpuStats(): String {
        return try {
            val process = Runtime.getRuntime().exec("cat /proc/stat")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            "CPU stats not available"
        }
    }
    
    fun readIoStats(): String {
        return try {
            val process = Runtime.getRuntime().exec("cat /proc/diskstats")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            "I/O stats not available"
        }
    }
    
    fun readInterrupts(): String {
        return try {
            val process = Runtime.getRuntime().exec("cat /proc/interrupts")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            var count = 0
            while (reader.readLine().also { line = it } != null && count < 30) {
                output.append(line).append("\n")
                count++
            }
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            "Interrupts not available"
        }
    }
    
    fun readVmStats(): String {
        return try {
            val process = Runtime.getRuntime().exec("cat /proc/vmstat")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            "VM stats not available"
        }
    }
    
    fun readGpuInfo(): String {
        return try {
            val output = StringBuilder()
            // Qualcomm Adreno
            val adreno = readFile("/sys/class/kgsl/kgsl-3d0/devfreq/cur_freq")
            if (adreno.isNotEmpty()) {
                output.append("GPU Frequency (Adreno): ").append(adreno).append(" Hz\n")
            }
            output.toString().trim()
        } catch (e: Exception) {
            "GPU info not available"
        }
    }
    
    private fun readFile(path: String): String {
        return try {
            java.io.File(path).readText().trim()
        } catch (e: Exception) {
            ""
        }
    }
}