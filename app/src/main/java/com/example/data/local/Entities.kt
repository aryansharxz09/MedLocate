package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "availability_requests")
data class RequestEntity(
    @PrimaryKey val id: String,
    val equipmentId: String,
    val equipmentName: String,
    val hospitalId: String,
    val hospitalName: String,
    val patientName: String,
    val patientPhone: String,
    val preferredDate: String,
    val preferredTime: String,
    val status: String, // PENDING, CONFIRMED, DECLINED
    val timestampMillis: Long,
    val notes: String
)

@Entity(tableName = "saved_hospitals")
data class SavedHospitalEntity(
    @PrimaryKey val hospitalId: String,
    val savedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestampMillis: Long,
    val isRead: Boolean = false,
    val type: String
)
