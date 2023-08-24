package vip.chuansvip.gongyunxiaozhu.util

import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class EncryptDate(key: String) {
    private val secretKey: SecretKeySpec
    private val aes: Cipher
    private val unpad: (ByteArray) -> ByteArray

    init {
        secretKey = SecretKeySpec(key.toByteArray(), "AES")
        aes = Cipher.getInstance("AES/ECB/PKCS7Padding")


        aes.init(Cipher.ENCRYPT_MODE, secretKey)
        unpad = { data: ByteArray -> data.dropLast(data.last().toInt()).toByteArray() }

    }

    private fun fillMethod(aesStr: String): ByteArray {
        return pad(aesStr.toByteArray(StandardCharsets.UTF_8), AES_BLOCK_SIZE).asByteArrayPadFunction()
    }

    fun encrypt(encrData: String): String {
        val res = aes.doFinal(fillMethod(encrData))
        return Base64.getEncoder().encodeToString(res)
    }

    fun decrypt(decrData: String): String {
        val res = Base64.getDecoder().decode(decrData)
        val msg = aes.doFinal(res).toString(StandardCharsets.UTF_8)
        return unpad(msg.toByteArray(StandardCharsets.UTF_8)).toString(StandardCharsets.UTF_8)
    }

    companion object {
        private const val AES_BLOCK_SIZE = 16

        private fun ByteArray.asByteArrayUnpadFunction(): ByteArray {
            return dropLast(size - last().toInt()).toByteArray()
        }

        private fun ByteArray.asByteArrayPadFunction(): ByteArray {
            val padLength = AES_BLOCK_SIZE - size % AES_BLOCK_SIZE
            val paddedArray = toMutableList()
            for (i in 0 until padLength) {
                paddedArray.add(padLength.toByte())
            }
            return paddedArray.toByteArray()
        }

        private fun pad(input: ByteArray, blockSize: Int): ByteArray {
            val padLength = blockSize - input.size % blockSize
            val paddedArray = input.copyOf(input.size + padLength)
            for (i in 0 until padLength) {
                paddedArray[input.size + i] = padLength.toByte()
            }
            return paddedArray
        }
    }
}
