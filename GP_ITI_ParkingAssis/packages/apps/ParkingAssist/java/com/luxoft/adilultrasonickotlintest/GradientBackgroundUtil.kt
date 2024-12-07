package com.luxoft.adilultrasonickotlintest

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat

class GradientBackgroundUtil {

    companion object {
        /**
         * Updates the gradient background of a view dynamically with configurable corner radii.
         *
         * @param context The context to access resources.
         * @param view The view to apply the gradient background.
         * @param startColorId The resource ID for the start color.
         * @param centerColorId The resource ID for the center color.
         * @param endColorId The resource ID for the end color.
         * @param orientation The orientation of the gradient (default: TOP_BOTTOM).
         * @param cornerRadii A float array representing the corner radii for the drawable:
         *        [top-left, top-left, top-right, top-right, bottom-right, bottom-right, bottom-left, bottom-left].
         *        Pass `null` to skip setting corner radii.
         */
        fun applyGradientBackground(
            context: Context,
            view: View,
            startColorId: Int,
            centerColorId: Int,
            endColorId: Int,
            orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
            cornerRadii: FloatArray? = null
        ) {
            val startColor = ContextCompat.getColor(context, startColorId)
            val centerColor = ContextCompat.getColor(context, centerColorId)
            val endColor = ContextCompat.getColor(context, endColorId)

            val gradientDrawable = GradientDrawable(
                orientation,
                intArrayOf(startColor, centerColor, endColor)
            )

            // Apply corner radii if provided
            cornerRadii?.let {
                gradientDrawable.cornerRadii = it
            }

            view.background = gradientDrawable
        }
    }
}
