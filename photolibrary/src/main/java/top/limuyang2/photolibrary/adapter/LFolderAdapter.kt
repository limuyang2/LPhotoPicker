package top.limuyang2.photolibrary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.model.LPhotoModel
import top.limuyang2.photolibrary.util.ImageEngineUtils
import java.util.*

/**
 *
 * Date 2018/8/1
 * @author limuyang
 * popWindow适配器
 */
class LFolderAdapter(private val context: Context) : RecyclerView.Adapter<LFolderAdapter.ViewHolder>() {

    private var onPhotoItemClick: OnFolderItemClick? = null

    private val list: ArrayList<LPhotoModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.l_pp_item_photo_folder, parent, false)
        val holder = ViewHolder(view)
        onPhotoItemClick?.let {
            holder.itemView.setOnClickListener { v ->
                it(v, holder.layoutPosition)
            }
        }
        return holder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isEmpty()) return
        holder.folderCount.text = list[position].photoInfoList.size.toString()
        holder.folderName.text = list[position].name

        val path = if (list[position].photoInfoList.isNotEmpty()) {
            list[position].photoInfoList[0].photoPath
        } else ""

        ImageEngineUtils.engine.load(context, holder.imgView, path, R.drawable.ic_l_pp_ic_holder_light, holder.imgView.layoutParams.width, holder.imgView.layoutParams.width)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView: ImageView = itemView.findViewById(R.id.folder_photo_iv)
        val folderName: TextView = itemView.findViewById(R.id.folder_name)
        val folderCount: TextView = itemView.findViewById(R.id.folder_count)
    }

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
