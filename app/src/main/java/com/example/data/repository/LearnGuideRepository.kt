package com.example.data.repository

import com.example.data.model.GuideSection
import com.example.data.model.GuideTopic

object LearnGuideRepository {

    val guides: List<GuideTopic> = listOf(
        GuideTopic(
            id = "how-solar-works",
            title = "How Solar Systems Work",
            subtitle = "Understand the basic components and energy flow",
            category = "Basics",
            icon = "solar",
            readTimeMinutes = 4,
            keyPoints = listOf(
                "Solar panels convert sunlight into DC electrical energy.",
                "Charge controller protects batteries from overcharging and regulates voltage.",
                "Battery bank stores electrical energy for nighttime or cloudy days.",
                "Inverter converts stored DC power into 220V/110V AC power for household appliances."
            ),
            contentSections = listOf(
                GuideSection(
                    heading = "1. Solar Photovoltaic (PV) Panels",
                    body = "Solar panels capture photon energy from sunlight and convert it directly into Direct Current (DC) electricity using silicon semiconductors. Peak generation occurs during sunny midday hours.",
                    tip = "Keep panels tilted towards the equator (south in Northern hemisphere, north in Southern hemisphere) free from shadows."
                ),
                GuideSection(
                    heading = "2. Charge Controller (MPPT vs PWM)",
                    body = "The charge controller sits between the panels and batteries. MPPT (Maximum Power Point Tracking) controllers are up to 30% more efficient than basic PWM controllers, tracking the ideal electrical point in changing weather.",
                    formula = "Efficiency: MPPT (95-99%) vs PWM (70-75%)"
                ),
                GuideSection(
                    heading = "3. Energy Storage (Battery Bank)",
                    body = "Batteries store surplus solar energy generated during sunny hours so you can power lights, refrigeration, and electronics at night or during grid outages.",
                    tip = "Deep-cycle batteries (LiFePO4 Lithium or Deep Cycle AGM) are designed specifically for solar cycles."
                ),
                GuideSection(
                    heading = "4. Power Inverter",
                    body = "Most household appliances (TVs, fridges, blenders, ACs) run on Alternating Current (AC). The inverter transforms the DC battery voltage into clean AC power."
                )
            )
        ),
        GuideTopic(
            id = "how-to-calculate-load",
            title = "How to Calculate Load",
            subtitle = "Step by step load sizing for accurate system designs",
            category = "Calculations",
            icon = "calculator",
            readTimeMinutes = 5,
            keyPoints = listOf(
                "Power (Watts) is instant electrical draw; Energy (Watt-hours) is power over time.",
                "Total simultaneous load determines your minimum inverter size.",
                "Daily energy consumption (Wh/day) determines required solar panel and battery size."
            ),
            contentSections = listOf(
                GuideSection(
                    heading = "Step 1: List all Appliances and Wattages",
                    body = "Look at the rating plate on the back or bottom of each device. For example: LED TV = 100W, Standing Fan = 60W, Refrigerator = 150W.",
                    tip = "If only Volts and Amps are given, calculate: Watts = Volts × Amps."
                ),
                GuideSection(
                    heading = "Step 2: Estimate Hours of Daily Usage",
                    body = "Estimate how many hours each appliance runs each day. A TV might run 5 hours/day, while a standing fan might run 8 hours/day.",
                    formula = "Daily Energy (Wh) = Wattage (W) × Quantity × Hours per Day"
                ),
                GuideSection(
                    heading = "Step 3: Total Load vs Daily Energy",
                    body = "Total Load (Watts) = Sum of all appliance wattages turned on at the same time. Daily Energy (Watt-hours) = Sum of daily consumption for all devices.",
                    tip = "Always add a 20% safety margin for inverter losses and startup surges."
                )
            )
        ),
        GuideTopic(
            id = "choosing-battery",
            title = "Choosing the Right Battery",
            subtitle = "Ah vs Wh, Depth of Discharge (DoD), and Lithium vs AGM",
            category = "Batteries",
            icon = "battery",
            readTimeMinutes = 6,
            keyPoints = listOf(
                "Amp-Hours (Ah) must be multiplied by Voltage (V) to get true Energy in Watt-hours (Wh).",
                "Lithium (LiFePO4) can be safely discharged to 80-90% DoD.",
                "Lead-acid / AGM should only be discharged to 50% DoD to avoid rapid degradation."
            ),
            contentSections = listOf(
                GuideSection(
                    heading = "Why Voltage Matters for Battery Ah",
                    body = "A 12V 200Ah battery contains 2,400 Wh of nominal energy (12 × 200 = 2,400Wh). A 24V 100Ah battery also contains 2,400 Wh! Always compare batteries in Watt-hours (Wh) or kilowatt-hours (kWh).",
                    formula = "Energy (Wh) = Voltage (V) × Capacity (Ah)"
                ),
                GuideSection(
                    heading = "Depth of Discharge (DoD)",
                    body = "You cannot use 100% of lead-acid capacity without destroying the cells. A 2,400Wh AGM battery gives around 1,200Wh of usable power (50% DoD). A 2,400Wh Lithium battery gives ~2,000Wh of usable power (85% DoD).",
                    tip = "Lithium LiFePO4 lasts 3,000-6,000 cycles, while Lead-Acid typically lasts 500-800 cycles."
                ),
                GuideSection(
                    heading = "Calculating Battery Runtime",
                    body = "Runtime (Hours) = Usable Battery Energy (Wh) ÷ Active Load (Watts).",
                    formula = "Runtime = (Voltage × Ah × DoD × 0.85 Efficiency) ÷ Appliance Load"
                )
            )
        ),
        GuideTopic(
            id = "inverter-guide",
            title = "Inverter Sizing & Types",
            subtitle = "Pure Sine Wave vs Modified Sine Wave and Surge Ratings",
            category = "Inverters",
            icon = "inverter",
            readTimeMinutes = 5,
            keyPoints = listOf(
                "Pure Sine Wave produces clean utility-grade power identical to the electrical grid.",
                "Modified Sine Wave can cause motor buzzing, overheating, and electronic damage.",
                "Always size inverters with 25-30% continuous headroom above total simultaneous load."
            ),
            contentSections = listOf(
                GuideSection(
                    heading = "Pure Sine Wave vs Modified Sine Wave",
                    body = "Pure Sine Wave inverters output a smooth sine curve, making them safe for sensitive electronics (laptops, audio, smart TVs, medical gear) and inductive loads (refrigerators, pumps, fans). Modified sine wave inverters are cheaper but cause inductive motors to run hot and fail prematurely.",
                    tip = "Always choose Pure Sine Wave for home and office solar installations."
                ),
                GuideSection(
                    heading = "Continuous Rating vs Surge Power",
                    body = "Inductive appliances like refrigerator compressors and water pumps require 2x to 4x their rated wattage for 2-3 seconds at startup. Ensure your inverter's peak surge rating can handle these spikes.",
                    formula = "Suggested Inverter Size ≥ Total Max Simultaneous Load × 1.25"
                )
            )
        ),
        GuideTopic(
            id = "solar-panel-guide",
            title = "Solar Panel Guide",
            subtitle = "Mono vs Poly, Watt-peak (Wp) ratings, and Peak Sun Hours",
            category = "Solar Panels",
            icon = "sun",
            readTimeMinutes = 4,
            keyPoints = listOf(
                "Monocrystalline panels offer higher efficiency (20-22%) and better low-light performance.",
                "Peak Sun Hours represent equivalent hours of 1,000 W/m² irradiance (typically 4.5 to 6 hours daily).",
                "Daily Solar Production ≈ Total Solar Capacity (Wp) × Peak Sun Hours × Efficiency (0.80)."
            ),
            contentSections = listOf(
                GuideSection(
                    heading = "Monocrystalline vs Polycrystalline",
                    body = "Monocrystalline panels (black appearance) are made from single-crystal silicon, providing the highest power output per square meter. Polycrystalline panels (blue marble look) are slightly cheaper but require more roof space for the same wattage.",
                    tip = "Tier 1 Monocrystalline PERC or TOPCon panels offer the best modern lifespan and warranty (25 years)."
                ),
                GuideSection(
                    heading = "Understanding Peak Sun Hours",
                    body = "Sun hours are not the total daylight hours, but the normalized number of hours where sunlight intensity is at its peak. In tropical and temperate sunny regions, average peak sun hours range from 4.5 to 5.5 hours per day.",
                    formula = "Daily Generation (Wh) = Solar Wp × Peak Sun Hours × 0.8 System Factor"
                )
            )
        ),
        GuideTopic(
            id = "system-maintenance-tips",
            title = "System Maintenance Tips",
            subtitle = "Keep your solar system efficient, safe, and long-lasting",
            category = "Maintenance",
            icon = "maintenance",
            readTimeMinutes = 3,
            keyPoints = listOf(
                "Dust and bird droppings can reduce solar output by 15-25%.",
                "Keep battery terminals clean and tightly torqued to prevent resistive heating.",
                "Ensure inverters and batteries are installed in well-ventilated, cool areas."
            ),
            contentSections = listOf(
                GuideSection(
                    heading = "1. Solar Panel Cleaning",
                    body = "Wipe panels with water and a soft microfiber sponge or squeegee in the early morning or evening when panels are cool to prevent thermal glass shock.",
                    tip = "Never use abrasive cleaners or power washers on solar panel glass."
                ),
                GuideSection(
                    heading = "2. Ventilation & Temperature",
                    body = "Batteries and inverters generate heat during charge and discharge cycles. High ambient temperatures degrade battery capacity rapidly.",
                    tip = "Ideal battery room temperature is between 20°C and 25°C (68°F - 77°F)."
                ),
                GuideSection(
                    heading = "3. Periodic Wiring & Breaker Checks",
                    body = "Check DC breakers, surge protection devices (SPD), and grounding connections annually to ensure electrical fire safety."
                )
            )
        )
    )

    fun getGuideById(id: String): GuideTopic? = guides.find { it.id == id }
}
