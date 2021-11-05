package cangjie.scale.sorting.ui.purchase

import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutPurchseItemBinding
import cangjie.scale.sorting.entity.PurchaseInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author CangJie
 * @date 2021/11/5 17:40
 */
class PurchaseAdapter :
    BaseQuickAdapter<PurchaseInfo, BaseDataBindingHolder<LayoutPurchseItemBinding>>(R.layout.layout_purchse_item) {
    override fun convert(
        holder: BaseDataBindingHolder<LayoutPurchseItemBinding>,
        item: PurchaseInfo
    ) {
        holder.dataBinding?.let {
            holder.itemView.isSelected = item.isCurrent
            it.tvOrder.text = (getItemPosition(item) + 1).toString()
            if (item.picture != null) {//按客户分拣

            } else {
                it.tvName.text = item.purchaser
                it.tvQuantity.text = item.quantity
                it.tvUnit.text = item.unit
                it.tvExtra.text = "补货"
                it.tvPurchaseBatch.text = "0"
            }
        }
    }
}