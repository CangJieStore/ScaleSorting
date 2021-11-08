package cangjie.scale.sorting.ui.purchase

import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.LayoutLabelItemBinding
import cangjie.scale.sorting.entity.LabelInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author: guruohan
 * @date: 2021/11/6
 */
class LabelAdapter :
    BaseQuickAdapter<LabelInfo, BaseDataBindingHolder<LayoutLabelItemBinding>>(R.layout.layout_label_item) {
    override fun convert(holder: BaseDataBindingHolder<LayoutLabelItemBinding>, item: LabelInfo) {
        holder.dataBinding?.let {
            it.tvBatch.text = "分拣批次：" + (getItemPosition(item) + 1)
            it.tvUnit.text = "配送单位：" + item.unit
            it.tvNum.text = "本批数量：" + item.currentNum
        }
    }
}