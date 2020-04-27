package top.limuyang2.photolibrary.activity

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.limuyang2.photolibrary.LPhotoHelper.Companion.EXTRA_THEME
import top.limuyang2.photolibrary.LPhotoHelper.Companion.EXTRA_TYPE
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.adapter.LFolderAdapter
import top.limuyang2.photolibrary.databinding.LPpActivityFolderBinding
import top.limuyang2.photolibrary.util.*
import top.limuyang2.photolibrary.util.dip
import top.limuyang2.photolibrary.util.setStatusBarColor
import java.io.File

/**
 * @author 李沐阳
 * @date：2020/4/26
 * @description:
 */
class LPhotoFolderActivity : LBaseActivity<LPpActivityFolderBinding>() {

    private val mFolderAdapter: LFolderAdapter by lazy(LazyThreadSafetyMode.NONE) { LFolderAdapter() }

    private val showTypeArray by lazy { intent.getStringArrayExtra(EXTRA_TYPE) }

    override fun initBinding(): LPpActivityFolderBinding {
        return LPpActivityFolderBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

        initAttr()
        viewBinding.apply {
            recyclerView.adapter = mFolderAdapter

            mFolderAdapter.setOnItemClick { _, _, model ->
                println(model)
                intent.component = ComponentName(this@LPhotoFolderActivity, LPhotoPickerActivity::class.java)
                intent.putExtra("bucketId", model.bucketId)
                intent.putExtra("bucketName", model.bucketName)
                startActivity(intent)
            }
        }
    }

    /**
     * 获取设置的控件属性
     */
    private fun initAttr() {
        val typedArray = theme.obtainStyledAttributes(R.styleable.LPPAttr)

        val statusBarColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_status_bar_color, resources.getColor(R.color.l_pp_colorPrimaryDark))
        setStatusBarColor(statusBarColor)


        viewBinding.apply {
            val toolBarHeight = typedArray.getDimensionPixelSize(R.styleable.LPPAttr_l_pp_toolBar_height, dip(56).toInt())
            val l = toolBar.layoutParams
            l.height = toolBarHeight
            toolBar.requestLayout()

            // 导航栏背景
            val toolBarBackgroundRes = typedArray.getResourceId(R.styleable.LPPAttr_l_pp_toolBar_background, 0)
            val toolBarBackgroundColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_toolBar_background, resources.getColor(R.color.l_pp_colorPrimary))
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


}