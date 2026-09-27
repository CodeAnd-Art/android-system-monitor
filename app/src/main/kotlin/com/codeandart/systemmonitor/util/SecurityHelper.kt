package com.codeandart.systemmonitor.util

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64 as Base64Encoding

object SecurityHelper {
    
    /**
     * SHA-256 Hash üretimi
     */
    fun generateSHA256(input: String): String {
        val bytes = input.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * SHA-512 Hash üretimi (Daha güçlü)
     */
    fun generateSHA512(input: String): String {
        val bytes = input.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-512")
        val hashBytes = messageDigest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * PBKDF2 ile password hashing (Salt ile)
     */
    fun generatePBKDF2Hash(password: String, salt: String = generateSecureRandom()): Pair<String, String> {
        try {
            val spec = javax.crypto.spec.PBKDFKeySpec(
                password.toCharArray(),
                salt.toByteArray(),
                100000, // iterations
                256 // key length
            )
            val factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val hash = factory.generateSecret(spec).encoded
            val hashString = Base64.encodeToString(hash, Base64.DEFAULT)
            return Pair(hashString, salt)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair("", "")
        }
    }
    
    /**
     * AES-256 ile şifreleme (GCM mode - Güvenli)
     */
    fun encryptAES256(plainText: String, password: String): String {
        return try {
            // Password'den key türet
            val digest = MessageDigest.getInstance("SHA-256")
            val key = digest.digest(password.toByteArray())
            val secretKey = SecretKeySpec(key, 0, key.size, "AES")
            
            // GCM parametreleri
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val random = SecureRandom()
            val iv = ByteArray(12) // GCM IV uzunluğu
            random.nextBytes(iv)
            
            val gcmSpec = javax.crypto.spec.GCMParameterSpec(128, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
            
            val ciphertext = cipher.doFinal(plainText.toByteArray())
            
            // IV + Ciphertext
            val result = iv + ciphertext
            Base64.encodeToString(result, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    
    /**
     * AES-256 ile şifre çözme
     */
    fun decryptAES256(encryptedText: String, password: String): String {
        return try {
            val decoded = Base64.decode(encryptedText, Base64.DEFAULT)
            val iv = decoded.slice(0 until 12).toByteArray()
            val ciphertext = decoded.slice(12 until decoded.size).toByteArray()
            
            // Key türet
            val digest = MessageDigest.getInstance("SHA-256")
            val key = digest.digest(password.toByteArray())
            val secretKey = SecretKeySpec(key, 0, key.size, "AES")
            
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = javax.crypto.spec.GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            
            val plaintext = cipher.doFinal(ciphertext)
            String(plaintext)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    
    /**
     * Cihaz parmak izi (Device fingerprint)
     */
    fun generateDeviceFingerprint(context: android.content.Context): String {
        val fingerprint = """
            Model: ${android.os.Build.MODEL}
            Manufacturer: ${android.os.Build.MANUFACTURER}
            Device: ${android.os.Build.DEVICE}
            Product: ${android.os.Build.PRODUCT}
            Board: ${android.os.Build.BOARD}
            Display: ${android.os.Build.DISPLAY}
            Fingerprint: ${android.os.Build.FINGERPRINT}
            Hardware: ${android.os.Build.HARDWARE}
            Host: ${android.os.Build.HOST}
            ID: ${android.os.Build.ID}
            Tags: ${android.os.Build.TAGS}
            Type: ${android.os.Build.TYPE}
            User: ${android.os.Build.USER}
            Android Version: ${android.os.Build.VERSION.RELEASE}
            SDK: ${android.os.Build.VERSION.SDK_INT}
            Incremental: ${android.os.Build.VERSION.INCREMENTAL}
        """.trimIndent()
        
        return generateSHA256(fingerprint)
    }
    
    /**
     * Güvenli random string üretimi
     */
    fun generateSecureRandom(length: Int = 32): String {
        val random = SecureRandom()
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING)
    }
    
    /**
     * Biometric/Fingerprint check
     */
    fun canUseBiometric(context: android.content.Context): Boolean {
        return try {
            val biometricManager = androidx.biometric.BiometricManager.from(context)
            biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                    androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Şifreli dizede güvenli kaydı
     */
    fun securePreference(context: android.content.Context, key: String, value: String) {
        try {
            val encryptedValue = encryptAES256(value, generateDeviceFingerprint(context))
            val prefs = context.getSharedPreferences("secure_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString(key, encryptedValue).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Şifreli dizeden okuma
     */
    fun readSecurePreference(context: android.content.Context, key: String): String {
        return try {
            val prefs = context.getSharedPreferences("secure_prefs", android.content.Context.MODE_PRIVATE)
            val encryptedValue = prefs.getString(key, null) ?: return ""
            decryptAES256(encryptedValue, generateDeviceFingerprint(context))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}