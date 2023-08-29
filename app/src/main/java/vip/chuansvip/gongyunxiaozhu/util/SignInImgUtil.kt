package vip.chuansvip.gongyunxiaozhu.util


import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import vip.chuansvip.gongyunxiaozhu.bean.UploadingImgResponseBody
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.random.Random

class SignInImgUtil {
    interface SignInImgCallback {
        fun onSignInImgComplete(uploadingImgResponseBody: UploadingImgResponseBody)
    }

    fun S4(): String {
        return (65536 * (1 + Random.nextDouble())).toInt().toString(16).substring(1)
    }

    fun guid(): String {
        return S4() + S4() + S4() + S4() + S4() + S4() + S4() + S4()
    }


    fun uploadingImg(token: String, imgAttachmentByte: Bitmap,callback:SignInImgCallback) {

        // 将 Bitmap 转换为字节数组
        val outputStream = ByteArrayOutputStream()
        imgAttachmentByte.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        //upload/a79f97b18b885ed076ad048279a322aa.jpg
        val key = "upload/" + guid() + ".jpg"


        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("token", token)
            .addFormDataPart("key", key)
            .addFormDataPart(
                "file",
                "1693208424860_59a192fce288bb815c9506d29762ecb.jpg",
                //将字节数组封装到请求体中
                RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)
            )
            .build()

        val request = Request.Builder()
            .url("https://upload.qiniup.com/")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("图片提交结果", "onResponse: $responseBody")
                    val uploadingImgResponseBody =
                        Gson().fromJson(responseBody, UploadingImgResponseBody::class.java)

                    callback.onSignInImgComplete(uploadingImgResponseBody)

                } else {
                    println("Request failed: ${response.code} - ${response.message}")
                }
            }
        })
    }

}

fun main(){
    val timeStamp = System.currentTimeMillis().toString()
    println(timeStamp)
}


