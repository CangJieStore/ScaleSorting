package cangjie.scale.sorting.ui.purchase

import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutPurchseGoodsItemBinding
import cangjie.scale.sorting.databinding.LayoutPurchseGoodsSortedItemBinding
import cangjie.scale.sorting.entity.PurchaseInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author CangJie
 * @date 2021/11/5 17:40
 */
class PurchaseGoodsSortedAdapter :
    BaseQuickAdapter<PurchaseInfo, BaseDataBindingHolder<LayoutPurchseGoodsSortedItemBinding>>(R.layout.layout_purchse_goods_sorted_item) {
    private var handleAction: HandleAction? = null
    override fun convert(
        holder: BaseDataBindingHolder<LayoutPurchseGoodsSortedItemBinding>,
        item: PurchaseInfo
    ) {
        holder.dataBinding?.let {
            holder.itemView.isSelected = item.isCurrent
            it.tvOrder.text = (getItemPosition(item) + 1).toString()
            it.tvName.text = item.purchaser
            it.tvQuantity.text = item.quantity
            it.tvDeliveryUnit.text = item.unit
            it.tvSortBatch.text = item.deliver_quantity
            it.tvHandle.setOnClickListener {
                handleAction?.action(item.item_id)
            }
        }
    }

    fun setHandleAction(action: HandleAction) {
        this.handleAction = action
    }

    interface HandleAction {
        fun action(itemId: String)
    }
}