package com.example.sakafo.data.repository

import com.example.sakafo.data.Api.model.CreateOrderRequest
import com.example.sakafo.data.Api.model.Order
import com.example.sakafo.data.Api.model.OrderStatus
import com.example.sakafo.data.Api.model.UpdateOrderStatusRequest

interface OrderRepository {
    suspend fun getAllOrder(orderStatus: OrderStatus? = null): List<Order>
    suspend fun getUserOrders(userId: Int): List<Order>
    suspend fun getOrderById(id: Int): Order
    suspend fun createOrder(request: CreateOrderRequest): Order
    suspend fun updateOrderStatus(orderId: Int, request: UpdateOrderStatusRequest): Order
    suspend fun cancelOrder(orderId: Int): Order
}