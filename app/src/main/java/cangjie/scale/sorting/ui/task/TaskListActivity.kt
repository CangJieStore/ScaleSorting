package cangjie.scale.sorting.ui.task

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.ActivityTaskBinding
import cangjie.scale.sorting.entity.MessageEvent
import cangjie.scale.sorting.vm.TaskViewModel
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.tab.Title
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar
import org.greenrobot.eventbus.EventBus

/**
 * @author: guruohan
 * @date: 2021/10/24
 */
class TaskListActivity : BaseMvvmActivity<ActivityTaskBinding, TaskViewModel>() {

    private var orderId = ""

    override fun layoutId(): Int = R.layout.activity_task


    override fun initActivity(savedInstanceState: Bundle?) {
        orderId = intent.getStringExtra("id").toString()
        viewModel.getProjectByGoods(orderId, "0",0)
        val list = arrayListOf(
            Title("待领取", "待领取", "待领取"),
            Title("已领取", "已领取", "已领取")
        )
        mBinding.tabPurchase.setTitles(list)
        mBinding.tabPurchase.onSelectChangeListener = {
            mBinding.vpPurchase.currentItem = it
        }

        mBinding.vpPurchase.adapter = FragmentPagerAdapter(
            this, listOf(
                { TaskToGetFragment.newInstance(0, orderId) },
                { TaskReceivedFragment.newInstance(0, orderId) }
            )
        )
        mBinding.vpPurchase.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mBinding.tabPurchase.select(position)
            }
        })
        mBinding.rgPurchaseType.setOnCheckedChangeListener { _, childId ->
            if (childId == R.id.rb_goods) {
                viewModel.getProjectByGoods(orderId, "0",0)
                EventBus.getDefault().post(MessageEvent(5010,""))
            } else {
                viewModel.getProjectByGoods(orderId, "0",1)
                EventBus.getDefault().post(MessageEvent(5011,""))
            }
        }
    }

    override fun initVariableId(): Int = BR.taskModel

    override fun handleEvent(msg: MsgEvent) {
        super.handleEvent(msg)
        when (msg.code) {
            -1 -> {
                finish()
            }
        }
    }

    override fun subscribeModel(model: TaskViewModel) {
        super.subscribeModel(model)
        model.getTaskData().observe(this, {
            mBinding.info = it
            if (it.purchaser != null) {
                mBinding.tvUnpCount.text = "待分拣客户:" + it.purchaser_count
                mBinding.tvPCount.text = "已分拣客户:" + it.sorting_count
            } else if (it.goods != null) {
                mBinding.tvUnpCount.text = "待分拣商品:" + it.item_count
                mBinding.tvPCount.text = "已分拣商品:" + it.sorting_count
            }
        })
    }

    override fun initImmersionBar() {
        super.initImmersionBar()
        immersionBar {
            fullScreen(true)
            hideBar(BarHide.FLAG_HIDE_BAR)
            statusBarDarkFont(false)
            init()
        }
    }
}