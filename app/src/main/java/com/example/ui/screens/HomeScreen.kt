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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PowerTrackTopBar
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
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.PowerTrackViewModel

@Composable
fun HomeScreen(
    viewModel: PowerTrackViewModel,
    onNavigateToSolarCalculator: () -> Unit,
    onNavigateToWhatDoINeed: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToLearn: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val savedCalculations by viewModel.savedCalculations.collectAsState()
    val latestCalc = savedCalculations.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        PowerTrackTopBar(
            title = "PowerTrack",
            subtitle = "Solar Calculator",
            navigationIcon = Icons.Default.Menu,
            onNavigationClick = onOpenDrawer,
            actionIcon = Icons.Default.Settings,
            onActionClick = onNavigateToSettings
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Welcome Greeting
            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = TextPrimaryDark
            )
            Text(
                text = "What would you like to do?",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Highlighted Quick Stat Overview (if user has saved or default calculation)
            if (latestCalc != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent System Overview",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondaryDark
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StatusSuccessBg,
                                border = BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = latestCalc.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = StatusSuccess,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Solar Wp
                            QuickStatBox(
                                title = "Solar Capacity",
                                value = "${latestCalc.totalSolarWp.toInt()} Wp",
                                subtitle = "${String.format("%.1f", latestCalc.estimatedSolarDailyWh / 1000.0)} kWh/day",
                                icon = Icons.Default.WbSunny,
                                color = PowerTrackGreen,
                                bgColor = PowerTrackGreenContainer,
                                modifier = Modifier.weight(1f)
                            )

                            // Load Watts
                            QuickStatBox(
                                title = "Active Load",
                                value = "${latestCalc.totalLoadWatts.toInt()} W",
                                subtitle = "${String.format("%.1f", latestCalc.dailyEnergyWh / 1000.0)} kWh/day",
                                icon = Icons.Default.Bolt,
                                color = LoadBlue,
                                bgColor = LoadBlueContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Battery Energy
                            QuickStatBox(
                                title = "Battery Energy",
                                value = "${latestCalc.nominalBatteryWh.toInt()} Wh",
                                subtitle = "Usable ${(latestCalc.usableBatteryWh / 1000.0).let { String.format("%.2f", it) }} kWh",
                                icon = Icons.Default.BatteryChargingFull,
                                color = SolarAmber,
                                bgColor = SolarAmberContainer,
                                modifier = Modifier.weight(1f)
                            )

                            // Est. Runtime
                            val h = latestCalc.estimatedRuntimeHours.toInt()
                            val m = ((latestCalc.estimatedRuntimeHours - h) * 60).toInt()
                            QuickStatBox(
                                title = "Est. Runtime",
                                value = "${h}h ${m}m",
                                subtitle = "Under load",
                                icon = Icons.Default.CheckCircle,
                                color = PowerTrackGreenLight,
                                bgColor = PowerTrackGreenContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSolarCalculator() }
                        .testTag("home_empty_start_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PowerTrackGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = PowerTrackGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ready to Calculate",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Add your appliances and system specs to calculate solar performance.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryDark
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = PowerTrackGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Main Action 1: Solar Calculator Card
            ActionNavigationCard(
                title = "Solar Calculator",
                subtitle = "Calculate your existing solar system, runtime & loads",
                icon = Icons.Default.Calculate,
                iconColor = PowerTrackGreen,
                iconBg = PowerTrackGreenContainer,
                onClick = onNavigateToSolarCalculator,
                testTag = "home_solar_calculator_card"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Main Action 2: What Do I Need? Card
            ActionNavigationCard(
                title = "What Do I Need?",
                subtitle = "Find out what solar panels, battery & inverter you need",
                icon = Icons.Default.Search,
                iconColor = LoadBlue,
                iconBg = LoadBlueContainer,
                onClick = onNavigateToWhatDoINeed,
                testTag = "home_what_do_i_need_card"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Main Action 3: Saved Calculations Card
            ActionNavigationCard(
                title = "Saved Calculations",
                subtitle = "View and compare your saved solar setups (${savedCalculations.size})",
                icon = Icons.Default.Bookmark,
                iconColor = SolarAmber,
                iconBg = SolarAmberContainer,
                onClick = onNavigateToSaved,
                testTag = "home_saved_calculations_card"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Educational Quick Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToLearn() }
                    .testTag("home_learn_guide_banner"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PowerTrackGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = PowerTrackGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Learn Solar Basics",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Explore guides on battery Ah, inverters, and load sizing",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Explore",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ActionNavigationCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = TextSecondaryDark
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = PowerTrackGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun QuickStatBox(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextMutedDark
            )
        }
    }
}
