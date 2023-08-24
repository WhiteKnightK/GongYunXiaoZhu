package vip.chuansvip.gongyunxiaozhu.util


import vip.chuansvip.gongyunxiaozhu.MyApplication
import vip.chuansvip.gongyunxiaozhu.R
import java.util.Base64

class EncryptionAndDecryptUtils {

    private val secretKey = MyApplication.context.getString(R.string.SecretKEY)

    fun encryptAndPrint(s: String): String {

        val encryptDate = EncryptDate(secretKey)

        val en = encryptDate.encrypt(s)
        println(en)
        val de = encryptDate.decrypt(en)
        var res = Base64.getDecoder().decode(en).toHexString()
        res = res.replace("565162508F92B1055147C6DC4D7FD037565162508F92B1055147C6DC4D7FD037","")
        println("$s ---> $res")
        return res
    }

    fun ByteArray.toHexString(): String {
        return joinToString("") { String.format("%02X", it) }
    }

    fun decryptData(encryptedData: String): String {
        val decryptDate = DecryptDate(secretKey)
        val decryptedData = decryptDate.decrypt(encryptedData)

        return decryptedData
    }










}
