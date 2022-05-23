package cangjie.scale.sorting.ui

import android.content.Intent
import android.os.Bundle
import cangjie.scale.sorting.R
import cangjie.scale.sorting.base.getLocalVersionName
import cangjie.scale.sorting.databinding.ActivityLoginBinding
import cangjie.scale.sorting.scale.SerialPortManager
import cangjie.scale.sorting.vm.ScaleViewModel
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.show
import com.gyf.immersionbar.BarHide

import com.gyf.immersionbar.ktx.immersionBar
import kotlin.system.exitProcess

/**
 * @author: guruohan
 * @date: 2021/9/9
 */
class LoginActivity : BaseMvvmActivity<ActivityLoginBinding, ScaleViewModel>() {

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.tvExit.setOnClickListener {
            exitProcess(0)
        }
        mBinding.tvCurrentVersion.text = "当前版本:${getLocalVersionName(this)}"
    }

    override fun initVariableId(): Int = cangjie.scale.sorting.BR.loginModel

    override fun layoutId(): Int = R.layout.activity_login
    override fun initImmersionBar() {
        immersionBar {
            fullScreen(true)
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            statusBarDarkFont(true, 0.2f)
            init()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        SerialPortManager.instance().close()
        exitProcess(0)
    }

    override fun toast(notice: String?) {
        super.toast(notice)
        show(this, 2000, notice!!)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            init()
        }
    }

    override fun handleEvent(msg: MsgEvent) {
        super.handleEvent(msg)
        if (msg.code == 0) {
            startActivity(Intent(this, MainActivity::class.java))
            this@LoginActivity.finish()
        }
    }

}