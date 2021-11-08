package cangjie.scale.sorting.ui.task

import android.view.View
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutReceiveItemBinding
import cangjie.scale.sorting.databinding.LayoutTaskItemBinding
import cangjie.scale.sorting.entity.TaskGoodsItem
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author CangJie
 * @date 2021/11/4 14:15
 */
class TaskReceiveAdapter :
    BaseQuickAdapter<TaskGoodsItem, BaseDataBindingHolder<LayoutReceiveItemBinding>>(R.layout.layout_receive_item) {
    override fun convert(
        holder: BaseDataBindingHolder<LayoutReceiveItemBinding>,
        item: TaskGoodsItem
    ) {
        holder.dataBinding?.let {
            if (item.purchaser_name != null) {
                it.llGoods.visibility = View.GONE
                it.cardCustomer.visibility = View.VISIBLE
                it.tvCustomer.text = item.purchaser_name
                it.tvGoodsCount.text = "领取人:" + item.purchaser_name
            } else {
                it.llGoods.visibility = View.VISIBLE
                it.cardCustomer.visibility = View.GONE
                it.ivGoodsImg.load(item.picture)
                it.tvGoodsName.text = item.name
                it.tvCustomerCount.text = "领取人：" + item.purchaser_name
            }
        }
    }
}