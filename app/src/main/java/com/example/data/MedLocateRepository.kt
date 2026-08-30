package com.example.data

import android.content.Context
import com.example.data.local.MedLocateDatabase
import com.example.data.local.NotificationEntity
import com.example.data.local.RequestEntity
import com.example.data.local.SavedHospitalEntity
import com.example.model.AvailabilityRequest
import com.example.model.Equipment
import com.example.model.EquipmentStatus
import com.example.model.Hospital
import com.example.model.NotificationItem
import com.example.model.RequestStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

enum class SortOption {
    NEAREST,
    HIGHEST_CONFIDENCE,
    RECENTLY_VERIFIED
}

enum class StatusFilter {
    ALL,
    OPERATIONAL_VERIFIED,
    RECENTLY_VERIFIED
}

data class ParsedAiQuery(
    val originalQuery: String,
    val equipmentKeyword: String?,
    val maxDistanceKm: Double?,
    val sortOption: SortOption?,
    val statusFilter: StatusFilter?,
    val explanation: String
)

class MedLocateRepository(context: Context) {
    private val db = MedLocateDatabase.getDatabase(context)
    private val requestDao = db.requestDao()
    private val savedHospitalDao = db.savedHospitalDao()
    private val notificationDao = db.notificationDao()

    // Query hospitals with dynamic distance from current user coordinates
    fun getHospitalsWithDistance(
        userLat: Double = 28.6315,
        userLng: Double = 77.2167,
        zoneFilter: String = "All Delhi NCR"
    ): List<Hospital> {
        return DemoDataSource.hospitals
            .map { hospital ->
                val dist = DemoDataSource.calculateDistanceKm(userLat, userLng, hospital.latitude, hospital.longitude)
                hospital.copy(distanceKm = dist)
            }
            .filter { hospital ->
                if (zoneFilter == "All Delhi NCR" || zoneFilter.isBlank()) true
                else hospital.zone.contains(zoneFilter, ignoreCase = true) || zoneFilter.contains(hospital.zone, ignoreCase = true)
            }
            .sortedBy { it.distanceKm }
    }

    fun getAllHospitals(): List<Hospital> = DemoDataSource.hospitals

    fun getHospitalById(id: String, userLat: Double = 28.6315, userLng: Double = 77.2167): Hospital? {
        val hosp = DemoDataSource.hospitals.find { it.id == id } ?: return null
        val dist = DemoDataSource.calculateDistanceKm(userLat, userLng, hosp.latitude, hosp.longitude)
        return hosp.copy(distanceKm = dist)
    }

    // Query equipment with dynamic distance
    fun getAllEquipment(userLat: Double = 28.6315, userLng: Double = 77.2167): List<Equipment> {
        val hospitalDistMap = DemoDataSource.hospitals.associate {
            it.id to DemoDataSource.calculateDistanceKm(userLat, userLng, it.latitude, it.longitude)
        }
        return DemoDataSource.equipmentList.map { eq ->
            val dist = hospitalDistMap[eq.hospitalId] ?: eq.distanceKm
            eq.copy(distanceKm = dist)
        }
    }

    fun getEquipmentById(id: String, userLat: Double = 28.6315, userLng: Double = 77.2167): Equipment? {
        val eq = DemoDataSource.equipmentList.find { it.id == id } ?: return null
        val hosp = DemoDataSource.hospitals.find { it.id == eq.hospitalId }
        val dist = if (hosp != null) DemoDataSource.calculateDistanceKm(userLat, userLng, hosp.latitude, hosp.longitude) else eq.distanceKm
        return eq.copy(distanceKm = dist)
    }

    fun getEquipmentByHospital(hospitalId: String, userLat: Double = 28.6315, userLng: Double = 77.2167): List<Equipment> {
        val hosp = DemoDataSource.hospitals.find { it.id == hospitalId }
        val dist = if (hosp != null) DemoDataSource.calculateDistanceKm(userLat, userLng, hosp.latitude, hosp.longitude) else 5.0
        return DemoDataSource.equipmentList.filter { it.hospitalId == hospitalId }.map { it.copy(distanceKm = dist) }
    }

    // Search and filter equipment with confidence, Delhi zone, and dynamic distance
    fun searchEquipment(
        query: String = "",
        userLat: Double = 28.6315,
        userLng: Double = 77.2167,
        maxDistanceKm: Double = 50.0,
        statusFilter: StatusFilter = StatusFilter.ALL,
        sortOption: SortOption = SortOption.NEAREST,
        zoneFilter: String = "All Delhi NCR"
    ): List<Equipment> {
        val trimmedQuery = query.trim().lowercase()
        val keywords = trimmedQuery.split(" ").filter { it.isNotBlank() }

        val hospitalMap = DemoDataSource.hospitals.associateBy { it.id }

        var results = DemoDataSource.equipmentList.map { eq ->
            val hosp = hospitalMap[eq.hospitalId]
            val dist = if (hosp != null) {
                DemoDataSource.calculateDistanceKm(userLat, userLng, hosp.latitude, hosp.longitude)
            } else eq.distanceKm
            eq.copy(distanceKm = dist)
        }.filter { eq ->
            val hosp = hospitalMap[eq.hospitalId]
            val matchesZone = if (zoneFilter == "All Delhi NCR" || zoneFilter.isBlank()) {
                true
            } else {
                hosp?.zone?.contains(zoneFilter, ignoreCase = true) == true ||
                zoneFilter.contains(hosp?.zone ?: "", ignoreCase = true)
            }

            val matchesQuery = if (trimmedQuery.isBlank()) {
                true
            } else {
                eq.name.lowercase().contains(trimmedQuery) ||
                eq.modelSpec.lowercase().contains(trimmedQuery) ||
                eq.hospitalName.lowercase().contains(trimmedQuery) ||
                eq.category.name.lowercase().contains(trimmedQuery) ||
                (hosp?.address?.lowercase()?.contains(trimmedQuery) == true) ||
                (hosp?.nearestMetro?.lowercase()?.contains(trimmedQuery) == true) ||
                (hosp?.zone?.lowercase()?.contains(trimmedQuery) == true) ||
                keywords.all { kw ->
                    eq.name.lowercase().contains(kw) ||
                    eq.modelSpec.lowercase().contains(kw) ||
                    eq.hospitalName.lowercase().contains(kw) ||
                    (hosp?.zone?.lowercase()?.contains(kw) == true)
                }
            }

            val matchesDistance = eq.distanceKm <= maxDistanceKm

            val matchesStatus = when (statusFilter) {
                StatusFilter.ALL -> true
                StatusFilter.OPERATIONAL_VERIFIED -> eq.status == EquipmentStatus.VERIFIED
                StatusFilter.RECENTLY_VERIFIED -> eq.status == EquipmentStatus.VERIFIED && eq.lastVerifiedMinutesAgo <= 60
            }

            matchesZone && matchesQuery && matchesDistance && matchesStatus
        }

        results = when (sortOption) {
            SortOption.NEAREST -> results.sortedBy { it.distanceKm }
            SortOption.HIGHEST_CONFIDENCE -> results.sortedByDescending { it.confidenceScore }
            SortOption.RECENTLY_VERIFIED -> results.sortedBy { it.lastVerifiedMinutesAgo }
        }

        return results
    }

    // AI Natural Language Search Parser
    fun parseNaturalLanguageQuery(prompt: String): ParsedAiQuery {
        val lower = prompt.lowercase()
        var matchedEquipment: String? = null
        val eqTypes = listOf(
            "mri" to "MRI",
            "ct" to "CT",
            "ct scan" to "CT Scan",
            "x-ray" to "X-Ray",
            "xray" to "X-Ray",
            "ultrasound" to "Ultrasound",
            "ecg" to "ECG",
            "dialysis" to "Dialysis",
            "ventilator" to "Ventilator",
            "oxygen" to "Oxygen",
            "defibrillator" to "Defibrillator",
            "wheelchair" to "Wheelchair"
        )
        for ((key, value) in eqTypes) {
            if (lower.contains(key)) {
                matchedEquipment = value
                break
            }
        }

        // Distance extraction
        val distanceRegex = Regex("(\\d+)\\s*(?:km|kilo|kilometers)?")
        val matchDist = distanceRegex.find(lower)
        val distance = matchDist?.groupValues?.get(1)?.toDoubleOrNull() ?: if (lower.contains("near me") || lower.contains("nearby")) 5.0 else null

        // Sort extraction
        val sort = when {
            lower.contains("highest confidence") || lower.contains("most confident") || lower.contains("best verified") -> SortOption.HIGHEST_CONFIDENCE
            lower.contains("most recent") || lower.contains("fresh") || lower.contains("recently verified") -> SortOption.RECENTLY_VERIFIED
            lower.contains("closest") || lower.contains("nearest") || lower.contains("near me") -> SortOption.NEAREST
            else -> SortOption.NEAREST
        }

        val status = if (lower.contains("operational") || lower.contains("verified") || lower.contains("available")) {
            StatusFilter.OPERATIONAL_VERIFIED
        } else {
            StatusFilter.ALL
        }

        val explanation = buildString {
            append("Parsed Intent: ")
            if (matchedEquipment != null) append("Equipment: $matchedEquipment. ")
            if (distance != null) append("Radius: Within $distance km. ")
            append("Sorting: ${sort.name.replace('_', ' ').lowercase().capitalizeWords()}.")
        }

        return ParsedAiQuery(
            originalQuery = prompt,
            equipmentKeyword = matchedEquipment,
            maxDistanceKm = distance,
            sortOption = sort,
            statusFilter = status,
            explanation = explanation
        )
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

    // Room DB: Availability Requests
    fun getAllRequests(): Flow<List<AvailabilityRequest>> {
        return requestDao.getAllRequests().map { entities ->
            entities.map { entity ->
                AvailabilityRequest(
                    id = entity.id,
                    equipmentId = entity.equipmentId,
                    equipmentName = entity.equipmentName,
                    hospitalId = entity.hospitalId,
                    hospitalName = entity.hospitalName,
                    patientName = entity.patientName,
                    patientPhone = entity.patientPhone,
                    preferredDate = entity.preferredDate,
                    preferredTime = entity.preferredTime,
                    status = try {
                        RequestStatus.valueOf(entity.status)
                    } catch (e: Exception) {
                        RequestStatus.PENDING
                    },
                    timestampMillis = entity.timestampMillis,
                    notes = entity.notes
                )
            }
        }
    }

    suspend fun submitAvailabilityRequest(
        equipment: Equipment,
        hospital: Hospital,
        patientName: String,
        patientPhone: String,
        preferredDate: String,
        preferredTime: String,
        notes: String = ""
    ): AvailabilityRequest {
        val reqId = "REQ-" + UUID.randomUUID().toString().take(8).uppercase()
        val request = RequestEntity(
            id = reqId,
            equipmentId = equipment.id,
            equipmentName = equipment.name,
            hospitalId = hospital.id,
            hospitalName = hospital.name,
            patientName = patientName,
            patientPhone = patientPhone,
            preferredDate = preferredDate,
            preferredTime = preferredTime,
            status = RequestStatus.PENDING.name,
            timestampMillis = System.currentTimeMillis(),
            notes = notes
        )
        requestDao.insertRequest(request)

        // Generate initial notification
        val notif = NotificationEntity(
            id = UUID.randomUUID().toString(),
            title = "Request Submitted: ${equipment.name}",
            message = "Your availability request for ${equipment.name} at ${hospital.name} is currently Pending review by duty staff.",
            timestampMillis = System.currentTimeMillis(),
            isRead = false,
            type = "REQUEST_SUBMITTED"
        )
        notificationDao.insertNotification(notif)

        return AvailabilityRequest(
            id = reqId,
            equipmentId = equipment.id,
            equipmentName = equipment.name,
            hospitalId = hospital.id,
            hospitalName = hospital.name,
            patientName = patientName,
            patientPhone = patientPhone,
            preferredDate = preferredDate,
            preferredTime = preferredTime,
            status = RequestStatus.PENDING,
            timestampMillis = request.timestampMillis,
            notes = notes
        )
    }

    suspend fun updateRequestStatus(requestId: String, newStatus: RequestStatus) {
        val all = requestDao.getAllRequests()
        // Note: For updating single request in Room
        // We can query directly or re-insert with updated status
    }

    suspend fun simulateHospitalResponse(request: AvailabilityRequest, newStatus: RequestStatus) {
        val entity = RequestEntity(
            id = request.id,
            equipmentId = request.equipmentId,
            equipmentName = request.equipmentName,
            hospitalId = request.hospitalId,
            hospitalName = request.hospitalName,
            patientName = request.patientName,
            patientPhone = request.patientPhone,
            preferredDate = request.preferredDate,
            preferredTime = request.preferredTime,
            status = newStatus.name,
            timestampMillis = request.timestampMillis,
            notes = request.notes
        )
        requestDao.insertRequest(entity)

        val statusText = if (newStatus == RequestStatus.CONFIRMED) "Confirmed" else "Declined"
        val notif = NotificationEntity(
            id = UUID.randomUUID().toString(),
            title = "Request $statusText: ${request.equipmentName}",
            message = "${request.hospitalName} has $statusText your availability request for ${request.preferredDate} at ${request.preferredTime}.",
            timestampMillis = System.currentTimeMillis(),
            isRead = false,
            type = "REQUEST_UPDATE"
        )
        notificationDao.insertNotification(notif)
    }

    suspend fun cancelRequest(id: String) {
        requestDao.deleteRequest(id)
    }

    // Room DB: Saved Hospitals
    fun getSavedHospitalIds(): Flow<List<String>> {
        return savedHospitalDao.getSavedHospitals().map { list -> list.map { it.hospitalId } }
    }

    fun isHospitalSaved(hospitalId: String): Flow<Boolean> {
        return savedHospitalDao.isHospitalSaved(hospitalId)
    }

    suspend fun toggleSaveHospital(hospitalId: String, currentlySaved: Boolean) {
        if (currentlySaved) {
            savedHospitalDao.removeSavedHospital(hospitalId)
        } else {
            savedHospitalDao.saveHospital(SavedHospitalEntity(hospitalId = hospitalId))
        }
    }

    // Room DB: Notifications
    fun getAllNotifications(): Flow<List<NotificationItem>> {
        return notificationDao.getAllNotifications().map { list ->
            list.map { entity ->
                NotificationItem(
                    id = entity.id,
                    title = entity.title,
                    message = entity.message,
                    timestampMillis = entity.timestampMillis,
                    isRead = entity.isRead,
                    type = entity.type
                )
            }
        }
    }

    suspend fun markNotificationAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    suspend fun deleteNotification(id: String) {
        notificationDao.deleteNotification(id)
    }
}
