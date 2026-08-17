package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_calculations")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val calculationType: String, // "SOLAR_SETUP" or "WHAT_DO_I_NEED"
    val timestamp: Long = System.currentTimeMillis(),
    
    // Solar & Battery Setup inputs
    val panelWattage: Double,
    val panelCount: Int,
    val totalSolarWp: Double,
    val batteryVoltage: Int,
    val batteryAh: Double,
    val nominalBatteryWh: Double,
    val usableBatteryWh: Double,
    val inverterWatts: Double,
    
    // Calculated results
    val totalLoadWatts: Double,
    val dailyEnergyWh: Double,
    val estimatedRuntimeHours: Double,
    val estimatedSolarDailyWh: Double,
    val inverterStatus: String,
    
    // JSON or structured serialized strings
    val appliancesSummary: String, // e.g. "TV (100W), Fan (60W), Fridge (150W)"
    val recommendationsSummary: String,
    val detailedSummary: String
)
