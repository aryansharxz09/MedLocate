package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SortOption
import com.example.data.StatusFilter
import com.example.model.Equipment
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.EquipmentResultCard
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.viewmodel.MedLocateViewModel

@Composable
fun SearchScreen(
    viewModel: MedLocateViewModel,
    onViewEquipment: (Equipment) -> Unit,
    onRequestAvailability: (Equipment) -> Unit,
    onWhyScore: (Equipment) -> Unit,
    onOpenLocationSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedDistance by viewModel.selectedDistance.collectAsState()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsState()
    val selectedSortOption by viewModel.selectedSortOption.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val aiParsedResult by viewModel.aiParsedResult.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }

    val distanceOptions = listOf(
        2.0 to "Within 2 km",
        5.0 to "Within 5 km",
        10.0 to "Within 10 km",
        25.0 to "Within 25 km",
        50.0 to "Within 50 km"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        // Search & Location Header - Clean Minimalism
        Surface(
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                // Location Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenLocationSelector() }
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = BluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Near: $currentLocation",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondaryLight,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "Change",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )
                }

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search equipment or medical service...", fontSize = 13.sp, color = TextMutedLight) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Search",
                                    tint = TextMutedLight
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BorderSubtleLight,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field")
                )

                // AI Parsed Intent Chip (if applied)
                AnimatedVisibility(visible = aiParsedResult != null) {
                    aiParsedResult?.let { parsed ->
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = parsed.explanation,
                                        fontSize = 11.sp,
                                        color = BluePrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.clearAiParsed() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Dismiss AI Filter",
                                        tint = TextMutedLight,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter & Sort Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sort Dropdown Button
                    Box {
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight),
                            modifier = Modifier
                                .clickable { showSortMenu = true }
                                .testTag("sort_menu_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "Sort",
                                    tint = TextSecondaryLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (selectedSortOption) {
                                        SortOption.NEAREST -> "Nearest"
                                        SortOption.HIGHEST_CONFIDENCE -> "Highest Confidence"
                                        SortOption.RECENTLY_VERIFIED -> "Recently Verified"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Nearest to me") },
                                onClick = {
                                    viewModel.setSortOption(SortOption.NEAREST)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Highest Confidence Score") },
                                onClick = {
                                    viewModel.setSortOption(SortOption.HIGHEST_CONFIDENCE)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Recently Verified First") },
                                onClick = {
                                    viewModel.setSortOption(SortOption.RECENTLY_VERIFIED)
                                    showSortMenu = false
                                }
                            )
                        }
                    }

                    // Status Filters
                    FilterChip(
                        selected = selectedStatusFilter == StatusFilter.ALL,
                        onClick = { viewModel.setStatusFilter(StatusFilter.ALL) },
                        label = { Text("All Status", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilterChip(
                        selected = selectedStatusFilter == StatusFilter.OPERATIONAL_VERIFIED,
                        onClick = { viewModel.setStatusFilter(StatusFilter.OPERATIONAL_VERIFIED) },
                        label = { Text("Operational Only", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilterChip(
                        selected = selectedStatusFilter == StatusFilter.RECENTLY_VERIFIED,
                        onClick = { viewModel.setStatusFilter(StatusFilter.RECENTLY_VERIFIED) },
                        label = { Text("Verified < 1 hr", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Distance Chips
                    distanceOptions.forEach { (dist, label) ->
                        FilterChip(
                            selected = selectedDistance == dist,
                            onClick = { viewModel.setDistanceFilter(dist) },
                            label = { Text(label, fontSize = 11.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Delhi Zone Chips
                    val selectedZone by viewModel.selectedZoneFilter.collectAsState()
                    val delhiZones = listOf("Central Delhi", "South Delhi", "West Delhi", "North Delhi", "East Delhi", "South-West Delhi")
                    delhiZones.forEach { zone ->
                        FilterChip(
                            selected = selectedZone == zone,
                            onClick = { viewModel.setZoneFilter(if (selectedZone == zone) null else zone) },
                            label = { Text(zone, fontSize = 11.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Search Results List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Found ${searchResults.size} Equipment Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (searchQuery.isNotBlank() || selectedStatusFilter != StatusFilter.ALL || selectedDistance != 25.0) {
                        TextButton(
                            onClick = {
                                viewModel.setSearchQuery("")
                                viewModel.setStatusFilter(StatusFilter.ALL)
                                viewModel.setDistanceFilter(25.0)
                                viewModel.setSortOption(SortOption.NEAREST)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Reset", fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                DisclaimerBanner()
            }

            if (searchResults.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "No Results",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No equipment found matching criteria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try increasing the distance radius or searching for general categories like MRI, CT, or Ventilator.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.setSearchQuery("")
                                viewModel.setStatusFilter(StatusFilter.ALL)
                                viewModel.setDistanceFilter(50.0)
                            }
                        ) {
                            Text("Expand Search to 50 km")
                        }
                    }
                }
            } else {
                items(searchResults, key = { it.id }) { equipment ->
                    EquipmentResultCard(
                        equipment = equipment,
                        onViewDetails = { onViewEquipment(it) },
                        onNavigate = { eq ->
                            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode("${eq.hospitalName}, ${eq.name}")}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                // Fallback web navigation
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(eq.hospitalName)}"))
                                context.startActivity(webIntent)
                            }
                        },
                        onRequestAvailability = { onRequestAvailability(it) },
                        onWhyScore = { onWhyScore(it) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
