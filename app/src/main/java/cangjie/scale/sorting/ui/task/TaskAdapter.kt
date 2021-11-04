package cangjie.scale.sorting.ui.task

import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutTaskItemBinding
import cangjie.scale.sorting.entity.TaskGoodsItem
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author CangJie
 * @date 2021/11/4 14:15
 */
class TaskAdapter :
    BaseQuickAdapter<TaskGoodsItem, BaseDataBindingHolder<LayoutTaskItemBinding>>(R.layout.layout_task_item) {
    override fun convert(
        holder: BaseDataBindingHolder<LayoutTaskItemBinding>,
        item: TaskGoodsItem
    ) {
        holder.dataBinding?.let {
            if (item.purchaser_name != null) {

            } else {
                it.ivGoodsImg.load(item.picture)
                it.tvGoodsName.text = item.name
                it.tvCustomerCount.text="客户数："+item.purchaser_count
            }
        }
    }
}