package com.example.influencer.Core.Utils

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import com.google.android.material.chip.Chip

object BackgroundAndTextColors{

    //funcion de extencion para modificar el Textview y asi poder agregar el color del background como tambien las puntas redondeadas/color del texto en base a color del fondo
    fun View.setRoundedBackgroundColor(color: Int, cornerRadiusDp: Float) {
        val density = context.resources.displayMetrics.density
        // Convert dp to pixels
        val cornerRadiusPx = cornerRadiusDp * density
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = cornerRadiusPx
        }
        background = drawable

        // Determine if the background color is dark or light
        val isDark = isColorDark(color)

        // If the view is a TextView (or subclass), set its text color based on the background color
        if (this is TextView) {
            this.setTextColor(if (isDark) Color.WHITE else Color.BLACK)
        }
    }

    fun Chip.setChipTextColor(color: Int) {
        val isDark = isColorDark(color)
        this.setTextColor(if (isDark) Color.WHITE else Color.BLACK)
    }

    fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5 // It means dark colors if true
    }
}