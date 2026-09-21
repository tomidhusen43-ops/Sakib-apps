package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ServiceOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<ServiceOrder>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderById(id: Long): Flow<ServiceOrder?>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<ServiceOrder>>

    @Query("""
        SELECT * FROM orders 
        WHERE customerName LIKE '%' || :query || '%' 
           OR phone LIKE '%' || :query || '%' 
           OR passportNo LIKE '%' || :query || '%'
           OR serviceType LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchOrders(query: String): Flow<List<ServiceOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: ServiceOrder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<ServiceOrder>)

    @Update
    suspend fun updateOrder(order: ServiceOrder)

    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, status: String)

    @Delete
    suspend fun deleteOrder(order: ServiceOrder)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: Long)

    @Query("DELETE FROM orders")
    suspend fun clearAll()
}
