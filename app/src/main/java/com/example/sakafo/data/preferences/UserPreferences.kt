package com.example.sakafo.data.preferences


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"

        private const val KEY_USER_PHONE = "user_phone"
    }

    fun saveAuthData(token: String, userId: Int, name: String, email: String, phone: String) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_PHONE, phone)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun getUserPhone(): String? = prefs.getString(KEY_USER_PHONE, null)
    fun getUserId() : Int? = prefs.getInt(KEY_USER_ID, 0)


    fun isLoggedIn(): Boolean = getToken() != null

    fun clearAuthData() {
        prefs.edit { clear() }
    }
}
