package com.rozdoum.socialcomponents.utils

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.view.Display
import android.view.WindowManager

import com.rozdoum.socialcomponents.R


object Utils {

    fun getDisplayWidth(context: Context): Int {
        return getSize(context).x
    }

    fun getDisplayHeight(context: Context): Int {
        return getSize(context).y
    }

    private fun getSize(context: Context): Point {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    fun share(context: Context, link: String) {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, link)
        context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.send_intent_title)))
    }
}
