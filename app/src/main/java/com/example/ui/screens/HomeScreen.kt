package com.example.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoDataSource
import com.example.model.Equipment
import com.example.model.Hospital
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.EquipmentResultCard
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.StatusVerifiedGreen
import com.example.ui.theme.StatusVerifiedGreenBg
import com.example.ui.theme.StatusVerifiedGreenText
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MedLocateViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MedLocateViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToMap: () -> Unit,
    onViewEquipment: (Equipment) -> Unit,
    onViewHospital: (Hospital) -> Unit,
    onRequestAvailability: (Equipment) -> Unit,
    onWhyScore: (Equipment) -> Unit,
    onOpenLocationSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    var homeSearchText by remember { mutableStateOf("") }
    var showAiAssistant by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Section - Clean Minimalism
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Top App Bar Identity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MEDLOCATE",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp,
                                color = BluePrimary
                            )
                            Text(
                                text = "FIND. VERIFY. TRACK.",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMutedLight,
                                letterSpacing = 1.5.sp
                            )
                        }

                        // Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onOpenLocationSelector() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search Context Bar (Clean Minimalism Inset Box)
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenLocationSelector() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Search",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Search Near",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextMutedLight
                                    )
                                    Text(
                                        text = viewModel.currentLocation.value,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onOpenLocationSelector() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Change Location",
                                    tint = TextSecondaryLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Main Search Input Field
                    OutlinedTextField(
                        value = homeSearchText,
                        onValueChange = { homeSearchText = it },
                        placeholder = { Text("Search MRI, CT, Dialysis, Ventilator...", fontSize = 13.sp, color = TextMutedLight) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextSecondaryLight,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (homeSearchText.isNotBlank()) {
                                Button(
                                    onClick = {
                                        viewModel.setSearchQuery(homeSearchText)
                                        onNavigateToSearch()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .height(34.dp)
                                ) {
                                    Text("Find", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderSubtleLight,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Find Equipment Primary CTA Button
                    Button(
                        onClick = {
                            if (homeSearchText.isNotBlank()) {
                                viewModel.setSearchQuery(homeSearchText)
                            }
                            onNavigateToSearch()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_find_equipment")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (homeSearchText.isBlank()) "Find Equipment & Availability" else "Search '$homeSearchText'",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    DisclaimerBanner()
                }
            }
        }

        // Quick Search Categories Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Search by Equipment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "10 Categories",
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    DemoDataSource.quickSearchKeywords.forEach { (keyword, subtitle) ->
                        QuickSearchChip(
                            title = keyword,
                            subtitle = subtitle,
                            onClick = {
                                viewModel.selectQuickCategory(keyword)
                            }
                        )
                    }
                }
            }
        }

        // Natural Language AI Search Assistant Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("ai_assistant_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Assistant",
                                tint = BluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MedLocate AI Assistant",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        TextButton(
                            onClick = { showAiAssistant = !showAiAssistant },
                            modifier = Modifier.testTag("toggle_ai_assistant")
                        ) {
                            Text(if (showAiAssistant) "Collapse" else "Sample Queries", fontSize = 12.sp, color = BluePrimary)
                        }
                    }

                    Text(
                        text = "Ask in plain natural language (e.g., 'Find 3T MRI within 5 km' or 'ICU Ventilator near me').",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )

                    AnimatedVisibility(visible = showAiAssistant) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val samplePrompts = listOf(
                                "Find 3T MRI within 5 km with highest confidence",
                                "Which hospital has the most recently verified CT Scan?",
                                "Urgent ICU Ventilator near me",
                                "Find Dialysis Station with verified status"
                            )

                            samplePrompts.forEach { prompt ->
                                Surface(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.executeNaturalLanguageAiSearch(prompt)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "“$prompt”",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = BluePrimary
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = TextMutedLight,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Urgent Emergency Equipment Highlights
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Emergency",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Urgent Critical Care Equipment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "24x7 Standby",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusVerifiedGreen
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val emergencyItems = DemoDataSource.equipmentList.filter {
                    it.name.contains("Ventilator") || it.name.contains("Oxygen") || it.name.contains("Defibrillator")
                }

                emergencyItems.take(2).forEach { eq ->
                    EquipmentResultCard(
                        equipment = eq,
                        onViewDetails = { onViewEquipment(it) },
                        onNavigate = {
                            val uri = "geo:${eq.distanceKm},${eq.distanceKm}?q=${Uri.encode(eq.hospitalName)}"
                        },
                        onRequestAvailability = { onRequestAvailability(it) },
                        onWhyScore = { onWhyScore(it) },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }

        // Nearby Verified Delhi Hospitals Showcase (Dynamically sorted by proximity)
        item {
            val nearestHospitals by viewModel.nearestHospitals.collectAsState()
            val selectedZone by viewModel.selectedZoneFilter.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Nearest Hospitals to You",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sorted from ${viewModel.currentLocation.value} (${nearestHospitals.size} available)",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }
                    TextButton(onClick = onNavigateToMap) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = BluePrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Live Map", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Delhi Zone Quick Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val zones = listOf("All Zones", "Central Delhi", "South Delhi", "West Delhi", "North Delhi", "East Delhi", "South-West Delhi")
                    items(zones) { zone ->
                        val isSelected = (zone == "All Zones" && selectedZone == null) || (zone == selectedZone)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BluePrimary else BorderSubtleLight),
                            modifier = Modifier
                                .clickable {
                                    viewModel.setZoneFilter(if (zone == "All Zones") null else zone)
                                }
                        ) {
                            Text(
                                text = zone,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(nearestHospitals, key = { it.id }) { hospital ->
                        HospitalSummaryCard(
                            hospital = hospital,
                            onOpen = { onViewHospital(hospital) }
                        )
                    }
                }
            }
        }

        // Bottom Trust & Verification Policy Card
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "How MedLocate Verification Works",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Hospital biomedical engineers and triage duty staff report live telemetry and availability cycles. MedLocate computes the Confidence Score based on recency, authorized sign-off, and operational consistency.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = TextSecondaryLight
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun QuickSearchChip(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("quick_category_$title")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(BluePrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun HospitalSummaryCard(
    hospital: Hospital,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onOpen() }
            .testTag("hospital_summary_${hospital.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${hospital.distanceKm} km",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryLight,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "★ ${hospital.rating}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = hospital.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = hospital.tagline,
                fontSize = 11.sp,
                color = TextSecondaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "24x7 Emergency",
                    fontSize = 10.sp,
                    color = StatusVerifiedGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "View >",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }
        }
    }
}
