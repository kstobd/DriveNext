package com.example.drivenext.util

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

/**
 * Утилитный класс для хеширования паролей
 */
object PasswordUtils {
    private const val SALT_LENGTH = 16
    
    /**
     * Хеширует пароль с использованием SHA-256 и добавлением соли
     * @param password исходный пароль
     * @return хешированный пароль с солью в формате "соль:хеш"
     */
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val saltedHash = hashWithSalt(password, salt)
        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        
        return "$saltBase64:$saltedHash"
    }
    
    /**
     * Проверяет соответствие пароля его хешу
     * @param password введенный пароль
     * @param storedHash хешированный пароль из базы данных
     * @return true если пароль соответствует хешу
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) return false
        
        val salt = Base64.decode(parts[0], Base64.NO_WRAP)
        val calculatedHash = hashWithSalt(password, salt)
        
        return calculatedHash == parts[1]
    }
    
    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }
    
    private fun hashWithSalt(password: String, salt: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hash = md.digest(password.toByteArray())
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}