package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DemoDataSource
import com.example.data.MedLocateRepository
import com.example.data.ParsedAiQuery
import com.example.data.SortOption
import com.example.data.StatusFilter
import com.example.model.DelhiLocality
import com.example.model.AvailabilityRequest
import com.example.model.Equipment
import com.example.model.Hospital
import com.example.model.NotificationItem
import com.example.model.RequestStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    SEARCH,
    MAP,
    REQUESTS,
    PROFILE,
    HOSPITAL_DETAIL,
    EQUIPMENT_DETAIL
}

data class PatientProfileState(
    val isGuest: Boolean = false,
    val name: String = "Rahul Sharma",
    val phone: String = "+91 98765 43210",
    val email: String = "rahul.sharma@delhihealth.in",
    val preferredLocation: String = "Connaught Place / Central Delhi",
    val notificationsEnabled: Boolean = true,
    val locationPermissionGranted: Boolean = true,
    val selectedLanguage: String = "English"
)

class MedLocateViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MedLocateRepository(application)

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedHospital = MutableStateFlow<Hospital?>(null)
    val selectedHospital: StateFlow<Hospital?> = _selectedHospital.asStateFlow()

    private val _selectedEquipment = MutableStateFlow<Equipment?>(null)
    val selectedEquipment: StateFlow<Equipment?> = _selectedEquipment.asStateFlow()

    // Delhi User Coordinates & Location state
    private val _userLatitude = MutableStateFlow(28.6315) // Connaught Place
    val userLatitude: StateFlow<Double> = _userLatitude.asStateFlow()

    private val _userLongitude = MutableStateFlow(77.2167)
    val userLongitude: StateFlow<Double> = _userLongitude.asStateFlow()

    private val _currentLocation = MutableStateFlow("Connaught Place / Central Delhi")
    val currentLocation: StateFlow<String> = _currentLocation.asStateFlow()

    private val _selectedZoneFilter = MutableStateFlow("All Delhi NCR")
    val selectedZoneFilter: StateFlow<String> = _selectedZoneFilter.asStateFlow()

    // Dynamic Nearest Hospitals in Delhi
    private val _nearestHospitals = MutableStateFlow<List<Hospital>>(emptyList())
    val nearestHospitals: StateFlow<List<Hospital>> = _nearestHospitals.asStateFlow()

    // Search & Filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDistance = MutableStateFlow(35.0)
    val selectedDistance: StateFlow<Double> = _selectedDistance.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow(StatusFilter.ALL)
    val selectedStatusFilter: StateFlow<StatusFilter> = _selectedStatusFilter.asStateFlow()

    private val _selectedSortOption = MutableStateFlow(SortOption.NEAREST)
    val selectedSortOption: StateFlow<SortOption> = _selectedSortOption.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Equipment>>(emptyList())
    val searchResults: StateFlow<List<Equipment>> = _searchResults.asStateFlow()

    // Dialogs & Sheets
    private val _requestingEquipment = MutableStateFlow<Equipment?>(null)
    val requestingEquipment: StateFlow<Equipment?> = _requestingEquipment.asStateFlow()

    private val _confidenceExplainingEquipment = MutableStateFlow<Equipment?>(null)
    val confidenceExplainingEquipment: StateFlow<Equipment?> = _confidenceExplainingEquipment.asStateFlow()

    private val _showLocationSelector = MutableStateFlow(false)
    val showLocationSelector: StateFlow<Boolean> = _showLocationSelector.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    // Map screen selection
    private val _mapSelectedHospital = MutableStateFlow<Hospital?>(DemoDataSource.hospitals.first())
    val mapSelectedHospital: StateFlow<Hospital?> = _mapSelectedHospital.asStateFlow()

    // AI Natural Language Assistant
    private val _aiAssistantActive = MutableStateFlow(false)
    val aiAssistantActive: StateFlow<Boolean> = _aiAssistantActive.asStateFlow()

    private val _aiParsedResult = MutableStateFlow<ParsedAiQuery?>(null)
    val aiParsedResult: StateFlow<ParsedAiQuery?> = _aiParsedResult.asStateFlow()

    // Patient profile
    private val _patientProfile = MutableStateFlow(PatientProfileState())
    val patientProfile: StateFlow<PatientProfileState> = _patientProfile.asStateFlow()

    // Message / Toast Snackbar
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Room DB Flows
    val requests: StateFlow<List<AvailabilityRequest>> = repository.getAllRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedHospitalIds: StateFlow<List<String>> = repository.getSavedHospitalIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        updateHospitalsAndSearch()
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectHospital(hospital: Hospital) {
        val updated = repository.getHospitalById(hospital.id, _userLatitude.value, _userLongitude.value) ?: hospital
        _selectedHospital.value = updated
        _currentScreen.value = AppScreen.HOSPITAL_DETAIL
    }

    fun selectEquipment(equipment: Equipment) {
        val updated = repository.getEquipmentById(equipment.id, _userLatitude.value, _userLongitude.value) ?: equipment
        _selectedEquipment.value = updated
        _currentScreen.value = AppScreen.EQUIPMENT_DETAIL
    }

    fun openHospitalById(hospitalId: String) {
        val hosp = repository.getHospitalById(hospitalId, _userLatitude.value, _userLongitude.value)
        if (hosp != null) {
            selectHospital(hosp)
        }
    }

    fun setMapSelectedHospital(hospital: Hospital) {
        val updated = repository.getHospitalById(hospital.id, _userLatitude.value, _userLongitude.value) ?: hospital
        _mapSelectedHospital.value = updated
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        performSearch()
    }

    fun setDistanceFilter(distanceKm: Double) {
        _selectedDistance.value = distanceKm
        performSearch()
    }

    fun setStatusFilter(filter: StatusFilter) {
        _selectedStatusFilter.value = filter
        performSearch()
    }

    fun setSortOption(sort: SortOption) {
        _selectedSortOption.value = sort
        performSearch()
    }

    fun setZoneFilter(zone: String?) {
        _selectedZoneFilter.value = zone ?: "All Delhi NCR"
        updateHospitalsAndSearch()
    }

    fun selectQuickCategory(keyword: String) {
        _searchQuery.value = keyword
        _selectedStatusFilter.value = StatusFilter.ALL
        _currentScreen.value = AppScreen.SEARCH
        performSearch()
    }

    private fun updateHospitalsAndSearch() {
        val hospitals = repository.getHospitalsWithDistance(
            userLat = _userLatitude.value,
            userLng = _userLongitude.value,
            zoneFilter = _selectedZoneFilter.value
        )
        _nearestHospitals.value = hospitals
        if (_mapSelectedHospital.value == null && hospitals.isNotEmpty()) {
            _mapSelectedHospital.value = hospitals.first()
        }
        performSearch()
    }

    fun performSearch() {
        _searchResults.value = repository.searchEquipment(
            query = _searchQuery.value,
            userLat = _userLatitude.value,
            userLng = _userLongitude.value,
            maxDistanceKm = _selectedDistance.value,
            statusFilter = _selectedStatusFilter.value,
            sortOption = _selectedSortOption.value,
            zoneFilter = _selectedZoneFilter.value
        )
    }

    fun executeNaturalLanguageAiSearch(prompt: String) {
        val parsed = repository.parseNaturalLanguageQuery(prompt)
        _aiParsedResult.value = parsed
        if (parsed.equipmentKeyword != null) {
            _searchQuery.value = parsed.equipmentKeyword
        }
        if (parsed.maxDistanceKm != null) {
            _selectedDistance.value = parsed.maxDistanceKm
        }
        if (parsed.sortOption != null) {
            _selectedSortOption.value = parsed.sortOption
        }
        if (parsed.statusFilter != null) {
            _selectedStatusFilter.value = parsed.statusFilter
        }
        _currentScreen.value = AppScreen.SEARCH
        performSearch()
        _toastMessage.value = "AI applied filters: ${parsed.explanation}"
    }

    fun clearAiParsed() {
        _aiParsedResult.value = null
    }

    fun openRequestDialog(equipment: Equipment) {
        _requestingEquipment.value = equipment
    }

    fun closeRequestDialog() {
        _requestingEquipment.value = null
    }

    fun openConfidenceDialog(equipment: Equipment) {
        _confidenceExplainingEquipment.value = equipment
    }

    fun closeConfidenceDialog() {
        _confidenceExplainingEquipment.value = null
    }

    fun openLocationSelector() {
        _showLocationSelector.value = true
    }

    fun closeLocationSelector() {
        _showLocationSelector.value = false
    }

    fun setLocation(location: String) {
        // Match with known Delhi Localities if available
        val matched = DemoDataSource.delhiLocalities.find {
            location.contains(it.name.split("/")[0].trim(), ignoreCase = true) ||
            it.name.contains(location, ignoreCase = true)
        }

        if (matched != null) {
            _userLatitude.value = matched.latitude
            _userLongitude.value = matched.longitude
            _currentLocation.value = matched.name
        } else {
            _currentLocation.value = location
        }

        _showLocationSelector.value = false
        _toastMessage.value = "Delhi location updated to ${_currentLocation.value}"
        updateHospitalsAndSearch()
    }

    fun setDelhiLocality(locality: DelhiLocality) {
        _userLatitude.value = locality.latitude
        _userLongitude.value = locality.longitude
        _currentLocation.value = locality.name
        _showLocationSelector.value = false
        _toastMessage.value = "Location set to ${locality.name}. Distances recalculated!"
        updateHospitalsAndSearch()
    }

    fun useLiveGpsLocation(lat: Double = 28.5672, lng: Double = 77.2100, label: String = "AIIMS / South Delhi (Live GPS)") {
        _userLatitude.value = lat
        _userLongitude.value = lng
        _currentLocation.value = label
        _showLocationSelector.value = false
        _toastMessage.value = "GPS Location locked: $label. Nearest hospitals updated."
        updateHospitalsAndSearch()
    }

    fun toggleSaveHospital(hospitalId: String) {
        viewModelScope.launch {
            val isSaved = savedHospitalIds.value.contains(hospitalId)
            repository.toggleSaveHospital(hospitalId, isSaved)
            _toastMessage.value = if (isSaved) "Hospital removed from saved" else "Hospital saved to favorites"
        }
    }

    fun submitAvailabilityRequest(
        equipment: Equipment,
        preferredDate: String,
        preferredTime: String,
        patientName: String,
        patientPhone: String,
        notes: String
    ) {
        viewModelScope.launch {
            val hospital = repository.getHospitalById(equipment.hospitalId) ?: DemoDataSource.hospitals.first()
            val req = repository.submitAvailabilityRequest(
                equipment = equipment,
                hospital = hospital,
                patientName = patientName.ifBlank { _patientProfile.value.name },
                patientPhone = patientPhone.ifBlank { _patientProfile.value.phone },
                preferredDate = preferredDate,
                preferredTime = preferredTime,
                notes = notes
            )
            _requestingEquipment.value = null
            _toastMessage.value = "Availability request submitted for ${equipment.name}!"
            _currentScreen.value = AppScreen.REQUESTS
        }
    }

    fun simulateHospitalResponse(request: AvailabilityRequest, newStatus: RequestStatus) {
        viewModelScope.launch {
            repository.simulateHospitalResponse(request, newStatus)
            _toastMessage.value = "Simulated update: Request status marked ${newStatus.name}"
        }
    }

    fun cancelRequest(requestId: String) {
        viewModelScope.launch {
            repository.cancelRequest(requestId)
            _toastMessage.value = "Request removed."
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun toggleGuestMode() {
        val current = _patientProfile.value
        _patientProfile.value = current.copy(isGuest = !current.isGuest)
        _toastMessage.value = if (_patientProfile.value.isGuest) "Browsing in Guest Mode" else "Logged in as ${current.name}"
    }

    fun updateProfile(name: String, phone: String, email: String, preferredLoc: String) {
        _patientProfile.value = _patientProfile.value.copy(
            name = name,
            phone = phone,
            email = email,
            preferredLocation = preferredLoc
        )
        _toastMessage.value = "Profile updated."
    }

    fun toggleLocationPermission() {
        val cur = _patientProfile.value
        _patientProfile.value = cur.copy(locationPermissionGranted = !cur.locationPermissionGranted)
    }

    fun toggleNotifications() {
        val cur = _patientProfile.value
        _patientProfile.value = cur.copy(notificationsEnabled = !cur.notificationsEnabled)
    }

    fun getEquipmentForHospital(hospitalId: String): List<Equipment> {
        return repository.getEquipmentByHospital(hospitalId)
    }

    fun getHospital(hospitalId: String): Hospital? {
        return repository.getHospitalById(hospitalId)
    }
}
