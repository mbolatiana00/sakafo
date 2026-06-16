package com.example.sakafo.data.Api

import com.example.sakafo.data.Api.model.CreateOrderRequest
import com.example.sakafo.data.Api.model.CreateOrderResponse
import com.example.sakafo.data.Api.model.CreatePaymentRequest
import com.example.sakafo.data.Api.model.CreatePaymentResponse
import com.example.sakafo.data.Api.model.ForgotPasswordRequest
import com.example.sakafo.data.Api.model.LoggedUser
import com.example.sakafo.data.Api.model.LoginRequest
import com.example.sakafo.data.Api.model.LoginResponse
import com.example.sakafo.data.Api.model.MessageResponse
import com.example.sakafo.data.Api.model.Order
import com.example.sakafo.data.Api.model.OrderDetailResponse
import com.example.sakafo.data.Api.model.OrderListResponse
import com.example.sakafo.data.Api.model.OrderResponse
import com.example.sakafo.data.Api.model.RegisterRequest
import com.example.sakafo.data.Api.model.RegisterResponse
import com.example.sakafo.data.Api.model.ResetPasswordRequest
import com.example.sakafo.data.Api.model.UpdateOrderResponse
import com.example.sakafo.data.Api.model.UpdateOrderStatusRequest
import com.example.sakafo.data.Api.model.VerifyEmailRequest
import com.example.sakafo.data.Api.model.VerifyEmailResponse
import com.example.sakafo.data.Api.model.RestaurantListResponse
import com.example.sakafo.data.Api.model.RestaurantDetailResponse
import com.example.sakafo.data.Api.model.MenuItemListResponse
import com.example.sakafo.data.Api.model.CategoriesResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("resto_api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("resto_api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("resto_api/users/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<VerifyEmailResponse>

    @POST("resto_api/users/forgotPassword")
    suspend fun forgotPassword(@Body request : ForgotPasswordRequest) : Response <MessageResponse>

    @POST("resto_api/users/resetPassword")
    suspend fun  resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>

    @GET("resto_api/users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<LoggedUser>

    @GET("resto_api/orders")
    suspend fun getAllOrders(@Query("status") status: String?): Response<OrderListResponse>


    @GET("resto_api/orders/user/{userId}")
    suspend fun getUserOrders(@Path("userId") userId: Int): Response<OrderListResponse>

    @GET("resto_api/orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<Order>  // ✅ Response<Order> directement
    @POST("resto_api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<CreateOrderResponse> // ✅
    @PATCH("resto_api/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Int,
        @Body request: UpdateOrderStatusRequest
    ): Response<UpdateOrderResponse>  // ✅ nouveau typeResponse<OrderResponse>

    @PATCH("resto_api/orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: Int): Response<OrderResponse>

    @POST("resto_api/payments")
    suspend fun createPayment(@Body request: CreatePaymentRequest) : Response<CreatePaymentResponse>


        @GET("resto_api/restaurants")
        suspend fun getRestaurants(
            @Query("page")   page:   Int    = 1,
            @Query("limit")  limit:  Int    = 20,
            @Query("search") search: String? = null,
        ): Response<RestaurantListResponse>

        @GET("resto_api/restaurants/{id}")
        suspend fun getRestaurantById(
            @Path("id") id: Int
        ): Response<RestaurantDetailResponse>

        @GET("resto_api/restaurants/{id}/menu")
        suspend fun getMenuByRestaurant(
            @Path("id")      restaurantId: Int,
            @Query("page")   page:         Int     = 1,
            @Query("limit")  limit:        Int     = 20,
            @Query("category") category:   String? = null,
        ): Response<MenuItemListResponse>

        @GET("resto_api/restaurants/menu/all")
        suspend fun getAllMenuItems(
            @Query("page")     page:     Int     = 1,
            @Query("limit")    limit:    Int     = 20,
            @Query("category") category: String? = null,
            @Query("search")   search:   String? = null,
        ): Response<MenuItemListResponse>

        @GET("resto_api/restaurants/categories")
        suspend fun getCategories(): Response<CategoriesResponse>



}

