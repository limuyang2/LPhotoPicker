package top.limuyang2.photolibrary.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.model.LPhotoModel
import java.io.File


/**
 *
 * Date 2018/7/31
 * @author limuyang
 */

/**
 * Value of dp to value of px.
 *
 * @param dpValue The value of dp.
 * @return value of px
 */
internal fun dp2px(context: Context, dpValue: Float): Float {
    val scale = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5f)
}



/**
 * Return the width of screen, in pixel.
 *
 * @return the width of screen, in pixel
 */
internal fun getScreenWidth(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            ?: return context.resources.displayMetrics.widthPixels
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        wm.defaultDisplay.getRealSize(point)
    } else {
        wm.defaultDisplay.getSize(point)
    }
    return point.x
}


/**
 * Return the height of screen, in pixel.
 *
 * @return the height of screen, in pixel
 */
internal fun getScreenHeight(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            ?: return context.resources.displayMetrics.heightPixels
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        wm.defaultDisplay.getRealSize(point)
    } else {
        wm.defaultDisplay.getSize(point)
    }
    return point.y
}

internal fun isNotImageFile(path: String): Boolean {
    if (TextUtils.isEmpty(path)) {
        return true
    }

    val file = File(path)
    return !file.exists() || file.length() == 0L

    // 获取图片的宽和高，但不把图片加载到内存中
    //        BitmapFactory.Options options = new BitmapFactory.Options();
    //        options.inJustDecodeBounds = true;
    //        BitmapFactory.decodeFile(path, options);
    //        return options.outMimeType == null;
}


internal fun findPhoto(context: Context, showType: Array<String>?): List<LPhotoModel> {
    val photoModelList = ArrayList<LPhotoModel>()

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
            arrayOf(MediaStore.Images.Media.DATA),
            selectionBuilder.toString(),
            typeArray,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
    )

    try {
        if (cursor == null || cursor.count <= 0) return photoModelList
        val folderModelMap = HashMap<String, LPhotoModel>()
        val allPhotoModel = LPhotoModel(context.resources.getString(R.string.l_pp_all_image))
        while (cursor.moveToNext()) {
            val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))

            if (isNotImageFile(imagePath)) {
                continue
            }

            //每扫描，都把图片都添加进“全部”
            allPhotoModel.photoInfoList.add(LPhotoModel.PhotoInfo(photoPath = imagePath))

            val folder = File(imagePath).parentFile
            val folderPath = if (folder != null) {
                folder.absolutePath
            } else {
                val end = imagePath.lastIndexOf(File.separator)
                if (end != -1) {
                    imagePath.substring(0, end)
                } else ""
            }

            if (folderPath.isNotBlank()) {
                val model: LPhotoModel = if (folderModelMap.containsKey(folderPath)) {
                    folderModelMap[folderPath] ?: LPhotoModel("")
                } else {
                    var folderName = folderPath.substring(folderPath.lastIndexOf(File.separator) + 1)
                    if (folderName.isEmpty()) {
                        folderName = "/"
                    }

                    val newModel = LPhotoModel(folderName)
                    folderModelMap[folderPath] = newModel
                    newModel
                }
                model.photoInfoList.add(LPhotoModel.PhotoInfo(photoPath = imagePath))
            }

        }
        photoModelList.add(allPhotoModel)
        photoModelList.addAll(folderModelMap.values)
    } catch (e: Exception) {

    } finally {
        cursor?.close()
    }

    return photoModelList
}
