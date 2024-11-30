package com.luxoft.adilultrasonickotlintest

interface IParkingAssistantRepository {
    fun getUltrasonicReading(sensorId: Int): Int
}