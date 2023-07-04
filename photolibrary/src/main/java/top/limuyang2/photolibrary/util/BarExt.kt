package top.limuyang2.photolibrary.util

import android.app.Activity
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat

/**
 * @author 李沐阳
 * @date：2020/4/26
 * @description:
 */
private const val TAG_STATUS_BAR = "TAG_STATUS_BAR"

/**
 * 获取状态栏的高度
 */
internal inline val Activity.statusBarHeight: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars()).top
        } else {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else {
                0
            }
        }
    }


internal inline var Window.statusBarLightMode: Boolean
    set(value) {
        WindowCompat.getInsetsController(this, decorView)?.isAppearanceLightStatusBars = value
    }
    get() {
        return WindowCompat.getInsetsController(this, decorView)?.isAppearanceLightStatusBars ?: false
    }

internal fun Activity.setStatusBarColor(@ColorInt color: Int, isMarginTop: Boolean = true, isDecor: Boolean = false) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = color
    }
}

/**
 * 导航栏颜色
 */
internal inline var Activity.navigationBarColor: Int
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    set(value) {
        window.navigationBarColor = value
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = window.navigationBarColor
