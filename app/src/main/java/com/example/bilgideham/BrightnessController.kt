package com.example.bilgideham

import android.app.Activity
import android.view.WindowManager

object BrightnessController {

    /**
     * level:
     * 0 -> normal (sisteme dokunma)
     * 1 -> %70
     * 2 -> %45
     * 3 -> %25
     */
    fun apply(activity: Activity, level: Int) {
        val lp = activity.window.attributes
        val brightness = when (level.coerceIn(0, 3)) {
            0 -> WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            1 -> 0.7f
            2 -> 0.45f
            3 -> 0.25f
            else -> WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }
        lp.screenBrightness = brightness
        activity.window.attributes = lp
    }
}
