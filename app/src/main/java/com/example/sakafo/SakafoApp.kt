package com.example.sakafo

import android.app.Application
import android.util.Log
import com.example.sakafo.data.Api.retrofit.RetrofitClient
import com.example.sakafo.data.preferences.UserPreferences

class SakafoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(UserPreferences(this))
        Log.d("SakafoApp", "Application initialized")
    }
}