package vip.chuansvip.gongyunxiaozhu.util

import android.util.Log
import okio.Utf8
import java.nio.charset.StandardCharsets

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class DecryptDate(key: String) {
    private val secretKey: SecretKeySpec
    private val aes: Cipher

    init {
        secretKey = SecretKeySpec(key.toByteArray(), "AES")
        aes = Cipher.getInstance("AES/ECB/PKCS7Padding")
        aes.init(Cipher.DECRYPT_MODE, secretKey)
    }

    fun decrypt(decrData: String): String {
        val decodedData = hexStringToByteArray(decrData)
        val decryptedBytes = aes.doFinal(decodedData)
        // 解析JSON字符串
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }

    fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4)
                    + Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}





