package top.limuyang2.photolibrary.adapter

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.model.LPhotoModel
import top.limuyang2.photolibrary.util.ImageEngineUtils
import top.limuyang2.photolibrary.util.dp2px
import top.limuyang2.photolibrary.widget.LPPSmoothCheckBox


/**
 * Date 2018/7/31
 *
 * @author limuyang
 */

private typealias OnPhotoItemClick = (view: View, path: String, pos: Int) -> Unit

class PhotoPickerRecyclerAdapter(private val context: Context,
                                 private val imgWidth: Int,
                                 private val maxSelectNum: Int) : RecyclerView.Adapter<PhotoPickerRecyclerAdapter.ViewHolder>() {

    var onPhotoItemClick: OnPhotoItemClick? = null

    private val list: ArrayList<LPhotoModel.PhotoInfo> = arrayListOf()

    private val selectedSet = HashSet<String>(maxSelectNum)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = createItemView(parent.context, imgWidth)

        val holder = ViewHolder(view)
        onPhotoItemClick?.let {
            holder.itemView.setOnClickListener { v -> it(v, list[holder.layoutPosition].photoPath, holder.layoutPosition) }
        }
        return holder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isEmpty()) return

        val photoPatch = list[position].photoPath
        val size = holder.imgView.layoutParams.width
        ImageEngineUtils.engine.load(context, holder.imgView, photoPatch, R.drawable.ic_l_pp_ic_holder_light, size, size)

        holder.checkBox.setChecked(selectedSet.contains(photoPatch), false)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView: ImageView = itemView.findViewById(IMAGE_VIEW_ID)
        val checkBox: LPPSmoothCheckBox = itemView.findViewById(CHECK_BOX_ID)
    }

    fun setData(list: List<LPhotoModel.PhotoInfo>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun setChooseItem(path: String, checkBox: LPPSmoothCheckBox) {
        if (selectedSet.contains(path)) {
            selectedSet.remove(path)
        } else {
            if (selectedSet.size >= maxSelectNum) {
                Toast.makeText(context, context.getString(R.string.l_pp_toast_photo_picker_max, maxSelectNum), Toast.LENGTH_SHORT).show()
                return
            }
            selectedSet.add(path)
        }

        checkBox.setChecked(!checkBox.isChecked, true)
    }

    fun getSelectedItems(): ArrayList<String> = ArrayList<String>().apply { addAll(selectedSet) }

    fun getSelectedItemSize(): Int = selectedSet.size

    fun hasSelected(): Boolean = selectedSet.isNotEmpty()

    fun setSelectedItemsPath(pathList: List<String>?) {
        selectedSet.clear()
        pathList?.let {
            selectedSet.addAll(it)
        }
        notifyDataSetChanged()
    }

    /**
     * 代码创建item布局
     * @param context Context
     * @param imgWidth Int
     * @return View
     */
    private fun createItemView(context: Context, imgWidth: Int): View {
        val imageView = ImageView(context).apply {
            id = IMAGE_VIEW_ID
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, imgWidth)
        }

        val checkBox = LPPSmoothCheckBox(context).apply {
            id = CHECK_BOX_ID
            val size = context.dp2px(25)
            layoutParams = FrameLayout.LayoutParams(size, size).apply {
                gravity = Gravity.END or Gravity.TOP
                setMargins(0, context.dp2px(8), context.dp2px(8), 0)
            }
        }

        return FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            addView(imageView)
            addView(checkBox)
        }
    }

    companion object {
        private const val IMAGE_VIEW_ID = 110
        const val CHECK_BOX_ID = 111
    }
}

/**
 *
 * Date 2018/8/1
 * @author limuyang
 * 分割线 px
 */
internal class LPPGridDivider(private val space: Int, private val columnsNumber: Int, private val bottomLayoutHeight: Int = 0) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView, state: RecyclerView.State?) {
        outRect?.let {

            //当前第几行, 因为从0行开始，所以要+1
            val nowLine = (parent.getChildAdapterPosition(view) / columnsNumber) + 1

            //总行数，从1开始
            val allLines = Math.ceil(parent.adapter.itemCount.toDouble() / columnsNumber).toInt()

            //最后一行要加上底部工具栏的高度
            it.bottom = if (nowLine == allLines) {
                space + bottomLayoutHeight
            } else {
                space
            }

            it.left = space
            it.right = space
            it.top = space
        }
    }
}