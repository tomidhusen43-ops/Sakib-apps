package com.example.data.remote

import com.example.data.model.ServiceOrder
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class OrderRequest(
    val customerName: String,
    val phone: String,
    val passportNo: String? = null,
    val serviceType: String,
    val status: String = "Pending",
    val amount: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class OrderListResponse(
    val success: Boolean = true,
    val data: List<OrderRemoteDto>? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class OrderSingleResponse(
    val success: Boolean = true,
    val data: OrderRemoteDto? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class OrderRemoteDto(
    @Json(name = "_id")
    val id: String? = null,
    val customerName: String = "",
    val phone: String = "",
    val passportNo: String? = null,
    val serviceType: String = "",
    val status: String = "Pending",
    val amount: Double = 0.0,
    val createdAt: String? = null
)

interface OrderApiService {
    @POST("api/orders")
    suspend fun createOrder(@Body request: OrderRequest): Response<OrderSingleResponse>

    @GET("api/orders")
    suspend fun getOrders(): Response<OrderListResponse>

    companion object {
        private const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/"

        fun create(baseUrl: String = DEFAULT_BASE_URL): OrderApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

            return Retrofit.Builder()
                .baseUrl(normalizedUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(OrderApiService::class.java)
        }
    }
}
