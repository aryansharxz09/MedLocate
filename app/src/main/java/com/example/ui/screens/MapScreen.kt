package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoDataSource
import com.example.model.Equipment
import com.example.model.EquipmentStatus
import com.example.model.Hospital
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.StatusStaleYellow
import com.example.ui.theme.StatusUnavailableRed
import com.example.ui.theme.StatusVerifiedGreen
import com.example.ui.theme.StatusVerifiedGreenBg
import com.example.ui.theme.StatusVerifiedGreenText
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.viewmodel.MedLocateViewModel

@Composable
fun MapScreen(
    viewModel: MedLocateViewModel,
    onViewHospital: (Hospital) -> Unit,
    onViewEquipment: (Equipment) -> Unit,
    onRequestAvailability: (Equipment) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isMapView by remember { mutableStateOf(true) }
    val userLat by viewModel.userLatitude.collectAsState()
    val userLng by viewModel.userLongitude.collectAsState()
    val userLocationName by viewModel.currentLocation.collectAsState()
    val selectedZone by viewModel.selectedZoneFilter.collectAsState()
    val nearestHospitals by viewModel.nearestHospitals.collectAsState()
    val mapSelectedHospital by viewModel.mapSelectedHospital.collectAsState()

    val delhiZones = listOf("All Delhi NCR", "Central Delhi", "South Delhi", "West Delhi", "North Delhi", "East Delhi", "South-West / Dwarka")

    // Delhi Bounding Box for GPS coordinate normalization
    val minLat = 28.46
    val maxLat = 28.74
    val minLng = 77.02
    val maxLng = 77.34

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_screen")
    ) {
        // Map Top Bar with View Toggle & Delhi Zone Filter
        Surface(
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Delhi Health GIS Locator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "📍 $userLocationName (${nearestHospitals.size} Centers)",
                            fontSize = 11.sp,
                            color = BluePrimary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // View Mode Toggle (Map vs List)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            Surface(
                                color = if (isMapView) BluePrimary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .clickable { isMapView = true }
                                    .testTag("toggle_map_view")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = "Map View",
                                        tint = if (isMapView) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "MAP",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMapView) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                color = if (!isMapView) BluePrimary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .clickable { isMapView = false }
                                    .testTag("toggle_list_view")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatListBulleted,
                                        contentDescription = "List View",
                                        tint = if (!isMapView) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIST",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isMapView) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Delhi Zone Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(delhiZones) { zone ->
                        val isSelected = selectedZone == zone
                        Surface(
                            color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BluePrimary else BorderSubtleLight),
                            modifier = Modifier
                                .clickable { viewModel.setZoneFilter(zone) }
                                .testTag("zone_filter_$zone")
                        ) {
                            Text(
                                text = zone,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (isMapView) {
            // Interactive Delhi Healthcare Map Canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val mapWidth = maxWidth
                    val mapHeight = maxHeight

                    // Canvas simulated map grid of Delhi NCR
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF1F5F9))
                    ) {
                        val w = size.width
                        val h = size.height

                        val roadPaint = Color(0xFFE2E8F0)
                        val riverYamuna = Color(0xFF93C5FD).copy(alpha = 0.6f)
                        val ringRoadPaint = Color(0xFFCBD5E1)
                        val metroLine = Color(0xFFFCA5A5).copy(alpha = 0.7f)

                        // Grid background
                        for (x in 0..w.toInt() step 50) {
                            drawLine(roadPaint, Offset(x.toFloat(), 0f), Offset(x.toFloat(), h), strokeWidth = 1f)
                        }
                        for (y in 0..h.toInt() step 50) {
                            drawLine(roadPaint, Offset(0f, y.toFloat()), Offset(w, y.toFloat()), strokeWidth = 1f)
                        }

                        // Yamuna River curve (East of Central Delhi)
                        val riverPath = Path().apply {
                            moveTo(w * 0.72f, 0f)
                            cubicTo(w * 0.70f, h * 0.35f, w * 0.78f, h * 0.65f, w * 0.74f, h)
                        }
                        drawPath(riverPath, riverYamuna, style = Stroke(width = 12f))

                        // Outer & Inner Ring Roads
                        drawCircle(
                            color = ringRoadPaint,
                            radius = w * 0.36f,
                            center = Offset(w * 0.50f, h * 0.48f),
                            style = Stroke(width = 5f)
                        )
                        drawCircle(
                            color = ringRoadPaint.copy(alpha = 0.7f),
                            radius = w * 0.22f,
                            center = Offset(w * 0.50f, h * 0.48f),
                            style = Stroke(width = 3f)
                        )

                        // Delhi Metro Lines
                        drawLine(metroLine, Offset(0f, h * 0.52f), Offset(w, h * 0.52f), strokeWidth = 3f)
                        drawLine(Color(0xFFFDE047).copy(alpha = 0.8f), Offset(w * 0.48f, 0f), Offset(w * 0.52f, h), strokeWidth = 3f)

                        // User GPS live location circle pulse
                        val userX = ((userLng - minLng) / (maxLng - minLng)).coerceIn(0.1, 0.9).toFloat() * w
                        val userY = (1.0f - ((userLat - minLat) / (maxLat - minLat)).coerceIn(0.1, 0.9).toFloat()) * h

                        drawCircle(
                            color = BluePrimary.copy(alpha = 0.18f),
                            radius = 36f,
                            center = Offset(userX, userY)
                        )
                        drawCircle(
                            color = BluePrimary,
                            radius = 7f,
                            center = Offset(userX, userY)
                        )
                    }

                    // User Location Floating Pin
                    val userXFrac = ((userLng - minLng) / (maxLng - minLng)).coerceIn(0.12, 0.88).toFloat()
                    val userYFrac = (1.0f - ((userLat - minLat) / (maxLat - minLat)).coerceIn(0.12, 0.88).toFloat())

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(
                                x = (mapWidth * userXFrac) - 45.dp,
                                y = (mapHeight * userYFrac) - 34.dp
                            )
                    ) {
                        Surface(
                            color = BluePrimary,
                            shape = RoundedCornerShape(10.dp),
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "My Location",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("You (${viewModel.currentLocation.value.split("/")[0].take(10)})", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Hospital Markers Positioned Accurately by GPS coordinates
                    nearestHospitals.forEach { hospital ->
                        val isSelected = (mapSelectedHospital?.id ?: nearestHospitals.firstOrNull()?.id) == hospital.id
                        val hospitalEquipment = DemoDataSource.equipmentList.filter { it.hospitalId == hospital.id }
                        val hasVerified = hospitalEquipment.any { it.status == EquipmentStatus.VERIFIED }

                        val xFrac = ((hospital.longitude - minLng) / (maxLng - minLng)).coerceIn(0.08, 0.92).toFloat()
                        val yFrac = (1.0f - ((hospital.latitude - minLat) / (maxLat - minLat)).coerceIn(0.08, 0.92).toFloat())

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(
                                    x = (mapWidth * xFrac) - 38.dp,
                                    y = (mapHeight * yFrac) - 28.dp
                                )
                                .clickable { viewModel.setMapSelectedHospital(hospital) }
                                .testTag("map_marker_${hospital.id}")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isSelected) BluePrimary else BorderSubtleLight
                                    ),
                                    shadowElevation = if (isSelected) 6.dp else 2.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(if (hasVerified) StatusVerifiedGreen else StatusStaleYellow)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = hospital.name.split(" ").first(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = hospital.name,
                                    tint = if (isSelected) BluePrimary else Color(0xFF0F766E),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Map Overlay Top Badge
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "📍 Delhi NCR Real-Time Medical GIS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Bottom Floating Selected Hospital Card
                    (mapSelectedHospital ?: nearestHospitals.firstOrNull())?.let { hospital ->
                        val hospitalEquipment = DemoDataSource.equipmentList.filter { it.hospitalId == hospital.id }

                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(12.dp)
                                .testTag("map_selected_hospital_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = hospital.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "🚇 Metro: ${hospital.nearestMetro} • ${hospital.zone}",
                                            fontSize = 11.sp,
                                            color = BluePrimary,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = hospital.address,
                                            fontSize = 10.sp,
                                            color = TextMutedLight,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Surface(
                                        color = BluePrimary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${hospital.distanceKm} km",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BluePrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // ICU & Ventilator bed live indicators
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = StatusVerifiedGreenBg,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "ICU Beds: ${hospital.icuBeds}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusVerifiedGreenText,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Ventilators: ${hospital.ventilatorBeds}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "★ ${hospital.rating}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Equipment Highlights in this hospital
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(hospitalEquipment) { eq ->
                                        Surface(
                                            color = when (eq.status) {
                                                EquipmentStatus.VERIFIED -> StatusVerifiedGreenBg
                                                EquipmentStatus.STALE -> Color(0xFFFFFBEB)
                                                EquipmentStatus.UNAVAILABLE -> Color(0xFFFEF2F2)
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                when (eq.status) {
                                                    EquipmentStatus.VERIFIED -> StatusVerifiedGreen
                                                    EquipmentStatus.STALE -> StatusStaleYellow
                                                    EquipmentStatus.UNAVAILABLE -> StatusUnavailableRed
                                                }.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier.clickable { onViewEquipment(eq) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = when (eq.status) {
                                                        EquipmentStatus.VERIFIED -> "🟢 "
                                                        EquipmentStatus.STALE -> "🟡 "
                                                        EquipmentStatus.UNAVAILABLE -> "🔴 "
                                                    } + eq.name,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = BorderSubtleLight)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val phoneToDial = hospital.emergencyPhone.ifBlank { hospital.phone }
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneToDial"))
                                            try { context.startActivity(intent) } catch (e: Exception) {}
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp), tint = BluePrimary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Call ER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
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
                                        modifier = Modifier.weight(1.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(14.dp), tint = BluePrimary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Directions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                                    }

                                    Button(
                                        onClick = { onViewHospital(hospital) },
                                        modifier = Modifier.weight(1.2f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                                    ) {
                                        Text("View All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // List View of all Delhi Hospitals sorted by nearest distance
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("map_list_view"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    DisclaimerBanner()
                }

                items(nearestHospitals, key = { it.id }) { hospital ->
                    DelhiHospitalListItem(
                        hospital = hospital,
                        onOpen = { onViewHospital(hospital) },
                        onCall = {
                            val phoneToDial = hospital.emergencyPhone.ifBlank { hospital.phone }
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneToDial"))
                            try { context.startActivity(intent) } catch (e: Exception) {}
                        },
                        onDirections = {
                            val uri = "geo:${hospital.latitude},${hospital.longitude}?q=${Uri.encode(hospital.name)}"
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
                            } catch (e: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(hospital.name)}")))
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
private fun DelhiHospitalListItem(
    hospital: Hospital,
    onOpen: () -> Unit,
    onCall: () -> Unit,
    onDirections: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("hospital_list_item_${hospital.id}"),
        shape = RoundedCornerShape(16.dp),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hospital.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = hospital.tagline,
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }

                Surface(
                    color = BluePrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${hospital.distanceKm} km",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Metro station & Zone
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Metro: ${hospital.nearestMetro} • Zone: ${hospital.zone}",
                    fontSize = 11.sp,
                    color = TextSecondaryLight,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bed and rating badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = StatusVerifiedGreenBg,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "ICU Beds: ${hospital.icuBeds}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusVerifiedGreenText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Ventilators: ${hospital.ventilatorBeds}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "★ ${hospital.rating}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                )
                Text(
                    text = "• ${hospital.categoryType}",
                    fontSize = 10.sp,
                    color = TextMutedLight
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderSubtleLight)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onCall,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(13.dp), tint = BluePrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ER Call", fontSize = 11.sp, color = BluePrimary)
                    }

                    OutlinedButton(
                        onClick = onDirections,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(13.dp), tint = BluePrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("GPS Map", fontSize = 11.sp, color = BluePrimary)
                    }
                }

                Button(
                    onClick = onOpen,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("View Hospital >", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
