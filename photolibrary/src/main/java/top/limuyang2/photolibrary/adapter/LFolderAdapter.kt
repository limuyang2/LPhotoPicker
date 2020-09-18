package top.limuyang2.photolibrary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.limuyang2.photolibrary.R
import top.limuyang2.photolibrary.databinding.LPpItemPhotoFolderBinding
import top.limuyang2.photolibrary.model.LFolderModel
import top.limuyang2.photolibrary.util.ImageEngineUtils
import java.util.*

/**
 *
 * Date 2018/8/1
 * @author limuyang
 *
 * popWindow适配器
 */
internal class LFolderAdapter : RecyclerView.Adapter<LFolderAdapter.ViewHolder>() {

    private var onPhotoItemClick: ((view: View, pos: Int, model: LFolderModel) -> Unit)? = null

    private val list: ArrayList<LFolderModel> = arrayListOf()

    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val biding = LPpItemPhotoFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(biding).apply {
            onPhotoItemClick?.let {
                itemView.setOnClickListener { v ->
                    it(v, layoutPosition, list[layoutPosition])
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isEmpty()) return

        val data = list[position]
        holder.binding.folderCount.text = data.count.toString()
        holder.binding.folderName.text = data.bucketName

        val resize = holder.binding.folderPhotoIv.layoutParams.width
        ImageEngineUtils.engine.load(context, holder.binding.folderPhotoIv, data.previewImgPath, data.imageType, R.drawable.ic_l_pp_ic_holder_light, resize, resize)

    }

    class ViewHolder(val binding: LPpItemPhotoFolderBinding) : RecyclerView.ViewHolder(binding.root)

    fun setData(list: List<LFolderModel>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnItemClick(onPhotoItemClick: (view: View, pos: Int, model: LFolderModel) -> Unit) {
        this.onPhotoItemClick = onPhotoItemClick
    }
}
