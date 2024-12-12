package com.luxoft.parkingassist

data class DistanceLevel(
    val name: String,
    val minDistance: Int,
    val maxDistance: Int,
    val hysteresisThreshold: Int
) {
    fun isWithinRange(distance: Int): Boolean {
        return distance in minDistance..maxDistance
    }

    fun isWithinHysteresis(previousLevel: String, distance: Int): Boolean {
        return name == previousLevel && distance <= hysteresisThreshold
    }
}

