package top.limuyang2.photolibrary.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 *
 * Date 2018/7/31
 * @author limuyang
 */
abstract class LBaseActivity<V : ViewBinding> : AppCompatActivity() {

    protected lateinit var viewBinding: V

    abstract fun getThemeId():Int

    abstract fun initBinding(): V

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initListener()

    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeId())
        super.onCreate(savedInstanceState)
        viewBinding = initBinding()
        setContentView(viewBinding.root)
        initView(savedInstanceState)
        initListener()
        initData()
    }
}