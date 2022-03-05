package cangjie.scale.sorting.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cangjie.scale.sorting.R
import cangjie.scale.sorting.adapter.DetailAdapter
import cangjie.scale.sorting.databinding.ActivityCheckedDetailBinding
import cangjie.scale.sorting.entity.GoodsTaskInfo
import cangjie.scale.sorting.entity.OrderInfo
import cangjie.scale.sorting.ui.purchase.PurchaseCustomerActivity
import cangjie.scale.sorting.ui.purchase.PurchaseGoodsActivity
import cangjie.scale.sorting.ui.task.TaskReceiveAdapter
import cangjie.scale.sorting.vm.ScaleViewModel
import cangjie.scale.sorting.vm.TaskViewModel
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.gyf.immersionbar.BarHide

import com.gyf.immersionbar.ktx.immersionBar

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/14 15:38
 */
class CheckedDetailActivity : BaseMvvmActivity<ActivityCheckedDetailBinding, TaskViewModel>() {

    private var currentOrder: OrderInfo? = null
    private var orderId = ""
    private val taskReceiveAdapter by lazy {
        TaskReceiveAdapter()
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.info = GoodsTaskInfo("******", "****-**-**", 0, 0, 0, null, null)
        orderId = intent.getStringExtra("id").toString()
        viewModel.getProjectByGoods(orderId, "1", 0)
        val layoutManager = GridLayoutManager(this, 28)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 4
            }
        }
        dividerBuilder()
            .color(Color.TRANSPARENT)
            .size(10, TypedValue.COMPLEX_UNIT_DIP)
            .showFirstDivider()
            .showSideDividers()
            .showLastDivider()
            .build()
            .addTo(mBinding.ryReceiveTarget)
        mBinding.ryReceiveTarget.layoutManager = layoutManager
        mBinding.ryReceiveTarget.adapter = taskReceiveAdapter
        taskReceiveAdapter.setOnItemClickListener { _, _, position ->
            val dataItem = taskReceiveAdapter.data[position]
            if (dataItem.staff_own == 1) {
                val bundle = Bundle()
                bundle.putSerializable("item", dataItem)
                bundle.putInt("from", 1)
                if (dataItem.name != null) {
                    val intent =
                        Intent(this@CheckedDetailActivity, PurchaseGoodsActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                } else {
                    val intent =
                        Intent(this@CheckedDetailActivity, PurchaseCustomerActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }
        }
    }

    override fun initVariableId(): Int = cangjie.scale.sorting.BR.detailModel

    override fun layoutId(): Int = R.layout.activity_checked_detail
    override fun initImmersionBar() {
        immersionBar {
            fullScreen(true)
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            statusBarDarkFont(false)
            init()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            init()
        }
    }

    override fun subscribeModel(model: TaskViewModel) {
        super.subscribeModel(model)
        model.getTaskData().observe(this, {
            mBinding.info = it
            if (it.purchaser != null) {
                mBinding.tvPCount.text = "已分拣客户:" + it.sorting_count
            } else if (it.goods != null) {
                taskReceiveAdapter.setList(it.goods)
                mBinding.tvPCount.text = "已分拣商品:" + it.sorting_count
            }
        })
    }

    override fun handleEvent(msg: MsgEvent) {
        super.handleEvent(msg)
        if (msg.code == 3) {
            finish()
        } else if (msg.code == 300) {
//            val intent = Intent(this, CheckActivity::class.java)
//            intent.putExtra("id", currentOrder!!.trade_no)
//            startActivity(intent)
        }
    }
}