package top.limuyang2.photolibrary.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.l_pp_item_photo_picker.view.*
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.model.LPhotoModel
import top.limuyang2.photolibrary.util.ImageEngineUtils
import top.limuyang2.photolibrary.widget.LPPSmoothCheckBox


/**
 * Date 2018/7/31
 *
 * @author limuyang
 */

typealias OnPhotoItemClick = (view: View, path: String, pos: Int) -> Unit

typealias OnPhotoItemChildClick = (view: View, path: String, pos: Int) -> Unit

typealias OnPhotoItemLongClick = (view: View, path: String, pos: Int) -> Unit


class PhotoPickerRecyclerAdapter(private val context: Context,
                                 private val maxSelectNum: Int,
                                 private val imgWidth: Int) : RecyclerView.Adapter<PhotoPickerRecyclerAdapter.ViewHolder>() {

    var onPhotoItemClick: OnPhotoItemClick? = null

    var onPhotoItemLongClick: OnPhotoItemLongClick? = null

    var onPhotoItemChildClick: OnPhotoItemChildClick? = null

    private val list: ArrayList<LPhotoModel.PhotoInfo> = arrayListOf()

    private val selectedSet = HashSet<String>(maxSelectNum)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.l_pp_item_photo_picker, parent, false)

        val imgParams = view.imgView.layoutParams
        imgParams.width = imgWidth
        imgParams.height = imgWidth
        view.imgView.layoutParams = imgParams

        val holder = ViewHolder(view)
        onPhotoItemClick?.let {
            holder.itemView.setOnClickListener { v -> it(v, list[holder.layoutPosition].photoPath, holder.layoutPosition) }
        }

        return holder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isEmpty()) return

        ImageEngineUtils.engine.load(context, holder.imgView, list[position].photoPath, R.drawable.ic_l_pp_ic_holder_light, holder.imgView.layoutParams.width, holder.imgView.layoutParams.width)

        holder.checkBox.setChecked(selectedSet.contains(list[position].photoPath), false)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView: ImageView = itemView.findViewById(R.id.imgView)
        val checkBox: LPPSmoothCheckBox = itemView.findViewById(R.id.checkView)
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
}


/**
 *
 * Date 2018/8/1
 * @author limuyang
 * 分割线 px
 */
class LPPGridDivider(private val space: Int, private val columnsNumber: Int, private val bottomLayoutHeight: Int = 0) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.let {
            parent.childCount

            //当前第几行, 因为从0行开始，所以要+1
            val nowLine = (parent.getChildAdapterPosition(view) / columnsNumber) + 1

            //总行数，从1开始
            val allLines = Math.ceil((parent.adapter?.itemCount
                                      ?: 0).toDouble() / columnsNumber).toInt()

            //最后一行要加上底部工具栏的高度
            if (nowLine == allLines) {
                it.bottom = space + bottomLayoutHeight
            } else {
                it.bottom = space
            }

            it.left = space
            it.right = space
            it.top = space
            //            it.bottom = space
        }
    }
}