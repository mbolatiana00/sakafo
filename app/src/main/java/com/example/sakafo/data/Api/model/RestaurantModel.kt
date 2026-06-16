package com.example.sakafo.data.Api.model
import com.google.gson.annotations.SerializedName

// ── Modèles de données ────────────────────────────────────────────────────────

data class Restaurant(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val address: String?,
    val phone: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isOpen: Boolean = true,
)

data class MenuItem(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val category: String,
    val isAvailable: Boolean = true,
    val restaurantId: Int,
    val restaurant: RestaurantSummary? = null,  // inclus dans /menu/all
)

// Version légère du restaurant (incluse dans MenuItem via /menu/all)
data class RestaurantSummary(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val isOpen: Boolean = true,
)

// ── Réponses API ──────────────────────────────────────────────────────────────

data class RestaurantListResponse(
    val success: Boolean,
    val restaurants: List<Restaurant>,
    val total: Int,
    val page: Int,
    val totalPages: Int,
)

data class RestaurantDetailResponse(
    val success: Boolean,
    val restaurant: Restaurant,
)

data class MenuItemListResponse(
    val success: Boolean,
    val menuItems: List<MenuItem>,
    val total: Int,
    val page: Int,
    val totalPages: Int,
)

data class CategoriesResponse(
    val success: Boolean,
    val categories: List<String>,
)
