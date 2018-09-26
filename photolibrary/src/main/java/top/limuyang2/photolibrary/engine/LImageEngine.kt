package top.limuyang2.photolibrary.engine

import android.content.Context
import android.support.annotation.DrawableRes
import android.widget.ImageView

interface LImageEngine {
    /**
     * 加载图片
     */
    fun load(context: Context, imageView: ImageView, path: String?, @DrawableRes placeholderRes: Int, resizeX: Int, resizeY: Int)

    /**
     * 暂停加载
     */
    fun pause(context: Context)

    /**
     * 恢复加载
     */
    fun resume(context: Context)
}