package top.limuyang2.photolibrary.util

import android.app.Activity
import android.view.Window
import androidx.core.view.WindowCompat

/**
 * @author 李沐阳
 * @date：2020/4/26
 * @description:
 */


internal inline var Window.statusBarLightMode: Boolean
    set(value) {
        WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = value
    }
    get() {
        return WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars
    }


/**
 * 导航栏颜色
 */
internal inline var Activity.navigationBarColor: Int
    set(value) {
        window.navigationBarColor = value
    }
    get() = window.navigationBarColor
