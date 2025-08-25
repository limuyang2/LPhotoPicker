package top.limuyang2.pohotopicker

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import top.limuyang2.photolibrary.LPhotoHelper
import top.limuyang2.photolibrary.util.LPPImageType
import top.limuyang2.pohotopicker.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        private const val CHOOSE_PHOTO_REQUEST = 10

    }

    private val permissionLaunch =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            var isAllOk = true

            for ((k, v) in map) {
                if (!v) {
                    isAllOk = false
                    println("------------->>> no per: ${k}")
                }
            }

            if (isAllOk) {
                getPhoto()
            } else {
                Toast.makeText(this, "图片选择需要以下权限:1.访问设备上的照片", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private val cropLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.let { intent ->
                    val resultUri = UCrop.getOutput(intent) ?: return@let
                    Glide.with(this).load(resultUri).into(viewBinding.imgView)
                }
            }
        }

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) {v,insets ->
            val bar = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            viewBinding.fakeBar.updateLayoutParams { height = bar.top }

            insets
        }


        viewBinding.toolBar.title = getString(R.string.app_name)

        // 获取系统当前是否是暗色模式
        val mode: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        viewBinding.switchBtn.isChecked = mode == Configuration.UI_MODE_NIGHT_YES
        // switch 设置点击切换事件
        viewBinding.switchBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
        }

        viewBinding.columnsNumberSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewBinding.columnsNumberMumTv.text = (progress + 3).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        viewBinding.useUCropCb.isEnabled = viewBinding.singleChooseCb.isChecked
        viewBinding.singleChooseCb.setOnCheckedChangeListener { _, isChecked ->
            viewBinding.useUCropCb.isEnabled = isChecked
            viewBinding.multiNumSeekBar.isEnabled = !isChecked
        }

        viewBinding.multiNumSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewBinding.multiMumTv.text = (progress + 1).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        viewBinding.photoBtn.setOnClickListener { getPhoto() }

        viewBinding.theme2Btn.setOnClickListener { getPhoto(R.style.LPhotoTheme2) }

        viewBinding.darkThemeBtn.setOnClickListener { getPhoto(R.style.BlackTheme) }
    }


    private fun getPhoto(theme: Int = top.limuyang2.photolibrary.R.style.LPhotoTheme) {
        // android 10 必须添加 ACCESS_MEDIA_LOCATION 权限，否则无法加载 HEIF 格式图片
        val perArr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        //验证权限
        if (hasPermissions(this, *perArr)) {

            LPhotoHelper.Builder()
                .maxChooseCount(viewBinding.multiMumTv.text.toString().toInt())
                .columnsNumber(viewBinding.columnsNumberMumTv.text.toString().toInt())
                .imageType(LPPImageType.ofAll())
                .pauseOnScroll(viewBinding.pauseOnScrollCb.isChecked)
                .isSingleChoose(viewBinding.singleChooseCb.isChecked)
                .isOpenLastAlbum(true)
                .theme(theme)
                .build()
                .start(this, CHOOSE_PHOTO_REQUEST)

        } else {
            permissionLaunch.launch(perArr)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CHOOSE_PHOTO_REQUEST -> {
                    val selectedPhotos = LPhotoHelper.getSelectedPhotos(data)

                    if (viewBinding.singleChooseCb.isChecked) { //单选模式
                        if (viewBinding.useUCropCb.isChecked) {
                            //使用UCrop裁剪图片
                            val outUri =
                                Uri.fromFile(File(cacheDir, "${System.currentTimeMillis()}.jpg"))
                            UCrop.of(selectedPhotos[0], outUri)
                                .withAspectRatio(1f, 1f)
                                .withMaxResultSize(800, 800)
                                .start(this, cropLaunch)
                        } else {
                            Glide.with(this).load(selectedPhotos[0]).into(viewBinding.imgView)
                        }
                    } else {
                        var uriStr = ""
                        for (uri in selectedPhotos) {
                            Log.i("MainActivity", uri.toString())
                            uriStr += uri.toString() + "\n"
                        }
                        viewBinding.multiPathTv.text = uriStr
                    }
                }

                UCrop.REQUEST_CROP -> {
                    data?.let {
                        val resultUri = UCrop.getOutput(data)
                        Log.d("UCrop.REQUEST_CROP", resultUri.toString())
                        Glide.with(this).load(resultUri).into(viewBinding.imgView)
                    }
                }
            }
        }
    }
}
