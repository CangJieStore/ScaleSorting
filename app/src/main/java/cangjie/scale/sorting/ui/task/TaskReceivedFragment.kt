package cangjie.scale.sorting.ui.task

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.FragmentTaskItemBinding
import cangjie.scale.sorting.vm.TaskViewModel
import com.cangjie.frame.core.BaseMvvmFragment
import com.fondesa.recyclerviewdivider.dividerBuilder

/**
 * @author: guruohan
 * @date: 2021/11/3
 */
class TaskReceivedFragment : BaseMvvmFragment<FragmentTaskItemBinding, TaskViewModel>() {
    companion object {
        fun newInstance(type: Int, oId: String) = TaskReceivedFragment().apply {
            arguments = bundleOf("type" to type, "oId" to oId)
        }
    }

    private var orderId = ""
    private var pType = 0

    private val taskAdapter by lazy {
        TaskAdapter()
    }

    override fun layoutId(): Int = R.layout.fragment_task_item

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        orderId = arguments?.getString("oId") as String
        pType = arguments?.getInt("type") as Int
        val layoutManager =
            GridLayoutManager(requireActivity(), 6)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (pType == 0) {
                    6
                } else {
                    4
                }
            }
        }
        mBinding?.let {
            requireActivity().dividerBuilder()
                .color(Color.TRANSPARENT)
                .size(5, TypedValue.COMPLEX_UNIT_DIP)
                .showLastDivider()
                .build()
                .addTo(mBinding!!.ryPurchaseTarget)
            it.ryPurchaseTarget.layoutManager = layoutManager
            it.ryPurchaseTarget.adapter = taskAdapter
        }
        changeType()
    }

    private fun changeType() {
        viewModel.getProjectByGoods(orderId, "1",0)
    }

    override fun subscribeModel(model: TaskViewModel) {
        super.subscribeModel(model)
        model.getTaskData().observe(this, {
            if (it.purchaser != null) {
                taskAdapter.setList(it.purchaser)
            } else if (it.goods != null) {
                taskAdapter.setList(it.goods)
            }
        })
    }

    override fun initVariableId(): Int = BR.itemModel
}