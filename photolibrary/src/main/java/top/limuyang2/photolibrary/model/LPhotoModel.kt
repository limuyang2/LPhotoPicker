package top.limuyang2.photolibrary.model

import android.net.Uri
import top.limuyang2.photolibrary.util.LPPImageType


/**
 *
 * Date 2018/7/31
 * @author limuyang
 */
internal data class LPhotoModel(val id: String, val name: String, val photoPath: Uri?, val imageType: LPPImageType)