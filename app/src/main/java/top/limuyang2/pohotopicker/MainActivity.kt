package top.limuyang2.pohotopicker

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import top.limuyang2.photolibrary.activity.LPhotoPickerActivity
import top.limuyang2.photolibrary.engine.LGlideEngine
import top.limuyang2.photolibrary.util.LPPImageType
import java.io.File


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val PER_REQUEST = 100
        private const val CHOOSE_PHOTO_REQUEST = 10

        private const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        columnsNumberSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                columnsNumberMumTv.text = (progress + 3).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        singleChoose_cb.setOnCheckedChangeListener { buttonView, isChecked ->
            multiNumSeekBar.isEnabled = !isChecked
        }

        multiNumSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                multiMumTv.text = (progress + 1).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        photoBtn.setOnClickListener { getPhoto() }

        theme2Btn.setOnClickListener { getPhoto(R.style.LPhotoTheme2) }

        darkThemeBtn.setOnClickListener { getPhoto(R.style.DarkTheme) }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PER_REQUEST) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                AppSettingsDialog.Builder(this).build().show()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PER_REQUEST) {
            getPhoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun getPhoto(theme: Int = R.style.LPhotoTheme) {
        //验证权限
        if (EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE)) {

            val intent = LPhotoPickerActivity.IntentBuilder(this)
                    .maxChooseCount(multiMumTv.text.toString().toInt())
                    .columnsNumber(columnsNumberMumTv.text.toString().toInt())
                    .imageType(LPPImageType.ofAll())
                    .pauseOnScroll(pauseOnScroll_cb.isChecked)
                    .isSingleChoose(singleChoose_cb.isChecked)
                    .imageEngine(LGlideEngine())
                    .theme(theme)
//                    .selectedPhotos(ArrayList<String>().apply {
//                        add("/storage/emulated/0/DCIM/Screenshots/Screenshot_2018-07-09-11-43-08-936_com.wxm.android.png")
//                        add("/storage/emulated/0/pictures/li_mumuの二维码.jpg")
//                    })
                    .build()

            startActivityForResult(intent, CHOOSE_PHOTO_REQUEST)
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PER_REQUEST, WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CHOOSE_PHOTO_REQUEST -> {
                    val selectedPhotos = LPhotoPickerActivity.getSelectedPhotos(data)

                    if (singleChoose_cb.isChecked) { //单选模式
                        if (use_uCrop_cb.isChecked) {
                            //使用UCrop裁剪图片
                            val outUri = Uri.fromFile(File(cacheDir, "${System.currentTimeMillis()}.jpg"))
//                            UCrop.of(Uri.fromFile(File(selectedPhotos[0])), outUri)
//                                    .withAspectRatio(1f, 1f)
//                                    .withMaxResultSize(800, 800)
//                                    .start(this)
                        } else {
                            Glide.with(this).load(selectedPhotos[0]).into(imgView)
                        }
                    } else {
                        var pathStr = ""
                        for (path in selectedPhotos) {
                            Log.i("MainActivity", path)
                            pathStr += path + "\n"
                        }
                        multiPathTv.text = pathStr
                    }
                }

//                UCrop.REQUEST_CROP   -> {
//                    data?.let {
//                        val resultUri = UCrop.getOutput(data)
//                        Log.d("UCrop.REQUEST_CROP", resultUri.toString())
//                        Glide.with(this).load(resultUri).into(imgView)
//                    }
//                }
            }
        }
    }
}
