package cangjie.scale.sorting.adapter


import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutSubmitItemBinding
import cangjie.scale.sorting.entity.SubmitInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/16 14:18
 */
class SubmitAdapter :
    BaseQuickAdapter<SubmitInfo, BaseDataBindingHolder<LayoutSubmitItemBinding>>(R.layout.layout_submit_item) {
    override fun convert(holder: BaseDataBindingHolder<LayoutSubmitItemBinding>, item: SubmitInfo) {
        holder.dataBinding?.info = item
    }
}