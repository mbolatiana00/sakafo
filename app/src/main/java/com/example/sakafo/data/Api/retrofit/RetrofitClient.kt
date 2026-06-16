package com.example.sakafo.data.Api.retrofit

import com.example.sakafo.data.Api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.sakafo.data.preferences.UserPreferences


object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.217:3000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // ✅ UserPreferences injecté via init()
    private lateinit var userPreferences: UserPreferences

    fun init(userPreferences: UserPreferences) {
        this.userPreferences = userPreferences
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(userPreferences)) // ✅ token ajouté ici
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}