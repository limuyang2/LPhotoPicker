package top.limuyang2.photolibrary.model

/**
 * @author 李沐阳
 * @date：2020/4/26
 * @description:
 */
internal data class LFolderModel(val bucketName: String = "",
                                 val bucketId: Long = 0,
                                 val previewImgPath: String = "",
                                 var count: Int = -1)