package top.limuyang2.photolibrary.model

import android.net.Uri
import top.limuyang2.photolibrary.util.LPPImageType

/**
 * @author 李沐阳
 * @date：2020/4/26
 * @description:
 */
internal data class LFolderModel(val bucketName: String = "",
                                 val bucketId: Long = 0L,
                                 var previewImgPath: Uri? = null,
                                 var count: Int = -1,
                                 val imageType: LPPImageType? = null)