package top.limuyang2.photolibrary.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 *
 * Date 2018/8/2
 * @author limuyang
 */
class LPPViewPage constructor(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }

    }
}