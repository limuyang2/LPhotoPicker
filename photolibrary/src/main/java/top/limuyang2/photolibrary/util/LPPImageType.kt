package top.limuyang2.photolibrary.util

import java.util.*

/**
 * 图片类型枚举
 */
enum class LPPImageType {
    JPEG {
        override fun getType(): Array<String> = arrayOf("image/jpeg", "image/jpg")
    },

    PNG {
        override fun getType(): Array<String> = arrayOf("image/png")
    },

    WEBP{
        override fun getType(): Array<String> = arrayOf("image/webp")
    },

    GIF {
        override fun getType(): Array<String> = arrayOf("image/gif")
    };

    abstract fun getType(): Array<String>

    companion object {

        @JvmStatic
        fun ofAll(): Array<String> = ArrayList<String>().apply {
            addAll(JPEG.getType())
            addAll(PNG.getType())
            addAll(WEBP.getType())
            addAll(GIF.getType())
        }.toTypedArray()

        @JvmStatic
        fun of(vararg types: LPPImageType): Array<String> = HashSet<String>().apply {
            for (t in types) {
                addAll(t.getType())
            }
        }.toTypedArray()
    }
}


