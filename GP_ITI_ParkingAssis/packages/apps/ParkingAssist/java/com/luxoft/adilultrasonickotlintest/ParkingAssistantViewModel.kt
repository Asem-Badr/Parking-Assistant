package com.luxoft.adilultrasonickotlintest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ParkingAssistantViewModel(private val repository: IParkingAssistantRepository) : ViewModel() {

    private val _sensorLevels = MutableStateFlow<Map<Int, String>>(emptyMap()) // Emits levels with hysteresis
    val sensorLevels: StateFlow<Map<Int, String>> get() = _sensorLevels


    private val _steeringWheelAngle = MutableStateFlow(0)
    val steeringWheelAngle: StateFlow<Int> get() = _steeringWheelAngle

    private var isRunningSensor = true
    private var isRunningBuzzer = true
    private var isUpdatingSteeringWheel = true



    private val levels = listOf(
        DistanceLevel("Level 4", 4, 30, 35),
        DistanceLevel("Level 3", 31, 45, 48),
        DistanceLevel("Level 2", 46, 60, 63),
        DistanceLevel("Level 1", 61, 75, 65)
    )

    private var currentBuzzerLevel = 0 // Holds the current buzzer level

    private val noObstacle = DistanceLevel("No Obstacle", 76, Int.MAX_VALUE, Int.MAX_VALUE)

    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())



    /********************************************************************************************/


    init {
        startUltrasonicMonitoring()
        startSteeringWheelMonitoring()
        runBuzzer()
    }

    private fun startUltrasonicMonitoring() {
        val previousLevels = mutableMapOf<Int, String>() // To store previous levels for each sensor
        viewModelScope.launch {
            while (isRunningSensor) {
                val levels = mutableMapOf<Int, String>() // Current levels map
                for (sensorId in 0..5) {

                    var distance = repository.getUltrasonicReading(sensorId)
                    if (distance != -1) {
                        // Determine the level based on the average distance
                        val previousLevel = previousLevels[sensorId] ?: "No Obstacle"
                        val currentLevel = determineLevelWithHysteresis(distance, previousLevel)

                        levels[sensorId] = "$currentLevel (Avg: $distance cm)"
                        previousLevels[sensorId] = currentLevel // Update the previous level
                    }
                }

                _sensorLevels.emit(levels) // Emit updated levels
                // Automatically adjust buzzer level
                adjustBuzzerLevel(levels)
            }
        }
    }



    private fun adjustBuzzerLevel(sensorLevels: Map<Int, String>) {
        var activeLevel = 0

        // Explicitly check each level condition
        for (levelInfo in sensorLevels.values) {
            if (levelInfo.startsWith("Level 4")) {
                activeLevel = maxOf(activeLevel, 4)
            } else if (levelInfo.startsWith("Level 3")) {
                activeLevel = maxOf(activeLevel, 3)
            } else if (levelInfo.startsWith("Level 2")) {
                activeLevel = maxOf(activeLevel, 2)
            } else if (levelInfo.startsWith("Level 1")) {
                activeLevel = maxOf(activeLevel, 1)
            } else if (levelInfo.startsWith("No Obstacle")) {
                activeLevel = maxOf(activeLevel, 0)
            }
        }

        if (currentBuzzerLevel != activeLevel) {
            currentBuzzerLevel = activeLevel
            Log.i("Buzzer", "Updated activeLevel: $activeLevel, currentBuzzerLevel: $currentBuzzerLevel")
        } else {
            Log.i("Buzzer", "No change in level: activeLevel: $activeLevel, currentBuzzerLevel: $currentBuzzerLevel")
        }
    }


    private fun runBuzzer() {
        viewModelScope.launch {
            while (isRunningBuzzer) {
                if (!isRunningBuzzer) break // Ensure immediate exit

                val success = repository.setBuzzerLevel(currentBuzzerLevel)
                Log.i("Buzzer", "runBuzzer: currentBuzzerLevel=$currentBuzzerLevel, success=$success")

                if (!success) {
                    Log.i("Buzzer", "Stopping buzzer due to failure in repository")
                    isRunningBuzzer = false
                    repository.setBuzzerLevel(0) // Ensure buzzer is off
                    break
                }
                //delay(200) // Add delay to avoid busy looping
            }
        }
    }


    // Function to start the buzzer
    fun startBuzzer() {
        if (!isRunningBuzzer) {
            isRunningBuzzer = true
            runBuzzer() // Restart the buzzer coroutine
            Log.i("BuzzerControl", "Buzzer started")
        }
    }

    // Function to stop the buzzer
    fun stopBuzzer() {
        if (isRunningBuzzer) {
            isRunningBuzzer = false
            Log.i("BuzzerControl", "Buzzer stop requested")

            viewModelScope.launch {
                val success = repository.setBuzzerLevel(0)
                if (success) {
                    Log.i("BuzzerControl", "Buzzer stopped successfully")
                } else {
                    Log.i("BuzzerControl", "Failed to stop the buzzer")
                }
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


    private fun determineLevelWithHysteresis(distance: Int, previousLevel: String): String {
        // Check if the current distance matches any level or its hysteresis
        levels.forEach { level ->
            if (level.isWithinRange(distance) || level.isWithinHysteresis(previousLevel, distance)) {
                return level.name
            }
        }
        // Default to "No Obstacle" if no level matches
        return if (noObstacle.isWithinHysteresis(previousLevel, distance)) {
            noObstacle.name
        } else {
            "No Obstacle"
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
