package com.example.sakafo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sakafo.data.Api.model.*
import com.example.sakafo.data.repository.OrderRepository   // ✅ bon package
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OrderUiState {
    object Idle : OrderUiState()
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class SingleSuccess(val order: Order) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

class OrderViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Idle)
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder.asStateFlow()

    fun getAllOrders(status: OrderStatus? = null) {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                _uiState.value = OrderUiState.Success(orderRepository.getAllOrder(status))
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun getUserOrders(userId: Int) {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                _uiState.value = OrderUiState.Success(orderRepository.getUserOrders(userId))
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun getOrderById(id: Int) {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                val order = orderRepository.getOrderById(id)
                _currentOrder.value = order
                _uiState.value = OrderUiState.SingleSuccess(order)
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun createOrder(request: CreateOrderRequest) {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                val order = orderRepository.createOrder(request)
                _currentOrder.value = order
                _uiState.value = OrderUiState.SingleSuccess(order)
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun updateOrderStatus(orderId: Int, status: OrderStatus) {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                val order = orderRepository.updateOrderStatus(orderId, UpdateOrderStatusRequest(status))
                _currentOrder.value = order
                _uiState.value = OrderUiState.SingleSuccess(order)
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun cancelOrder(orderId: Int) {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                val order = orderRepository.cancelOrder(orderId)
                _currentOrder.value = order
                _uiState.value = OrderUiState.SingleSuccess(order)
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun resetState() { _uiState.value = OrderUiState.Idle }
}

class OrderViewModelFactory(
    private val repository: OrderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}