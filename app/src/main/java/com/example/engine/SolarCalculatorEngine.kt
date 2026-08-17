package com.example.engine

import com.example.data.model.Appliance
import com.example.data.model.AppSettings
import com.example.data.model.CalculationResult
import com.example.data.model.InverterStatus
import com.example.data.model.SolarSetupInput
import com.example.data.model.WhatDoINeedResult
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.max

object SolarCalculatorEngine {

    val PRESET_APPLIANCES = listOf(
        Appliance(name = "TV (LED 43\")", wattage = 100.0, quantity = 1, hoursPerDay = 5.0, iconCategory = "tv"),
        Appliance(name = "Standing Fan", wattage = 60.0, quantity = 1, hoursPerDay = 8.0, iconCategory = "fan"),
        Appliance(name = "Ceiling Fan", wattage = 75.0, quantity = 2, hoursPerDay = 8.0, iconCategory = "fan"),
        Appliance(name = "Refrigerator (Inverter)", wattage = 150.0, quantity = 1, hoursPerDay = 12.0, iconCategory = "fridge"),
        Appliance(name = "Deep Freezer", wattage = 200.0, quantity = 1, hoursPerDay = 10.0, iconCategory = "fridge"),
        Appliance(name = "LED Bulbs (10W)", wattage = 10.0, quantity = 4, hoursPerDay = 6.0, iconCategory = "light"),
        Appliance(name = "Laptop", wattage = 65.0, quantity = 1, hoursPerDay = 6.0, iconCategory = "laptop"),
        Appliance(name = "Phone Charger", wattage = 18.0, quantity = 2, hoursPerDay = 3.0, iconCategory = "phone"),
        Appliance(name = "Decoder / TV Box", wattage = 25.0, quantity = 1, hoursPerDay = 6.0, iconCategory = "tv"),
        Appliance(name = "Desktop Computer", wattage = 250.0, quantity = 1, hoursPerDay = 5.0, iconCategory = "laptop"),
        Appliance(name = "Washing Machine", wattage = 500.0, quantity = 1, hoursPerDay = 1.0, iconCategory = "motor"),
        Appliance(name = "Water Pump (0.5 HP)", wattage = 750.0, quantity = 1, hoursPerDay = 0.5, iconCategory = "motor"),
        Appliance(name = "Microwave Oven", wattage = 800.0, quantity = 1, hoursPerDay = 0.3, iconCategory = "kitchen")
    )

    fun createPresetAppliance(preset: Appliance): Appliance {
        return preset.copy(id = UUID.randomUUID().toString())
    }

    /**
     * Calculates the full Solar Calculator results for existing setup
     */
    fun calculate(
        input: SolarSetupInput,
        appliances: List<Appliance>,
        settings: AppSettings = AppSettings()
    ): CalculationResult {
        val totalSolarWp = max(0.0, input.panelWattage * max(0, input.panelCount))
        val nominalBatteryWh = max(0.0, input.batteryVoltage.toDouble() * max(0.0, input.batteryAh))
        val usableBatteryWh = nominalBatteryWh * settings.batteryDoD * settings.systemEfficiency
        
        val totalLoadWatts = appliances.sumOf { max(0.0, it.wattage * max(0, it.quantity)) }
        val dailyEnergyWh = appliances.sumOf { max(0.0, it.wattage * max(0, it.quantity) * max(0.0, it.hoursPerDay)) }
        
        val estimatedRuntimeHours = if (totalLoadWatts > 0) {
            usableBatteryWh / totalLoadWatts
        } else {
            0.0
        }

        val estimatedSolarDailyWh = totalSolarWp * settings.peakSunHours * settings.systemEfficiency

        val inverterStatus = when {
            input.inverterWatts <= 0 -> InverterStatus.EXCEEDED
            totalLoadWatts > input.inverterWatts -> InverterStatus.EXCEEDED
            totalLoadWatts >= (input.inverterWatts * 0.85) -> InverterStatus.APPROACHING
            else -> InverterStatus.WITHIN_LIMIT
        }

        val recommendations = generateRecommendations(
            totalSolarWp = totalSolarWp,
            estimatedSolarDailyWh = estimatedSolarDailyWh,
            totalLoadWatts = totalLoadWatts,
            dailyEnergyWh = dailyEnergyWh,
            inverterWatts = input.inverterWatts,
            usableBatteryWh = usableBatteryWh,
            estimatedRuntimeHours = estimatedRuntimeHours,
            inverterStatus = inverterStatus
        )

        return CalculationResult(
            totalSolarCapacityWp = totalSolarWp,
            panelWattage = input.panelWattage,
            panelCount = input.panelCount,
            batteryVoltage = input.batteryVoltage,
            batteryAh = input.batteryAh,
            nominalBatteryWh = nominalBatteryWh,
            usableBatteryWh = usableBatteryWh,
            inverterWatts = input.inverterWatts,
            totalLoadWatts = totalLoadWatts,
            dailyEnergyWh = dailyEnergyWh,
            estimatedRuntimeHours = estimatedRuntimeHours,
            estimatedSolarDailyWh = estimatedSolarDailyWh,
            inverterStatus = inverterStatus,
            appliances = appliances,
            recommendations = recommendations
        )
    }

    /**
     * Reverse Solar Calculator: "What Do I Need?"
     */
    fun calculateWhatDoINeed(
        appliances: List<Appliance>,
        dailyUsageHours: Double,
        settings: AppSettings = AppSettings()
    ): WhatDoINeedResult {
        val totalLoadWatts = appliances.sumOf { max(0.0, it.wattage * max(0, it.quantity)) }
        val dailyEnergyWh = appliances.sumOf { max(0.0, it.wattage * max(0, it.quantity) * dailyUsageHours) }

        // Recommended Inverter: Needs at least 25-30% buffer over peak load for startup surges
        val minInverter = totalLoadWatts * 1.25
        val recommendedInverterWatts = when {
            minInverter <= 500 -> 600.0
            minInverter <= 1000 -> 1200.0
            minInverter <= 1500 -> 2000.0
            minInverter <= 2400 -> 3000.0
            minInverter <= 4000 -> 5000.0
            else -> ceil(minInverter / 1000.0) * 1000.0
        }

        val inverterText = "${recommendedInverterWatts.toInt()} W (Pure Sine Wave)"

        // Recommended Solar: (Daily Wh * 1.25 safety loss factor) / (PeakSunHours * Efficiency)
        val dailySolarRequired = dailyEnergyWh * 1.25
        val baseSolarWp = if (settings.peakSunHours > 0 && settings.systemEfficiency > 0) {
            dailySolarRequired / (settings.peakSunHours * settings.systemEfficiency)
        } else {
            0.0
        }

        val roundedMinSolar = max(400.0, ceil(baseSolarWp / 100.0) * 100.0)
        val roundedMaxSolar = roundedMinSolar * 1.25

        val panelWattageOption = if (roundedMinSolar >= 1200) 450 else 300
        val minPanels = ceil(roundedMinSolar / panelWattageOption).toInt()
        val maxPanels = ceil(roundedMaxSolar / panelWattageOption).toInt()

        val panelsText = "${formatNumber(roundedMinSolar)} – ${formatNumber(roundedMaxSolar)} Wp ($minPanels – $maxPanels panels of ${panelWattageOption}W)"

        // Recommended Battery: Needs to cover desired runtime Wh
        // Desired battery coverage: e.g. at least 60% of daily energy or night backup
        val batteryNeededWh = dailyEnergyWh * 0.75
        val nominalBatteryWhNeeded = batteryNeededWh / (settings.batteryDoD * settings.systemEfficiency)
        
        val recommendedBatteryWh = max(1200.0, ceil(nominalBatteryWhNeeded / 600.0) * 600.0)

        val batteryText = when {
            recommendedBatteryWh <= 2400 -> "12V 200Ah (or 24V 100Ah) ≈ ${formatNumber(recommendedBatteryWh)} Wh"
            recommendedBatteryWh <= 4800 -> "24V 200Ah (or 48V 100Ah) ≈ ${formatNumber(recommendedBatteryWh)} Wh"
            else -> {
                val ahAt48v = (recommendedBatteryWh / 48.0).toInt()
                "48V ${ahAt48v}Ah ≈ ${formatNumber(recommendedBatteryWh)} Wh"
            }
        }

        val recs = mutableListOf<String>()
        if (totalLoadWatts > 0) {
            recs.add("For a daily load of ${formatNumber(totalLoadWatts)} W running for ${dailyUsageHours.toInt()}h, this configuration provides ample overhead.")
            recs.add("Pure Sine Wave inverters are strongly recommended to protect compressors, motors, and delicate electronics.")
            recs.add("Lithium (LiFePO4) batteries are recommended for longer lifecycle (80-90% DoD) compared to Lead Acid (50% DoD).")
        } else {
            recs.add("Add your appliances to get personalized solar, battery, and inverter recommendations.")
        }

        return WhatDoINeedResult(
            appliances = appliances,
            dailyUsageHours = dailyUsageHours,
            totalLoadWatts = totalLoadWatts,
            dailyEnergyWh = dailyEnergyWh,
            recommendedSolarMinWp = roundedMinSolar,
            recommendedSolarMaxWp = roundedMaxSolar,
            recommendedPanelsText = panelsText,
            recommendedBatteryWh = recommendedBatteryWh,
            recommendedBatteryText = batteryText,
            recommendedInverterWatts = recommendedInverterWatts,
            recommendedInverterText = inverterText,
            recommendations = recs
        )
    }

    private fun generateRecommendations(
        totalSolarWp: Double,
        estimatedSolarDailyWh: Double,
        totalLoadWatts: Double,
        dailyEnergyWh: Double,
        inverterWatts: Double,
        usableBatteryWh: Double,
        estimatedRuntimeHours: Double,
        inverterStatus: InverterStatus
    ): List<String> {
        val list = mutableListOf<String>()

        when (inverterStatus) {
            InverterStatus.EXCEEDED -> {
                list.add("⚠️ Inverter Overload: Your total appliance load (${formatNumber(totalLoadWatts)} W) exceeds your inverter rating (${formatNumber(inverterWatts)} W). Turn off heavy appliances or upgrade your inverter.")
            }
            InverterStatus.APPROACHING -> {
                list.add("⚡ High Inverter Load: Running at ${((totalLoadWatts / inverterWatts) * 100).toInt()}% inverter capacity. Avoid starting motorized appliances simultaneously.")
            }
            InverterStatus.WITHIN_LIMIT -> {
                list.add("✅ Inverter capacity is adequate for your active appliance load.")
            }
        }

        if (totalSolarWp > 0 && dailyEnergyWh > 0) {
            if (estimatedSolarDailyWh >= dailyEnergyWh * 1.1) {
                list.add("☀️ Solar generation (${formatNumber(estimatedSolarDailyWh / 1000.0, 2)} kWh/day) is estimated to fully replenish your daily energy consumption.")
            } else {
                list.add("📉 Solar deficit: Daily solar production (${formatNumber(estimatedSolarDailyWh / 1000.0, 2)} kWh) may be lower than daily usage (${formatNumber(dailyEnergyWh / 1000.0, 2)} kWh). Consider adding more solar panels.")
            }
        }

        if (totalLoadWatts > 0) {
            if (estimatedRuntimeHours < 2.0) {
                list.add("🔋 Short Battery Backup: Estimated runtime is under 2 hours. Consider increasing battery Ah capacity or reducing high-draw appliances.")
            } else {
                val hours = estimatedRuntimeHours.toInt()
                val minutes = ((estimatedRuntimeHours - hours) * 60).toInt()
                list.add("🔋 Battery backup provides approximately ${hours}h ${minutes}m of continuous runtime under current load.")
            }
        }

        return list
    }

    fun formatRuntime(hours: Double): String {
        if (hours <= 0.0 || hours.isNaN() || hours.isInfinite()) return "0h 0m"
        val h = hours.toInt()
        val m = ((hours - h) * 60).toInt()
        return "${h}h ${m}m"
    }

    fun formatNumber(value: Double, decimals: Int = 0): String {
        return if (decimals == 0) {
            String.format("%,d", value.toLong())
        } else {
            String.format("%,.${decimals}f", value)
        }
    }
}
