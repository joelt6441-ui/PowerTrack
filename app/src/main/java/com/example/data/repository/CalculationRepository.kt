package com.example.data.repository

import com.example.data.local.CalculationDao
import com.example.data.local.CalculationEntity
import com.example.data.model.CalculationResult
import com.example.data.model.WhatDoINeedResult
import com.example.engine.SolarCalculatorEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CalculationRepository(private val dao: CalculationDao) {

    val allCalculations: Flow<List<CalculationEntity>> = dao.getAllCalculations()

    suspend fun getCalculationById(id: Long): CalculationEntity? = dao.getCalculationById(id)

    suspend fun insertCalculation(entity: CalculationEntity): Long = dao.insertCalculation(entity)

    suspend fun deleteById(id: Long) = dao.deleteCalculationById(id)

    suspend fun clearAll() = dao.clearAllCalculations()

    suspend fun saveSolarResult(title: String, result: CalculationResult): Long {
        val appSummary = result.appliances.joinToString(", ") { "${it.name} (${it.quantity}x ${it.wattage.toInt()}W)" }
        val recSummary = result.recommendations.joinToString("\n")
        val entity = CalculationEntity(
            title = title.ifBlank { "My Solar System" },
            calculationType = "SOLAR_SETUP",
            panelWattage = result.panelWattage,
            panelCount = result.panelCount,
            totalSolarWp = result.totalSolarCapacityWp,
            batteryVoltage = result.batteryVoltage,
            batteryAh = result.batteryAh,
            nominalBatteryWh = result.nominalBatteryWh,
            usableBatteryWh = result.usableBatteryWh,
            inverterWatts = result.inverterWatts,
            totalLoadWatts = result.totalLoadWatts,
            dailyEnergyWh = result.dailyEnergyWh,
            estimatedRuntimeHours = result.estimatedRuntimeHours,
            estimatedSolarDailyWh = result.estimatedSolarDailyWh,
            inverterStatus = result.inverterStatus.name,
            appliancesSummary = appSummary,
            recommendationsSummary = recSummary,
            detailedSummary = "Solar: ${result.totalSolarCapacityWp.toInt()}Wp | Battery: ${result.nominalBatteryWh.toInt()}Wh | Load: ${result.totalLoadWatts.toInt()}W"
        )
        return dao.insertCalculation(entity)
    }

    suspend fun saveWhatDoINeedResult(title: String, result: WhatDoINeedResult): Long {
        val appSummary = result.appliances.joinToString(", ") { "${it.name} (${it.quantity}x ${it.wattage.toInt()}W)" }
        val recSummary = result.recommendations.joinToString("\n")
        val entity = CalculationEntity(
            title = title.ifBlank { "Recommended System (${result.dailyUsageHours.toInt()}h daily)" },
            calculationType = "WHAT_DO_I_NEED",
            panelWattage = 450.0,
            panelCount = (result.recommendedSolarMinWp / 450.0).toInt().coerceAtLeast(1),
            totalSolarWp = result.recommendedSolarMinWp,
            batteryVoltage = 24,
            batteryAh = (result.recommendedBatteryWh / 24.0),
            nominalBatteryWh = result.recommendedBatteryWh,
            usableBatteryWh = result.recommendedBatteryWh * 0.8 * 0.85,
            inverterWatts = result.recommendedInverterWatts,
            totalLoadWatts = result.totalLoadWatts,
            dailyEnergyWh = result.dailyEnergyWh,
            estimatedRuntimeHours = result.dailyUsageHours,
            estimatedSolarDailyWh = result.recommendedSolarMinWp * 5.0 * 0.85,
            inverterStatus = "WITHIN_LIMIT",
            appliancesSummary = appSummary,
            recommendationsSummary = recSummary,
            detailedSummary = "Solar: ${result.recommendedPanelsText} | Battery: ${result.recommendedBatteryText} | Inverter: ${result.recommendedInverterText}"
        )
        return dao.insertCalculation(entity)
    }
}
