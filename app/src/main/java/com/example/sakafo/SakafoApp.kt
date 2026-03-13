package com.example.sakafo

import android.app.Application
import android.util.Log

class SakafoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("SakafoApp", "Application initialized")
    }
}