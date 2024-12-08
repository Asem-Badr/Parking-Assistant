package com.luxoft.adilultrasonickotlintest

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity() {

    private lateinit var sensorConstraintedArray: Array<View>
    private lateinit var sensorRadiiConfig: Array<FloatArray> // Array to hold radii configurations for each view
    private lateinit var sensorOrientationConfig: Array<GradientDrawable.Orientation> // Array for orientations
    private lateinit var steeringWheelView: SteeringWheelView
    private lateinit var steeringWheelViewInverted: SteeringWheelViewInverted
    private lateinit var txtDistanceFront: TextView
    private lateinit var txtDistanceBack: TextView

    private lateinit var toggleButton : ImageView

    private val repository = ParkingAssistantRepository()
    private val viewModel: ParkingAssistantViewModel by viewModels {
        ParkingAssistantViewModelFactory(repository)
    }

    @SuppressLint("SetTextI18n")
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

        txtDistanceFront = findViewById(R.id.txtDistanceFront)
        txtDistanceBack = findViewById(R.id.txtDistanceBack)



        // Map sensor IDs to views
        sensorConstraintedArray = arrayOf(
            constraintLeftSide,     // 0 start
            constraintBottomRight,  // 1
            constraintBottomLeft,   // 2
            constraintRightSide,    // 3
            constraintTopLeft,      // 4
            constraintTopRight      // 5
        ).filterNotNull().toTypedArray()

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

                //updateSteeringWheelColor(viewModel.steeringWheelAngle.value, Levels)


                Levels.forEach { (sensorId, level) ->
                    // Update background for a specific sensor and level
                    updateBackgroundForDistanceCondition(sensorId, level)


                }
            }
        }

        lifecycleScope.launch {
            val lastFiveFrontReadings = mutableListOf<Int>() // Store last 5 readings for front sensors
            val lastFiveBackReadings = mutableListOf<Int>() // Store last 5 readings for back sensors

            viewModel.level4Distances.collect { distances ->
                // Separate front and back sensors based on the specified keys
                val frontSensors = distances.filterKeys { it in listOf(4, 5) }
                val backSensors = distances.filterKeys { it in listOf(1, 2) }

                // Process front sensors
                if (frontSensors.isNotEmpty()) {
                    val smallestFrontDistance = frontSensors.values.minOrNull() ?: return@collect

                    // Add to front readings list
                    lastFiveFrontReadings.add(smallestFrontDistance)
                    if (lastFiveFrontReadings.size > 5) {
                        lastFiveFrontReadings.removeAt(0)
                    }

                    // Display average of last 5 front readings
                    if (lastFiveFrontReadings.size == 5) {
                        val avgFrontDistance = lastFiveFrontReadings.average().toInt()
                        txtDistanceFront.text = "$avgFrontDistance cm"
                    }
                } else {
                    txtDistanceFront.text = "" // Clear text if no front distances
                    lastFiveFrontReadings.clear()
                }

                // Process back sensors
                if (backSensors.isNotEmpty()) {
                    val smallestBackDistance = backSensors.values.minOrNull() ?: return@collect

                    // Add to back readings list
                    lastFiveBackReadings.add(smallestBackDistance)
                    if (lastFiveBackReadings.size > 5) {
                        lastFiveBackReadings.removeAt(0)
                    }

                    // Display average of last 5 back readings
                    if (lastFiveBackReadings.size == 5) {
                        val avgBackDistance = lastFiveBackReadings.average().toInt()
                        txtDistanceBack.text = "$avgBackDistance cm"
                    }
                } else {
                    txtDistanceBack.text = "" // Clear text if no back distances
                    lastFiveBackReadings.clear()
                }

                delay(200) // Delay to throttle updates
            }
        }





        steeringWheelView = findViewById<SteeringWheelView>(R.id.steering_wheel_view)!!
        steeringWheelViewInverted = findViewById<SteeringWheelViewInverted>(R.id.steering_wheel_view_Inverted)!!



        lifecycleScope.launch {
            viewModel.steeringWheelAngle.collect { potValue ->
                if (potValue in 8..150) {
                    // Map 8..150 to -45..45
                    val mappedAngle = (potValue - 8) * (45f - (-45f)) / (150f - 8) + (-45f)

                    // Set the angle on the view
                    steeringWheelView.setAngle(mappedAngle)
                    steeringWheelViewInverted.setAngle(mappedAngle)
                }
            }
        }






        toggleButton = findViewById<ImageView>(R.id.imgBtnBuzzer)

        // Initialize button state
        var isBuzzerRunning = false
        toggleButton.setImageResource(R.drawable.volume_up_24px) // Initial image

        toggleButton.setOnClickListener {
            if (isBuzzerRunning) {
                Log.i("BuzzerControl", "Stopping buzzer via button")
                viewModel.stopBuzzer()
                toggleButton.setImageResource(R.drawable.no_sound_24px)
            } else {
                Log.i("BuzzerControl", "Starting buzzer via button")
                viewModel.startBuzzer()
                toggleButton.setImageResource(R.drawable.volume_up_24px)
            }
            isBuzzerRunning = !isBuzzerRunning
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
                centerColor = ContextCompat.getColor(this, R.color.gradient_center_color_red)

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


