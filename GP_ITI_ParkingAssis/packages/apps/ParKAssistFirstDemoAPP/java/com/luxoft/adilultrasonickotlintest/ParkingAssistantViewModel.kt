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


    private val bufferSize: Int = 5 // Buffer size can be controlled through this variable

    private val levels = listOf(
        DistanceLevel("Level 4", 0, 25, 30),
        DistanceLevel("Level 3", 26, 50, 55),
        DistanceLevel("Level 2", 51, 75, 80),
        DistanceLevel("Level 1", 76, 100, 105)
    )

    private var currentBuzzerLevel = 0 // Holds the current buzzer level

    private val noObstacle = DistanceLevel("No Obstacle", 101, Int.MAX_VALUE, Int.MAX_VALUE)

    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())



    /********************************************************************************************/

    // Buffer map to store the last 10 readings for each sensor
    private val sensorBuffers = mutableMapOf<Int, ArrayDeque<Int>>().apply {
        for (sensorId in 0..5) {
            this[sensorId] = ArrayDeque()
        }
    }

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
                    // Get the buffer, or initialize a new one if it doesn't exist
                    val buffer = sensorBuffers.getOrPut(sensorId) { ArrayDeque() }

                    var distance = repository.getUltrasonicReading(sensorId)
                    if (distance == -1) {
                        distance = 110
                    }


                    // Update the buffer: Remove oldest if size exceeds 10, then add the newest reading
                    if (buffer.size == bufferSize) {
                        buffer.removeFirst()
                    }
                    buffer.addLast(distance)

                    // Calculate the average of the buffer
                    val averageDistance = buffer.average().toInt()

                    // Determine the level based on the average distance
                    val previousLevel = previousLevels[sensorId] ?: "No Obstacle"
                    val currentLevel = determineLevelWithHysteresis(averageDistance, previousLevel)

                    levels[sensorId] = "$currentLevel (Avg: $averageDistance cm)"
                    previousLevels[sensorId] = currentLevel // Update the previous level
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
                val success = repository.setBuzzerLevel(currentBuzzerLevel)
                Log.i("Buzzer", "runBuzzer: " + currentBuzzerLevel + " " + success)
                if (!success) {
                    isRunningBuzzer = false
                    repository.setBuzzerLevel(0)
                }
                delay(1000)
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
