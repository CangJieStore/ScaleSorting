package cangjie.scale.sorting.ui.purchase

import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutStockItemBinding
import cangjie.scale.sorting.entity.StockInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author: guruohan
 * @date: 2021/12/5
 */
class StockAdapter :
    BaseQuickAdapter<StockInfo, BaseDataBindingHolder<LayoutStockItemBinding>>(R.layout.layout_stock_item) {
    override fun convert(holder: BaseDataBindingHolder<LayoutStockItemBinding>, item: StockInfo) {
        holder.dataBinding?.let {
            it.tvBatchNo.text = item.trade_no
            it.tvSpec.text = if (item.spec.isEmpty()) "/" else item.spec
            it.tvStockUnit.text = item.unit
            it.tvStockQuantity.text = item.stock.toString()
            it.tvBuyDate.text = item.collect_date
            it.ivCalLoss.isChecked = item.isCurrent
        }
    }
}