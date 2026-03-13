package com.example.sakafo.data.repository

import com.example.sakafo.data.Api.model.LoggedUser
import com.example.sakafo.data.Api.model.LoginRequest
import com.example.sakafo.data.Api.model.RegisterRequest
import com.example.sakafo.data.Api.model.VerifyEmailRequest
import com.example.sakafo.data.Api.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(name, email, password, phone)
            val response = apiService.register(request)

            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Inscription réussie"
                Result.success(message)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Erreur ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ Nouvelle fonction verifyEmail
    suspend fun verifyEmail(
        email: String,
        code: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = VerifyEmailRequest(email, code)
            val response = apiService.verifyEmail(request)

            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Email vérifié"
                Result.success(message)
            } else {
                val errorMsg = when (response.code()) {
                    400 -> "Code invalide ou expiré"
                    else -> "Erreur ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<LoggedUser> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)

            if (response.isSuccessful) {
                response.body()?.user?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Aucune donnée reçue"))
            } else {
                val errorMsg = when (response.code()) {
                    403 -> "Veuillez vérifier votre email d'abord"
                    401 -> "Email ou mot de passe incorrect"
                    404 -> "Utilisateur introuvable"
                    else -> "Erreur ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(token: String): Result<LoggedUser> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentUser("Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Aucun utilisateur trouvé"))
                } else {
                    Result.failure(Exception("Token invalide"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun logout(): Result<String> =
        withContext(Dispatchers.IO) {
            Result.success("Déconnexion réussie")
        }
}