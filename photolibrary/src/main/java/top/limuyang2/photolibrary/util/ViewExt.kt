package top.limuyang2.photolibrary.util

import android.view.View


/**
 *  扩展属性 延时触发  即多少时间内重复点击无反应
 */
private var View.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else -1
    set(value) {
        setTag(1123461123, value)
    }

/**
 *  扩展属性 上次点击事件时间
 */
private var View.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else 0
    set(value) {
        setTag(1123460103, value)
    }

/**
 * 私有扩展方法 计算是否过了延时期
 */
private fun View.delayOver(): Boolean {
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        triggerLastTime = currentClickTime
        return true
    }
    return false
}

fun View.singleClick(time: Long = 600, block: (View) -> Unit) {
    triggerDelay = time

    setOnClickListener {
        if (delayOver()) {
            block(it)
        }
    }
}
