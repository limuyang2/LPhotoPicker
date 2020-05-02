package top.limuyang2.photolibrary.engine

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import top.limuyang2.photolibrary.util.dip

class LGlideEngine : LImageEngine {

    private val glideTransition by lazy { DrawableTransitionOptions.withCrossFade() }

    private val glideOptions by lazy { RequestOptions().centerCrop() }

    override fun load(context: Context, imageView: ImageView, path: String?, @DrawableRes placeholderRes: Int, resizeX: Int, resizeY: Int) {
        Glide.with(context)
                .load(path)
                .apply(glideOptions.placeholder(placeholderRes).override(resizeX, resizeY))
                .transform(CenterCrop(), RoundedCorners(context.dip(4).toInt()))
//                .transition(glideTransition)
                .into(imageView)
    }

    override fun pause(context: Context) {
        Glide.with(context).pauseRequests()
    }

    override fun resume(context: Context) {
        Glide.with(context).resumeRequestsRecursive()
    }
}