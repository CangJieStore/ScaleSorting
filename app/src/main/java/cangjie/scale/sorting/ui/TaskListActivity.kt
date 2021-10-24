package cangjie.scale.sorting.ui

import android.os.Bundle
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.ActivityTaskBinding
import cangjie.scale.sorting.vm.TaskViewModel
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.gyf.immersionbar.ktx.hideStatusBar
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
            statusBarDarkFont(false)
            hideStatusBar()
            init()
        }
    }
}