package cangjie.scale.sorting.ui.purchase

import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutPurchseGoodsItemBinding
import cangjie.scale.sorting.entity.PurchaseInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author CangJie
 * @date 2021/11/5 17:40
 */
class PurchaseGoodsAdapter :
    BaseQuickAdapter<PurchaseInfo, BaseDataBindingHolder<LayoutPurchseGoodsItemBinding>>(R.layout.layout_purchse_goods_item) {
    override fun convert(
        holder: BaseDataBindingHolder<LayoutPurchseGoodsItemBinding>,
        item: PurchaseInfo
    ) {
        holder.dataBinding?.let {
            holder.itemView.isSelected = item.isCurrent
            it.tvOrder.text = (getItemPosition(item) + 1).toString()
            it.tvName.text = item.purchaser
            it.tvQuantity.text = item.quantity
            it.tvDeliveryUnit.text = item.unit
            it.tvGoodsOrderNo.text = item.trade_no
        }
    }
}