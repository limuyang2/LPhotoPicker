package top.limuyang2.photolibrary.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
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