package cangjie.scale.sorting.ui.purchase

import android.graphics.Color
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutLabelItemBinding
import cangjie.scale.sorting.entity.LabelInfo
import cangjie.scale.sorting.scale.FormatUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author: guruohan
 * @date: 2021/11/6
 */
class LabelAdapter :
    BaseQuickAdapter<LabelInfo, BaseDataBindingHolder<LayoutLabelItemBinding>>(R.layout.layout_label_item) {

    private var current = -1
    override fun convert(holder: BaseDataBindingHolder<LayoutLabelItemBinding>, item: LabelInfo) {
        holder.dataBinding?.let {
            holder.itemView.isSelected = current == holder.layoutPosition
            if (current == holder.layoutPosition) {
                it.tvBatch.setTextColor(Color.WHITE)
                it.tvNum.setTextColor(Color.WHITE)
                it.tvUnit.setTextColor(Color.WHITE)
            } else {
                it.tvBatch.setTextColor(Color.BLACK)
                it.tvNum.setTextColor(Color.BLACK)
                it.tvUnit.setTextColor(Color.BLACK)
            }
            it.tvBatch.text = "分拣批次：" + (getItemPosition(item) + 1)
            it.tvUnit.text = "配送单位：" + item.unit
            it.tvNum.text = "本批数量：" + FormatUtil.roundByScale(item.currentNum.toDouble(), 2)
        }
    }

    fun selectPos(pos: Int) {
        this.current = pos
        notifyDataSetChanged()
    }

    fun getSelect(): Int {
        return this.current
    }
}