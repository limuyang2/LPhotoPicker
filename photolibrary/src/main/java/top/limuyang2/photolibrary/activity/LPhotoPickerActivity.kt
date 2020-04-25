package top.limuyang2.photolibrary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.l_activity_photo_picker.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.adapter.LPPGridDivider
import top.limuyang2.photolibrary.adapter.PhotoPickerRecyclerAdapter
import top.limuyang2.photolibrary.engine.LImageEngine
import top.limuyang2.photolibrary.model.LPhotoModel
import top.limuyang2.photolibrary.popwindow.LPhotoFolderPopWin
import top.limuyang2.photolibrary.util.*



/**
 *
 * Date 2018/7/31
 * @author limuyang
 */

@Suppress("DEPRECATION")
class LPhotoPickerActivity : LBaseActivity() {

    companion object {
//        private const val EXTRA_CAMERA_FILE_DIR = "EXTRA_CAMERA_FILE_DIR"
        private const val EXTRA_SELECTED_PHOTOS = "EXTRA_SELECTED_PHOTOS"
        private const val EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT"
        private const val EXTRA_PAUSE_ON_SCROLL = "EXTRA_PAUSE_ON_SCROLL"
        private const val EXTRA_COLUMNS_NUMBER = "EXTRA_COLUMNS_NUMBER"
        private const val EXTRA_IS_SINGLE_CHOOSE = "EXTRA_IS_SINGLE_CHOOSE"
        private const val EXTRA_TYPE = "EXTRA_TYPE"
        private const val EXTRA_THEME = "EXTRA_THEME"

        private val STATE_SELECTED_PHOTOS = "STATE_SELECTED_PHOTOS"

        /**
         * 预览照片的请求码
         */
        private const val RC_PREVIEW_CODE = 2

        private val SPAN_COUNT = 3


        /**
         * 获取已选择的图片集合
         *
         * @param intent
         * @return
         */
        @JvmStatic
        fun getSelectedPhotos(intent: Intent?): ArrayList<String> {
            return intent?.getStringArrayListExtra(EXTRA_SELECTED_PHOTOS) ?: ArrayList()
        }
    }

    class IntentBuilder(context: Context) {
        private val mIntent: Intent = Intent(context, LPhotoPickerActivity::class.java)

//        /**
//         * 拍照后图片保存的目录。如果传 null 表示没有拍照功能，如果不为 null 则具有拍照功能，
//         */
//        fun cameraFileDir(cameraFileDir: File?): IntentBuilder {
//            mIntent.putExtra(EXTRA_CAMERA_FILE_DIR, cameraFileDir)
//            return this
//        }

        /**
         * 需要显示哪种类型的图片(JPG\PNG\GIF\WEBP)，默认全部加载
         * @return IntentBuilder
         */
        fun imageType(typeArray: Array<String>): IntentBuilder {
            mIntent.putExtra(EXTRA_TYPE, typeArray)
            return this
        }

        /**
         * 图片选择张数的最大值
         *
         * @param maxChooseCount
         * @return
         */
        fun maxChooseCount(maxChooseCount: Int): IntentBuilder {
            mIntent.putExtra(EXTRA_MAX_CHOOSE_COUNT, maxChooseCount)
            return this
        }

        /**
         * 是否是单选模式，默认false
         * @param isSingle Boolean
         * @return IntentBuilder
         */
        fun isSingleChoose(isSingle: Boolean): IntentBuilder {
            mIntent.putExtra(EXTRA_IS_SINGLE_CHOOSE, isSingle)
            return this
        }

        /**
         * 当前已选中的图片路径集合，可以传 null
         */
        fun selectedPhotos(selectedPhotos: java.util.ArrayList<String>?): IntentBuilder {
            mIntent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos)
            return this
        }

        /**
         * 滚动列表时是否暂停加载图片，默认为 false
         */
        fun pauseOnScroll(pauseOnScroll: Boolean): IntentBuilder {
            mIntent.putExtra(EXTRA_PAUSE_ON_SCROLL, pauseOnScroll)
            return this
        }

        /**
         * 图片选择以几列展示，默认3列
         */
        fun columnsNumber(number: Int): IntentBuilder {
            mIntent.putExtra(EXTRA_COLUMNS_NUMBER, number)
            return this
        }

        /**
         * 设置图片加载引擎
         */
        fun imageEngine(engine: LImageEngine): IntentBuilder {
            ImageEngineUtils.engine = engine
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

//    // 获取拍照图片保存目录
//    private val cameraFileDir by lazy { intent.getSerializableExtra(EXTRA_CAMERA_FILE_DIR) as File }

    // 获取图片选择的最大张数
    private val maxChooseCount by lazy { intent.getIntExtra(EXTRA_MAX_CHOOSE_COUNT, 1) }

    //外部传进来的已选中的图片路径集合
    private val selectedPhotos by lazy { intent.getStringArrayListExtra(EXTRA_SELECTED_PHOTOS) }

    private val isSingleChoose by lazy { intent.getBooleanExtra(EXTRA_IS_SINGLE_CHOOSE, false) }

    //列数
    private val columnsNumber by lazy { intent.getIntExtra(EXTRA_COLUMNS_NUMBER, 3) }

    private val showTypeArray by lazy { intent.getStringArrayExtra(EXTRA_TYPE) }

    private val intentTheme by lazy { intent.getIntExtra(EXTRA_THEME, R.style.LPhotoTheme) }

    private var segmentingLineWidth: Int = 0

    private val adapter by lazy {
        val width = getScreenWidth(this)
        val imgWidth = (width - segmentingLineWidth * (columnsNumber + 1)) / columnsNumber
        val a = PhotoPickerRecyclerAdapter(this, maxChooseCount, imgWidth)
        a.setSelectedItemsPath(selectedPhotos)
        a
    }

    private val folderPopWindow by lazy {
        LPhotoFolderPopWin(this, toolBar, object : LPhotoFolderPopWin.Delegate {
            override fun onSelectedFolder(position: Int) {
                reloadPhotos(position)
            }

            override fun executeDismissAnim() {
                ViewCompat.animate(photoPickerArrow).setDuration(LPhotoFolderPopWin.ANIM_DURATION.toLong()).rotation(0f).start()
            }
        })
    }

    private val photoModelList = ArrayList<LPhotoModel>()

    override fun getLayout(): Int = R.layout.l_activity_photo_picker

    override fun getThemeId(): Int = intentTheme

    override fun initView(savedInstanceState: Bundle?) {
        initAttr()

        initRecyclerView()
        setBottomBtn()

        applyBtn.isEnabled = selectedPhotos != null && selectedPhotos.isNotEmpty()
    }

    /**
     * 获取设置的控件属性
     */
    private fun initAttr() {
        val typedArray = theme.obtainStyledAttributes(R.styleable.LPPAttr)

        val activityBg = typedArray.getColor(R.styleable.LPPAttr_l_pp_picker_activity_bg, Color.parseColor("#F9F9F9"))
        window.setBackgroundDrawable(ColorDrawable(activityBg))

        val statusBarColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_status_bar_color, resources.getColor(R.color.l_pp_colorPrimaryDark))
        setStatusBarColor(this, statusBarColor)

        val toolBarHeight = typedArray.getDimensionPixelSize(R.styleable.LPPAttr_l_pp_toolBar_height, dp2px(this, 56f).toInt())
        val l = toolBar.layoutParams
        l.height = toolBarHeight
        toolBar.layoutParams = l

        val backIcon = typedArray.getResourceId(R.styleable.LPPAttr_l_pp_toolBar_backIcon, R.drawable.ic_l_pp_back_android)
        toolBar.setNavigationIcon(backIcon)

        val toolBarBackgroundRes = typedArray.getResourceId(R.styleable.LPPAttr_l_pp_toolBar_background, 0)
        val toolBarBackgroundColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_toolBar_background, resources.getColor(R.color.l_pp_colorPrimary))

        if (toolBarBackgroundRes != 0) {
            toolBar.setBackgroundResource(toolBarBackgroundRes)
        } else {
            toolBar.setBackgroundColor(toolBarBackgroundColor)
        }

        val bottomBarBgColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_picker_bottomBar_background, Color.parseColor("#96ffffff"))
        topBlurView.setOverlayColor(bottomBarBgColor)

        val bottomBarHeight = typedArray.getDimensionPixelSize(R.styleable.LPPAttr_l_pp_bottomBar_height, dp2px(this, 50f).toInt())
        val newBl = bottomLayout.layoutParams
        newBl.height = bottomBarHeight
        bottomLayout.layoutParams = newBl

        val bottomBarEnableTextColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_picker_bottomBar_enabled_text_color, Color.parseColor("#333333"))
        val bottomBarUnEnableTextColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_picker_bottomBar_unEnabled_text_color, Color.GRAY)
        val colors = intArrayOf(bottomBarEnableTextColor,  bottomBarUnEnableTextColor)
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_enabled)
        states[1] = intArrayOf(android.R.attr.state_window_focused)
        val colorList = ColorStateList(states, colors)
        previewBtn.setTextColor(colorList)
        applyBtn.setTextColor(colorList)

        val titleColor = typedArray.getColor(R.styleable.LPPAttr_l_pp_toolBar_title_color, Color.WHITE)
        val titleSize = typedArray.getDimension(R.styleable.LPPAttr_l_pp_toolBar_title_size, dp2px(this, 16f))
        photoPickerTitle.setTextColor(titleColor)
        photoPickerTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
        photoPickerArrow.setColorFilter(titleColor)

        segmentingLineWidth = typedArray.getDimensionPixelOffset(R.styleable.LPPAttr_l_pp_picker_segmenting_line_width, dp2px(this, 5f).toInt())

        typedArray.recycle()
    }

    private fun initRecyclerView() {
        pickerRecycler.apply {
            layoutManager = GridLayoutManager(this@LPhotoPickerActivity, columnsNumber)
            adapter = this@LPhotoPickerActivity.adapter
            if (isSingleChoose) {
                addItemDecoration(LPPGridDivider(segmentingLineWidth, columnsNumber))
                bottomLayout.visibility = View.GONE
            } else {
                addItemDecoration(LPPGridDivider(segmentingLineWidth, columnsNumber, bottomLayout.layoutParams.height))
            }

            if (intent.getBooleanExtra(EXTRA_PAUSE_ON_SCROLL, false)) {
                addOnScrollListener(LPPOnScrollListener(this@LPhotoPickerActivity))
            }
        }
    }

    override fun initListener() {
        toolBar.setNavigationOnClickListener { finish() }
        titleLayout.setOnClickListener(object : OnNoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                showPhotoFolderPopWindow()
            }
        })
        previewBtn.setOnClickListener {
            gotoPreview()
        }

        applyBtn.setOnClickListener(object : OnNoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                returnSelectedPhotos(adapter.getSelectedItems())
            }
        })

        adapter.onPhotoItemClick = { view, path, _ ->
            if (isSingleChoose) {
                val list = ArrayList<String>().apply { add(path) }
                returnSelectedPhotos(list)
            } else {
                adapter.setChooseItem(path, view.findViewById(R.id.checkView))
                setBottomBtn()
            }
        }

    }

    override fun initData() {
        async(UI) {
            val photoModelList = bg { findPhoto(this@LPhotoPickerActivity, showTypeArray) }.await()
            this@LPhotoPickerActivity.photoModelList.addAll(photoModelList)

            reloadPhotos(0)
        }
    }

    private fun reloadPhotos(pos: Int) {
        if (photoModelList.size >= pos) {
            photoPickerTitle.text = photoModelList[pos].name
            adapter.setData(photoModelList[pos].photoInfoList)
        }
    }

    private fun showPhotoFolderPopWindow() {
        folderPopWindow.setData(photoModelList)
        folderPopWindow.show()

        ViewCompat.animate(photoPickerArrow).setDuration(LPhotoFolderPopWin.ANIM_DURATION.toLong()).rotation(-180f).start()
    }

    @SuppressLint("SetTextI18n")
    private fun setBottomBtn() {
        if (adapter.hasSelected()) {
            applyBtn.isEnabled = true
            applyBtn.text = "${getString(R.string.l_pp_apply)}(${adapter.getSelectedItemSize()}/$maxChooseCount)"

            previewBtn.isEnabled = true
        } else {
            applyBtn.isEnabled = false
            applyBtn.text = getString(R.string.l_pp_apply)

            previewBtn.isEnabled = false
        }
    }

    /**
     * 返回已选中的图片集合
     *
     * @param selectedPhotos
     */
    private fun returnSelectedPhotos(selectedPhotos: ArrayList<String>) {
        val intent = Intent()
        intent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun gotoPreview() {
        val intent = LPhotoPickerPreviewActivity.IntentBuilder(this)
                .maxChooseCount(maxChooseCount)
                .selectedPhotos(adapter.getSelectedItems())
                .theme(intentTheme)
                .isFromTakePhoto(false)
                .build()
        startActivityForResult(intent, RC_PREVIEW_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_PREVIEW_CODE -> {
                when (resultCode) {
                    Activity.RESULT_CANCELED -> {
                        data?.let {
                            adapter.setSelectedItemsPath(LPhotoPickerPreviewActivity.getSelectedPhotos(it))
                            setBottomBtn()
                        }
                    }

                    Activity.RESULT_OK       -> {
                        data?.let {
                            returnSelectedPhotos(LPhotoPickerPreviewActivity.getSelectedPhotos(it))
                        }
                    }
                }
            }
        }

    }

    /**
     * recyclerView 滑动监听，滑动时暂停加载图片
     * @property context Context
     * @constructor
     */
    private class LPPOnScrollListener(private val context: Context) : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                ImageEngineUtils.engine.resume(context)
            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                ImageEngineUtils.engine.pause(context)
            }
        }
    }
}