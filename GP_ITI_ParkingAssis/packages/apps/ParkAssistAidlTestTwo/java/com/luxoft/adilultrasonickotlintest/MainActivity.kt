package com.luxoft.adilultrasonickotlintest

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var txtSteeringWheelAngle: TextView


    private lateinit var txtUltrasonicOne: TextView
    private lateinit var txtUltrasonicTwo: TextView
    private lateinit var txtUltrasonicThree: TextView
    private lateinit var txtUltrasonicFour: TextView
    private lateinit var txtUltrasonicFive: TextView
    private lateinit var txtUltrasonicSix: TextView

    private lateinit var btnStop: Button
    private lateinit var btnLevel1: Button
    private lateinit var btnLevel2: Button
    private lateinit var btnLevel3: Button
    private lateinit var btnLevel4: Button


    private val repository = ParkingAssistantRepository()
    private val viewModel: ParkingAssistantViewModel by viewModels {
        ParkingAssistantViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TextView references
        txtSteeringWheelAngle = findViewById(R.id.txtSteeringWheelAngle)
        txtUltrasonicOne = findViewById(R.id.txtUltrasonicOne)
        txtUltrasonicTwo = findViewById(R.id.txtUltrasonicTwo)
        txtUltrasonicThree = findViewById(R.id.txtUltrasonicThree)
        txtUltrasonicFour = findViewById(R.id.txtUltrasonicFour)
        txtUltrasonicFive = findViewById(R.id.txtUltrasonicFive)
        txtUltrasonicSix = findViewById(R.id.txtUltrasonicSix)

        btnStop = findViewById(R.id.btn_0)
        btnLevel1 = findViewById(R.id.btn_1)
        btnLevel2 = findViewById(R.id.btn_2)
        btnLevel3 = findViewById(R.id.btn_3)
        btnLevel4 = findViewById(R.id.btn_4)

        lifecycleScope.launch {
            viewModel.steeringWheelAngle.collect { angle ->

                if (angle in 0..180) {
                    txtSteeringWheelAngle.text = "Steering Angle: $angle°"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currentLevel.collect { level ->
                if (level == -1) {
                    Toast.makeText(this@MainActivity, "Buzzer stopped", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Buzzer running at level $level", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnStop.setOnClickListener {
            viewModel.stopBuzzer()
            Toast.makeText(this, "Stop button clicked", Toast.LENGTH_SHORT).show()
        }

        btnLevel1.setOnClickListener {
            viewModel.startBuzzer(1)
            Toast.makeText(this, "Level 1 button clicked", Toast.LENGTH_SHORT).show()
        }

        btnLevel2.setOnClickListener {
            viewModel.startBuzzer(2)
            Toast.makeText(this, "Level 2 button clicked", Toast.LENGTH_SHORT).show()
        }

        btnLevel3.setOnClickListener {
            viewModel.startBuzzer(3)
            Toast.makeText(this, "Level 3 button clicked", Toast.LENGTH_SHORT).show()
        }

        btnLevel4.setOnClickListener {
            viewModel.startBuzzer(4)
            Toast.makeText(this, "Level 4 button clicked", Toast.LENGTH_SHORT).show()
        }


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
        if (reading != -1) {


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
}
