package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appliance
import com.example.data.model.WhatDoINeedResult
import com.example.engine.SolarCalculatorEngine
import com.example.ui.components.ApplianceItemRow
import com.example.ui.components.PrimaryActionButton
import com.example.ui.components.SectionContainerCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LoadBlue
import com.example.ui.theme.LoadBlueContainer
import com.example.ui.theme.PowerTrackGreen
import com.example.ui.theme.PowerTrackGreenContainer
import com.example.ui.theme.PowerTrackGreenLight
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.SolarAmberContainer
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.PowerTrackViewModel

enum class WhatDoINeedStep {
    INPUT_APPLIANCES,
    CALCULATING,
    RECOMMENDED_SYSTEM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatDoINeedScreen(
    viewModel: PowerTrackViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSaved: () -> Unit
) {
    var step by remember { mutableStateOf(WhatDoINeedStep.INPUT_APPLIANCES) }

    val appliances by viewModel.whatDoINeedAppliances.collectAsState()
    val dailyHours by viewModel.whatDoINeedUsageHours.collectAsState()
    val result by viewModel.whatDoINeedResult.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitle by remember { mutableStateOf("Recommended Home Setup") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (step == WhatDoINeedStep.RECOMMENDED_SYSTEM) "Recommended System" else "What Do I Need?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (step == WhatDoINeedStep.RECOMMENDED_SYSTEM) {
                            step = WhatDoINeedStep.INPUT_APPLIANCES
                        } else {
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.testTag("what_i_need_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimaryDark
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = TextPrimaryDark
            )
        )

        when (step) {
            WhatDoINeedStep.INPUT_APPLIANCES -> {
                WhatDoINeedInputContent(
                    appliances = appliances,
                    dailyHours = dailyHours,
                    onDailyHoursChange = { viewModel.updateWhatDoINeedUsageHours(it) },
                    onAddAppliance = { viewModel.addWhatDoINeedAppliance(it) },
                    onDeleteAppliance = { viewModel.removeWhatDoINeedAppliance(it) },
                    onCalculate = {
                        step = WhatDoINeedStep.CALCULATING
                        viewModel.performWhatDoINeedCalculation {
                            step = WhatDoINeedStep.RECOMMENDED_SYSTEM
                        }
                    }
                )
            }

            WhatDoINeedStep.CALCULATING -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = PowerTrackGreen,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Sizing your ideal solar system...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Matching solar panels, battery bank, and pure sine inverter",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            WhatDoINeedStep.RECOMMENDED_SYSTEM -> {
                result?.let { recResult ->
                    RecommendedSystemContent(
                        result = recResult,
                        onSave = { showSaveDialog = true }
                    )
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = "Save Recommended Setup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter a title for this recommended configuration:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = saveTitle,
                        onValueChange = { saveTitle = it },
                        label = { Text("Configuration Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PowerTrackGreen,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveWhatDoINeedCalculation(saveTitle) {
                            showSaveDialog = false
                            onNavigateToSaved()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PowerTrackGreen, contentColor = Color.Black)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }
}

@Composable
private fun WhatDoINeedInputContent(
    appliances: List<Appliance>,
    dailyHours: Double,
    onDailyHoursChange: (Double) -> Unit,
    onAddAppliance: (Appliance) -> Unit,
    onDeleteAppliance: (String) -> Unit,
    onCalculate: () -> Unit
) {
    var applianceName by remember { mutableStateOf("") }
    var wattageStr by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }

    val totalLoad = appliances.sumOf { it.totalLoad }
    val totalDailyEnergy = appliances.sumOf { it.totalLoad * dailyHours }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Text(
                    text = "Tell us what you want to power and for how long:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Quick presets
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SolarCalculatorEngine.PRESET_APPLIANCES) { preset ->
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    applianceName = preset.name
                                    wattageStr = preset.wattage.toInt().toString()
                                    quantity = preset.quantity
                                }
                                .testTag("what_preset_${preset.name}"),
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkSurfaceBorder)
                        ) {
                            Text(
                                text = "${preset.name} (${preset.wattage.toInt()}W)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimaryDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Add Appliance Box
                SectionContainerCard(title = "Add Appliance") {
                    OutlinedTextField(
                        value = applianceName,
                        onValueChange = { applianceName = it },
                        label = { Text("Appliance Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("what_appliance_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PowerTrackGreen,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = wattageStr,
                            onValueChange = { wattageStr = it },
                            label = { Text("Wattage (W)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("what_wattage_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PowerTrackGreen,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        // Quantity counter
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DarkSurfaceElevated,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { if (quantity > 1) quantity-- }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimaryDark, modifier = Modifier.size(16.dp))
                                }
                            }

                            Text(
                                text = "Qty: $quantity",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )

                            Surface(
                                shape = CircleShape,
                                color = DarkSurfaceElevated,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { quantity++ }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimaryDark, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    PrimaryActionButton(
                        text = "Add to List",
                        onClick = {
                            val w = wattageStr.toDoubleOrNull() ?: 0.0
                            if (applianceName.isNotBlank() && w > 0) {
                                onAddAppliance(
                                    Appliance(
                                        name = applianceName.trim(),
                                        wattage = w,
                                        quantity = quantity,
                                        hoursPerDay = dailyHours
                                    )
                                )
                                applianceName = ""
                                wattageStr = ""
                                quantity = 1
                            }
                        },
                        leadingIcon = Icons.Default.Add,
                        enabled = applianceName.isNotBlank() && (wattageStr.toDoubleOrNull() ?: 0.0) > 0,
                        testTag = "what_add_confirm_button"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Target Usage Hours Card
                SectionContainerCard(title = "Usage Requirement") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Target Usage:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                        Text(
                            text = "${dailyHours.toInt()} Hours/Day",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = SolarAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = dailyHours.toFloat(),
                        onValueChange = { onDailyHoursChange(it.toDouble()) },
                        valueRange = 1f..24f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = PowerTrackGreen,
                            activeTrackColor = PowerTrackGreen,
                            inactiveTrackColor = DarkSurfaceElevated
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("usage_hours_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(4.0, 8.0, 12.0, 24.0).forEach { h ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onDailyHoursChange(h) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (dailyHours == h) PowerTrackGreenContainer else DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (dailyHours == h) PowerTrackGreen else DarkSurfaceBorder)
                            ) {
                                Text(
                                    text = "${h.toInt()}h",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (dailyHours == h) PowerTrackGreenLight else TextSecondaryDark,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Appliances Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Selected Appliances (${appliances.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )

                    Text(
                        text = "Load: ${totalLoad.toInt()} W",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = LoadBlue
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (appliances.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Text(
                            text = "Add at least one appliance above to calculate your recommended setup.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(20.dp).fillMaxWidth()
                        )
                    }
                }
            } else {
                items(appliances, key = { it.id }) { appliance ->
                    ApplianceItemRow(
                        appliance = appliance,
                        onDelete = { onDeleteAppliance(appliance.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        Surface(
            color = DarkBackground,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
            PrimaryActionButton(
                text = "Calculate Sizing Requirements",
                onClick = onCalculate,
                enabled = appliances.isNotEmpty(),
                testTag = "what_calculate_button"
            )
        }
    }
}

@Composable
private fun RecommendedSystemContent(
    result: WhatDoINeedResult,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Top Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PowerTrackGreenContainer,
            border = BorderStroke(1.dp, PowerTrackGreen.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Based on your ${result.appliances.size} appliances & ${result.dailyUsageHours.toInt()}h daily usage",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = PowerTrackGreenLight
                )
                Text(
                    text = "Total Active Load: ${result.totalLoadWatts.toInt()} W • Daily Energy: ${String.format("%.2f", result.dailyEnergyWh / 1000.0)} kWh",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Recommended Solar Panels
        RecommendationCard(
            title = "Recommended Solar Panel",
            mainText = result.recommendedPanelsText,
            subText = "Generates ≈ ${String.format("%.1f", result.recommendedSolarMinWp * 5.0 * 0.85 / 1000.0)} kWh/day under typical 5 peak sun hours",
            icon = Icons.Default.WbSunny,
            accentColor = PowerTrackGreen,
            bgColor = PowerTrackGreenContainer
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Recommended Battery
        RecommendationCard(
            title = "Recommended Battery",
            mainText = result.recommendedBatteryText,
            subText = "Provides sufficient runtime with 80% usable depth of discharge",
            icon = Icons.Default.BatteryChargingFull,
            accentColor = SolarAmber,
            bgColor = SolarAmberContainer
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Recommended Inverter
        RecommendationCard(
            title = "Recommended Inverter",
            mainText = result.recommendedInverterText,
            subText = "Provides continuous power with 25% surge safety margin over ${result.totalLoadWatts.toInt()}W load",
            icon = Icons.Default.ElectricBolt,
            accentColor = LoadBlue,
            bgColor = LoadBlueContainer
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Recommendations List
        SectionContainerCard(title = "Expert Sizing Advice") {
            result.recommendations.forEach { rec ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.titleMedium,
                        color = PowerTrackGreen,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = rec,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimaryDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryActionButton(
            text = "Save Result",
            onClick = onSave,
            leadingIcon = Icons.Default.Bookmark,
            testTag = "what_save_result_button"
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RecommendationCard(
    title: String,
    mainText: String,
    subText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    bgColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondaryDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = mainText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = accentColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subText,
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedDark
            )
        }
    }
}
