package top.limuyang2.photolibrary.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.ColorInt
import android.text.TextUtils
import android.view.WindowManager
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
 * @param value The value of dp.
 * @return value of px
 */
internal fun Context.dp2px(value: Int): Int = (value * resources.displayMetrics.density).toInt()

/**
 * Return the status bar's height.
 *
 * @return the status bar's height
 */
internal fun Context.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

internal fun Activity.setStatusBarColor(@ColorInt color: Int) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color

            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Return the width of screen, in pixel.
 *
 * @return the width of screen, in pixel
 */
internal fun Context.getScreenWidth(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            ?: return resources.displayMetrics.widthPixels
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
internal fun Context.getScreenHeight(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            ?: return resources.displayMetrics.heightPixels
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        wm.defaultDisplay.getRealSize(point)
    } else {
        wm.defaultDisplay.getSize(point)
    }
    return point.y
}

private fun isNotImageFile(path: String): Boolean {
    if (TextUtils.isEmpty(path)) {
        return true
    }

    val file = File(path)
    return !file.exists() || file.length() == 0L
}


internal fun findPhoto(context: Context, showType: Array<String>?): List<LPhotoModel> {
    val photoModelList = ArrayList<LPhotoModel>()

    val typeArray = showType ?: arrayOf("image/jpeg", "image/png", "image/jpg", "image/gif")
    val selectionBuilder = StringBuilder()
    for (i in 0 until typeArray.size) {
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
