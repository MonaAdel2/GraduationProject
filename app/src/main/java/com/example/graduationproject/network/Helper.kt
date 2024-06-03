package com.example.graduationproject.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Helper {
    val gson = GsonBuilder().serializeNulls().create()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://f904-196-204-183-233.ngrok-free.app/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}