package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appliance
import com.example.data.model.CalculationResult
import com.example.data.model.InverterStatus
import com.example.engine.SolarCalculatorEngine
import com.example.ui.components.ApplianceItemRow
import com.example.ui.components.MetricStatRow
import com.example.ui.components.PrimaryActionButton
import com.example.ui.components.SecondaryActionButton
import com.example.ui.components.SectionContainerCard
import com.example.ui.components.StatusBadge
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
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.PowerTrackViewModel
import kotlinx.coroutines.delay

enum class CalculatorStep {
    SYSTEM_SETUP,
    ADD_APPLIANCES,
    CALCULATING,
    RESULTS_OVERVIEW,
    DETAILED_RESULTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarCalculatorScreen(
    viewModel: PowerTrackViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSaved: () -> Unit
) {
    var currentStep by remember { mutableStateOf(CalculatorStep.SYSTEM_SETUP) }

    val solarSetupInput by viewModel.solarSetupInput.collectAsState()
    val appliances by viewModel.calculatorAppliances.collectAsState()
    val calculationResult by viewModel.currentCalculationResult.collectAsState()
    val isCalculating by viewModel.isCalculating.collectAsState()

    // Form states for System Setup
    var panelWattageStr by remember(solarSetupInput) { 
        mutableStateOf(if (solarSetupInput.panelWattage > 0) solarSetupInput.panelWattage.toInt().toString() else "") 
    }
    var panelCountStr by remember(solarSetupInput) { 
        mutableStateOf(if (solarSetupInput.panelCount > 0) solarSetupInput.panelCount.toString() else "") 
    }
    var batteryVoltage by remember(solarSetupInput) { mutableIntStateOf(if (solarSetupInput.batteryVoltage > 0) solarSetupInput.batteryVoltage else 12) }
    var batteryAhStr by remember(solarSetupInput) { 
        mutableStateOf(if (solarSetupInput.batteryAh > 0) solarSetupInput.batteryAh.toInt().toString() else "") 
    }
    var inverterWattsStr by remember(solarSetupInput) { 
        mutableStateOf(if (solarSetupInput.inverterWatts > 0) solarSetupInput.inverterWatts.toInt().toString() else "") 
    }

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitle by remember { mutableStateOf("My Solar Setup") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = when (currentStep) {
                        CalculatorStep.SYSTEM_SETUP -> "Solar Setup"
                        CalculatorStep.ADD_APPLIANCES -> "Appliances"
                        CalculatorStep.CALCULATING -> "Analyzing Setup"
                        CalculatorStep.RESULTS_OVERVIEW -> "Results"
                        CalculatorStep.DETAILED_RESULTS -> "Results Details"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        when (currentStep) {
                            CalculatorStep.SYSTEM_SETUP -> onNavigateBack()
                            CalculatorStep.ADD_APPLIANCES -> currentStep = CalculatorStep.SYSTEM_SETUP
                            CalculatorStep.CALCULATING -> currentStep = CalculatorStep.ADD_APPLIANCES
                            CalculatorStep.RESULTS_OVERVIEW -> currentStep = CalculatorStep.ADD_APPLIANCES
                            CalculatorStep.DETAILED_RESULTS -> currentStep = CalculatorStep.RESULTS_OVERVIEW
                        }
                    },
                    modifier = Modifier.testTag("solar_calc_back_button")
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

        when (currentStep) {
            CalculatorStep.SYSTEM_SETUP -> {
                SystemSetupContent(
                    panelWattageStr = panelWattageStr,
                    onPanelWattageChange = { panelWattageStr = it },
                    panelCountStr = panelCountStr,
                    onPanelCountChange = { panelCountStr = it },
                    batteryVoltage = batteryVoltage,
                    onBatteryVoltageChange = { batteryVoltage = it },
                    batteryAhStr = batteryAhStr,
                    onBatteryAhChange = { batteryAhStr = it },
                    inverterWattsStr = inverterWattsStr,
                    onInverterWattsChange = { inverterWattsStr = it },
                    onNext = {
                        val pWatt = panelWattageStr.toDoubleOrNull() ?: 0.0
                        val pCount = panelCountStr.toIntOrNull() ?: 0
                        val bAh = batteryAhStr.toDoubleOrNull() ?: 0.0
                        val invWatts = inverterWattsStr.toDoubleOrNull() ?: 0.0
                        viewModel.updateSolarSetup(pWatt, pCount, batteryVoltage, bAh, invWatts)
                        currentStep = CalculatorStep.ADD_APPLIANCES
                    }
                )
            }

            CalculatorStep.ADD_APPLIANCES -> {
                AppliancesInputContent(
                    appliances = appliances,
                    onAddAppliance = { viewModel.addCalculatorAppliance(it) },
                    onDeleteAppliance = { viewModel.removeCalculatorAppliance(it) },
                    onCalculate = {
                        currentStep = CalculatorStep.CALCULATING
                        viewModel.performCalculation {
                            currentStep = CalculatorStep.RESULTS_OVERVIEW
                        }
                    }
                )
            }

            CalculatorStep.CALCULATING -> {
                CalculatingAnimationView()
            }

            CalculatorStep.RESULTS_OVERVIEW -> {
                calculationResult?.let { result ->
                    ResultsOverviewContent(
                        result = result,
                        onViewFullDetails = { currentStep = CalculatorStep.DETAILED_RESULTS }
                    )
                }
            }

            CalculatorStep.DETAILED_RESULTS -> {
                calculationResult?.let { result ->
                    DetailedResultsContent(
                        result = result,
                        onShare = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "⚡ PowerTrack Solar Calculation:\n" +
                                            "• Solar: ${result.totalSolarCapacityWp.toInt()} Wp (${result.panelCount} panels)\n" +
                                            "• Battery: ${result.batteryVoltage}V ${result.batteryAh.toInt()}Ah (${result.nominalBatteryWh.toInt()} Wh)\n" +
                                            "• Load: ${result.totalLoadWatts.toInt()} W\n" +
                                            "• Est. Runtime: ${SolarCalculatorEngine.formatRuntime(result.estimatedRuntimeHours)}\n" +
                                            "• Daily Solar: ${String.format("%.2f", result.estimatedSolarDailyWh / 1000.0)} kWh/day"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Solar Result"))
                        },
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
                    text = "Save Calculation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter a memorable name for this solar system setup:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = saveTitle,
                        onValueChange = { saveTitle = it },
                        label = { Text("Setup Name") },
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
                        viewModel.saveCurrentCalculation(saveTitle) {
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
private fun SystemSetupContent(
    panelWattageStr: String,
    onPanelWattageChange: (String) -> Unit,
    panelCountStr: String,
    onPanelCountChange: (String) -> Unit,
    batteryVoltage: Int,
    onBatteryVoltageChange: (Int) -> Unit,
    batteryAhStr: String,
    onBatteryAhChange: (String) -> Unit,
    inverterWattsStr: String,
    onInverterWattsChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val pWatt = panelWattageStr.toDoubleOrNull() ?: 0.0
    val pCount = panelCountStr.toIntOrNull() ?: 0
    val totalSolarWp = pWatt * pCount

    val bAh = batteryAhStr.toDoubleOrNull() ?: 0.0
    val nominalWh = batteryVoltage * bAh

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Solar Panels Card
        SectionContainerCard(title = "Solar Panels") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = panelWattageStr,
                    onValueChange = onPanelWattageChange,
                    label = { Text("Panel Wattage") },
                    trailingIcon = { Text("W", color = TextMutedDark, modifier = Modifier.padding(end = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("panel_wattage_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PowerTrackGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )

                OutlinedTextField(
                    value = panelCountStr,
                    onValueChange = onPanelCountChange,
                    label = { Text("Number of Panels") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("panel_count_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PowerTrackGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Solar Capacity:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                    Text(
                        text = "${totalSolarWp.toInt()} Wp",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = PowerTrackGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery Card
        SectionContainerCard(title = "Battery") {
            Text(
                text = "Nominal Voltage",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(12, 24, 48).forEach { voltage ->
                    val isSelected = batteryVoltage == voltage
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clickable { onBatteryVoltageChange(voltage) }
                            .testTag("battery_voltage_${voltage}V"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PowerTrackGreen else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) PowerTrackGreen else DarkSurfaceBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${voltage}V",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.Black else TextPrimaryDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = batteryAhStr,
                onValueChange = onBatteryAhChange,
                label = { Text("Battery Capacity") },
                trailingIcon = { Text("Ah", color = TextMutedDark, modifier = Modifier.padding(end = 12.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("battery_ah_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PowerTrackGreen,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nominal Energy:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                    Text(
                        text = "${nominalWh.toInt()} Wh (${String.format("%.2f", nominalWh / 1000.0)} kWh)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = SolarAmber
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Inverter Card
        SectionContainerCard(title = "Inverter") {
            OutlinedTextField(
                value = inverterWattsStr,
                onValueChange = onInverterWattsChange,
                label = { Text("Inverter Rated Size") },
                trailingIcon = { Text("W", color = TextMutedDark, modifier = Modifier.padding(end = 12.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("inverter_size_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PowerTrackGreen,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Continuous rated power in Watts (W) to compare against active loads.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextMutedDark
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryActionButton(
            text = "Next: Add Appliances",
            onClick = onNext,
            enabled = pWatt > 0 && pCount > 0 && bAh > 0 && (inverterWattsStr.toDoubleOrNull() ?: 0.0) > 0,
            testTag = "setup_next_button"
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppliancesInputContent(
    appliances: List<Appliance>,
    onAddAppliance: (Appliance) -> Unit,
    onDeleteAppliance: (String) -> Unit,
    onCalculate: () -> Unit
) {
    var applianceName by remember { mutableStateOf("") }
    var wattageStr by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    var hoursPerDayStr by remember { mutableStateOf("6") }

    val totalLoad = appliances.sumOf { it.totalLoad }
    val totalDailyEnergy = appliances.sumOf { it.dailyEnergy }

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
                // Preset Quick Chips
                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

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
                                    hoursPerDayStr = preset.hoursPerDay.toInt().toString()
                                    quantity = preset.quantity
                                }
                                .testTag("preset_${preset.name}"),
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

                Spacer(modifier = Modifier.height(16.dp))

                // Add Appliance Input Card
                SectionContainerCard(title = "Add Appliance") {
                    OutlinedTextField(
                        value = applianceName,
                        onValueChange = { applianceName = it },
                        label = { Text("Appliance Name (e.g. TV, Fridge)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("appliance_name_input"),
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
                            modifier = Modifier.weight(1f).testTag("appliance_wattage_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PowerTrackGreen,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        OutlinedTextField(
                            value = hoursPerDayStr,
                            onValueChange = { hoursPerDayStr = it },
                            label = { Text("Hours/Day") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("appliance_hours_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PowerTrackGreen,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quantity Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quantity:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                                text = "$quantity",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark,
                                modifier = Modifier.padding(horizontal = 14.dp)
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
                            val h = hoursPerDayStr.toDoubleOrNull() ?: 4.0
                            if (applianceName.isNotBlank() && w > 0) {
                                onAddAppliance(
                                    Appliance(
                                        name = applianceName.trim(),
                                        wattage = w,
                                        quantity = quantity,
                                        hoursPerDay = h
                                    )
                                )
                                applianceName = ""
                                wattageStr = ""
                                quantity = 1
                            }
                        },
                        leadingIcon = Icons.Default.Add,
                        enabled = applianceName.isNotBlank() && (wattageStr.toDoubleOrNull() ?: 0.0) > 0,
                        testTag = "add_appliance_confirm_button"
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Your Appliances Header & Totals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Appliances (${appliances.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )

                    Text(
                        text = "Total: ${totalLoad.toInt()} W",
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
                            text = "No appliances added yet. Tap a quick preset above or enter custom appliances.",
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

        // Bottom Fixed Calculate Bar
        Surface(
            color = DarkBackground,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
            PrimaryActionButton(
                text = "Calculate Solar Setup",
                onClick = onCalculate,
                enabled = appliances.isNotEmpty(),
                testTag = "calculate_solar_button"
            )
        }
    }
}

@Composable
private fun CalculatingAnimationView() {
    var stepIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(250)
        stepIndex = 1
        delay(250)
        stepIndex = 2
        delay(250)
        stepIndex = 3
        delay(250)
        stepIndex = 4
        delay(250)
        stepIndex = 5
    }

    val checks = listOf(
        "Calculating total load",
        "Calculating battery runtime",
        "Calculating solar production",
        "Checking inverter capacity",
        "Generating results"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular progress ring
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(PowerTrackGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(90.dp),
                    color = PowerTrackGreen,
                    strokeWidth = 5.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = PowerTrackGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Calculating...",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
            Text(
                text = "Please wait while we analyze your solar data",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                checks.forEachIndexed { index, title ->
                    val isDone = stepIndex > index
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isDone) PowerTrackGreen else DarkSurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDone) TextPrimaryDark else TextMutedDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultsOverviewContent(
    result: CalculationResult,
    onViewFullDetails: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Results Overview",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
        )
        Text(
            text = "Here is how your solar setup performs with your load:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total Solar Capacity
        MetricStatRow(
            title = "Total Solar Capacity",
            value = "${SolarCalculatorEngine.formatNumber(result.totalSolarCapacityWp)} Wp",
            icon = Icons.Default.WbSunny,
            iconColor = PowerTrackGreen,
            iconBgColor = PowerTrackGreenContainer,
            valueColor = PowerTrackGreen,
            subtitle = "${result.panelCount} panels (${result.panelWattage.toInt()}W each)"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Total Load
        MetricStatRow(
            title = "Total Load",
            value = "${SolarCalculatorEngine.formatNumber(result.totalLoadWatts)} W",
            icon = Icons.Default.ElectricBolt,
            iconColor = LoadBlue,
            iconBgColor = LoadBlueContainer,
            valueColor = LoadBlue,
            subtitle = "${result.appliances.size} connected appliances"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Battery Energy
        MetricStatRow(
            title = "Battery Energy",
            value = "${SolarCalculatorEngine.formatNumber(result.nominalBatteryWh)} Wh",
            icon = Icons.Default.BatteryChargingFull,
            iconColor = SolarAmber,
            iconBgColor = SolarAmberContainer,
            valueColor = SolarAmber,
            subtitle = "${result.batteryVoltage}V • ${result.batteryAh.toInt()}Ah (Usable: ${(result.usableBatteryWh / 1000.0).let { String.format("%.2f", it) }} kWh)"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Estimated Runtime
        MetricStatRow(
            title = "Estimated Runtime",
            value = SolarCalculatorEngine.formatRuntime(result.estimatedRuntimeHours),
            icon = Icons.Default.CheckCircle,
            iconColor = PowerTrackGreenLight,
            iconBgColor = PowerTrackGreenContainer,
            valueColor = PowerTrackGreenLight,
            subtitle = "Under continuous active load"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Estimated Solar Production
        MetricStatRow(
            title = "Est. Solar Production",
            value = "${String.format("%.2f", result.estimatedSolarDailyWh / 1000.0)} kWh/day",
            icon = Icons.Default.WbSunny,
            iconColor = SolarAmber,
            iconBgColor = SolarAmberContainer,
            valueColor = SolarAmber,
            subtitle = "Estimated daily generation"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Inverter Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Inverter Status",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                    Text(
                        text = when (result.inverterStatus) {
                            InverterStatus.WITHIN_LIMIT -> "Load is within inverter capacity (${result.inverterWatts.toInt()}W)"
                            InverterStatus.APPROACHING -> "High load on inverter (${result.inverterWatts.toInt()}W)"
                            InverterStatus.EXCEEDED -> "Load exceeds inverter rating (${result.inverterWatts.toInt()}W)!"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedDark
                    )
                }

                StatusBadge(status = result.inverterStatus)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryActionButton(
            text = "View Full Details & Recommendations",
            onClick = onViewFullDetails,
            testTag = "view_full_details_button"
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailedResultsContent(
    result: CalculationResult,
    onShare: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // System Summary Table Card
        SectionContainerCard(title = "System Summary") {
            DetailItemRow(label = "Total Solar Capacity", value = "${SolarCalculatorEngine.formatNumber(result.totalSolarCapacityWp)} Wp")
            DetailItemRow(label = "Total Daily Energy", value = "${String.format("%.2f", result.dailyEnergyWh / 1000.0)} kWh")
            DetailItemRow(label = "Battery Nominal Energy", value = "${String.format("%.2f", result.nominalBatteryWh / 1000.0)} kWh")
            DetailItemRow(label = "Usable Battery (80% DoD)", value = "${String.format("%.2f", result.usableBatteryWh / 1000.0)} kWh")
            DetailItemRow(label = "Estimated Runtime", value = SolarCalculatorEngine.formatRuntime(result.estimatedRuntimeHours))
            DetailItemRow(label = "Estimated Solar Production", value = "${String.format("%.2f", result.estimatedSolarDailyWh / 1000.0)} kWh/day")
            DetailItemRow(label = "Inverter Size", value = "${result.inverterWatts.toInt()} W")
            DetailItemRow(label = "Total Active Load", value = "${result.totalLoadWatts.toInt()} W")
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inverter Status",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
                StatusBadge(status = result.inverterStatus)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recommendations Card
        SectionContainerCard(title = "Recommendations") {
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

        // Action Buttons Row (Share & Save)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryActionButton(
                text = "Share",
                onClick = onShare,
                leadingIcon = Icons.Default.Share,
                modifier = Modifier.weight(1f),
                testTag = "details_share_button"
            )

            PrimaryActionButton(
                text = "Save Setup",
                onClick = onSave,
                leadingIcon = Icons.Default.Bookmark,
                modifier = Modifier.weight(1f),
                testTag = "details_save_button"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
        )
    }
}
