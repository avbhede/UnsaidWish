package com.av.uwish

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


internal object AppConfig {
    private const val BASE_URL = "http://admin.unsaidwish.com/api/"
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}