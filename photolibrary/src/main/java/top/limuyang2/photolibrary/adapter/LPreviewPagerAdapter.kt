package top.limuyang2.photolibrary.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import top.limuyang2.photolibrary.fragment.LPreviewItemFragment

/**
 *
 * Date 2018/8/3
 * @author limuyang
 */
class LPreviewPagerAdapter(manager: FragmentManager, private val list: List<String>) : FragmentStatePagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {

        return LPreviewItemFragment.buildFragment(list[position])
    }

    override fun getCount(): Int = list.size

}