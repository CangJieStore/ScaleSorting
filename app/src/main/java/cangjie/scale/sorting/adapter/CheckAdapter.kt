package cangjie.scale.sorting.adapter

import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutCheckItemBinding
import cangjie.scale.sorting.entity.GoodsInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/15 08:47
 */
class CheckAdapter :
    BaseQuickAdapter<GoodsInfo, BaseDataBindingHolder<LayoutCheckItemBinding>>(R.layout.layout_check_item) {

    private var selectPosition = 0

    fun checkPosition(position: Int) {
        selectPosition = position
        notifyDataSetChanged()
    }

    private var lessListener: IsCalLessListener? = null
    private var dismissItem: GoodsInfo? = null

    override fun convert(holder: BaseDataBindingHolder<LayoutCheckItemBinding>, item: GoodsInfo) {
        holder.dataBinding?.let {
            it.tvOrderNo.text = (getItemPosition(item) + 1).toString()
            it.info = item
            holder.itemView.isSelected = item.isRepair
            it.calType =
                if (item.unit.contains("斤") || item.unit.contains("公斤") || item.unit.contains(
                        "千克"
                    ) || item.unit.contains("克") || item.unit.contains("两")
                ) {
                    "计重"
                } else {
                    "计数"
                }
            if (item.receive_loss == "1") {
                it.cbCalLoss.isChecked = true
                it.cbCalLoss.isClickable = false
            } else {
                it.cbCalLoss.isEnabled = item.isRepair
            }
            it.cbCalLoss.setOnCheckedChangeListener { _, p1 ->
                if (p1) {
                    lessListener?.isLess(item)
                } else {
                    lessListener?.isLess(null)
                }
            }
            dismissItem?.let { dis ->
                kotlin.run {
                    if (dismissItem == item) {
                        it.cbCalLoss.isChecked = false
                    }
                }
            }
        }
    }

    interface IsCalLessListener {
        fun isLess(info: GoodsInfo?)
    }

    fun setCalLessListener(listener: IsCalLessListener) {
        this.lessListener = listener
    }

    fun setDismissItem(item: GoodsInfo?) {
        this.dismissItem = item
        notifyDataSetChanged()
    }
}