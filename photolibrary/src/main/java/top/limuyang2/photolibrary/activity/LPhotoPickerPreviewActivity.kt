package top.limuyang2.photolibrary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import androidx.viewpager.widget.ViewPager
import top.limuyang2.photolibrary.LPhotoHelper.Companion.EXTRA_SELECTED_PHOTOS
import top.limuyang2.photolibrary.LPhotoHelper.Companion.EXTRA_THEME
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.adapter.LPreviewPagerAdapter
import top.limuyang2.photolibrary.databinding.LPpActivityPhotoPickerPreviewBinding
import top.limuyang2.photolibrary.util.click
import top.limuyang2.photolibrary.util.dip
import top.limuyang2.photolibrary.util.statusBarHeight
import top.limuyang2.photolibrary.util.transparentStatusBar

@SuppressLint("SetTextI18n")
class LPhotoPickerPreviewActivity : LBaseActivity<LPpActivityPhotoPickerPreviewBinding>() {

    private val nowSelectedPhotos = ArrayList<Uri>()

    private val intentSelectedPhotos by lazy { intent.getParcelableArrayListExtra<Uri>(EXTRA_SELECTED_PHOTOS) }

    private val viewPageAdapter by lazy { LPreviewPagerAdapter(supportFragmentManager, intentSelectedPhotos) }

    private var currentUri: Uri? = null

    override fun initBinding(): LPpActivityPhotoPickerPreviewBinding {
        return LPpActivityPhotoPickerPreviewBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.l_pp_photo_preview_bg)))
        initAttr()
        setStatusBar()
        viewBinding.checkBox.setChecked(checked = true, animate = false)
        viewBinding.previewTitleTv.text = "1/${intentSelectedPhotos.size}"
        viewBinding.viewPage.adapter = viewPageAdapter
    }


    override fun initListener() {
        viewBinding.toolBar.setNavigationOnClickListener { onBackPressed() }
        viewBinding.viewPage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                viewBinding.previewTitleTv.text = "${position + 1}/${intentSelectedPhotos.size}"
                currentUri = intentSelectedPhotos[position]
                viewBinding.checkBox.setChecked(nowSelectedPhotos.contains(intentSelectedPhotos[position]), false)
            }
        })

        viewBinding.checkBox.setOnClickListener {
            if (!viewBinding.checkBox.isChecked) {
                viewBinding.checkBox.setChecked(checked = true, animate = true)
                currentUri?.let { nowSelectedPhotos.add(it) }
            } else {
                viewBinding.checkBox.setChecked(checked = false, animate = true)
                nowSelectedPhotos.remove(currentUri)
            }
            viewBinding.applyBtn.isEnabled = nowSelectedPhotos.isNotEmpty()
        }

        viewBinding.applyBtn.click {
            val intent = Intent()
            intent.putParcelableArrayListExtra(EXTRA_SELECTED_PHOTOS, nowSelectedPhotos)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun initData() {
        nowSelectedPhotos.addAll(intentSelectedPhotos)
        currentUri = intentSelectedPhotos[0]
    }

    private fun initAttr() {
        val typedArray = theme.obtainStyledAttributes(R.styleable.LPPAttr)

        val toolBarHeight = typedArray.getDimensionPixelSize(R.styleable.LPPAttr_l_pp_toolBar_height, dip(56).toInt())
        val l = viewBinding.toolBar.layoutParams
        l.height = toolBarHeight
        viewBinding.toolBar.layoutParams = l

        val backIcon = typedArray.getResourceId(R.styleable.LPPAttr_l_pp_toolBar_backIcon, R.drawable.ic_l_pp_back_android)
        viewBinding.toolBar.setNavigationIcon(backIcon)

        val titleSize = typedArray.getDimension(R.styleable.LPPAttr_l_pp_toolBar_title_size, dip(16))
        viewBinding.previewTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)

        val bottomBarHeight = typedArray.getDimensionPixelSize(R.styleable.LPPAttr_l_pp_bottomBar_height, dip(50).toInt())
        val newBl = viewBinding.bottomLayout.layoutParams
        newBl.height = bottomBarHeight
        viewBinding.bottomLayout.layoutParams = newBl

        val colors = intArrayOf(Color.WHITE, Color.GRAY)
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_enabled)
        states[1] = intArrayOf(android.R.attr.state_window_focused)
        val colorList = ColorStateList(states, colors)
        viewBinding.applyBtn.setTextColor(colorList)

        typedArray.recycle()
    }

    private fun setStatusBar() {
        //5.0以上去除半透明遮罩，全透明
        transparentStatusBar()

        //获取状态栏高度,设置顶部layout高度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val allHeight = statusBarHeight + viewBinding.toolBar.layoutParams.height
            val newLayout = viewBinding.topBlurView.layoutParams
            newLayout.height = allHeight
            viewBinding.topBlurView.requestLayout()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putParcelableArrayListExtra(EXTRA_SELECTED_PHOTOS, nowSelectedPhotos)
        setResult(Activity.RESULT_CANCELED, intent)
        super.onBackPressed()
    }

    private var mIsHidden = false

    fun changeToolBar() {
        if (mIsHidden) {
            showTitleBarAndChooseBar()
        } else {
            hiddenToolBarAndChooseBar()
        }
    }

    private fun showTitleBarAndChooseBar() {
        ViewCompat.animate(viewBinding.toolBarLayout).translationY(0f).setInterpolator(DecelerateInterpolator(2f)).setListener(object : ViewPropertyAnimatorListenerAdapter() {
            override fun onAnimationEnd(view: View?) {
                mIsHidden = false
            }
        }).setDuration(DURATION_TIME).start()
        viewBinding.toolBarLayout.visibility = View.VISIBLE

        ViewCompat.animate(viewBinding.bottomLayout).translationY(0f).setInterpolator(DecelerateInterpolator(2f)).setListener(object : ViewPropertyAnimatorListenerAdapter() {
            override fun onAnimationEnd(view: View?) {
                mIsHidden = false
            }
        }).setDuration(DURATION_TIME).start()
        viewBinding.bottomLayout.visibility = View.VISIBLE
    }

    private fun hiddenToolBarAndChooseBar() {
        ViewCompat.animate(viewBinding.toolBarLayout).translationY((-viewBinding.toolBarLayout.height).toFloat()).setInterpolator(DecelerateInterpolator(2f)).setListener(object : ViewPropertyAnimatorListenerAdapter() {
            override fun onAnimationEnd(view: View?) {
                mIsHidden = true
                viewBinding.toolBarLayout.visibility = View.GONE
            }
        }).setDuration(DURATION_TIME).start()

        ViewCompat.animate(viewBinding.bottomLayout).translationY((viewBinding.bottomLayout.height).toFloat()).setInterpolator(DecelerateInterpolator(2f)).setListener(object : ViewPropertyAnimatorListenerAdapter() {
            override fun onAnimationEnd(view: View?) {
                mIsHidden = true
                viewBinding.bottomLayout.visibility = View.GONE
            }
        }).setDuration(DURATION_TIME).start()

    }

    class IntentBuilder(context: Context) {
        private val mIntent: Intent = Intent(context, LPhotoPickerPreviewActivity::class.java)

        /**
         * 当前已选中的图片路径集合
         */
        fun selectedPhotos(selectedPhotos: ArrayList<Uri>): IntentBuilder {
            mIntent.putParcelableArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos)
            return this
        }

        /**
         * 设置主题
         */
        fun theme(@StyleRes style: Int): IntentBuilder {
            mIntent.putExtra(EXTRA_THEME, style)
            return this
        }

        fun build(): Intent {
            return mIntent
        }
    }

    companion object {
        private const val DURATION_TIME = 600L
    }
}