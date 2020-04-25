/**
 * Copyright 2016 bingoogolapple
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.limuyang2.photolibrary.popwindow

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.l_pp_popwindow_photo_folder.view.*
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.adapter.LFolderAdapter
import top.limuyang2.photolibrary.model.LPhotoModel
import top.limuyang2.photolibrary.util.getScreenHeight
import java.util.*

/**
 * Date 2018/8/1
 *
 * @author limuyang
 * 选择图片目录的PopupWindow
 */
class LPhotoFolderPopWin(activity: Activity,
                         anchorView: View,
                         private val mDelegate: Delegate?)
    : LBasePopupWindow(activity, R.layout.l_pp_popwindow_photo_folder, anchorView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT) {

    private lateinit var mFolderAdapter: LFolderAdapter
    private var currentPosition: Int = 0

    override fun initView() {
        mFolderAdapter = LFolderAdapter(contentView.folderRecycler.context)

        animationStyle = android.R.style.Animation
        setBackgroundDrawable(ColorDrawable(-0x70000000))

        contentView.folderRecycler.layoutManager = LinearLayoutManager(mActivity)
        contentView.folderRecycler.adapter = mFolderAdapter
    }

    override fun initListener() {
        contentView.folderRootView.setOnClickListener(this)
        mFolderAdapter.setOnItemClick { _, pos ->
            if (mDelegate != null && currentPosition != pos) {
                mDelegate.onSelectedFolder(pos)
            }
            currentPosition = pos
            dismiss()
        }
    }

    override fun initData() {
    }

    /**
     * 设置目录数据集合
     *
     * @param data
     */
    fun setData(data: ArrayList<LPhotoModel>) {
        mFolderAdapter.setData(data)
    }

    override fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val location = IntArray(2)
            mAnchorView.getLocationInWindow(location)
            val offsetY = location[1] + mAnchorView.height
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                height = getScreenHeight(mActivity) - offsetY
            }
            showAtLocation(mAnchorView, Gravity.NO_GRAVITY, 0, offsetY)
        } else {
            showAsDropDown(mAnchorView)
        }

        ViewCompat.animate(contentView.folderRecycler).translationY((-mWindowRootView.height).toFloat()).setDuration(0).start()
        ViewCompat.animate(contentView.folderRecycler).translationY(0f).setDuration(ANIM_DURATION.toLong()).start()
        ViewCompat.animate(contentView.folderRootView).alpha(0f).setDuration(0).start()
        ViewCompat.animate(contentView.folderRootView).alpha(1f).setDuration(ANIM_DURATION.toLong()).start()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.folderRootView) {
            dismiss()
        }
    }

    override fun dismiss() {
        ViewCompat.animate(contentView.folderRecycler).translationY((-mWindowRootView.height).toFloat()).setDuration(ANIM_DURATION.toLong()).start()
        ViewCompat.animate(contentView.folderRootView).alpha(1f).setDuration(0).start()
        ViewCompat.animate(contentView.folderRootView).alpha(0.8f).setDuration(ANIM_DURATION.toLong()).start()

        mDelegate?.executeDismissAnim()

        contentView.folderRecycler.postDelayed({ super@LPhotoFolderPopWin.dismiss() }, ANIM_DURATION.toLong())
    }


    interface Delegate {
        fun onSelectedFolder(position: Int)

        fun executeDismissAnim()
    }

    companion object {
        const val ANIM_DURATION = 300
    }
}