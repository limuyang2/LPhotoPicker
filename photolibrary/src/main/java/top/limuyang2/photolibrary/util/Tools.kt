package top.limuyang2.photolibrary.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager


/**
 *
 * Date 2018/7/31
 * @author limuyang
 */


internal fun Context.dip(dpValue: Int): Float {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f)
}

/**
 * Return the width of screen, in pixel.
 *
 * @return the width of screen, in pixel
 */
internal fun Activity.getScreenWidth(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowManager.currentWindowMetrics.bounds.width()
    } else {
        val point = Point()
        windowManager.defaultDisplay.getRealSize(point)
        point.x
    }
}


