package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalculationEntity
import com.example.data.model.InverterStatus
import com.example.engine.SolarCalculatorEngine
import com.example.ui.components.PrimaryActionButton
import com.example.ui.components.SecondaryActionButton
import com.example.ui.components.SectionContainerCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PowerTrackGreen
import com.example.ui.theme.PowerTrackGreenContainer
import com.example.ui.theme.StatusError
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.PowerTrackViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationDetailsScreen(
    viewModel: PowerTrackViewModel,
    onNavigateBack: () -> Unit
) {
    val calculation by viewModel.selectedSavedCalculation.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (calculation == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Calculation not found",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryActionButton(text = "Go Back", onClick = onNavigateBack)
        }
        return
    }

    val calc = calculation!!
    val formattedDate = remember(calc.timestamp) {
        val sdf = SimpleDateFormat("dd MMMM yyyy • hh:mm a", Locale.getDefault())
        sdf.format(Date(calc.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Calculation Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("details_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimaryDark
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "⚡ PowerTrack Solar Setup: ${calc.title}\n" +
                                        "• ${calc.detailedSummary}\n" +
                                        "• Est. Runtime: ${SolarCalculatorEngine.formatRuntime(calc.estimatedRuntimeHours)}\n" +
                                        "• Daily Generation: ${String.format("%.2f", calc.estimatedSolarDailyWh / 1000.0)} kWh/day\n" +
                                        "• Appliances: ${calc.appliancesSummary}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Solar Details"))
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = TextPrimaryDark)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = TextPrimaryDark
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Title & Timestamp Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PowerTrackGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = PowerTrackGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = calc.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // System Summary Card
            SectionContainerCard(title = "System Summary") {
                SummaryRow(label = "Total Solar Capacity", value = "${SolarCalculatorEngine.formatNumber(calc.totalSolarWp)} Wp")
                SummaryRow(label = "Total Daily Energy", value = "${String.format("%.2f", calc.dailyEnergyWh / 1000.0)} kWh")
                SummaryRow(label = "Battery Energy", value = "${String.format("%.2f", calc.nominalBatteryWh / 1000.0)} kWh (${calc.batteryVoltage}V ${calc.batteryAh.toInt()}Ah)")
                SummaryRow(label = "Usable Battery (80% DoD)", value = "${String.format("%.2f", calc.usableBatteryWh / 1000.0)} kWh")
                SummaryRow(label = "Estimated Runtime", value = SolarCalculatorEngine.formatRuntime(calc.estimatedRuntimeHours))
                SummaryRow(label = "Estimated Solar Production", value = "${String.format("%.2f", calc.estimatedSolarDailyWh / 1000.0)} kWh/day")
                SummaryRow(label = "Inverter Size", value = "${calc.inverterWatts.toInt()} W")
                SummaryRow(label = "Total Active Load", value = "${calc.totalLoadWatts.toInt()} W")
                
                val statusEnum = when (calc.inverterStatus) {
                    "EXCEEDED" -> InverterStatus.EXCEEDED
                    "APPROACHING" -> InverterStatus.APPROACHING
                    else -> InverterStatus.WITHIN_LIMIT
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Inverter Status", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                    StatusBadge(status = statusEnum)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Connected Appliances Card
            SectionContainerCard(title = "Appliances Included") {
                Text(
                    text = calc.appliancesSummary.ifBlank { "Standard solar appliance load" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimaryDark,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendations Card
            if (calc.recommendationsSummary.isNotBlank()) {
                SectionContainerCard(title = "Recommendations & Notes") {
                    Text(
                        text = calc.recommendationsSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimaryDark,
                        lineHeight = 22.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryActionButton(
                    text = "Delete",
                    onClick = { showDeleteDialog = true },
                    leadingIcon = Icons.Default.Delete,
                    modifier = Modifier.weight(1f),
                    testTag = "detail_delete_button"
                )

                PrimaryActionButton(
                    text = "Share Setup",
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "⚡ PowerTrack Solar Setup: ${calc.title}\n" +
                                        "• ${calc.detailedSummary}\n" +
                                        "• Est. Runtime: ${SolarCalculatorEngine.formatRuntime(calc.estimatedRuntimeHours)}\n" +
                                        "• Daily Generation: ${String.format("%.2f", calc.estimatedSolarDailyWh / 1000.0)} kWh/day"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Solar Details"))
                    },
                    leadingIcon = Icons.Default.Share,
                    modifier = Modifier.weight(1f),
                    testTag = "detail_share_button"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Setup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this saved calculation?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSavedCalculation(calc.id)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError, contentColor = Color.White)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
    }
}
