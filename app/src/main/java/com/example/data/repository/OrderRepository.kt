package com.example.data.repository

import android.util.Log
import com.example.data.local.OrderDao
import com.example.data.model.ServiceOrder
import com.example.data.remote.OrderApiService
import com.example.data.remote.OrderRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OrderRepository(
    private val orderDao: OrderDao
) {
    val allOrders: Flow<List<ServiceOrder>> = orderDao.getAllOrders()

    fun searchOrders(query: String): Flow<List<ServiceOrder>> {
        return if (query.isBlank()) {
            orderDao.getAllOrders()
        } else {
            orderDao.searchOrders(query.trim())
        }
    }

    suspend fun saveOrder(order: ServiceOrder, backendUrl: String? = null): Long = withContext(Dispatchers.IO) {
        val insertedId = orderDao.insertOrder(order)

        // If a backend URL is configured, also push to the Express server
        if (!backendUrl.isNullOrBlank()) {
            try {
                val api = OrderApiService.create(backendUrl)
                val response = api.createOrder(
                    OrderRequest(
                        customerName = order.customerName,
                        phone = order.phone,
                        passportNo = order.passportNo.ifBlank { null },
                        serviceType = ServiceOrder.getCategoryEnglishName(order.serviceType),
                        status = order.status,
                        amount = order.amount
                    )
                )
                if (response.isSuccessful) {
                    val serverOrder = response.body()?.data
                    if (serverOrder?.id != null) {
                        orderDao.updateOrder(order.copy(id = insertedId, serverId = serverOrder.id))
                    }
                }
            } catch (e: Exception) {
                Log.w("OrderRepository", "Backend sync deferred/failed: ${e.message}")
            }
        }

        insertedId
    }

    suspend fun updateOrderStatus(id: Long, newStatus: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(id, newStatus)
    }

    suspend fun updateOrder(order: ServiceOrder) = withContext(Dispatchers.IO) {
        orderDao.updateOrder(order)
    }

    suspend fun deleteOrder(order: ServiceOrder) = withContext(Dispatchers.IO) {
        orderDao.deleteOrder(order)
    }

    suspend fun deleteOrderById(id: Long) = withContext(Dispatchers.IO) {
        orderDao.deleteOrderById(id)
    }

    suspend fun syncWithBackend(backendUrl: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val api = OrderApiService.create(backendUrl)
            val response = api.getOrders()
            if (response.isSuccessful) {
                val remoteOrders = response.body()?.data ?: emptyList()
                val converted = remoteOrders.map { dto ->
                    ServiceOrder(
                        serverId = dto.id,
                        customerName = dto.customerName,
                        phone = dto.phone,
                        passportNo = dto.passportNo ?: "",
                        serviceType = ServiceOrder.getCategoryDisplayName(dto.serviceType),
                        status = dto.status,
                        amount = dto.amount,
                        createdAt = System.currentTimeMillis()
                    )
                }
                if (converted.isNotEmpty()) {
                    orderDao.insertOrders(converted)
                }
                Result.success(converted.size)
            } else {
                Result.failure(Exception("HTTP error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
