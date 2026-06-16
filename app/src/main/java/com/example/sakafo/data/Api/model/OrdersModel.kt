package com.example.sakafo.data.Api.model

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PICKED_UP,    // ✅ remplace IN_PROGRESS
    IN_TRANSIT,   // ✅ ajoute
    DELIVERED,
    CANCELED
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



// ── Requête création paiement ─────────────────────────────────────────────────
data class CreatePaymentRequest(
    val orderId: Int,
    val amount: Double,
    val method: String   // "CASH", "CARD", "MOBILE_MONEY"
)

// ── Modèle Payment ────────────────────────────────────────────────────────────
data class Payment(
    val id: Int,
    val orderId: Int,
    val amount: Double,
    val method: String,
    val status: String,
    val paidAt: String? = null
)

// ── Réponse API création paiement ─────────────────────────────────────────────
data class CreatePaymentResponse(
    val message: String?,
    val payment: Payment?
)
data class CreateOrderRequest(
    val userId: Int,
    val pickupAddress: String,
    val deliveryAddress: String,
    val price: Double,
    val restaurantId: Int
)

data class UpdateOrderStatusRequest(val status: OrderStatus)
// ── Réponse création commande ─────────────────────────────────────────────────
data class CreateOrderResponse(
    val message: String?,
    val order: Order?
)
data class UpdateOrderResponse(
    val message: String?,
    val order: Order?   // ✅ "order" au lieu de "data"
)
data class OrderDetailResponse(
    val message: String? = null,
    val order: Order?    = null,  // ✅ "order" au lieu de "data"
    val data: Order?     = null   // ✅ garde "data" au cas où
)
// ── Modèle Order corrigé ──────────────────────────────────────────────────────
data class Order(
    val id: Int,
    val userId: Int,
    val pickupAddress: String?,
    val deliveryAddress: String?,
    val totalPrice: Double,        // ✅ était "price", l'API retourne "totalPrice"
    val status: OrderStatus?,
    val createdAt: String?,
    val updatedAt: String?,
    val user: UserSummary? = null,
    val delivery: Delivery? = null,
    val payment: Order? = null
)

data class OrderResponse(val data: Order?)
// ✅ remplace l'ancien
data class OrderListResponse(
    val count: Int?,
    val orders: List<Order>?  // "orders" au lieu de "data"
)
// ✅ FIX : data nullable dans les réponses pour éviter crash si body mal formé
