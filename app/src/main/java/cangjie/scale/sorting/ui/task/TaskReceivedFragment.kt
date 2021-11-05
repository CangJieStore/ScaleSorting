package cangjie.scale.sorting.ui.task

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.FragmentReceiveItemBinding
import cangjie.scale.sorting.entity.MessageEvent
import cangjie.scale.sorting.ui.purchase.PurchaseActivity
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
class TaskReceivedFragment : BaseMvvmFragment<FragmentReceiveItemBinding, TaskViewModel>() {
    companion object {
        fun newInstance(type: Int, oId: String) = TaskReceivedFragment().apply {
            arguments = bundleOf("type" to type, "oId" to oId)
        }
    }

    private var orderId = ""
    private var pType = 0

    private val taskReceiveAdapter by lazy {
        TaskReceiveAdapter()
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

    override fun layoutId(): Int = R.layout.fragment_receive_item

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
                .addTo(mBinding!!.ryReceiveTarget)
            it.ryReceiveTarget.layoutManager = layoutManager
            it.ryReceiveTarget.adapter = taskReceiveAdapter
        }
        changeType(0)
        taskReceiveAdapter.setOnItemClickListener { _, _, position ->
            val dataItem = taskReceiveAdapter.data[position]
            val bundle = Bundle()
            bundle.putSerializable("item", dataItem)
            val intent = Intent(requireActivity(), PurchaseActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun changeType(type: Int) {
        viewModel.getProjectByGoods(orderId, "1", type)
    }

    override fun subscribeModel(model: TaskViewModel) {
        super.subscribeModel(model)
        model.getTaskData().observe(this, {
            if (it.purchaser != null) {
                taskReceiveAdapter.setList(it.purchaser)
            } else if (it.goods != null) {
                taskReceiveAdapter.setList(it.goods)
            }
        })
    }

    override fun initVariableId(): Int = BR.itemModel
}