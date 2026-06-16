package com.example.sakafo.data.repository


import com.example.sakafo.data.Api.ApiService
import com.example.sakafo.data.Api.model.MenuItem
import com.example.sakafo.data.Api.model.Restaurant

interface RestaurantRepository {
    suspend fun getRestaurants(page: Int = 1, limit: Int = 20, search: String? = null): List<Restaurant>
    suspend fun getRestaurantById(id: Int): Restaurant
    suspend fun getMenuByRestaurant(restaurantId: Int, page: Int = 1, category: String? = null): List<MenuItem>
    suspend fun getAllMenuItems(page: Int = 1, category: String? = null, search: String? = null): List<MenuItem>
    suspend fun getCategories(): List<String>
}

class RestaurantRepositoryImplement(
    private val apiService: ApiService
) : RestaurantRepository {

    override suspend fun getRestaurants(page: Int, limit: Int, search: String?): List<Restaurant> {
        val response = apiService.getRestaurants(page, limit, search)
        if (response.isSuccessful) {
            return response.body()?.restaurants ?: emptyList()
        }
        throw Exception("Erreur getRestaurants : ${response.code()} ${response.message()}")
    }

    override suspend fun getRestaurantById(id: Int): Restaurant {
        val response = apiService.getRestaurantById(id)
        if (response.isSuccessful) {
            return response.body()?.restaurant
                ?: throw Exception("Restaurant introuvable (body null)")
        }
        throw Exception("Erreur getRestaurantById : ${response.code()} ${response.message()}")
    }

    override suspend fun getMenuByRestaurant(
        restaurantId: Int,
        page: Int,
        category: String?
    ): List<MenuItem> {
        val response = apiService.getMenuByRestaurant(restaurantId, page, category = category)
        if (response.isSuccessful) {
            return response.body()?.menuItems ?: emptyList()
        }
        throw Exception("Erreur getMenuByRestaurant : ${response.code()} ${response.message()}")
    }

    override suspend fun getAllMenuItems(page: Int, category: String?, search: String?): List<MenuItem> {
        val response = apiService.getAllMenuItems(page, category = category, search = search)
        if (response.isSuccessful) {
            return response.body()?.menuItems ?: emptyList()
        }
        throw Exception("Erreur getAllMenuItems : ${response.code()} ${response.message()}")
    }

    override suspend fun getCategories(): List<String> {
        val response = apiService.getCategories()
        if (response.isSuccessful) {
            return response.body()?.categories ?: listOf("Tout")
        }
        return listOf("Tout")  // fallback silencieux
    }
}