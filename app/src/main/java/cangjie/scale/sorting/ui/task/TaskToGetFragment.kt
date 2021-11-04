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
import cangjie.scale.sorting.entity.MessageEvent
import cangjie.scale.sorting.vm.TaskViewModel
import com.cangjie.frame.core.BaseMvvmFragment
import com.fondesa.recyclerviewdivider.dividerBuilder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author: guruohan
 * @date: 2021/11/3
 */
class TaskToGetFragment : BaseMvvmFragment<FragmentTaskItemBinding, TaskViewModel>() {
    companion object {
        fun newInstance(type: Int, oId: String) = TaskToGetFragment().apply {
            arguments = bundleOf("type" to type, "oId" to oId)
        }
    }

    private var orderId = ""
    private var pType = 1
    override fun layoutId(): Int = R.layout.fragment_task_item
    private val taskAdapter by lazy {
        TaskAdapter()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.code == 5010) {
            pType = 0
            changeType(0)
        } else if (event.code == 5011) {
            pType = 1
            changeType(1)
        }
    }

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        orderId = arguments?.getString("oId") as String
        pType = arguments?.getInt("type") as Int
        val layoutManager =
            GridLayoutManager(requireActivity(), 28)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (pType == 0) {
                    4
                } else {
                    7
                }
            }
        }
        mBinding?.let {
            requireActivity().dividerBuilder()
                .color(Color.TRANSPARENT)
                .size(10, TypedValue.COMPLEX_UNIT_DIP)
                .showFirstDivider()
                .showSideDividers()
                .showLastDivider()
                .build()
                .addTo(mBinding!!.ryPurchaseTarget)
            it.ryPurchaseTarget.adapter = taskAdapter
            it.ryPurchaseTarget.layoutManager = layoutManager
        }
        changeType(0)
    }

    private fun changeType(type: Int) {
        viewModel.getProjectByGoods(orderId, "0", type)
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