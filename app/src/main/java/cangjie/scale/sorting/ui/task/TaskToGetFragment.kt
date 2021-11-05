package cangjie.scale.sorting.ui.task

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.FragmentTaskItemBinding
import cangjie.scale.sorting.entity.GoodsTaskInfo
import cangjie.scale.sorting.entity.MessageEvent
import cangjie.scale.sorting.entity.TaskGoodsItem
import cangjie.scale.sorting.vm.TaskViewModel
import com.cangjie.frame.core.BaseMvvmFragment
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.show
import com.cangjie.frame.kit.tab.dp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener
import com.kongzue.dialogx.util.TextInfo
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
            changeType(pType)
        } else if (event.code == 5011) {
            pType = 1
            changeType(pType)
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
        taskAdapter.setOnItemClickListener { _, _, position ->
            val dataItem = taskAdapter.data[position]
            showGetTips(dataItem)
        }
        changeType(0)
    }

    private fun showGetTips(data: TaskGoodsItem) {
        DialogX.implIMPLMode = DialogX.IMPL_MODE.DIALOG_FRAGMENT
        val title = if (data.name != null) {
            data.name + "商品分拣任务?"
        } else {
            data.purchaser_name + "客户分拣任务?"
        }
        val messageDialog =
            MessageDialog("领取分拣任务", "确定领取$title", "确定", "取消")
                .setButtonOrientation(LinearLayout.HORIZONTAL)
                .setOkButtonClickListener { _, _ ->
                    if (data.name != null) {
                        viewModel.receiveTask(data.id, "", 0)
                    } else {
                        viewModel.receiveTask(data.sorting_id, data.purchaser_id, 1)
                    }
                    false
                }
        messageDialog.okTextInfo = TextInfo().setFontColor(Color.parseColor("#EB5545"))
        messageDialog.cancelTextInfo = TextInfo().setFontColor(Color.parseColor("#999999"))
        messageDialog.maxWidth = 400.dp
        messageDialog.show()
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

    override fun handleEvent(msg: MsgEvent) {
        super.handleEvent(msg)
        if (msg.code == 100) {
            changeType(pType)
        }
    }

    override fun toast(notice: String?) {
        super.toast(notice)
        show(requireActivity(), 2000, notice!!)
    }

    override fun initVariableId(): Int = BR.itemModel
}