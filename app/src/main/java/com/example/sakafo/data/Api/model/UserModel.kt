package com.example.sakafo.data.Api.model

// ─── REQUEST MODELS ───────────────────────────────────────────────────────────

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class VerifyEmailRequest(
    val email: String,
    val code: String
)

// ─── RESPONSE MODELS - REGISTER ───────────────────────────────────────────────

data class RegisterResponse(
    val message: String,
    val user: RegisteredUser?
)

data class RegisteredUser(
    val id: Int,
    val name: String,           // ✅ ajouté
    val email: String,
    val phone: String? = null,  // ✅ ajouté
    val role: String,           // ✅ ajouté — défaut "CLIENT"
    val isVerified: Boolean     // ✅ ajouté — défaut false
)

// ─── RESPONSE MODELS - VERIFY EMAIL ──────────────────────────────────────────

data class VerifyEmailResponse(
    val message: String
)

// ─── RESPONSE MODELS - LOGIN ──────────────────────────────────────────────────

data class LoginResponse(
    val token: String?,
    val message: String?,
    val user: LoggedUser?
)

data class LoggedUser(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String,
    val isVerified: Boolean     // ✅ ajouté
)

// ─── RESPONSE MODELS - PROFILE ───────────────────────────────────────────────

data class ProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String,
    val isVerified: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// ─── RESPONSE MODELS - ERREUR ────────────────────────────────────────────────

data class ErrorResponse(
    val message: String
)