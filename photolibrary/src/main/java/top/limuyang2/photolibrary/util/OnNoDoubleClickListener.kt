package top.limuyang2.photolibrary.util

import android.view.View

abstract class OnNoDoubleClickListener : View.OnClickListener{

    companion object {
        private const val mThrottleFirstTime = 500
    }

    private var mLastClickTime: Long = 0

    override fun onClick(v: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - mLastClickTime > mThrottleFirstTime) {
            mLastClickTime = currentTime
            onNoDoubleClick(v)
        }
    }

    abstract fun onNoDoubleClick(v: View)
}