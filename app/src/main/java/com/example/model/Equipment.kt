package com.example.model

enum class EquipmentCategory {
    IMAGING,        // MRI, CT, X-Ray, Ultrasound
    CRITICAL_CARE,  // Ventilator, Oxygen, Dialysis
    DIAGNOSTICS,    // ECG, Patient Monitor, Defibrillator
    MOBILITY_ASSIST // Wheelchair, Stretcher
}

data class ConfidenceFactors(
    val recencyScore: Int,       // Max 40
    val authorizedStaff: Int,    // Max 25
    val statusConsistency: Int,  // Max 20
    val maintenanceHealth: Int   // Max 15
) {
    val totalScore: Int
        get() = (recencyScore + authorizedStaff + statusConsistency + maintenanceHealth).coerceIn(0, 100)
}

data class Equipment(
    val id: String,
    val hospitalId: String,
    val hospitalName: String,
    val name: String,
    val modelSpec: String,
    val category: EquipmentCategory,
    val status: EquipmentStatus,
    val lastVerifiedMinutesAgo: Int,
    val verifiedBy: String,
    val confidenceScore: Int,
    val confidenceLabel: String,
    val distanceKm: Double,
    val maintenanceStatus: String,
    val roomLocation: String,
    val estimatedWaitMinutes: Int,
    val is24x7: Boolean = true,
    val confidenceFactors: ConfidenceFactors,
    val notes: String = ""
) {
    val verificationTimeFormatted: String
        get() = when {
            lastVerifiedMinutesAgo < 60 -> "$lastVerifiedMinutesAgo minutes ago"
            lastVerifiedMinutesAgo < 1440 -> "${lastVerifiedMinutesAgo / 60} hours ago"
            else -> "${lastVerifiedMinutesAgo / 1440} days ago"
        }
}
