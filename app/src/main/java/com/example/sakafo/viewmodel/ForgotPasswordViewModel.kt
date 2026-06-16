package com.example.sakafo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sakafo.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel (private val repository: AuthRepository): ViewModel() {
    var email by mutableStateOf("")
    var code by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)
    var step by mutableStateOf(1) // 1 = email, 2 = code + nouveau mdp

    fun sendOtp() {
        viewModelScope.launch {
            isLoading = true
            repository.forgotPassword(email)
                .onSuccess {
                    message = it
                    step = 2
                }
                .onFailure { message = it.message }
            isLoading = false
        }
    }

    fun resetPassword(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            repository.resetPassword(email, code, newPassword)
                .onSuccess {
                    message = it
                    onSuccess()
                }
                .onFailure { message = it.message }
            isLoading = false
        }
    }
}

class ForgotPasswordViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForgotPasswordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
