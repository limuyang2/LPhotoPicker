package top.limuyang2.photolibrary.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt

/**
 * @author 李沐阳
 * @date：2020/4/26
 * @description:
 */
private const val TAG_STATUS_BAR = "TAG_STATUS_BAR"
private const val KEY_OFFSET = -123

/**
 * 获取状态栏的高度
 */
inline val Context.statusBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

/**
 * Set the status bar's light mode.
 *
 */
internal inline var Activity.statusBarLightMode: Boolean
    set(value) {
        window.statusBarLightMode = value
    }
    get() = window.statusBarLightMode

internal inline var Window.statusBarLightMode: Boolean
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var vis = decorView.systemUiVisibility
            vis = if (value) {
                this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.systemUiVisibility = vis
        }
    }
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val vis = decorView.systemUiVisibility
            return vis == vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        return false
    }


/**
 * Add the top margin size equals status bar's height for view.
 *
 */
internal fun View.addMarginTopEqualStatusBarHeight() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    val haveSetOffset = getTag(KEY_OFFSET)
    if (haveSetOffset != null && haveSetOffset is Boolean) return
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
            layoutParams.leftMargin,
            layoutParams.topMargin + context.statusBarHeight,
            layoutParams.rightMargin,
            layoutParams.bottomMargin
    )
    setTag(KEY_OFFSET, true)
}

internal fun Activity.setStatusBarColor(@ColorInt color: Int, isMarginTop: Boolean = true, isDecor: Boolean = false) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

    try {
        transparentStatusBar()
        addStatusBarColor(color, isMarginTop, isDecor)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun Activity.addStatusBarColor(@ColorInt color: Int, isMarginTop: Boolean = true, isDecor: Boolean = false): View {
    val parent = if (isMarginTop) {
        (findViewById<View>(android.R.id.content) as ViewGroup).apply {
            getChildAt(0)?.fitsSystemWindows = true
        }
    } else {
        if (isDecor)
            window.decorView as ViewGroup
        else
            findViewById<View>(android.R.id.content) as ViewGroup
    }

    val fakeStatusBarView = parent.findViewWithTag<View>(TAG_STATUS_BAR)
    return if (fakeStatusBarView != null) {
        if (fakeStatusBarView.visibility == View.GONE) {
            fakeStatusBarView.visibility = View.VISIBLE
        }
        fakeStatusBarView.setBackgroundColor(color)
        fakeStatusBarView
    } else {
        val view = createColorStatusBarView(color)
        parent.addView(view)
        view
    }
}

private fun Context.createColorStatusBarView(color: Int): View {
    val statusBarView = View(this)
    statusBarView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, this.statusBarHeight
    )
    statusBarView.setBackgroundColor(color)
    statusBarView.tag = TAG_STATUS_BAR
    return statusBarView
}


internal fun Activity.transparentStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = option or window.decorView.systemUiVisibility
        window.statusBarColor = Color.TRANSPARENT
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}