package top.limuyang2.photolibrary.engine

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import top.limuyang2.photolibrary.util.LPPImageType
import top.limuyang2.photolibrary.util.dip

class LGlideEngine : LImageEngine {

    private val glideOptions by lazy { RequestOptions().centerCrop() }

    override fun load(context: Context, imageView: ImageView, uri: Uri?, imageType: LPPImageType?, @DrawableRes placeholderRes: Int, resizeX: Int, resizeY: Int) {
        Glide.with(context)
                .load(uri)
                .apply(glideOptions.placeholder(placeholderRes).override(resizeY))
                .transform(CenterCrop(), RoundedCorners(context.dip(4).toInt()))
                .format(DecodeFormat.PREFER_RGB_565)
                .into(imageView)
    }

    override fun pause(context: Context) {
        Glide.with(context).pauseRequests()
    }

    override fun resume(context: Context) {
        Glide.with(context).resumeRequestsRecursive()
    }
}