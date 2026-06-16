package com.example.sakafo.data.repository

import com.example.sakafo.data.Api.model.Payment

interface PaymentRepository {
    suspend fun createPayment(orderId: Int, amount: Double, method: String): Payment
}