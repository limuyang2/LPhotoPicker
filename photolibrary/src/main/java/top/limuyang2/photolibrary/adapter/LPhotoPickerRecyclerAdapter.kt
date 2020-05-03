package top.limuyang2.photolibrary.adapter

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.databinding.LPpItemPhotoPickerBinding
import top.limuyang2.photolibrary.model.LPhotoModel
import top.limuyang2.photolibrary.util.ImageEngineUtils
import top.limuyang2.photolibrary.widget.LPPSmoothCheckBox
import kotlin.math.ceil


/**
 * Date 2018/7/31
 *
 * @author limuyang
 */

typealias OnPhotoItemClick = (view: View, uri: Uri, pos: Int) -> Unit

typealias OnPhotoItemChildClick = (view: View, path: String, pos: Int) -> Unit

typealias OnPhotoItemLongClick = (view: View, path: String, pos: Int) -> Unit


internal class PhotoPickerRecyclerAdapter(private val maxSelectNum: Int,
                                          private val imgWidth: Int) : RecyclerView.Adapter<PhotoPickerRecyclerAdapter.ViewHolder>() {

    var onPhotoItemClick: OnPhotoItemClick? = null

    var onPhotoItemLongClick: OnPhotoItemLongClick? = null

    var onPhotoItemChildClick: OnPhotoItemChildClick? = null

    private val list: ArrayList<LPhotoModel> = arrayListOf()

    private lateinit var mContext: Context

    private val selectedSet = HashSet<Uri>(maxSelectNum)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mContext = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LPpItemPhotoPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, imgWidth).apply {
            itemView.setOnClickListener { v -> onPhotoItemClick?.invoke(v, list[adapterPosition].photoPath, layoutPosition) }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isEmpty()) return

        ImageEngineUtils.engine.load(mContext, holder.binding.imgView, list[position].photoPath, R.drawable.ic_l_pp_ic_holder_light, holder.binding.imgView.layoutParams.width, holder.binding.imgView.layoutParams.width)

        holder.binding.checkView.setChecked(selectedSet.contains(list[position].photoPath), false)
    }

    class ViewHolder(val binding: LPpItemPhotoPickerBinding, imgWidth: Int) : RecyclerView.ViewHolder(binding.root) {
        init {
            val lp = binding.imgView.layoutParams
            lp.width = imgWidth
            lp.height = imgWidth
            binding.imgView.layoutParams = lp
        }
    }


    fun setData(list: List<LPhotoModel>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun setChooseItem(uri: Uri, checkBox: LPPSmoothCheckBox) {
        if (selectedSet.contains(uri)) {
            selectedSet.remove(uri)
        } else {
            if (selectedSet.size >= maxSelectNum) {
                Toast.makeText(mContext, mContext.getString(R.string.l_pp_toast_photo_picker_max, maxSelectNum), Toast.LENGTH_SHORT).show()
                return
            }
            selectedSet.add(uri)
        }

        checkBox.setChecked(!checkBox.isChecked, true)
    }

    fun getSelectedItems(): ArrayList<Uri> = ArrayList<Uri>().apply { addAll(selectedSet) }

    fun getSelectedItemSize(): Int = selectedSet.size

    fun hasSelected(): Boolean = selectedSet.isNotEmpty()

    fun setSelectedItemsPath(pathList: List<Uri>?) {
        selectedSet.clear()
        pathList?.let {
            selectedSet.addAll(it)
        }
        notifyDataSetChanged()
    }
}


/**
 * 分割线 px
 * @author limuyang
 */
internal class LPPGridDivider(private val space: Int, private val columnsNumber: Int, private val bottomLayoutHeight: Int = 0) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let {
            val lp = view.layoutParams as GridLayoutManager.LayoutParams

            if (lp.spanIndex == 0) {
                // 第一个，左右两边都带间距
                outRect.left = space
                outRect.right = space
            } else {
                // 其他的，只有右边带间距
                outRect.right = space
            }

            //当前第几行, 因为从0行开始，所以要+1
            val nowLine = (parent.getChildAdapterPosition(view) / columnsNumber) + 1

            //总行数，从1开始
            val allLines = ceil(it.itemCount.toDouble() / columnsNumber).toInt()


            if (nowLine == 1) {
                outRect.top = space
            }

            //最后一行要加上底部工具栏的高度
            if (nowLine == allLines) {
                outRect.bottom = space + bottomLayoutHeight
            } else {
                outRect.bottom = space
            }

        }
    }
}