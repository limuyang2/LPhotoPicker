package top.limuyang2.photolibrary.util

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.MediaStore
import android.view.WindowManager
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.model.LFolderModel
import top.limuyang2.photolibrary.model.LPhotoModel
import java.io.File


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
internal fun getScreenWidth(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            ?: return context.resources.displayMetrics.widthPixels
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        wm.defaultDisplay.getRealSize(point)
    } else {
        wm.defaultDisplay.getSize(point)
    }
    return point.x
}


/**
 * Return the height of screen, in pixel.
 *
 * @return the height of screen, in pixel
 */
internal fun getScreenHeight(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            ?: return context.resources.displayMetrics.heightPixels
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        wm.defaultDisplay.getRealSize(point)
    } else {
        wm.defaultDisplay.getSize(point)
    }
    return point.y
}
