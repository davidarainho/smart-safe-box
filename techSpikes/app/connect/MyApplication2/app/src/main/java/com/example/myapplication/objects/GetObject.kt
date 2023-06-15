package com.example.myapplication.objects

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GetObject {

    val baseUrl = "http://34.125.181.207:8081"

    //val okHttpClient  OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES).readTimeout(30, TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS).build()

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}