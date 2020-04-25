package top.limuyang2.photolibrary.activity

import android.os.Bundle
import android.support.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 *
 * Date 2018/7/31
 * @author limuyang
 */
abstract class LBaseActivity : AppCompatActivity() {

    abstract fun getThemeId():Int

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initListener()

    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeId())
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        initView(savedInstanceState)
        initListener()
        initData()
    }
}