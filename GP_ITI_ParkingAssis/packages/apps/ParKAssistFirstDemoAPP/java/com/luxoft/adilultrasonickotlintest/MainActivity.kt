package com.luxoft.adilultrasonickotlintest

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var sensorConstraintedArray: Array<View>
    private lateinit var sensorRadiiConfig: Array<FloatArray> // Array to hold radii configurations for each view
    private lateinit var sensorOrientationConfig: Array<GradientDrawable.Orientation> // Array for orientations


    private val repository = ParkingAssistantRepository()
    private val viewModel: ParkingAssistantViewModel by viewModels {
        ParkingAssistantViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialize views
        val constraintLeftSide = findViewById<View>(R.id.constraint_left_side)
        val constraintRightSide = findViewById<View>(R.id.constraint_right_side)
        val constraintBottomRight = findViewById<View>(R.id.constraint_bottom_right)
        val constraintTopRight = findViewById<View>(R.id.constraint_top_right)
        val constraintBottomLeft = findViewById<View>(R.id.constraint_bottom_left)
        val constraintTopLeft = findViewById<View>(R.id.constraint_top_left)

        // Map sensor IDs to views
        sensorConstraintedArray = arrayOf(
            constraintLeftSide,     // 0 start
            constraintBottomRight,  // 1
            constraintBottomLeft,   // 2
            constraintRightSide,    // 3
            constraintTopLeft,      // 4
            constraintTopRight      // 5
        )

        sensorRadiiConfig = arrayOf(
            // Top-left X, Top-left Y, Top-right X, Top-right Y, Bottom-right X, Bottom-right Y, Bottom-left X, Bottom-left Y

            floatArrayOf(dpToPx(60f), dpToPx(60f), dpToPx(0f), dpToPx(0f), dpToPx(0f), dpToPx(0f), dpToPx(60f), dpToPx(60f)), // Left-side
            floatArrayOf(dpToPx(100f), dpToPx(100f), dpToPx(10f), dpToPx(10f), dpToPx(0f), dpToPx(0f), dpToPx(10f), dpToPx(10f)), // Bottom-right
            floatArrayOf(dpToPx(10f), dpToPx(10f), dpToPx(100f), dpToPx(100f), dpToPx(0f), dpToPx(0f), dpToPx(10f), dpToPx(10f)), // Bottom-left
            floatArrayOf(dpToPx(0f), dpToPx(0f), dpToPx(60f), dpToPx(60f), dpToPx(60f), dpToPx(60f), dpToPx(0f), dpToPx(0f)), // Right-side
            floatArrayOf(dpToPx(10f), dpToPx(10f), dpToPx(0f), dpToPx(0f), dpToPx(100f), dpToPx(100f), dpToPx(10f), dpToPx(10f)), // Top-left
            floatArrayOf(dpToPx(10f), dpToPx(10f), dpToPx(0f), dpToPx(0f), dpToPx(10f), dpToPx(10f), dpToPx(100f), dpToPx(100f))  // Top-right
        )


        sensorOrientationConfig = arrayOf(
            GradientDrawable.Orientation.LEFT_RIGHT,   // Left-side (0°)       0
            GradientDrawable.Orientation.TL_BR,       // Bottom-right (315°)   1
            GradientDrawable.Orientation.TR_BL,       // Bottom-left (225°)    2
            GradientDrawable.Orientation.RIGHT_LEFT,  // Right-side (180°)     3
            GradientDrawable.Orientation.BR_TL,       // Top-left (135°)       4
            GradientDrawable.Orientation.BL_TR        // Top-right (45°)       5
        )






        // Observe StateFlow for sensor Levels
        lifecycleScope.launch {
            viewModel.sensorLevels.collect { Levels ->
                Levels.forEach { (sensorId, level) ->
                    // Update background for a specific sensor and level
                    updateBackgroundForDistanceCondition(sensorId, level)
                }
            }
        }

    }


    private fun applyGradientDrawable(
        view: View,
        orientation: GradientDrawable.Orientation,
        colors: IntArray,
        cornerRadii: FloatArray
    ) {
        val gradientDrawable = GradientDrawable(orientation, colors).apply {
            this.cornerRadii = cornerRadii
        }
        view.background = gradientDrawable
    }

    private fun updateBackgroundForDistanceCondition(sensorId: Int, level: String) {
        var startColor = ContextCompat.getColor(this, R.color.gradient_start_color)
        var centerColor = ContextCompat.getColor(this, R.color.gradient_center_color)
        var endColor = ContextCompat.getColor(this, R.color.gradient_end_color)

        // Decide width based on the level
        val newWidth = when {
            level.startsWith("Level 4") -> {
                startColor = ContextCompat.getColor(this, R.color.gradient_start_color_red)
                centerColor = ContextCompat.getColor(this, R.color.gradient_end_color)

                100
            }
            level.startsWith("Level 3") -> {
                startColor = ContextCompat.getColor(this, R.color.gradient_start_color_dark_orange)
                centerColor = ContextCompat.getColor(this, R.color.gradient_center_color_dark_orange)

                150
            }
            level.startsWith("Level 2") -> {
                startColor = ContextCompat.getColor(this, R.color.gradient_start_color_yellow)
                centerColor = ContextCompat.getColor(this, R.color.gradient_center_color_yellow)
                200
            }
            level.startsWith("Level 1") -> {
                startColor = ContextCompat.getColor(this, R.color.gradient_start_color_green)
                centerColor = ContextCompat.getColor(this, R.color.gradient_center_color_green)

                250
            }
            level.startsWith("No Obstacle") -> {
                startColor = ContextCompat.getColor(this, R.color.gradient_start_color)
                300
            }
            else -> 100
        }

        val targetView = sensorConstraintedArray.getOrNull(sensorId)
        if (targetView != null) {
            val layoutParams = targetView.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.width = newWidth
            targetView.layoutParams = layoutParams

            val radii = sensorRadiiConfig.getOrNull(sensorId)
                ?: floatArrayOf(dpToPx(60f), dpToPx(60f), dpToPx(60f), dpToPx(60f), dpToPx(0f), dpToPx(0f), dpToPx(0f), dpToPx(0f))

            val orientation = sensorOrientationConfig.getOrNull(sensorId)
                ?: GradientDrawable.Orientation.LEFT_RIGHT

            applyGradientDrawable(
                view = targetView,
                orientation = orientation,
                colors = intArrayOf(startColor, centerColor, endColor),
                cornerRadii = radii
            )
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }






}



/*
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




        // Observe StateFlow for sensor Levels
        lifecycleScope.launch {
            viewModel.sensorLevels.collect { Levels ->
                Levels.forEach { (sensorId, level) ->
                    updateUI(sensorId, level)
                }
            }
        }

        Toast.makeText(this, "Ultrasonic monitoring started", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(sensorId: Int, level: String) {



            when (sensorId) {
                0 -> txtUltrasonicOne.text = level
                1 -> txtUltrasonicTwo.text = level
                2 -> txtUltrasonicThree.text = level
                3 -> txtUltrasonicFour.text = level
                4 -> txtUltrasonicFive.text = level
                5 -> txtUltrasonicSix.text = level
                else -> Log.e("ULTRASONIC", "Invalid sensor ID: $sensorId")
            }

    }

}


 */