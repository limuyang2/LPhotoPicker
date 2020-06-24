package top.limuyang2.photolibrary.util

import android.media.MediaFormat.MIMETYPE_IMAGE_ANDROID_HEIC
import android.os.Build
import androidx.annotation.RequiresApi
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

    WEBP {
        override fun getType(): Array<String> = arrayOf("image/webp")
    },

    GIF {
        override fun getType(): Array<String> = arrayOf("image/gif")
    },

    @RequiresApi(Build.VERSION_CODES.Q)
    HEIF {
        override fun getType(): Array<String> = arrayOf("image/heic", MIMETYPE_IMAGE_ANDROID_HEIC)
    };

    abstract fun getType(): Array<String>

    companion object {

        @JvmStatic
        fun ofAll(): Array<String> = ArrayList<String>().apply {
            addAll(JPEG.getType())
            addAll(PNG.getType())
            addAll(WEBP.getType())
            addAll(GIF.getType())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                addAll(HEIF.getType())
            }
        }.toTypedArray()

        @JvmStatic
        fun of(vararg types: LPPImageType): Array<String> = HashSet<String>().apply {
            for (t in types) {
                addAll(t.getType())
            }
        }.toTypedArray()

        fun getImageType(mimeType: String): LPPImageType? {
            return when(mimeType) {
                "image/jpeg", "image/jpg" -> JPEG
                "image/png" -> PNG
                "image/webp" -> WEBP
                "image/gif" -> GIF
                "image/heic", MIMETYPE_IMAGE_ANDROID_HEIC -> HEIF
                else -> null
            }
        }
    }
}


