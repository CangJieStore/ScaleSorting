package cangjie.scale.sorting.adapter


import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutImgItemBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/15 16:27
 */
class ImageAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<LayoutImgItemBinding>>(R.layout.layout_img_item) {
    override fun convert(holder: BaseDataBindingHolder<LayoutImgItemBinding>, item: String) {
        holder.dataBinding?.let {
            it.path = item
            it.pos = (holder.adapterPosition+1).toString()
        }
    }
}