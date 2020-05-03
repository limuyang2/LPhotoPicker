package top.limuyang2.photolibrary.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.limuyang2.photolibrary.LPhotoHelper.Companion.EXTRA_LAST_OPENED_ALBUM
import top.limuyang2.photolibrary.LPhotoHelper.Companion.EXTRA_TYPE
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.adapter.LFolderAdapter
import top.limuyang2.photolibrary.databinding.LPpActivityFolderBinding
import top.limuyang2.photolibrary.model.LFolderModel
import top.limuyang2.photolibrary.util.dip
import top.limuyang2.photolibrary.util.findFolder
import top.limuyang2.photolibrary.util.setStatusBarColor

/**
 * @author 李沐阳
 * @date：2020/4/26
 * @description:
 */
class LPhotoFolderActivity : LBaseActivity<LPpActivityFolderBinding>() {

    private val mFolderAdapter: LFolderAdapter by lazy(LazyThreadSafetyMode.NONE) { LFolderAdapter() }

    private val showTypeArray by lazy(LazyThreadSafetyMode.NONE) { intent.getStringArrayExtra(EXTRA_TYPE) }

    private val isOpenLastAlbum by lazy(LazyThreadSafetyMode.NONE) { intent.getBooleanExtra(EXTRA_LAST_OPENED_ALBUM, false) }

    override fun initBinding(): LPpActivityFolderBinding {
        return LPpActivityFolderBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 判断是否需要打开最后记录的相册
        if (isOpenLastAlbum) {
            val lastModel = lastOpenAlbum
            if (lastModel.bucketId != 0L) {
                openAlbum(lastModel)
            }
        }

        initAttr()
        viewBinding.apply {
            recyclerView.adapter = mFolderAdapter

            mFolderAdapter.setOnItemClick { _, _, model ->
                openAlbum(model)
                lastOpenAlbum = model
            }
        }
    }

    /**
     * 获取设置的控件属性
     */
    private fun initAttr() {
        val typedArray = theme.obtainStyledAttributes(R.styleable.LPPAttr)

        val statusBarColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_status_bar_color, resources.getColor(R.color.colorPrimaryDark))
        setStatusBarColor(statusBarColor)


        viewBinding.apply {
            // 背景色
            val activityBg = typedArray.getColor(R.styleable.LPPAttr_l_pp_picker_activity_bg, resources.getColor(R.color.l_pp_activity_bg))
            window.setBackgroundDrawable(ColorDrawable(activityBg))

            val toolBarHeight = typedArray.getDimensionPixelSize(R.styleable.LPPAttr_l_pp_toolBar_height, dip(56).toInt())
            val l = toolBar.layoutParams
            l.height = toolBarHeight
            toolBar.requestLayout()

            // 导航栏背景
            val toolBarBackgroundRes = typedArray.getResourceId(R.styleable.LPPAttr_l_pp_toolBar_background, 0)
            val toolBarBackgroundColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_toolBar_background, resources.getColor(R.color.colorPrimary))
            if (toolBarBackgroundRes != 0) {
                toolBar.setBackgroundResource(toolBarBackgroundRes)
            } else {
                toolBar.setBackgroundColor(toolBarBackgroundColor)
            }

            // 返回按钮
            val backIcon = typedArray.getResourceId(R.styleable.LPPAttr_l_pp_toolBar_backIcon, R.drawable.ic_l_pp_back_android)
            toolBar.setNavigationIcon(backIcon)

            // 标题颜色字体
            val titleColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_toolBar_title_color, Color.WHITE)
            val titleSize = typedArray.getDimension(R.styleable.LPPAttr_l_pp_toolBar_title_size, dip(16))
            viewBinding.toolBarTitle.setTextColor(titleColor)
            viewBinding.toolBarTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
        }
    }

    override fun initListener() {
        viewBinding.toolBar.setNavigationOnClickListener { finish() }
    }

    override fun initData() {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                findFolder(this@LPhotoFolderActivity, showTypeArray)
            }
            mFolderAdapter.setData(list)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    // 透传数据
                    setResult(Activity.RESULT_OK, data)
                    finish()
                } else {
                    // 如果取消了选择，则清除最后打开的记录
                    lastOpenAlbum = LFolderModel()
                }
            }
        }
    }

    private fun openAlbum(model: LFolderModel) {
        intent.component = ComponentName(this@LPhotoFolderActivity, LPhotoPickerActivity::class.java)
        intent.putExtra("bucketId", model.bucketId)
        intent.putExtra("bucketName", model.bucketName)
        startActivityForResult(intent, PICK_CODE)
    }


    private var lastOpenAlbum: LFolderModel
        set(value) {
            val sp = getSharedPreferences("l_pp_sp", Context.MODE_PRIVATE)
            sp.edit().putLong(SP_LAST_BUCKET_ID, value.bucketId)
                    .putString(SP_LAST_BUCKET_NAME, value.bucketName)
                    .apply()
        }
        get() {
            val sp = getSharedPreferences("l_pp_sp", Context.MODE_PRIVATE)
            val id = sp.getLong(SP_LAST_BUCKET_ID, 0)
            val name = sp.getString(SP_LAST_BUCKET_NAME, "") ?: ""
            return LFolderModel(name, id, null, -1)
        }

    companion object {
        private const val PICK_CODE = 8
        private const val SP_LAST_BUCKET_ID = "bucketId"
        private const val SP_LAST_BUCKET_NAME = "bucketName"
    }
}