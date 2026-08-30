package com.example.model

enum class EquipmentStatus {
    VERIFIED,     // Green: Operational & recently verified
    STALE,        // Yellow: Verification older than 12h
    UNAVAILABLE   // Red: Under maintenance or offline
}

data class Hospital(
    val id: String,
    val name: String,
    val tagline: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val phone: String,
    val email: String,
    val operatingHours: String,
    val hasEmergency24x7: Boolean = true,
    val rating: Float = 4.8f,
    val totalBeds: Int = 350,
    val icuBeds: Int = 45,
    val ventilatorBeds: Int = 20,
    val accreditation: String = "NABH & JCI Certified",
    val authorizedOfficer: String = "Dr. S. Roy (Chief Biomedical Admin)",
    val zone: String = "Central Delhi",
    val nearestMetro: String = "Delhi Metro Station",
    val categoryType: String = "Super Speciality",
    val emergencyPhone: String = "102 / 108",
    val specialties: List<String> = listOf("Trauma & ER", "Cardiology", "Neurology", "Radiology")
)
