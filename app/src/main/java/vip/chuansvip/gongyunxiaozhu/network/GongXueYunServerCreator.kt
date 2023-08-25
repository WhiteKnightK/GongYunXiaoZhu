package vip.chuansvip.gongyunxiaozhu.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GongXueYunServerCreator {
    private const val Base_URL = "https://api.moguding.net:9000/"

    // 创建自定义的 OkHttpClient
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val modifiedRequest = originalRequest.newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; MuMu Build/V417IR; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36 uni-app Html5Plus/1.0 (Immersed/24.0)")
                .build()
            chain.proceed(modifiedRequest)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Base_URL)
        .client(okHttpClient) // 使用自定义的 OkHttpClient
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}
