package cangjie.scale.sorting.ui.task

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.ActivityTaskBinding
import cangjie.scale.sorting.vm.TaskViewModel
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.tab.Title
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar

/**
 * @author: guruohan
 * @date: 2021/10/24
 */
class TaskListActivity : BaseMvvmActivity<ActivityTaskBinding, TaskViewModel>() {

    private var orderId = ""

    override fun layoutId(): Int = R.layout.activity_task

    override fun initActivity(savedInstanceState: Bundle?) {
        orderId = intent.getStringExtra("id").toString()
        viewModel.getProjectByGoods(orderId, "0")
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
                { TaskItemFragment.newInstance(1) },
                { TaskItemFragment.newInstance(2) }
            )
        )

        mBinding.vpPurchase.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mBinding.tabPurchase.select(position)
            }
        })
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