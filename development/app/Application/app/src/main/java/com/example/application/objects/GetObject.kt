package com.example.myapplication.objects

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GetObject {

    val baseUrl = "http://34.125.181.207:8081"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}