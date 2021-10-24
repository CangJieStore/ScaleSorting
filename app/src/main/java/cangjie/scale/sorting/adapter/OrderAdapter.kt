package cangjie.scale.sorting.adapter


import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutOrderItemBinding
import cangjie.scale.sorting.entity.OrderInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/13 18:15
 */
class OrderAdapter(type: Int) :
    BaseQuickAdapter<OrderInfo, BaseDataBindingHolder<LayoutOrderItemBinding>>(R.layout.layout_order_item) {


    private var actionTYpe = type

    override fun convert(holder: BaseDataBindingHolder<LayoutOrderItemBinding>, item: OrderInfo) {
        holder.dataBinding?.let {
            it.tvOrderNo.text = (getItemPosition(item) + 1).toString()
            it.type = actionTYpe
            it.info = item
        }
    }
}