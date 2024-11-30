package com.luxoft.adilultrasonickotlintest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ParkingAssistantViewModel(private val repository: IParkingAssistantRepository) : ViewModel() {

    private val _sensorReadings = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val sensorReadings: StateFlow<Map<Int, Int>> get() = _sensorReadings

    private var isRunning = true
    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        startUltrasonicMonitoring()
    }

    private fun startUltrasonicMonitoring() {
        viewModelScope.launch {
            while (isRunning) {
                val readings = mutableMapOf<Int, Int>()
                for (sensorId in 0..5) {
                    readings[sensorId] = repository.getUltrasonicReading(sensorId)
                    delay(10)
                }
                _sensorReadings.emit(readings)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        isRunning = false
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
