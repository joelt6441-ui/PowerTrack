package com.example.data.model

import java.util.UUID

data class Appliance(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val wattage: Double,
    val quantity: Int = 1,
    val hoursPerDay: Double = 4.0,
    val iconCategory: String = "general"
) {
    val totalLoad: Double get() = wattage * quantity
    val dailyEnergy: Double get() = wattage * quantity * hoursPerDay
}

enum class InverterStatus {
    WITHIN_LIMIT,
    APPROACHING,
    EXCEEDED
}

data class SolarSetupInput(
    val panelWattage: Double = 450.0,
    val panelCount: Int = 4,
    val batteryVoltage: Int = 12,
    val batteryAh: Double = 200.0,
    val inverterWatts: Double = 2000.0
)

data class CalculationResult(
    val totalSolarCapacityWp: Double,
    val panelWattage: Double,
    val panelCount: Int,
    val batteryVoltage: Int,
    val batteryAh: Double,
    val nominalBatteryWh: Double,
    val usableBatteryWh: Double,
    val inverterWatts: Double,
    val totalLoadWatts: Double,
    val dailyEnergyWh: Double,
    val estimatedRuntimeHours: Double,
    val estimatedSolarDailyWh: Double,
    val inverterStatus: InverterStatus,
    val appliances: List<Appliance>,
    val recommendations: List<String>
)

data class WhatDoINeedResult(
    val appliances: List<Appliance>,
    val dailyUsageHours: Double,
    val totalLoadWatts: Double,
    val dailyEnergyWh: Double,
    val recommendedSolarMinWp: Double,
    val recommendedSolarMaxWp: Double,
    val recommendedPanelsText: String,
    val recommendedBatteryWh: Double,
    val recommendedBatteryText: String,
    val recommendedInverterWatts: Double,
    val recommendedInverterText: String,
    val recommendations: List<String>
)

data class GuideTopic(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: String,
    val readTimeMinutes: Int,
    val keyPoints: List<String>,
    val contentSections: List<GuideSection>
)

data class GuideSection(
    val heading: String,
    val body: String,
    val tip: String? = null,
    val formula: String? = null
)

data class AppSettings(
    val isDarkMode: Boolean = true,
    val peakSunHours: Double = 5.0,
    val batteryDoD: Double = 0.80, // 80% usable DoD for Lithium or 50% for Lead Acid
    val systemEfficiency: Double = 0.85, // Inverter + cable + dirt losses
    val currency: String = "$",
    val units: String = "Metric (W, Wh)"
)
