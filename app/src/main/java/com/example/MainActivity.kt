package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.AvailabilityRequestDialog
import com.example.ui.components.ConfidenceScoreExplanationDialog
import com.example.ui.components.LocationSelectorDialog
import com.example.ui.components.MedLocateBottomNavigation
import com.example.ui.screens.EquipmentDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HospitalDetailScreen
import com.example.ui.screens.MapScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RequestsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MedLocateTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MedLocateViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MedLocateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedLocateTheme {
                MedLocateApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MedLocateApp(viewModel: MedLocateViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedHospital by viewModel.selectedHospital.collectAsState()
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val requestingEquipment by viewModel.requestingEquipment.collectAsState()
    val explainingEquipment by viewModel.confidenceExplainingEquipment.collectAsState()
    val showLocationSelector by viewModel.showLocationSelector.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val patientProfile by viewModel.patientProfile.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val requests by viewModel.requests.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearToast()
        }
    }

    // Back handling for nested screens
    BackHandler(enabled = currentScreen == AppScreen.HOSPITAL_DETAIL || currentScreen == AppScreen.EQUIPMENT_DETAIL) {
        viewModel.navigateTo(AppScreen.HOME)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("medlocate_app_root"),
        bottomBar = {
            MedLocateBottomNavigation(
                currentScreen = currentScreen,
                onNavigate = { screen -> viewModel.navigateTo(screen) },
                unreadRequestsCount = requests.size
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                when (screen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToSearch = { viewModel.navigateTo(AppScreen.SEARCH) },
                            onNavigateToMap = { viewModel.navigateTo(AppScreen.MAP) },
                            onViewEquipment = { eq -> viewModel.selectEquipment(eq) },
                            onViewHospital = { hosp -> viewModel.selectHospital(hosp) },
                            onRequestAvailability = { eq -> viewModel.openRequestDialog(eq) },
                            onWhyScore = { eq -> viewModel.openConfidenceDialog(eq) },
                            onOpenLocationSelector = { viewModel.openLocationSelector() }
                        )
                    }

                    AppScreen.SEARCH -> {
                        SearchScreen(
                            viewModel = viewModel,
                            onViewEquipment = { eq -> viewModel.selectEquipment(eq) },
                            onRequestAvailability = { eq -> viewModel.openRequestDialog(eq) },
                            onWhyScore = { eq -> viewModel.openConfidenceDialog(eq) },
                            onOpenLocationSelector = { viewModel.openLocationSelector() }
                        )
                    }

                    AppScreen.MAP -> {
                        MapScreen(
                            viewModel = viewModel,
                            onViewHospital = { hosp -> viewModel.selectHospital(hosp) },
                            onViewEquipment = { eq -> viewModel.selectEquipment(eq) },
                            onRequestAvailability = { eq -> viewModel.openRequestDialog(eq) }
                        )
                    }

                    AppScreen.REQUESTS -> {
                        RequestsScreen(
                            viewModel = viewModel,
                            onNavigateToSearch = { viewModel.navigateTo(AppScreen.SEARCH) }
                        )
                    }

                    AppScreen.PROFILE -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            onViewHospital = { hosp -> viewModel.selectHospital(hosp) }
                        )
                    }

                    AppScreen.HOSPITAL_DETAIL -> {
                        selectedHospital?.let { hosp ->
                            HospitalDetailScreen(
                                hospital = hosp,
                                viewModel = viewModel,
                                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                                onViewEquipment = { eq -> viewModel.selectEquipment(eq) },
                                onRequestAvailability = { eq -> viewModel.openRequestDialog(eq) },
                                onWhyScore = { eq -> viewModel.openConfidenceDialog(eq) }
                            )
                        } ?: run {
                            viewModel.navigateTo(AppScreen.HOME)
                        }
                    }

                    AppScreen.EQUIPMENT_DETAIL -> {
                        selectedEquipment?.let { eq ->
                            EquipmentDetailScreen(
                                equipment = eq,
                                viewModel = viewModel,
                                onBack = { viewModel.navigateTo(AppScreen.SEARCH) },
                                onRequestAvailability = { targetEq -> viewModel.openRequestDialog(targetEq) },
                                onWhyScore = { targetEq -> viewModel.openConfidenceDialog(targetEq) }
                            )
                        } ?: run {
                            viewModel.navigateTo(AppScreen.SEARCH)
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    explainingEquipment?.let { eq ->
        ConfidenceScoreExplanationDialog(
            equipment = eq,
            onDismiss = { viewModel.closeConfidenceDialog() }
        )
    }

    requestingEquipment?.let { eq ->
        AvailabilityRequestDialog(
            equipment = eq,
            hospitalName = eq.hospitalName,
            initialPatientName = patientProfile.name,
            initialPhone = patientProfile.phone,
            onDismiss = { viewModel.closeRequestDialog() },
            onSubmit = { date, time, name, phone, notes ->
                viewModel.submitAvailabilityRequest(
                    equipment = eq,
                    preferredDate = date,
                    preferredTime = time,
                    patientName = name,
                    patientPhone = phone,
                    notes = notes
                )
            }
        )
    }

    if (showLocationSelector) {
        LocationSelectorDialog(
            currentLocation = currentLocation,
            onSelectLocation = { loc -> viewModel.setLocation(loc) },
            onDismiss = { viewModel.closeLocationSelector() }
        )
    }
}
