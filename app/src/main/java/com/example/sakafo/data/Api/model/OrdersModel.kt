package com.example.sakafo.data.Api.model

import com.google.gson.annotations.SerializedName

enum class OrderStatus {
    PENDING, CONFIRMED, IN_PROGRESS, DELIVERED, CANCELED
}

// ✅ FIX : tous les champs non-primitifs passés en nullable
// pour éviter un NullPointerException si l'API renvoie un champ manquant
data class UserSummary(
    val id: Int,
    val name: String,
    val email: String?,
    val phone: String?
)

data class UserShort(
    val id: Int,
    val name: String,
    val phone: String?
)

data class Vehicle(
    val id: Int,
    val brand: String?,
    val model: String?,
    val licensePlate: String?
)

data class Driver(
    val id: Int,
    val user: UserShort,
    val vehicle: Vehicle?
)

data class TrackingPoint(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String
)

data class Delivery(
    val id: Int,
    val driver: Driver?,
    val tracking: List<TrackingPoint>? = null
)

data class Payment(
    val id: Int,
    val amount: Double,
    val status: String,
    val createdAt: String
)

data class Order(
    val id: Int,
    val userId: Int,
    // ✅ FIX : nullable pour éviter crash si l'API renvoie null ou champ absent
    val pickupAddress: String?,
    val deliveryAddress: String?,
    val price: Double,
    // ✅ FIX : status nullable + valeur par défaut pour éviter
    // JsonDataException si l'API renvoie une valeur inconnue
    val status: OrderStatus?,
    val createdAt: String?,
    val updatedAt: String?,
    val user: UserSummary? = null,
    val delivery: Delivery? = null,
    val payment: Payment? = null
)

data class CreateOrderRequest(
    val userId: Int,
    val pickupAddress: String,
    val deliveryAddress: String,
    val price: Double
)

data class UpdateOrderStatusRequest(val status: OrderStatus)

// ✅ FIX : data nullable dans les réponses pour éviter crash si body mal formé
data class OrderResponse(val data: Order?)
data class OrderListResponse(val data: List<Order>?)