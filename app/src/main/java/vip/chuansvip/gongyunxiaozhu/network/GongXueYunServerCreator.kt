package vip.chuansvip.gongyunxiaozhu.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GongXueYunServerCreator {
    private const val Base_URL = "https://api.moguding.net:9000/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(Base_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}