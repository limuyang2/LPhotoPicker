package top.limuyang2.photolibrary

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import top.limuyang2.photolibrary.activity.LPhotoFolderActivity
import top.limuyang2.photolibrary.engine.LImageEngine
import top.limuyang2.photolibrary.util.ImageEngineUtils

/**
 * @author 李沐阳
 * @date：2020/4/26
 * @description:
 */
class LPhotoHelper internal constructor(private val intent: Intent) {
    companion object {
        const val EXTRA_LAST_OPENED_ALBUM = "EXTRA_LAST_OPENED_ALBUM"
        const val EXTRA_SELECTED_PHOTOS = "EXTRA_SELECTED_PHOTOS"
        const val EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT"
        const val EXTRA_PAUSE_ON_SCROLL = "EXTRA_PAUSE_ON_SCROLL"
        const val EXTRA_COLUMNS_NUMBER = "EXTRA_COLUMNS_NUMBER"
        const val EXTRA_IS_SINGLE_CHOOSE = "EXTRA_IS_SINGLE_CHOOSE"
        const val EXTRA_TYPE = "EXTRA_TYPE"
        const val EXTRA_THEME = "EXTRA_THEME"

        /**
         * 获取已选择的图片集合
         *
         * @param intent
         * @return
         */
        @JvmStatic
        fun getSelectedPhotos(intent: Intent?): List<Uri> {
            return intent?.getParcelableArrayListExtra(EXTRA_SELECTED_PHOTOS) ?: emptyList()
        }
    }


    fun start(fragment: Fragment, requestCode: Int) {
        intent.component = ComponentName(fragment.requireContext(), LPhotoFolderActivity::class.java)
        fragment.startActivityForResult(intent, requestCode)
    }

    fun start(activity: Activity, requestCode: Int) {
        intent.component = ComponentName(activity, LPhotoFolderActivity::class.java)
        activity.startActivityForResult(intent, requestCode)
    }

    class Builder {
        private val mIntent: Intent = Intent()


        fun isOpenLastAlbum(isOpen: Boolean): Builder {
            mIntent.putExtra(EXTRA_LAST_OPENED_ALBUM, isOpen)
            return this
        }



        /**
         * 需要显示哪种类型的图片(JPG\PNG\GIF\WEBP)，默认全部加载
         * @return IntentBuilder
         */
        fun imageType(typeArray: Array<String>): Builder {
            mIntent.putExtra(EXTRA_TYPE, typeArray)
            return this
        }

        /**
         * 图片选择张数的最大值
         *
         * @param maxChooseCount
         * @return
         */
        fun maxChooseCount(maxChooseCount: Int): Builder {
            mIntent.putExtra(EXTRA_MAX_CHOOSE_COUNT, maxChooseCount)
            return this
        }

        /**
         * 是否是单选模式，默认false
         * @param isSingle Boolean
         * @return IntentBuilder
         */
        fun isSingleChoose(isSingle: Boolean): Builder {
            mIntent.putExtra(EXTRA_IS_SINGLE_CHOOSE, isSingle)
            return this
        }

        /**
         * 当前已选中的图片路径集合，可以传 null
         */
        fun selectedPhotos(selectedPhotos: java.util.ArrayList<String>?): Builder {
            mIntent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos)
            return this
        }

        /**
         * 滚动列表时是否暂停加载图片，默认为 false
         */
        fun pauseOnScroll(pauseOnScroll: Boolean): Builder {
            mIntent.putExtra(EXTRA_PAUSE_ON_SCROLL, pauseOnScroll)
            return this
        }

        /**
         * 图片选择以几列展示，默认3列
         */
        fun columnsNumber(number: Int): Builder {
            mIntent.putExtra(EXTRA_COLUMNS_NUMBER, number)
            return this
        }

        /**
         * 设置图片加载引擎
         */
        fun imageEngine(engine: LImageEngine): Builder {
            ImageEngineUtils.engine = engine
            return this
        }

        /**
         * 设置主题
         */
        fun theme(@StyleRes style: Int): Builder {
            mIntent.putExtra(EXTRA_THEME, style)
            return this
        }

        fun build(): LPhotoHelper {
            return LPhotoHelper(mIntent)
        }
    }
}