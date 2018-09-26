package top.limuyang2.photolibrary.model

/**
 *
 * Date 2018/7/31
 * @author limuyang
 */
data class LPhotoModel(val name: String, val photoInfoList: ArrayList<PhotoInfo> = arrayListOf()) {
    data class PhotoInfo(
        val photoPath: String = "")
}