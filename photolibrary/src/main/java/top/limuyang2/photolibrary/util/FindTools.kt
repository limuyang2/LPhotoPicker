package top.limuyang2.photolibrary.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.model.LFolderModel
import top.limuyang2.photolibrary.model.LPhotoModel

/**
 * @author 李沐阳
 * @date：2020/4/27
 * @description:
 */

internal enum class SortType(val type: String) {
    ASC("ASC"), DESC("DESC")
}

internal fun findFolder(context: Context, showType: Array<String>?): List<LFolderModel> {
    val typeArray = showType ?: LPPImageType.ofAll()
    val selectionBuilder = StringBuilder()
    for (i in typeArray.indices) {
        if (i == 0) {
            selectionBuilder.append(MediaStore.Images.Media.MIME_TYPE).append("=?")
        } else {
            selectionBuilder.append(" or ").append(MediaStore.Images.Media.MIME_TYPE).append("=?")
        }
    }

    val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID),
            selectionBuilder.toString(),
            typeArray,
            MediaStore.Images.Media.DATE_ADDED
    )

    val list = ArrayList<LFolderModel>()

    try {
        if (cursor == null || cursor.count <= 0) {
            return list.apply { add(LFolderModel(context.resources.getString(R.string.l_pp_all_image), -1L, null, 0, null)) }
        }

        var allCount = 0

        val tempFolderMap = HashMap<Long, LFolderModel>()
        var id = ""
        var mimeType = ""
        while (cursor.moveToNext()) {

            id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))

            val bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    ?: "根目录"
            val bucketId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
            mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))

            val model = tempFolderMap[bucketId]
            if (model == null) {
                val uri = getImageUri(id)
                val newModel = LFolderModel(bucketName, bucketId, uri, 1, LPPImageType.getImageType(mimeType))
                tempFolderMap[bucketId] = newModel
            } else {
                model.previewImgPath = getImageUri(id)
                model.count++
            }

            allCount++
        }

        list.add(LFolderModel(context.resources.getString(R.string.l_pp_all_image), -1, getImageUri(id), allCount, LPPImageType.getImageType(mimeType)))
        list.addAll(tempFolderMap.values)

    } catch (e: Throwable) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }

    return list
}

internal fun Context.findPhoto(bucketId: Long, showType: Array<String>?, sortType: SortType = SortType.ASC): List<LPhotoModel> {
    val photoList = ArrayList<LPhotoModel>()

    val typeArray = showType ?: LPPImageType.ofAll()
    val selectionBuilder = StringBuilder()

    if (bucketId != -1L) {
        // 根据文件夹id
        selectionBuilder.append("${MediaStore.Images.Media.BUCKET_ID} = '$bucketId') and (")
    }

    for (i in typeArray.indices) {
        if (i == 0) {
            selectionBuilder.append(MediaStore.Images.Media.MIME_TYPE).append("=?")
        } else {
            selectionBuilder.append(" or ").append(MediaStore.Images.Media.MIME_TYPE).append("=?")
        }
    }

    val cursor = this.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE),
            selectionBuilder.toString(),
            typeArray,
            "${MediaStore.Images.Media.DATE_ADDED} ${sortType.type}"
    )

    try {
        if (cursor == null || cursor.count <= 0) return photoList

        while (cursor.moveToNext()) {

            val id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))

            val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    ?: ""
            val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))

            LPPImageType.getImageType(mimeType)?.let {
                photoList.add(LPhotoModel(id, name, getImageUri(id), it))
            }
        }

    } catch (e: Throwable) {
    } finally {
        cursor?.close()
    }

    return photoList
}


internal fun getImageUri(id: String): Uri? {
    return try {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(id).build()
    } catch (e: UnsupportedOperationException) {
        null
    }
}