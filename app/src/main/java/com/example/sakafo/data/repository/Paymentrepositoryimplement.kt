package com.example.sakafo.data.repository


import com.example.sakafo.data.Api.ApiService
import com.example.sakafo.data.Api.model.CreatePaymentRequest
import com.example.sakafo.data.Api.model.Payment

class PaymentRepositoryImplement(
    private val apiService: ApiService
) : PaymentRepository {

    override suspend fun createPayment(orderId: Int, amount: Double, method: String): Payment {
        val response = apiService.createPayment(
            CreatePaymentRequest(orderId = orderId, amount = amount, method = method)
        )
        if (response.isSuccessful) {
            return response.body()?.payment ?: throw Exception("Paiement échoué (body null)")
        }
        throw Exception("Erreur paiement : ${response.code()} ${response.message()}")
    }
}
