package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CalculationEntity
import com.example.data.model.Appliance
import com.example.data.model.AppSettings
import com.example.data.model.CalculationResult
import com.example.data.model.SolarSetupInput
import com.example.data.model.WhatDoINeedResult
import com.example.data.repository.CalculationRepository
import com.example.engine.SolarCalculatorEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PowerTrackViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CalculationRepository
    private val prefs = application.getSharedPreferences("powertrack_prefs", android.content.Context.MODE_PRIVATE)

    init {
        val db = AppDatabase.getInstance(application)
        repository = CalculationRepository(db.calculationDao())
    }

    val hasCompletedOnboarding: Boolean
        get() = prefs.getBoolean("onboarding_completed", false)

    fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }

    // App Settings
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    // Saved Calculations Flow
    val savedCalculations: StateFlow<List<CalculationEntity>> = repository.allCalculations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Solar Calculator State
    private val _solarSetupInput = MutableStateFlow(
        SolarSetupInput(
            panelWattage = prefs.getFloat("panelWattage", 0f).toDouble(),
            panelCount = prefs.getInt("panelCount", 0),
            batteryVoltage = prefs.getInt("batteryVoltage", 12),
            batteryAh = prefs.getFloat("batteryAh", 0f).toDouble(),
            inverterWatts = prefs.getFloat("inverterWatts", 0f).toDouble()
        )
    )
    val solarSetupInput: StateFlow<SolarSetupInput> = _solarSetupInput.asStateFlow()

    private val _calculatorAppliances = MutableStateFlow<List<Appliance>>(emptyList())
    val calculatorAppliances: StateFlow<List<Appliance>> = _calculatorAppliances.asStateFlow()

    private val _currentCalculationResult = MutableStateFlow<CalculationResult?>(null)
    val currentCalculationResult: StateFlow<CalculationResult?> = _currentCalculationResult.asStateFlow()

    // "What Do I Need?" State
    private val _whatDoINeedAppliances = MutableStateFlow<List<Appliance>>(emptyList())
    val whatDoINeedAppliances: StateFlow<List<Appliance>> = _whatDoINeedAppliances.asStateFlow()

    private val _whatDoINeedUsageHours = MutableStateFlow(8.0)
    val whatDoINeedUsageHours: StateFlow<Double> = _whatDoINeedUsageHours.asStateFlow()

    private val _whatDoINeedResult = MutableStateFlow<WhatDoINeedResult?>(null)
    val whatDoINeedResult: StateFlow<WhatDoINeedResult?> = _whatDoINeedResult.asStateFlow()

    // Active detail item
    private val _selectedSavedCalculation = MutableStateFlow<CalculationEntity?>(null)
    val selectedSavedCalculation: StateFlow<CalculationEntity?> = _selectedSavedCalculation.asStateFlow()

    // Transient UI status
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()

    fun updateSolarSetup(
        panelWattage: Double,
        panelCount: Int,
        batteryVoltage: Int,
        batteryAh: Double,
        inverterWatts: Double
    ) {
        _solarSetupInput.value = SolarSetupInput(
            panelWattage = panelWattage,
            panelCount = panelCount,
            batteryVoltage = batteryVoltage,
            batteryAh = batteryAh,
            inverterWatts = inverterWatts
        )
        prefs.edit()
            .putFloat("panelWattage", panelWattage.toFloat())
            .putInt("panelCount", panelCount)
            .putInt("batteryVoltage", batteryVoltage)
            .putFloat("batteryAh", batteryAh.toFloat())
            .putFloat("inverterWatts", inverterWatts.toFloat())
            .apply()
    }

    fun addCalculatorAppliance(appliance: Appliance) {
        _calculatorAppliances.value = _calculatorAppliances.value + appliance
    }

    fun removeCalculatorAppliance(id: String) {
        _calculatorAppliances.value = _calculatorAppliances.value.filter { it.id != id }
    }

    fun updateCalculatorAppliance(updated: Appliance) {
        _calculatorAppliances.value = _calculatorAppliances.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    fun addWhatDoINeedAppliance(appliance: Appliance) {
        _whatDoINeedAppliances.value = _whatDoINeedAppliances.value + appliance
    }

    fun removeWhatDoINeedAppliance(id: String) {
        _whatDoINeedAppliances.value = _whatDoINeedAppliances.value.filter { it.id != id }
    }

    fun updateWhatDoINeedUsageHours(hours: Double) {
        _whatDoINeedUsageHours.value = hours.coerceIn(1.0, 24.0)
    }

    fun performCalculation(onCompleted: () -> Unit) {
        viewModelScope.launch {
            _isCalculating.value = true
            // Animate realistic calculation steps
            delay(1200)
            val result = SolarCalculatorEngine.calculate(
                input = _solarSetupInput.value,
                appliances = _calculatorAppliances.value,
                settings = _settings.value
            )
            _currentCalculationResult.value = result
            _isCalculating.value = false
            onCompleted()
        }
    }

    fun performWhatDoINeedCalculation(onCompleted: () -> Unit) {
        viewModelScope.launch {
            _isCalculating.value = true
            delay(900)
            val result = SolarCalculatorEngine.calculateWhatDoINeed(
                appliances = _whatDoINeedAppliances.value,
                dailyUsageHours = _whatDoINeedUsageHours.value,
                settings = _settings.value
            )
            _whatDoINeedResult.value = result
            _isCalculating.value = false
            onCompleted()
        }
    }

    fun saveCurrentCalculation(title: String, onSuccess: () -> Unit) {
        val current = _currentCalculationResult.value ?: return
        viewModelScope.launch {
            repository.saveSolarResult(title, current)
            _snackbarMessage.value = "Calculation \"$title\" saved successfully!"
            onSuccess()
        }
    }

    fun saveWhatDoINeedCalculation(title: String, onSuccess: () -> Unit) {
        val current = _whatDoINeedResult.value ?: return
        viewModelScope.launch {
            repository.saveWhatDoINeedResult(title, current)
            _snackbarMessage.value = "Recommendation saved to Saved Calculations!"
            onSuccess()
        }
    }

    fun selectSavedCalculation(calc: CalculationEntity) {
        _selectedSavedCalculation.value = calc
    }

    fun deleteSavedCalculation(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
            if (_selectedSavedCalculation.value?.id == id) {
                _selectedSavedCalculation.value = null
            }
            _snackbarMessage.value = "Calculation deleted"
        }
    }

    fun clearAllSaved() {
        viewModelScope.launch {
            repository.clearAll()
            _selectedSavedCalculation.value = null
            _snackbarMessage.value = "All saved calculations cleared"
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
        _snackbarMessage.value = "Settings updated"
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
