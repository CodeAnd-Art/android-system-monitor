package com.codeandart.systemmonitor.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeandart.systemmonitor.util.AdbHelper
import com.codeandart.systemmonitor.util.DeepSystemMonitor
import com.codeandart.systemmonitor.util.SecurityHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedScreen() {
    var tabIndex by remember { mutableStateOf(0) } // 0: Security, 1: Deep Monitor, 2: Download
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🔐 Gelişmiş Özellikler",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
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
                0 -> SecurityTab(context)
                1 -> DeepMonitorTab(context)
                2 -> DownloadTab(context)
            }
        }
    }
}

@Composable
fun SecurityTab(context: android.content.Context) {
    var fingerprint by remember { mutableStateOf("") }
    var sha256 by remember { mutableStateOf("") }
    var sha512 by remember { mutableStateOf("") }
    var encrypted by remember { mutableStateOf("") }
    var testString by remember { mutableStateOf("test_data_123") }
    
    LaunchedEffect(Unit) {
        fingerprint = SecurityHelper.generateDeviceFingerprint(context)
        sha256 = SecurityHelper.generateSHA256(testString)
        sha512 = SecurityHelper.generateSHA512(testString)
        encrypted = SecurityHelper.encryptAES256(testString, "güçlü_şifre_2024")
    }
    
    Text(
        "🔐 Güvenlik & Şifreleme",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
    
    // Device Fingerprint
    SecurityCard(
        title = "📱 Cihaz Parmak İzi (Device Fingerprint)",
        content = fingerprint,
        icon = Icons.Default.Security
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // SHA-256
    SecurityCard(
        title = "🔐 SHA-256 Hash",
        content = "Input: $testString\n\nOutput: ${sha256.take(60)}...",
        icon = Icons.Default.Lock
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // SHA-512
    SecurityCard(
        title = "🔐 SHA-512 Hash (Güçlü)",
        content = "Input: $testString\n\nOutput: ${sha512.take(60)}...",
        icon = Icons.Default.Lock
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // AES-256 Şifreleme
    SecurityCard(
        title = "🔒 AES-256 Şifreleme (GCM Mode)",
        content = "Açık: $testString\n\nŞifreli (Base64): ${encrypted.take(60)}...",
        icon = Icons.Default.Lock
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    Button(
        onClick = {
            val randomToken = SecurityHelper.generateSecureRandom()
            testString = randomToken
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00D9FF)
        )
    ) {
        Text(
            "🎲 Yeni Token Üret",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DeepMonitorTab(context: android.content.Context) {
    var deepReport by remember { mutableStateOf("Raporunuzu hazırlanıyor...") }
    var isLoading by remember { mutableStateOf(false) }
    
    Text(
        "🔍 Derinlemesine Sistem Analizi",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
    
    Button(
        onClick = {
            isLoading = true
            deepReport = "Raporunuz hazırlanıyor..."
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6B35)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Yükleniyor...", color = Color.White)
        } else {
            Icon(Icons.Default.Terminal, "Rapor Al", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("📊 Detaylı Rapor Oluştur", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            deepReport,
            fontSize = 10.sp,
            color = Color(0xFF00D9FF),
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

@Composable
fun DownloadTab(context: android.content.Context) {
    Text(
        "📋 İndir & Güncelle",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
    
    DownloadCard(
        title = "📥 GitHub Release Sayfasından İndir",
        description = "En son APK dosyasını GitHub Release sayfasından indir",
        onClick = { AdbHelper.openGithubReleases(context) }
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    DownloadCard(
        title = "⚡ Doğrudan APK İndir",
        description = "APK dosyasını direkt olarak indir ve kur",
        onClick = { AdbHelper.downloadApkDirect(context) }
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    DownloadCard(
        title = "🔧 GitHub Deposu",
        description = "Kaynak kodları, issues ve discussions için repo'yu ziyaret et",
        onClick = { AdbHelper.openGithubRepo(context) }
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    DownloadCard(
        title = "📱 Termux Kurulum Komutu",
        description = """git clone https://github.com/CodeAnd-Art/android-system-monitor
cd android-system-monitor
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk""",
        onClick = { /* Sadece gösterim */ }
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Button(
        onClick = { AdbHelper.showRootRequestDialog(
            context,
            onGranted = { /* Root var */ },
            onDenied = { /* Root yok */ }
        ) },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7B68EE)
        )
    ) {
        Icon(Icons.Default.Lock, "Root İste")
        Spacer(modifier = Modifier.width(8.dp))
        Text("🔓 Root İzni İste", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SecurityCard(
    title: String,
    content: String,
    icon: androidx.compose.material.icons.materialIcon? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.border(
            1.dp,
            Color(0xFF00D9FF).copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.medium
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00D9FF)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                content,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

@Composable
fun DownloadCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, 
                       indication = androidx.compose.material.ripple.rememberRipple()) {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.border(
            2.dp,
            Color(0xFFFF6B35).copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.medium
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource