package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoDataSource
import com.example.model.ConfidenceFactors
import com.example.model.DelhiLocality
import com.example.model.Equipment
import com.example.model.EquipmentStatus
import com.example.model.Hospital
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.ConfidenceHigh
import com.example.ui.theme.ConfidenceLow
import com.example.ui.theme.ConfidenceMedium
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.StatusStaleYellow
import com.example.ui.theme.StatusStaleYellowBg
import com.example.ui.theme.StatusStaleYellowText
import com.example.ui.theme.StatusUnavailableRed
import com.example.ui.theme.StatusUnavailableRedBg
import com.example.ui.theme.StatusUnavailableRedText
import com.example.ui.theme.StatusVerifiedGreen
import com.example.ui.theme.StatusVerifiedGreenBg
import com.example.ui.theme.StatusVerifiedGreenText
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.viewmodel.AppScreen

// Patient App Bottom Navigation Bar - Clean Minimalism
@Composable
fun MedLocateBottomNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    unreadRequestsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
        modifier = modifier.testTag("bottom_nav_bar")
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            val items = listOf(
                Triple(AppScreen.HOME, "Home", Icons.Default.Home),
                Triple(AppScreen.SEARCH, "Search", Icons.Default.Search),
                Triple(AppScreen.MAP, "Map", Icons.Default.Map),
                Triple(AppScreen.REQUESTS, "Requests", Icons.Default.MedicalServices),
                Triple(AppScreen.PROFILE, "Profile", Icons.Default.Person)
            )

            items.forEach { (screen, label, icon) ->
                val isSelected = currentScreen == screen ||
                    (screen == AppScreen.HOME && currentScreen == AppScreen.HOSPITAL_DETAIL) ||
                    (screen == AppScreen.SEARCH && currentScreen == AppScreen.EQUIPMENT_DETAIL)

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(screen) },
                    icon = {
                        Box {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(22.dp)
                            )
                            if (screen == AppScreen.REQUESTS && unreadRequestsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(BluePrimary, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        unselectedIconColor = TextMutedLight,
                        unselectedTextColor = TextMutedLight,
                        indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("nav_item_${label.lowercase()}")
                )
            }
        }
    }
}

// Status Indicator Pill (Green: Operational, Yellow: Needs Verification, Red: Unavailable)
@Composable
fun StatusBadge(status: EquipmentStatus, lastVerifiedText: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, iconColor, statusLabel) = when (status) {
        EquipmentStatus.VERIFIED -> Quad(StatusVerifiedGreenBg, StatusVerifiedGreenText, StatusVerifiedGreen, "OPERATIONAL")
        EquipmentStatus.STALE -> Quad(StatusStaleYellowBg, StatusStaleYellowText, StatusStaleYellow, "STALE")
        EquipmentStatus.UNAVAILABLE -> Quad(StatusUnavailableRedBg, StatusUnavailableRedText, StatusUnavailableRed, "UNAVAILABLE")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(iconColor)
            )
            Text(
                text = statusLabel,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// Confidence Score Badge
@Composable
fun ConfidenceScoreBadge(
    score: Int,
    label: String,
    onWhyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badgeColor = when {
        score >= 85 -> ConfidenceHigh
        score >= 65 -> ConfidenceMedium
        else -> ConfidenceLow
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onWhyClick() }
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "$score",
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = badgeColor
        )
        Text(
            text = "/100",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextMutedLight
        )
        Icon(
            imageVector = Icons.Default.HelpOutline,
            contentDescription = "Why this score?",
            tint = TextMutedLight,
            modifier = Modifier.size(13.dp)
        )
    }
}

// Global Trust & Disclaimer Banner
@Composable
fun DisclaimerBanner(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Notice",
            tint = TextMutedLight,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Availability is hospital-reported and may change. Confidence score is for decision support only.",
            fontSize = 10.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center,
            color = TextMutedLight
        )
    }
}

// Healthcare Result Card - Clean Minimalism
@Composable
fun EquipmentResultCard(
    equipment: Equipment,
    onViewDetails: (Equipment) -> Unit,
    onNavigate: (Equipment) -> Unit,
    onRequestAvailability: (Equipment) -> Unit,
    onWhyScore: (Equipment) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetails(equipment) }
            .testTag("equipment_card_${equipment.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Hospital Name & Distance + Status & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = equipment.hospitalName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${equipment.distanceKm} km away",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(
                        status = equipment.status,
                        lastVerifiedText = equipment.verificationTimeFormatted
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Verified ${equipment.verificationTimeFormatted}",
                        fontSize = 9.sp,
                        color = TextMutedLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Inset Service & Confidence Box (Clean Minimalism)
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SERVICE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = TextSecondaryLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = equipment.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (equipment.modelSpec.isNotBlank()) {
                            Text(
                                text = equipment.modelSpec,
                                fontSize = 11.sp,
                                color = TextMutedLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        ConfidenceScoreBadge(
                            score = equipment.confidenceScore,
                            label = equipment.confidenceLabel,
                            onWhyClick = { onWhyScore(equipment) }
                        )
                        Text(
                            text = "Confidence Score",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onViewDetails(equipment) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_details_${equipment.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("View Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onNavigate(equipment) },
                    modifier = Modifier
                        .width(48.dp)
                        .height(44.dp)
                        .testTag("btn_navigate_${equipment.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Directions",
                        modifier = Modifier.size(18.dp)
                    )
                }

                OutlinedButton(
                    onClick = { onRequestAvailability(equipment) },
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("btn_request_${equipment.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineLight)
                ) {
                    Text("Request", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BluePrimary)
                }
            }
        }
    }
}

// "Why this score?" Confidence Score Dialog
@Composable
fun ConfidenceScoreExplanationDialog(
    equipment: Equipment,
    onDismiss: () -> Unit
) {
    val factors = equipment.confidenceFactors
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("MedLocate Confidence Score", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Score Display
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${equipment.confidenceScore}/100",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = equipment.confidenceLabel,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Verified By", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = equipment.verifiedBy,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Score Factors Breakdown:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                FactorProgressItem("Recency of Verification", factors.recencyScore, 40, "Verified ${equipment.verificationTimeFormatted}")
                FactorProgressItem("Authorized Bio-Staff Confirmation", factors.authorizedStaff, 25, equipment.verifiedBy)
                FactorProgressItem("Status Consistency History", factors.statusConsistency, 20, "Consistent operational telemetry")
                FactorProgressItem("Maintenance & Calibration Health", factors.maintenanceHealth, 15, equipment.maintenanceStatus)

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Confidence Score is based on how recently the equipment was verified, whether it was confirmed by authorized hospital staff, recent activity, maintenance information and consistency of reported status.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Disclaimer: This score is decision-support information and does not guarantee availability.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
private fun FactorProgressItem(label: String, score: Int, max: Int, subtext: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("$score / $max pts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { score.toFloat() / max.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
        Text(subtext, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// Availability Request Form Dialog
@Composable
fun AvailabilityRequestDialog(
    equipment: Equipment,
    hospitalName: String,
    initialPatientName: String,
    initialPhone: String,
    onDismiss: () -> Unit,
    onSubmit: (date: String, time: String, name: String, phone: String, notes: String) -> Unit
) {
    var patientName by remember { mutableStateOf(initialPatientName) }
    var patientPhone by remember { mutableStateOf(initialPhone) }
    var preferredDate by remember { mutableStateOf("Today - 21 Aug 2026") }
    var preferredTime by remember { mutableStateOf("11:30 AM") }
    var notes by remember { mutableStateOf("") }

    val dateOptions = listOf("Today - 21 Aug 2026", "Tomorrow - 22 Aug 2026", "23 Aug 2026")
    val timeOptions = listOf("10:00 AM", "11:30 AM", "02:00 PM", "04:30 PM", "06:00 PM", "Urgent / Immediate")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Request Equipment Availability", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = "${equipment.name} • $hospitalName",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Warning Banner
                Surface(
                    color = StatusStaleYellowBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusStaleYellow.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Availability is hospital-reported and may change. This request alerts the on-duty department.",
                        fontSize = 11.sp,
                        color = StatusStaleYellowText,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // Patient Name
                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Full Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_patient_name")
                )

                // Contact Phone
                OutlinedTextField(
                    value = patientPhone,
                    onValueChange = { patientPhone = it },
                    label = { Text("Contact Phone Number") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_patient_phone")
                )

                // Date Picker Quick Select
                Text("Preferred Date", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    dateOptions.forEach { date ->
                        val isSel = preferredDate == date
                        Surface(
                            color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { preferredDate = date }
                        ) {
                            Text(
                                text = date.split(" - ").first(),
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }

                // Time Slot Quick Select
                Text("Preferred Time Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    timeOptions.take(3).forEach { time ->
                        val isSel = preferredTime == time
                        Surface(
                            color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { preferredTime = time }
                        ) {
                            Text(
                                text = time,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }

                // Additional Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special requirements / Doctor's prescription notes (Optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(preferredDate, preferredTime, patientName, patientPhone, notes)
                },
                modifier = Modifier.testTag("submit_request_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Delhi Healthcare Location Selector Dialog - Clean Minimalism
@Composable
fun LocationSelectorDialog(
    currentLocation: String,
    onSelectLocation: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf("All Zones") }
    var manualInput by remember { mutableStateOf("") }

    val delhiZones = listOf("All Zones", "Central Delhi", "South Delhi", "West Delhi", "North Delhi", "East Delhi", "South-West / Dwarka")

    val filteredLocalities = remember(searchQuery, selectedZone) {
        DemoDataSource.delhiLocalities.filter { loc ->
            val matchesZone = if (selectedZone == "All Zones") true else loc.zone == selectedZone
            val matchesQuery = if (searchQuery.isBlank()) true else {
                loc.name.contains(searchQuery, ignoreCase = true) ||
                loc.landmark.contains(searchQuery, ignoreCase = true) ||
                loc.zone.contains(searchQuery, ignoreCase = true)
            }
            matchesZone && matchesQuery
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(BluePrimary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Delhi Location Finder", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Select your area to calculate nearest hospitals", fontSize = 11.sp, color = TextMutedLight)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Live GPS Button
                Surface(
                    color = BluePrimary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BluePrimary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectLocation("Ansari Nagar / AIIMS / South Delhi")
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Live GPS",
                            tint = BluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Use Live GPS Location",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            )
                            Text(
                                text = "Auto-detects nearest Delhi hospital with live distance",
                                fontSize = 10.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }

                // Search Filter for Delhi localities
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Delhi area, Metro, or landmark...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondaryLight)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Delhi Localities List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    androidx.compose.foundation.lazy.LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredLocalities.size) { index ->
                            val loc = filteredLocalities[index]
                            val isSel = currentLocation.contains(loc.name.split("/")[0].trim())
                            Surface(
                                color = if (isSel) BluePrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) BluePrimary else BorderSubtleLight),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectLocation(loc.name) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = loc.name,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSel) BluePrimary else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (loc.landmark.isNotBlank()) {
                                            Text(
                                                text = "📍 ${loc.landmark}",
                                                fontSize = 10.sp,
                                                color = TextMutedLight,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = loc.zone,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextSecondaryLight,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Custom Address Input
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    placeholder = { Text("Or type custom Delhi address / PIN", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (manualInput.isNotBlank()) {
                        onSelectLocation(manualInput.trim())
                    } else {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text("Set Location", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp)
            }
        }
    )
}
