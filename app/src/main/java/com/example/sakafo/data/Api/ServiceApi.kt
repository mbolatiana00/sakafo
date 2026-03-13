package com.example.sakafo.data.Api

import com.example.sakafo.data.Api.model.CreateOrderRequest
import com.example.sakafo.data.Api.model.LoggedUser
import com.example.sakafo.data.Api.model.LoginRequest
import com.example.sakafo.data.Api.model.LoginResponse
import com.example.sakafo.data.Api.model.OrderListResponse
import com.example.sakafo.data.Api.model.OrderResponse
import com.example.sakafo.data.Api.model.RegisterRequest
import com.example.sakafo.data.Api.model.RegisterResponse
import com.example.sakafo.data.Api.model.UpdateOrderStatusRequest
import com.example.sakafo.data.Api.model.VerifyEmailRequest
import com.example.sakafo.data.Api.model.VerifyEmailResponse
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

    @GET("resto_api/users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<LoggedUser>

    // ✅ FIX : @GET("") orphelin supprimé — il causait une IllegalArgumentException au runtime
    @GET("orders")
    suspend fun getAllOrders(@Query("status") status: String?): Response<OrderListResponse>

    // ✅ FIX : getUserOrders doit être déclaré AVANT getOrderById
    // sinon Retrofit interprète "user" comme un {id} et crashe avec une 404 ou mauvais endpoint
    @GET("orders/user/{userId}")
    suspend fun getUserOrders(@Path("userId") userId: Int): Response<OrderListResponse>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<OrderResponse>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<OrderResponse>

    @PATCH("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Int,
        @Body request: UpdateOrderStatusRequest
    ): Response<OrderResponse>

    @PATCH("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: Int): Response<OrderResponse>
}