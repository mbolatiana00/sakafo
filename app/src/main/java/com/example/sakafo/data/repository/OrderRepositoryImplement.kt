package com.example.sakafo.data.repository

import com.example.sakafo.data.Api.ApiService
import com.example.sakafo.data.Api.model.CreateOrderRequest
import com.example.sakafo.data.Api.model.Order
import com.example.sakafo.data.Api.model.OrderDetailResponse
import com.example.sakafo.data.Api.model.OrderStatus
import com.example.sakafo.data.Api.model.UpdateOrderStatusRequest

class OrderRepositoryImplement(
    private val apiService: ApiService
) : OrderRepository {

    override suspend fun getUserOrders(userId: Int): List<Order> {
        if (userId <= 0) return emptyList()
        val response = apiService.getUserOrders(userId)
        if (response.isSuccessful) {
            return response.body()?.orders ?: emptyList()  // ✅ "orders" au lieu de "data"
        }
        throw Exception("Erreur getUserOrders : ${response.code()} ${response.message()}")
    }

    override suspend fun getAllOrder(orderStatus: OrderStatus?): List<Order> {
        val response = apiService.getAllOrders(orderStatus?.name)
        if (response.isSuccessful) {
            return response.body()?.orders ?: emptyList()  // ✅ même fix
        }
        throw Exception("Erreur getAllOrders : ${response.code()} ${response.message()}")
    }

    override suspend fun getOrderById(id: Int): Order {
        val response = apiService.getOrderById(id)
        android.util.Log.d("REPO", "code: ${response.code()}")
        android.util.Log.d("REPO", "body: ${response.body()}")
        android.util.Log.d("REPO", "error: ${response.errorBody()?.string()}")
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Order introuvable (body null)")
        }
        throw Exception("Erreur getOrderById : ${response.code()} ${response.message()}")
    }

    override suspend fun createOrder(request: CreateOrderRequest): Order {
        val response = apiService.createOrder(request)
        if (response.isSuccessful) {
            return response.body()?.order ?: throw Exception("Création échouée (body null)") // ✅
        }
        throw Exception("Erreur createOrder : ${response.code()} ${response.message()}")
    }

    override suspend fun updateOrderStatus(orderId: Int, request: UpdateOrderStatusRequest): Order {
        val response = apiService.updateOrderStatus(orderId, request)
        if (response.isSuccessful) {
            return response.body()?.order ?: throw Exception("Update échoué (body null)") // ✅ "order"
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