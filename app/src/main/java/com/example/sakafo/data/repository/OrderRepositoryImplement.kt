package com.example.sakafo.data.repository

import com.example.sakafo.data.Api.ApiService
import com.example.sakafo.data.Api.model.CreateOrderRequest
import com.example.sakafo.data.Api.model.Order
import com.example.sakafo.data.Api.model.OrderStatus
import com.example.sakafo.data.Api.model.UpdateOrderStatusRequest

class OrderRepositoryImplement(
    private val apiService: ApiService
) : OrderRepository {

    override suspend fun getAllOrder(orderStatus: OrderStatus?): List<Order> {
        val response = apiService.getAllOrders(orderStatus?.name)
        if (response.isSuccessful) {
            // ✅ FIX : data est maintenant nullable → double sécurité avec ?: emptyList()
            return response.body()?.data ?: emptyList()
        }
        throw Exception("Erreur getAllOrders : ${response.code()} ${response.message()}")
    }

    override suspend fun getUserOrders(userId: Int): List<Order> {
        // ✅ FIX : guard userId <= 0 pour éviter un appel inutile avec userId=0
        // (cas où UserPreferences.getUserId() retourne null → 0 dans AuthNavigation)
        if (userId <= 0) return emptyList()

        val response = apiService.getUserOrders(userId)
        if (response.isSuccessful) {
            return response.body()?.data ?: emptyList()
        }
        throw Exception("Erreur getUserOrders : ${response.code()} ${response.message()}")
    }

    override suspend fun getOrderById(id: Int): Order {
        val response = apiService.getOrderById(id)
        if (response.isSuccessful) {
            // ✅ FIX : data est nullable dans OrderResponse
            return response.body()?.data ?: throw Exception("Order introuvable (body null)")
        }
        throw Exception("Erreur getOrderById : ${response.code()} ${response.message()}")
    }

    override suspend fun createOrder(request: CreateOrderRequest): Order {
        val response = apiService.createOrder(request)
        if (response.isSuccessful) {
            return response.body()?.data ?: throw Exception("Création échouée (body null)")
        }
        throw Exception("Erreur createOrder : ${response.code()} ${response.message()}")
    }

    override suspend fun updateOrderStatus(
        orderId: Int,
        request: UpdateOrderStatusRequest
    ): Order {
        val response = apiService.updateOrderStatus(orderId, request)
        if (response.isSuccessful) {
            return response.body()?.data ?: throw Exception("Update échoué (body null)")
        }
        throw Exception("Erreur updateOrderStatus : ${response.code()} ${response.message()}")
    }

    override suspend fun cancelOrder(orderId: Int): Order {
        val response = apiService.cancelOrder(orderId)
        if (response.isSuccessful) {
            return response.body()?.data ?: throw Exception("Annulation échouée (body null)")
        }
        throw Exception("Erreur cancelOrder : ${response.code()} ${response.message()}")
    }
}