package top.limuyang2.photolibrary.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import top.limuyang2.photolibrary.activity.LPhotoPickerPreviewActivity
import top.limuyang2.photolibrary.databinding.LPpFragmentPreviewItemBinding


/**
 *
 * Date 2018/8/2
 * @author limuyang
 */
class LPreviewItemFragment : Fragment() {

    private lateinit var mContext: Context

    private lateinit var viewBinding: LPpFragmentPreviewItemBinding

    private var mLastShowHiddenTime = 0L

    private val uri by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getParcelable<Uri>(URI_BUNDLE)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = LPpFragmentPreviewItemBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.photoView.setOnClickListener {
            if (System.currentTimeMillis() - mLastShowHiddenTime > 300) {
                mLastShowHiddenTime = System.currentTimeMillis()

                if (mContext is LPhotoPickerPreviewActivity) {
                    val activity = mContext as LPhotoPickerPreviewActivity
                    activity.changeToolBar()
                }
            }
        }

        viewBinding.photoView.maxScale = 5f

        uri?.let {
            viewBinding.photoView.setImage(ImageSource.uri(it))
        }
    }

    companion object {
        fun buildFragment(uri: Uri): LPreviewItemFragment {
            val bundle = Bundle()
            bundle.putParcelable(URI_BUNDLE, uri)
            val fragment = LPreviewItemFragment()
            fragment.arguments = bundle
            return fragment
        }

        private const val URI_BUNDLE = "PATH_BUNDLE"
    }
}