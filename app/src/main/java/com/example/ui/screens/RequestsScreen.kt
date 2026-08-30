package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvailabilityRequest
import com.example.model.RequestStatus
import com.example.ui.components.DisclaimerBanner
import com.example.ui.theme.StatusStaleYellow
import com.example.ui.theme.StatusStaleYellowBg
import com.example.ui.theme.StatusStaleYellowText
import com.example.ui.theme.StatusUnavailableRed
import com.example.ui.theme.StatusUnavailableRedBg
import com.example.ui.theme.StatusUnavailableRedText
import com.example.ui.theme.StatusVerifiedGreen
import com.example.ui.theme.StatusVerifiedGreenBg
import com.example.ui.theme.StatusVerifiedGreenText
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MedLocateViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RequestsScreen(
    viewModel: MedLocateViewModel,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val requests by viewModel.requests.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var selectedRequestForDetails by remember { mutableStateOf<AvailabilityRequest?>(null) }

    val tabs = listOf("All (${requests.size})", "Pending", "Confirmed", "Declined")

    val filteredRequests = remember(requests, selectedTab) {
        when (selectedTab) {
            0 -> requests
            1 -> requests.filter { it.status == RequestStatus.PENDING }
            2 -> requests.filter { it.status == RequestStatus.CONFIRMED }
            3 -> requests.filter { it.status == RequestStatus.DECLINED }
            else -> requests
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("requests_screen")
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Equipment Availability Requests",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track status of equipment holds and patient availability requests",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                DisclaimerBanner()
            }

            if (filteredRequests.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Availability Requests Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Search for equipment such as 3T MRI, CT Scan, or ICU Ventilator, and click 'Request Availability' to submit a hold.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToSearch,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Find Equipment Now")
                        }
                    }
                }
            } else {
                items(filteredRequests, key = { it.id }) { request ->
                    RequestCard(
                        request = request,
                        onClick = { selectedRequestForDetails = request },
                        onSimulateConfirm = { viewModel.simulateHospitalResponse(request, RequestStatus.CONFIRMED) },
                        onSimulateDecline = { viewModel.simulateHospitalResponse(request, RequestStatus.DECLINED) },
                        onCancel = { viewModel.cancelRequest(request.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Detail & Simulation Dialog
    selectedRequestForDetails?.let { req ->
        RequestDetailDialog(
            request = req,
            onDismiss = { selectedRequestForDetails = null },
            onSimulateConfirm = {
                viewModel.simulateHospitalResponse(req, RequestStatus.CONFIRMED)
                selectedRequestForDetails = req.copy(status = RequestStatus.CONFIRMED)
            },
            onSimulateDecline = {
                viewModel.simulateHospitalResponse(req, RequestStatus.DECLINED)
                selectedRequestForDetails = req.copy(status = RequestStatus.DECLINED)
            },
            onCancel = {
                viewModel.cancelRequest(req.id)
                selectedRequestForDetails = null
            }
        )
    }
}

@Composable
private fun RequestCard(
    request: AvailabilityRequest,
    onClick: () -> Unit,
    onSimulateConfirm: () -> Unit,
    onSimulateDecline: () -> Unit,
    onCancel: () -> Unit
) {
    val (statusBg, statusText, statusLabel, statusIcon) = when (request.status) {
        RequestStatus.PENDING -> Quad(StatusStaleYellowBg, StatusStaleYellowText, "PENDING REVIEW", Icons.Default.HourglassEmpty)
        RequestStatus.CONFIRMED -> Quad(StatusVerifiedGreenBg, StatusVerifiedGreenText, "CONFIRMED AVAILABLE", Icons.Default.CheckCircle)
        RequestStatus.DECLINED -> Quad(StatusUnavailableRedBg, StatusUnavailableRedText, "CURRENTLY FULL", Icons.Default.Close)
    }

    val formattedDate = remember(request.timestampMillis) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(request.timestampMillis))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("request_item_${request.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Equipment & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.equipmentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusText.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = statusIcon, contentDescription = null, tint = statusText, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = request.hospitalName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Time & Patient Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "Schedule",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${request.preferredDate} • ${request.preferredTime}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "ID: ${request.id}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Demo Simulation Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Submitted $formattedDate",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (request.status == RequestStatus.PENDING) {
                        Surface(
                            color = StatusVerifiedGreenBg,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { onSimulateConfirm() }
                        ) {
                            Text(
                                text = "Simulate Confirm",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusVerifiedGreenText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Request",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
private fun RequestDetailDialog(
    request: AvailabilityRequest,
    onDismiss: () -> Unit,
    onSimulateConfirm: () -> Unit,
    onSimulateDecline: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Request Details", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = request.equipmentName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = request.hospitalName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                Text("Patient: ${request.patientName} (${request.patientPhone})", fontSize = 12.sp)
                Text("Requested Slot: ${request.preferredDate} at ${request.preferredTime}", fontSize = 12.sp)
                Text("Current Status: ${request.status.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                if (request.notes.isNotBlank()) {
                    Text("Notes: ${request.notes}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Demo Control: You can simulate hospital staff confirmation or rejection for this hackathon test flow.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSimulateConfirm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simulate Confirm", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = onSimulateDecline,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simulate Decline", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Delete Request", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}
