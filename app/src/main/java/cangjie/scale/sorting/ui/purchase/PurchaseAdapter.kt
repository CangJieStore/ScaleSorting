package cangjie.scale.sorting.ui.purchase

import android.graphics.Color
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
            if (item.isCurrent) {
                it.tvOrder.setTextColor(Color.WHITE)
                it.tvName.setTextColor(Color.WHITE)
                it.tvQuantity.setTextColor(Color.WHITE)
                it.tvUnit.setTextColor(Color.WHITE)
                it.tvGoodsBatch.setTextColor(Color.WHITE)
            } else {
                it.tvOrder.setTextColor(Color.BLACK)
                it.tvName.setTextColor(Color.BLACK)
                it.tvQuantity.setTextColor(Color.BLACK)
                it.tvUnit.setTextColor(Color.BLACK)
                it.tvGoodsBatch.setTextColor(Color.BLACK)
            }
            it.tvOrder.text = (getItemPosition(item) + 1).toString()
            it.tvName.text = item.name
            it.tvUnit.text = item.unit
            it.tvQuantity.text = item.quantity
            it.tvGoodsBatch.text = item.trade_no
        }
    }
}