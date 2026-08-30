package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Equipment
import com.example.model.EquipmentStatus
import com.example.model.Hospital
import com.example.ui.components.ConfidenceScoreBadge
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.EquipmentResultCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.StatusVerifiedGreen
import com.example.ui.theme.StatusVerifiedGreenBg
import com.example.ui.theme.StatusVerifiedGreenText
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.viewmodel.MedLocateViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HospitalDetailScreen(
    hospital: Hospital,
    viewModel: MedLocateViewModel,
    onBack: () -> Unit,
    onViewEquipment: (Equipment) -> Unit,
    onRequestAvailability: (Equipment) -> Unit,
    onWhyScore: (Equipment) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedHospitalIds by viewModel.savedHospitalIds.collectAsState()
    val isSaved = savedHospitalIds.contains(hospital.id)

    val equipmentList = remember(hospital.id) {
        viewModel.getEquipmentForHospital(hospital.id)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("hospital_detail_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar & Hero Header
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Back & Bookmark Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("btn_back_from_hospital")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleSaveHospital(hospital.id) },
                            modifier = Modifier.testTag("btn_save_hospital")
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark Hospital",
                                tint = if (isSaved) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Hospital Title & Accreditations
                    Text(
                        text = hospital.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${hospital.categoryType} • ${hospital.zone}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BluePrimary
                    )
                    Text(
                        text = hospital.tagline,
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Badges (Distance, Rating, Metro, 24x7)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = BluePrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${hospital.distanceKm} km away",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "★ ${hospital.rating}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (hospital.hasEmergency24x7) {
                            Surface(
                                color = StatusVerifiedGreenBg,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "24x7 Emergency",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusVerifiedGreenText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ICU & Critical Care Bed Capacity Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Hotel, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Critical Care Bed Telemetry", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Text("Live Updates", fontSize = 10.sp, color = StatusVerifiedGreen, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = StatusVerifiedGreenBg,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "${hospital.icuBeds}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StatusVerifiedGreenText)
                                        Text(text = "Available ICU Beds", fontSize = 10.sp, color = StatusVerifiedGreenText)
                                    }
                                }

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = BluePrimary.copy(alpha = 0.10f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "${hospital.ventilatorBeds}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BluePrimary)
                                        Text(text = "Active Ventilators", fontSize = 10.sp, color = BluePrimary)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Address, Metro Station, Phone, Operating Hours
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Train, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Nearest Metro: ${hospital.nearestMetro} (${hospital.zone})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = hospital.address, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Emergency, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Emergency Helpline: ${hospital.emergencyPhone.ifBlank { hospital.phone }}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Hours: ${hospital.operatingHours}", fontSize = 12.sp, color = TextSecondaryLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Specialties tags
                    if (hospital.specialties.isNotEmpty()) {
                        Text("Center of Excellence & Specialties:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            hospital.specialties.forEach { spec ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = spec,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Action Buttons (Call Emergency, Directions)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val phoneToDial = hospital.emergencyPhone.ifBlank { hospital.phone }
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneToDial"))
                                try { context.startActivity(intent) } catch (e: Exception) {}
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_call_hospital"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CALL EMERGENCY", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val uri = "geo:${hospital.latitude},${hospital.longitude}?q=${Uri.encode(hospital.name)}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(hospital.name)}")))
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_get_directions"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp), tint = BluePrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GET DIRECTIONS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BluePrimary)
                        }
                    }
                }
            }
        }

        // Equipment Catalog in this Hospital
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
                        text = "Verified Medical Equipment (${equipmentList.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time Telemetry",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusVerifiedGreen
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                DisclaimerBanner()
            }
        }

        items(equipmentList, key = { it.id }) { equipment ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                EquipmentResultCard(
                    equipment = equipment,
                    onViewDetails = { onViewEquipment(it) },
                    onNavigate = { eq ->
                        val uri = "geo:0,0?q=${Uri.encode("${hospital.name}, ${eq.name}")}"
                        try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri))) } catch (e: Exception) {}
                    },
                    onRequestAvailability = { onRequestAvailability(it) },
                    onWhyScore = { onWhyScore(it) }
                )
            }
        }

        // Hospital Verification Governance Details
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, tint = BluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Institutional Verification & Governance", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Accreditation: ${hospital.accreditation}", fontSize = 12.sp)
                    Text("Authorized Sign-off: ${hospital.authorizedOfficer}", fontSize = 12.sp, color = TextSecondaryLight)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
