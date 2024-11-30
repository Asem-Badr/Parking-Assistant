package com.luxoft.adilultrasonickotlintest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ParkingAssistantViewModel(private val repository: IParkingAssistantRepository) : ViewModel() {

    private val _sensorReadings = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val sensorReadings: StateFlow<Map<Int, Int>> get() = _sensorReadings

    private val _currentLevel = MutableStateFlow(-1) // -1 means no level active
    val currentLevel: StateFlow<Int> get() = _currentLevel

    private val _steeringWheelAngle = MutableStateFlow(0)
    val steeringWheelAngle: StateFlow<Int> get() = _steeringWheelAngle

    private var isRunningSensor = true
    private var isRunningBuzzer = false
    private var isUpdatingSteeringWheel = true

    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())



    init {
        startUltrasonicMonitoring()
        startSteeringWheelMonitoring()
    }

    private fun startUltrasonicMonitoring() {
        viewModelScope.launch {
            while (isRunningSensor) {
                val readings = mutableMapOf<Int, Int>()
                for (sensorId in 0..5) {
                    readings[sensorId] = repository.getUltrasonicReading(sensorId)
                    delay(15)
                }
                _sensorReadings.emit(readings)
            }
        }
    }

    private fun startSteeringWheelMonitoring() {
        viewModelScope.launch {
            while (isUpdatingSteeringWheel) {
                val angle = repository.getSteeringWheelAngle()
                _steeringWheelAngle.emit(angle)
                delay(200) // Update every 200ms
            }
        }
    }


    fun startBuzzer(level: Int) {
        if (_currentLevel.value != level) {
            _currentLevel.value = level
            isRunningBuzzer = true
            runBuzzer()
        }
    }

    fun stopBuzzer() {
        isRunningBuzzer = false
        _currentLevel.value = -1
        repository.setBuzzerLevel(0)
    }

    private fun runBuzzer() {
        viewModelScope.launch {
            while (isRunningBuzzer && _currentLevel.value != -1) {
                val success = repository.setBuzzerLevel(_currentLevel.value)
                if (!success) {
                    isRunningBuzzer = false
                    _currentLevel.value = -1
                }
                delay(1000) // Adjust delay as needed
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        isRunningSensor = false
        isRunningBuzzer = false
        isUpdatingSteeringWheel = false
        viewModelScope.cancel()
    }
}

class ParkingAssistantViewModelFactory(
    private val repository: IParkingAssistantRepository,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParkingAssistantViewModel::class.java)) {
            return ParkingAssistantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
