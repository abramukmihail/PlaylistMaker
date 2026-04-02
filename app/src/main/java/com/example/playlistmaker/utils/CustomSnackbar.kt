package com.example.playlistmaker.utils

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.google.android.material.snackbar.Snackbar

object CustomSnackbar {

    fun show(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackbar = Snackbar.make(view, message, duration)

        val snackbarView = snackbar.view

        snackbarView.setBackgroundColor(
            ContextCompat.getColor(view.context, R.color.text_color)
        )

        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            dpToPx(view.context, 8),
            0,
            dpToPx(view.context, 8),
            dpToPx(view.context, 16)
        )
        snackbarView.layoutParams = params

        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.CustomSnackbarText)
        } else {
            textView.setTextAppearance(view.context, R.style.CustomSnackbarText)
        }
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.maxLines = 2
        textView.setPadding(
            dpToPx(view.context, 16),
            dpToPx(view.context, 12),
            dpToPx(view.context, 16),
            dpToPx(view.context, 12)
        )

        snackbar.show()
    }

    private fun dpToPx(context: android.content.Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}