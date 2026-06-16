package com.example.sakafo.data.repository

import com.example.sakafo.data.Api.model.ForgotPasswordRequest
import com.example.sakafo.data.Api.model.LoggedUser
import com.example.sakafo.data.Api.model.LoginRequest
import com.example.sakafo.data.Api.model.RegisterRequest
import com.example.sakafo.data.Api.model.ResetPasswordRequest
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
                val body = response.body()
                val user = body?.user
                val token = body?.token  // ✅ récupère le token

                if (user != null && token != null) {
                    // ✅ retourne user avec le token inclus
                    Result.success(user.copy(token = token))
                } else {
                    Result.failure(Exception("Aucune donnée reçue"))
                }
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

    suspend fun forgotPassword(email : String): Result<String>{
        return try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Code envoyé")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Erreur"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }

    }
    suspend fun  resetPassword(email: String, code: String, newPassword : String): Result<String>{
        return try {
            val response = apiService.resetPassword(ResetPasswordRequest(email, code, newPassword))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Mot de passe réinitialisé")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Erreur"))
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