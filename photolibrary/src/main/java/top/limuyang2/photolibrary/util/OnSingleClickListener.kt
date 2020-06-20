package top.limuyang2.photolibrary.util

import android.view.View

internal fun View.click(time: Long = 500, block: ((View) -> Unit)?) {
    setOnClickListener(object : OnSingleClickListener(time) {
        override fun onSingleClick(v: View) {
            block?.invoke(v)
        }
    })
}

private abstract class OnSingleClickListener(private val interval: Long) : View.OnClickListener {
    private var mLastClickTime: Long = 0

    override fun onClick(v: View) {
        val nowTime = System.currentTimeMillis()
        if (nowTime - mLastClickTime >= interval) { // 单次点击事件
            mLastClickTime = nowTime
            onSingleClick(v)
        }
    }
    protected abstract fun onSingleClick(v: View)
}