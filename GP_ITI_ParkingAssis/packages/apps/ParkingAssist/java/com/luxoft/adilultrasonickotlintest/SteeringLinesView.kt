package com.luxoft.adilultrasonickotlintest

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class SteeringLinesView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private val paint = Paint()
    private var steeringAngle = 0f // Steering angle in degrees (-45 to 45)
    private val path = Path()

    init {
        paint.color = Color.GRAY
        paint.strokeWidth = 12f
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true // Enable anti-aliasing for smoother lines
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get view dimensions
        val centerX = width / 2f
        val bottomY = height.toFloat()
        val lineSpacing = 150f // Space between the two lines

        // Draw left line with depth effect
        drawCurvedSteeringLine(canvas, centerX - lineSpacing, bottomY, steeringAngle)

        // Draw right line with depth effect
        drawCurvedSteeringLine(canvas, centerX + lineSpacing, bottomY, steeringAngle)
    }

    private fun drawCurvedSteeringLine(canvas: Canvas, startX: Float, bottomY: Float, angle: Float) {
        val curveLength = 800f // Length of the curved path
        val curveHeight = 600f // Height of the curve
        val segments = 8 // Number of segments in the dashed curve

        // Reset the path
        path.reset()

        // Starting point
        val startY = bottomY

        // Simulate perspective depth by scaling the line's length and stroke width based on Y position
        val perspectiveFactor = 1 - (startY / height) // 1 at the bottom, decreases as it goes up
        val maxPerspectiveFactor = 0.9f // Limit the depth effect to 60% of the original size (adjust this value)

        val adjustedPerspectiveFactor = perspectiveFactor.coerceAtMost(maxPerspectiveFactor)

        // Adjust the curve height and length for depth effect
        val adjustedCurveHeight = curveHeight * adjustedPerspectiveFactor // Reduce the height for "depth"
        val adjustedCurveLength = curveLength * adjustedPerspectiveFactor // Reduce the curve's length
        val adjustedStrokeWidth = 12f * adjustedPerspectiveFactor // Reduce stroke width for depth
        val alpha = (255 * (1 - adjustedPerspectiveFactor)).toInt().coerceAtLeast(100) // Ensure alpha is not too low

        // Ensure the adjusted values are reasonable (avoid negative or zero values)
        val safeAdjustedCurveHeight = adjustedCurveHeight.coerceAtLeast(500f)
        /********************************************************************/
        val safeAdjustedStrokeWidth = adjustedStrokeWidth.coerceAtLeast(15f) // Minimum stroke width of 4px

        // Update paint properties for depth effect (opacity and thickness)
        paint.alpha = alpha
        paint.strokeWidth = safeAdjustedStrokeWidth

        // Add initial segment (straight line at the bottom)
        path.moveTo(startX, startY)
        val midX = startX + (safeAdjustedCurveHeight * sin(Math.toRadians(angle.toDouble()))).toFloat()
        val midY = startY - (safeAdjustedCurveHeight * cos(Math.toRadians(angle.toDouble()))).toFloat()

        // Draw smooth curve using cubic Bezier path
        path.quadTo(
            startX, startY - safeAdjustedCurveHeight / 2, // Control point for curve
            midX, midY // End point
        )

        // Draw the path on the canvas
        canvas.drawPath(path, paint)

        // Add dashed effect
        val dashPaint = Paint(paint)
        dashPaint.pathEffect = DashPathEffect(floatArrayOf(30f, 20f), 0f)
        canvas.drawPath(path, dashPaint)
    }

    // Public method to update the steering angle
    fun setSteeringAngle(angle: Float) {
        steeringAngle = angle
        invalidate() // Redraw the view
    }
}
