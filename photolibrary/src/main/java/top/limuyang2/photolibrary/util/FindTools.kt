package top.limuyang2.photolibrary.util

import android.content.Context
import android.provider.MediaStore
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.model.LFolderModel
import top.limuyang2.photolibrary.model.LPhotoModel
import java.io.File

/**
 * @author 李沐阳
 * @date：2020/4/27
 * @description:
 */

internal fun isEmptyFile(path: String): Boolean {
    if (path.isBlank()) {
        return true
    }

    val file = File(path)
    return !file.exists() || file.length() == 0L
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
            arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID),
            selectionBuilder.toString(),
            typeArray,
            MediaStore.Images.Media.DATE_ADDED
    )

    val list = ArrayList<LFolderModel>()

    try {
        if (cursor == null || cursor.count <= 0) {
            return list.apply { add(LFolderModel(context.resources.getString(R.string.l_pp_all_image), -1, "", 0)) }
        }

        var allCount = 0
        var allFirstPath = ""

        val tempFolderMap = HashMap<Long, LFolderModel>()
        while (cursor.moveToNext()) {

            val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))

            val bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
            val bucketId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))

            if (isEmptyFile(imagePath)) {
                continue
            }

            val model = tempFolderMap[bucketId]
            if (model == null) {
                val newModel = LFolderModel(bucketName, bucketId, imagePath, 1)
                tempFolderMap[bucketId] = newModel
            } else {
                model.count++
            }

            allCount++
            if (allCount == 1) {
                allFirstPath = imagePath
            }
        }

        list.add(LFolderModel(context.resources.getString(R.string.l_pp_all_image), -1, allFirstPath, allCount))
        list.addAll(tempFolderMap.values)

    } catch (e: Throwable) {
    } finally {
        cursor?.close()
    }

    return list
}

internal fun findPhoto(context: Context, bucketId: Long, showType: Array<String>?): List<LPhotoModel> {
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


    val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME),
            selectionBuilder.toString(),
            typeArray,
            MediaStore.Images.Media.DATE_ADDED
    )

    try {
        if (cursor == null || cursor.count <= 0) return photoList

        while (cursor.moveToNext()) {
            val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))

            if (isEmptyFile(imagePath)) {
                continue
            }

            val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))

            photoList.add(LPhotoModel(name, imagePath))

        }

    } catch (e: Throwable) {
    } finally {
        cursor?.close()
    }

    return photoList
}
