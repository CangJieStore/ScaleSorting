package cangjie.scale.sorting.ui.task

import android.graphics.Color
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
        //是登录账户领取，未分拣完红色，分拣完绿色，非登录账户领取灰色不可点击
        holder.dataBinding?.let {
            if (item.purchaser_name != null) {
                it.llGoods.visibility = View.GONE
                it.cardCustomer.visibility = View.VISIBLE
                it.tvCustomer.text = item.purchaser_name
                it.tvGoodsCount.text = "领取人:" + item.staff_name
            } else {
                //not self
                if (item.staff_own != 1) {
                    it.mask.visibility = View.VISIBLE
                    it.tvGoodsName.setTextColor(Color.parseColor("#dddddd"))
                    it.tvCustomerCount.setTextColor(Color.parseColor("#dddddd"))
                    it.tvCustomerCount.text = "领取人：" + item.staff_name
                } else {
                    it.mask.visibility = View.GONE
                    if (item.quantity == item.sorting_quantity) {
                        it.tvGoodsName.setTextColor(Color.parseColor("#52A645"))
                        it.tvCustomerCount.setTextColor(Color.parseColor("#52A645"))
                        it.tvCustomerCount.text = "已完成分拣"
                    } else {
                        it.tvGoodsName.setTextColor(Color.parseColor("#F15252"))
                        it.tvCustomerCount.setTextColor(Color.parseColor("#F15252"))
                        if (item.quantity != null && item.sorting_quantity != null) {
                            it.tvCustomerCount.text =
                                "剩余数量:" + (item.quantity.toFloat() - item.sorting_quantity.toFloat())
                        }
                    }
                }
                it.llGoods.visibility = View.VISIBLE
                it.cardCustomer.visibility = View.GONE
                it.ivGoodsImg.load(item.picture)
                it.tvGoodsName.text = item.name
            }
        }
    }
}