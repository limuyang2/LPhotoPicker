package top.limuyang2.photolibrary.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import top.limuyang2.photolibrary.LPhotoHelper.Companion.EXTRA_THEME
import top.limuyang2.photolibrary.R

/**
 *
 * Date 2018/7/31
 * @author limuyang
 */
abstract class LBaseActivity<V : ViewBinding> : AppCompatActivity() {

    protected lateinit var viewBinding: V

    protected val intentTheme by lazy(LazyThreadSafetyMode.NONE) { intent.getIntExtra(EXTRA_THEME, R.style.LPhotoTheme) }

    abstract fun initBinding(): V

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initListener()

    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setTheme(intentTheme)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this.display
            } else {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay
            }

            if (display != null) {
                val mode = display.mode
                val currentWidth = mode.physicalWidth
                val currentHeight = mode.physicalHeight

                val modes = display.supportedModes

                modes.sortBy { m ->
                    m.refreshRate
                }

                // 只找90Hz的
                val filterModes = modes.filter { m ->
                    m.refreshRate == 90f && m.physicalWidth == currentWidth && m.physicalHeight == currentHeight
                }

                filterModes.lastOrNull()?.let { m ->
                    val lp = window.attributes
                    lp.preferredDisplayModeId = m.modeId
                    window.attributes = lp
                }
            }
        }


        super.onCreate(savedInstanceState)
        viewBinding = initBinding()
        setContentView(viewBinding.root)
        initView(savedInstanceState)
        initListener()
        initData()
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        // 传递主题
        intent.putExtra(EXTRA_THEME, intentTheme)
        super.startActivityForResult(intent, requestCode)
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        // 传递主题
        intent.putExtra(EXTRA_THEME, intentTheme)
        super.startActivityForResult(intent, requestCode, options)
    }
}