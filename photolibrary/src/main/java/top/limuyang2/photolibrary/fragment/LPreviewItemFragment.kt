package top.limuyang2.photolibrary.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.l_pp_fragment_preview_item.view.*
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.activity.LPhotoPickerPreviewActivity
import top.limuyang2.photolibrary.util.ImageEngineUtils


/**
 *
 * Date 2018/8/2
 * @author limuyang
 */
class LPreviewItemFragment : Fragment() {

    private lateinit var mContext: Context

    private var mLastShowHiddenTime = 0L


    val path by lazy {
        arguments?.getString(PATH_BUNDLE)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.l_pp_fragment_preview_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.photoView.setOnPhotoTapListener { _, _, _ ->
            if (System.currentTimeMillis() - mLastShowHiddenTime > 300) {
                mLastShowHiddenTime = System.currentTimeMillis()

                if (mContext is LPhotoPickerPreviewActivity) {
                    val activity = mContext as LPhotoPickerPreviewActivity
                    activity.changeToolBar()
                }
            }
        }

        load()
    }

    private fun load() {
        view?.let {

            val options = BitmapFactory.Options()
            /**
             * 最关键在此，把options.inJustDecodeBounds = true;
             * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
             */
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options) // 此时返回的bitmap为null
            /**
             *options.outHeight为原始图片的高
             */
            ImageEngineUtils.engine.load(mContext, it.photoView, path, R.drawable.ic_l_pp_ic_holder_light, options.outWidth, options.outHeight)

        }
    }

    companion object {
        fun buildFragment(path: String): LPreviewItemFragment {
            val bundle = Bundle()
            bundle.putString(PATH_BUNDLE, path)
            val fragment = LPreviewItemFragment()
            fragment.arguments = bundle
            return fragment
        }

        private const val PATH_BUNDLE = "PATH_BUNDLE"
    }
}