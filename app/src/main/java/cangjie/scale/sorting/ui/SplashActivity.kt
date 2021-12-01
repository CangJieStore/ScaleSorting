package cangjie.scale.sorting.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import cangjie.scale.sorting.R
import cangjie.scale.sorting.base.delayLaunch
import com.cangjie.frame.core.BaseActivity
import cangjie.scale.sorting.databinding.ActivitySplashBinding
import com.cangjie.frame.core.db.CangJie
import com.gyf.immersionbar.BarHide

import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.launch

/**
 * @author: guruohan
 * @date: 2021/9/9
 */
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun initActivity(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            delayLaunch(2000) {
                if (CangJie.getString("token", "").isEmpty()) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
                finish()
            }

        }
    }


    override fun layoutId(): Int = R.layout.activity_splash
    override fun initImmersionBar() {
        immersionBar {
            fullScreen(true)
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            statusBarDarkFont(true, 0.1f)
            init()
        }
    }
}