package com.luxoft.adilultrasonickotlintest

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var txtUltrasonicOne: TextView
    private lateinit var txtUltrasonicTwo: TextView
    private lateinit var txtUltrasonicThree: TextView
    private lateinit var txtUltrasonicFour: TextView
    private lateinit var txtUltrasonicFive: TextView
    private lateinit var txtUltrasonicSix: TextView

    private val repository = ParkingAssistantRepository()
    private val viewModel: ParkingAssistantViewModel by viewModels {
        ParkingAssistantViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TextView references
        txtUltrasonicOne = findViewById(R.id.txtUltrasonicOne)
        txtUltrasonicTwo = findViewById(R.id.txtUltrasonicTwo)
        txtUltrasonicThree = findViewById(R.id.txtUltrasonicThree)
        txtUltrasonicFour = findViewById(R.id.txtUltrasonicFour)
        txtUltrasonicFive = findViewById(R.id.txtUltrasonicFive)
        txtUltrasonicSix = findViewById(R.id.txtUltrasonicSix)

        // Observe StateFlow for sensor readings
        lifecycleScope.launch {
            viewModel.sensorReadings.collect { readings ->
                readings.forEach { (sensorId, reading) ->
                    updateUI(sensorId, reading)
                }
            }
        }

        Toast.makeText(this, "Ultrasonic monitoring started", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(sensorId: Int, reading: Int) {
        val readingText = "$reading cm"
        when (sensorId) {
            0 -> txtUltrasonicOne.text = readingText
            1 -> txtUltrasonicTwo.text = readingText
            2 -> txtUltrasonicThree.text = readingText
            3 -> txtUltrasonicFour.text = readingText
            4 -> txtUltrasonicFive.text = readingText
            5 -> txtUltrasonicSix.text = readingText
            else -> Log.e("ULTRASONIC", "Invalid sensor ID: $sensorId")
        }
    }
}
