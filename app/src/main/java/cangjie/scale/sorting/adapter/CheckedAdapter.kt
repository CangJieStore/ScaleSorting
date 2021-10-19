package cangjie.scale.sorting.adapter


import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutCheckedItemBinding
import cangjie.scale.sorting.entity.GoodsInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/15 08:47
 */
class CheckedAdapter(action: Action) :
    BaseQuickAdapter<GoodsInfo, BaseDataBindingHolder<LayoutCheckedItemBinding>>(R.layout.layout_checked_item) {

    private val againAction: Action = action
    override fun convert(holder: BaseDataBindingHolder<LayoutCheckedItemBinding>, item: GoodsInfo) {
        holder.dataBinding?.let {
            it.tvOrderNo.text = (getItemPosition(item) + 1).toString()
            it.info = item
            it.submitAgain.setOnClickListener {
                againAction.action(item)
            }
        }
    }

    interface Action {
        fun action(item: GoodsInfo)
    }
}