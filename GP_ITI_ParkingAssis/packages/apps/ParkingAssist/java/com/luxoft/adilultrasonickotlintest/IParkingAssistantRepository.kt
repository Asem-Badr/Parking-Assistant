package com.luxoft.parkingassist

interface IParkingAssistantRepository {
    fun getUltrasonicReading(sensorId: Int): Int
    fun setBuzzerLevel(level: Int): Boolean
    fun getSteeringWheelAngle(): Int

}