package com.luxoft.adilultrasonickotlintest

interface IParkingAssistantRepository {
    fun getUltrasonicReading(sensorId: Int): Int
    fun setBuzzerLevel(level: Int): Boolean
    fun getSteeringWheelAngle(): Int

}