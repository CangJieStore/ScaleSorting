package cangjie.scale.sorting.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import cangjie.scale.sorting.R
import cangjie.scale.sorting.base.BaseFragmentPagerAdapter
import cangjie.scale.sorting.base.DateUtil
import cangjie.scale.sorting.base.workOnIO
import cangjie.scale.sorting.databinding.ActivityMainBinding
import cangjie.scale.sorting.entity.MessageEvent
import cangjie.scale.sorting.entity.Update
import cangjie.scale.sorting.service.InitService
import cangjie.scale.sorting.vm.ScaleViewModel
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.show
import com.cangjie.frame.kit.update.model.DownloadInfo
import com.cangjie.frame.kit.update.model.TypeConfig
import com.cangjie.frame.kit.update.utils.AppUpdateUtils
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.launch

import org.greenrobot.eventbus.EventBus
import java.net.URL
import java.util.*

class MainActivity : BaseMvvmActivity<ActivityMainBinding, ScaleViewModel>() {

    override fun onStart() {
        super.onStart()
        startService(Intent(this, InitService::class.java))
    }

    private val title = arrayListOf("未验收", "已验收")
    private val mAdapter by lazy {
        BaseFragmentPagerAdapter(
            supportFragmentManager, arrayListOf(
                UncheckFragment(),
                CheckedFragment()
            ), title
        )
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        viewModel.loadUpdate()
        mBinding.vpOrders.adapter = mAdapter
        mBinding.tabOrders.setViewPager(mBinding.vpOrders)
        mBinding.tabOrders.currentTab = 0
        netTime()
    }

    private fun netTime() {
        lifecycleScope.launch {
            workOnIO {
                val infoUrl = URL("http://www.baidu.com")
                val connection = infoUrl.openConnection()
                connection.connect()
                val ld = connection.date
                val now = DateUtil.dateToString(Date(ld), DateUtil.DATE_FORMAT)
                viewModel.chooseDateFiled.set(now)
                EventBus.getDefault().post(MessageEvent(0, now))
            }
        }
    }

    override fun initVariableId(): Int = cangjie.scale.sorting.BR.mainModel

    override fun layoutId(): Int = R.layout.activity_main

    override fun initImmersionBar() {
        super.initImmersionBar()
        immersionBar {
            fullScreen(true)
            statusBarDarkFont(false)
            init()
        }
    }

    override fun toast(notice: String?) {
        super.toast(notice)
        show(this, 2000, notice!!)
    }

    override fun handleEvent(msg: MsgEvent) {
        super.handleEvent(msg)
        when (msg.code) {
            1 -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            2 -> {
                val bundle = Bundle()
                bundle.putString("date", viewModel.chooseDateFiled.get())
                DateDialogFragment.newInstance(bundle)
                    .setAction(object : DateDialogFragment.SubmitAction {
                        override fun submit(date: String) {
                            viewModel.chooseDateFiled.set(date)
                            EventBus.getDefault()
                                .post(MessageEvent(0, date))
                        }
                    }).show(supportFragmentManager, "date")
            }
            6 -> {
                EventBus.getDefault()
                    .post(MessageEvent(0, viewModel.chooseDateFiled.get().toString()))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(MessageEvent(0, viewModel.chooseDateFiled.get().toString()))
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, InitService::class.java))
    }

    private fun update(update: Update) {
        AppUpdateUtils.getInstance().clearAllData()
        val listModel = cangjie.scale.sorting.entity.ListModel()
        listModel.isForceUpdate = update.forceUpdate
        listModel.uiTypeValue = TypeConfig.UI_THEME_L
        listModel.isCheckFileMD5 = true
        listModel.sourceTypeVaule = TypeConfig.DATA_SOURCE_TYPE_MODEL
        val info =
            DownloadInfo().setApkUrl(update.apkUrl)
                .setFileSize(31338250)
                .setProdVersionCode(update.versionCode)
                .setProdVersionName(update.versionName)
                .setMd5Check("68919BF998C29DA3F5BD2C0346281AC0")
                .setForceUpdateFlag(if (listModel.isForceUpdate) 1 else 0)
                .setUpdateLog(update.updateLog)
        AppUpdateUtils.getInstance().updateConfig.uiThemeType = listModel.uiTypeValue
        AppUpdateUtils.getInstance().updateConfig.isNeedFileMD5Check = false
        AppUpdateUtils.getInstance().updateConfig.dataSourceType = listModel.sourceTypeVaule
        AppUpdateUtils.getInstance().updateConfig.isAutoDownloadBackground =
            listModel.isAutoUpdateBackground
        AppUpdateUtils.getInstance().updateConfig.isShowNotification =
            !listModel.isAutoUpdateBackground
        AppUpdateUtils.getInstance().checkUpdate(info)
    }

    override fun subscribeModel(model: ScaleViewModel) {
        super.subscribeModel(model)
        model.getUpdate().observe(this, androidx.lifecycle.Observer {
            it?.let {
                update(it)
            }
        })
    }

}