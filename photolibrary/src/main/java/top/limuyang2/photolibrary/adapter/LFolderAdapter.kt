package top.limuyang2.photolibrary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.databinding.LPpItemPhotoFolderBinding
import top.limuyang2.photolibrary.model.LPhotoModel
import top.limuyang2.photolibrary.util.ImageEngineUtils
import java.util.*

/**
 *
 * Date 2018/8/1
 * @author limuyang
 * popWindow适配器
 */
internal class LFolderAdapter(private val context: Context) : RecyclerView.Adapter<LFolderAdapter.ViewHolder>() {

    private var onPhotoItemClick: OnFolderItemClick? = null

    private val list: ArrayList<LPhotoModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val biding = LPpItemPhotoFolderBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(biding).apply {
            onPhotoItemClick?.let {
                itemView.setOnClickListener { v ->
                    it(v, layoutPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isEmpty()) return
        holder.binding.folderCount.text = list[position].photoInfoList.size.toString()
        holder.binding.folderName.text = list[position].name

        val path = if (list[position].photoInfoList.isNotEmpty()) {
            list[position].photoInfoList[0].photoPath
        } else ""

        ImageEngineUtils.engine.load(context, holder.binding.folderPhotoIv, path, R.drawable.ic_l_pp_ic_holder_light, holder.binding.folderPhotoIv.layoutParams.width, holder.binding.folderPhotoIv.layoutParams.width)

    }

    class ViewHolder(val binding: LPpItemPhotoFolderBinding) : RecyclerView.ViewHolder(binding.root)

    fun setData(list: List<LPhotoModel>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnItemClick(onPhotoItemClick: OnFolderItemClick?) {
        this.onPhotoItemClick = onPhotoItemClick
    }
}

typealias OnFolderItemClick = (view: View, pos: Int) -> Unit
