package com.codeandart.systemmonitor.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeandart.systemmonitor.util.ByteFormatter
import com.codeandart.systemmonitor.util.SystemInfoHelper
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var stats by remember { mutableStateOf(SystemInfoHelper.getKernelInfo()) }
    var tabIndex by remember { mutableStateOf(0) } // 0: Dashboard, 1: Kernel, 2: Deep Info
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000) // 2 saniyede bir güncelle
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "📊 SysMonitor Pro",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                NavigationBarItem(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    label = { Text("Ana Sayfa") },
                    icon = { Icon(Icons.Default.Info, "Ana Sayfa") }
                )
                NavigationBarItem(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    label = { Text("Kernel") },
                    icon = { Icon(Icons.Default.Settings, "Kernel") }
                )
                NavigationBarItem(
                    selected = tabIndex == 2,
                    onClick = { tabIndex = 2 },
                    label = { Text("Deep Info") },
                    icon = { Icon(Icons.Default.Terminal, "Deep Info") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            when (tabIndex) {
                0 -> DashboardTab()
                1 -> KernelTab(stats)
                2 -> DeepInfoTab()
            }
        }
    }
}

@Composable
fun DashboardTab() {
    var cpuUsage by remember { mutableStateOf(0f) }
    var ramInfo by remember { mutableStateOf(SystemInfoHelper.getRamInfo(androidx.compose.ui.platform.LocalContext.current)) }
    var storageInfo by remember { mutableStateOf(SystemInfoHelper.getStorageInfo()) }
    var thermalInfo by remember { mutableStateOf(SystemInfoHelper.getThermalInfo()) }
    var kernelInfo by remember { mutableStateOf(SystemInfoHelper.getKernelInfo()) }

    LaunchedEffect(Unit) {
        while (true) {
            cpuUsage = SystemInfoHelper.getCpuUsage()
            ramInfo = SystemInfoHelper.getRamInfo(androidx.compose.ui.platform.LocalContext.current)
            storageInfo = SystemInfoHelper.getStorageInfo()
            thermalInfo = SystemInfoHelper.getThermalInfo()
            kotlinx.coroutines.delay(2000)
        }
    }

    Text(
        "📱 Sistem Durumu",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.onSurface
    )

    // CPU Card
    StatCard(
        title = "CPU Kullanımı",
        value = "${cpuUsage.toInt()}%",
        icon = "⚡",
        color = Color(0xFFFF6B35)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // RAM Card
    StatCard(
        title = "RAM Kullanımı",
        value = "${ByteFormatter.formatBytesShort(ramInfo.used)} / ${ByteFormatter.formatBytesShort(ramInfo.total)}",
        subtitle = "${ramInfo.percentUsed.toInt()}%",
        icon = "💾",
        color = Color(0xFF00D9FF)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Storage Card
    StatCard(
        title = "Depolama",
        value = "${ByteFormatter.formatBytesShort(storageInfo.internalUsed)} / ${ByteFormatter.formatBytesShort(storageInfo.internalTotal)}",
        subtitle = "İç Bellek",
        icon = "🗂️",
        color = Color(0xFF1DB8D8)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Thermal Card
    if (thermalInfo.cpuTemp > 0) {
        StatCard(
            title = "CPU Sıcaklığı",
            value = "${thermalInfo.cpuTemp.toInt()}°C",
            icon = "🌡️",
            color = Color(0xFFFF6B6B)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    // Uptime Card
    StatCard(
        title = "Sistem Çalışma Süresi",
        value = ByteFormatter.formatUptimeHuman(kernelInfo.uptime),
        icon = "⏱️",
        color = Color(0xFF7B68EE)
    )
}

@Composable
fun KernelTab(kernelInfo: com.codeandart.systemmonitor.util.KernelInfo) {
    Text(
        "🔧 Kernel Bilgisi",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.onSurface
    )

    InfoCard("Çekirdek Sayısı", "${kernelInfo.cpuCores}")
    InfoCard("CPU Frekansı", kernelInfo.cpuFreq)
    InfoCard("Sistem Versiyonu", kernelInfo.version.take(50))
    InfoCard("Yük Ortalaması", kernelInfo.loadAverage)
}

@Composable
fun DeepInfoTab() {
    var deepData by remember { mutableStateOf("Yükleniyor...") }

    LaunchedEffect(Unit) {
        val rootHelper = com.codeandart.systemmonitor.util.RootHelper
        deepData = "\n=== KERNEL LOGS ===\n"
        deepData += rootHelper.readKernelLogs().take(500)
        deepData += "\n\n=== MEMORY INFO ===\n"
        deepData += rootHelper.readMemoryDetails().take(500)
        deepData += "\n\n=== CPU STATS ===\n"
        deepData += rootHelper.readCpuStats().take(300)
    }

    Text(
        "🔍 Derinlemesine İzleme (Root)",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.onSurface
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            deepData,
            fontSize = 10.sp,
            color = Color(0xFF00D9FF),
            modifier = Modifier.padding(12.dp),
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String = "",
    icon: String = "",
    color: Color = Color(0xFF00D9FF)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.border(
            2.dp,
            color = color.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.medium
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$icon $title",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InfoCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}