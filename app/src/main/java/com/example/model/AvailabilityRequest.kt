package com.example.model

enum class RequestStatus {
    PENDING,
    CONFIRMED,
    DECLINED
}

data class AvailabilityRequest(
    val id: String,
    val equipmentId: String,
    val equipmentName: String,
    val hospitalId: String,
    val hospitalName: String,
    val patientName: String,
    val patientPhone: String,
    val preferredDate: String,
    val preferredTime: String,
    val status: RequestStatus,
    val timestampMillis: Long,
    val notes: String = ""
)
