package com.example.sakafo.data.Api.retrofit

import com.example.sakafo.data.preferences.UserPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userPreferences: UserPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = userPreferences.getToken()

        val request = chain.request().newBuilder()
            .apply {
                if (!token.isNullOrEmpty()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()

        return chain.proceed(request)
    }
}