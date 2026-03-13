package com.example.sakafo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sakafo.data.Api.model.LoggedUser
import com.example.sakafo.data.preferences.UserPreferences
import com.example.sakafo.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class LoginSuccess(val user: LoggedUser, val token: String?) : AuthState()
    data class RegisterSuccess(val message: String, val email: String) : AuthState() // ✅ email ajouté
    data class VerifySuccess(val message: String) : AuthState()                      // ✅ nouveau
    data class Error(val message: String) : AuthState()
}

object AuthValidator {
    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
    }
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^[0-9+\\s\\-]{8,15}$"))
    }
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    val userPreferences: UserPreferences? = null
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // ─── REGISTER ─────────────────────────────────────────────────────────────
    fun register(name: String, email: String, phone: String, password: String) {
        if (name.isBlank()) { _authState.value = AuthState.Error("Nom requis"); return }
        if (email.isBlank()) { _authState.value = AuthState.Error("Email requis"); return }
        if (!AuthValidator.isValidEmail(email)) { _authState.value = AuthState.Error("Email invalide"); return }
        if (phone.isBlank()) { _authState.value = AuthState.Error("Téléphone requis"); return }
        if (!AuthValidator.isValidPhone(phone)) { _authState.value = AuthState.Error("Téléphone invalide"); return }
        if (password.isBlank()) { _authState.value = AuthState.Error("Mot de passe requis"); return }
        if (!AuthValidator.isValidPassword(password)) { _authState.value = AuthState.Error("Mot de passe trop court (min 6)"); return }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.register(name, email, password, phone)
                .onSuccess { message ->
                    // ✅ On passe l'email pour l'utiliser dans VerifyEmailScreen
                    _authState.value = AuthState.RegisterSuccess(message, email)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erreur d'inscription")
                }
        }
    }

    // ─── VERIFY EMAIL ──────────────────────────────────────────────────────────
    fun verifyEmail(email: String, code: String) {
        if (code.isBlank() || code.length != 6) {
            _authState.value = AuthState.Error("Le code doit contenir 6 chiffres")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.verifyEmail(email, code)
                .onSuccess { message ->
                    _authState.value = AuthState.VerifySuccess(message)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Code invalide")
                }
        }
    }

    // ─── LOGIN ─────────────────────────────────────────────────────────────────
    fun login(email: String, password: String) {
        if (email.isBlank()) { _authState.value = AuthState.Error("Email requis"); return }
        if (!AuthValidator.isValidEmail(email)) { _authState.value = AuthState.Error("Email invalide"); return }
        if (!AuthValidator.isValidPassword(password)) { _authState.value = AuthState.Error("Mot de passe trop court (min 6)"); return }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.login(email, password)
                .onSuccess { user ->
                    userPreferences?.saveAuthData(
                        token = "",
                        userId = user.id,
                        name = user.name,
                        email = user.email,
                        phone = user.phone ?: ""
                    )
                    _authState.value = AuthState.LoginSuccess(user, null)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erreur de connexion")
                }
        }
    }

    // ─── LOGOUT ────────────────────────────────────────────────────────────────
    fun logout() {
        viewModelScope.launch {
            userPreferences?.clearAuthData()
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository, userPreferences) as T
    }
}