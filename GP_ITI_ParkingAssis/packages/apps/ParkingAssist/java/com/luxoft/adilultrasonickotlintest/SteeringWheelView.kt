package com.luxoft.adilultrasonickotlintest

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

class SteeringWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    private val paintLine = Paint().apply {
        color = 0xFFD3D3D3.toInt() // Brighter gray for lines
        strokeWidth = 10f          // Line thickness
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val paintShadedArea = Paint().apply {
        color = 0x20D3D3D3.toInt() // Semi-transparent gray for shading
        style = Paint.Style.FILL
    }

    private val paintInnerShadow = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var angle: Float = 0f // Steering wheel angle (-90 to 90)

    // Method to set the angle and ensure it's within the valid range
    fun setAngle(newAngle: Float) {
        angle = newAngle.coerceIn(-90f, 90f) // Ensure angle is within bounds (-90 to 90)
        invalidate() // Request a redraw
    }

    // Method to set the color dynamically
    fun setColor(lineColor: Int, shadedAreaColor: Int) {
        paintLine.color = lineColor
        paintShadedArea.color = shadedAreaColor
        invalidate() // Request a redraw with the new colors
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val centerX = width / 2f
        val baseY = height // Base of the lines
        val lineLength = height * 0.9f // Length of the lines
        val lineSpacing = width / 8f // Distance between the two lines

        // Maximum offset for the curve based on the angle
        val maxCurveOffset = lineSpacing / 1f
        val leftCurveOffset = (maxCurveOffset * (angle / 90f)) // Arch to the left (-90 to 0)
        val rightCurveOffset = (maxCurveOffset * (angle / -90f)) // Arch to the right (0 to 90)

        // Save the original canvas state
        canvas.save()

        // Rotate the canvas based on the angle
        canvas.rotate(angle, centerX, baseY)

        // Define paths for the left and right curved lines
        val leftPath = Path().apply {
            moveTo(centerX - lineSpacing, baseY) // Start at the bottom-left line
            quadTo(
                centerX - lineSpacing - leftCurveOffset, baseY - lineLength / 2, // Curve midpoint
                centerX - lineSpacing, baseY - lineLength // End at the top-left line
            )
        }

        val rightPath = Path().apply {
            moveTo(centerX + lineSpacing, baseY) // Start at the bottom-right line
            quadTo(
                centerX + lineSpacing + rightCurveOffset, baseY - lineLength / 2, // Curve midpoint
                centerX + lineSpacing, baseY - lineLength // End at the top-right line
            )
        }

        // Create a path to fill the area between the curves
        val shadowPath = Path().apply {
            addPath(leftPath)
            lineTo(centerX + lineSpacing, baseY - lineLength) // Connect to the right curve top
            quadTo(
                centerX + lineSpacing + rightCurveOffset, baseY - lineLength / 2, // Right curve midpoint
                centerX + lineSpacing, baseY // Back to the bottom-right
            )
            lineTo(centerX - lineSpacing, baseY) // Close the path
            close()
        }

        // Set a gradient shader for the shadow (darker at the center, fading to transparent)
        paintInnerShadow.shader = LinearGradient(
            centerX - lineSpacing, baseY - lineLength, // Gradient start
            centerX + lineSpacing, baseY,             // Gradient end
            0x40000000.toInt(),                       // Semi-transparent black
            0x00000000,                               // Fully transparent
            Shader.TileMode.CLAMP
        )

        // Draw the shadow between the curves
        canvas.drawPath(shadowPath, paintInnerShadow)

        // Draw the left and right curved lines
        canvas.drawPath(leftPath, paintLine)
        canvas.drawPath(rightPath, paintLine)

        // Restore the canvas to its original state
        canvas.restore()
    }
}
