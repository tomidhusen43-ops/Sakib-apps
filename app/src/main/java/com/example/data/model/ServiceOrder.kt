package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "orders")
data class ServiceOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @Json(name = "_id")
    val serverId: String? = null,
    val customerName: String,
    val phone: String,
    val passportNo: String = "",
    val serviceType: String,
    val status: String = "Pending",
    val amount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    companion object {
        const val STATUS_PENDING = "Pending"
        const val STATUS_PROCESSING = "Processing"
        const val STATUS_COMPLETED = "Completed"

        val STATUS_LIST = listOf(STATUS_PENDING, STATUS_PROCESSING, STATUS_COMPLETED)

        // Maps Bengali and English service category names
        val CATEGORIES = listOf(
            ServiceCategory("ফটোকপি", "Photocopy", "ফটোকপি প্রিন্টিং ও ডকুমেন্ট কপি"),
            ServiceCategory("অনলাইনের কাজ", "Online Work", "বিভিন্ন অনলাইন আবেদন ও সেবা"),
            ServiceCategory("যাওযায়াত", "Jawazat", "ইকামা নবায়ন ও পাসপোর্ট সেবা"),
            ServiceCategory("টিকেট বুকিং", "Flight Ticket", "এয়ার টিকিট ও ফ্লাইট বুকিং"),
            ServiceCategory("ইন্স্যুরেন্স", "Insurance", "মেডিকেল ও সাধারণ ইন্স্যুরেন্স"),
            ServiceCategory("টি ইউ ভি কার্ড", "TUV Card", "টিইউভি ও অন্যান্য টেকনিক্যাল কার্ড")
        )

        fun getCategoryDisplayName(type: String): String {
            val match = CATEGORIES.find { it.bengali.equals(type, ignoreCase = true) || it.english.equals(type, ignoreCase = true) }
            return match?.bengali ?: type
        }

        fun getCategoryEnglishName(type: String): String {
            val match = CATEGORIES.find { it.bengali.equals(type, ignoreCase = true) || it.english.equals(type, ignoreCase = true) }
            return match?.english ?: type
        }
    }
}

data class ServiceCategory(
    val bengali: String,
    val english: String,
    val description: String
)
