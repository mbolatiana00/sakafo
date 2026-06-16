package com.example.sakafo.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sakafo.data.Api.model.Payment
import com.example.sakafo.data.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PaymentUiState {
    object Idle    : PaymentUiState()
    object Loading : PaymentUiState()
    data class Success(val payment: Payment) : PaymentUiState()
    data class Error(val message: String)    : PaymentUiState()
}

class PaymentViewModel(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun createPayment(orderId: Int, amount: Double, method: String) {
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading
            try {
                val payment = paymentRepository.createPayment(orderId, amount, method)
                _uiState.value = PaymentUiState.Success(payment)
            } catch (e: Exception) {
                _uiState.value = PaymentUiState.Error(e.message ?: "Erreur de paiement")
            }
        }
    }

    fun resetState() { _uiState.value = PaymentUiState.Idle }
}

class PaymentViewModelFactory(
    private val repository: PaymentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
