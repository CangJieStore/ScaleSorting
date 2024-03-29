package cangjie.scale.sorting.base

import android.app.Application
import androidx.lifecycle.viewModelScope
import cangjie.scale.sorting.R
import cangjie.scale.sorting.base.http.HttpManager
import cangjie.scale.sorting.scale.DeviceFilter
import cangjie.scale.sorting.scale.SerialPortManager
import com.blankj.utilcode.util.ViewUtils
import com.cangjie.frame.core.db.CangJie
import com.cangjie.frame.kit.OkHttp3Connection
import com.cangjie.frame.kit.lib.ToastUtils
import com.cangjie.frame.kit.lib.style.BlackToastStyle
import com.cangjie.frame.kit.update.model.TypeConfig
import com.cangjie.frame.kit.update.model.UpdateConfig
import com.cangjie.frame.kit.update.utils.AppUpdateUtils
import com.cangjie.frame.kit.update.utils.SSLUtils
import com.kongzue.dialogx.DialogX
import kotlinx.coroutines.*

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * @author CangJie
 * @date 2021/9/9 16:02
 */
class ScaleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setApplication(this)
        CangJie.init(this)
        CangJie.config {
            multiProcess = true
            encryptKey = "encryptKey"
        }
        ToastUtils.init(
            this,
            BlackToastStyle()
        )
        initWeight()
        HttpManager.init(this)
        update()
        DialogX.init(this)
    }

    /** 设备过滤器 */
    private val deviceFilter = object : DeviceFilter {
        override fun allow(device: String): Boolean {
            // 不允许打开usb的串口
            return !device.contains("usb", true)
        }
    }

    private fun initWeight() {
        try {
            GlobalScope.launch {
                val opened = withContext(Dispatchers.IO) {
                    SerialPortManager.instance().open(9600, deviceFilter)
                }
                if (!opened) {
                    ToastUtils.show("初始化称重主板错误！")
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ViewUtils.runOnUiThread {
                ToastUtils.show("初始化称重主板错误！")
            }
        }
    }

    private fun update() {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        builder.connectTimeout(30000, TimeUnit.SECONDS)
            .readTimeout(30000, TimeUnit.SECONDS)
            .writeTimeout(30000, TimeUnit.SECONDS)
            .sslSocketFactory(
                SSLUtils.getSslSocketFactory().sSLSocketFactory,
                SSLUtils.getSslSocketFactory().trustManager
            )
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .retryOnConnectionFailure(true)

        val updateConfig: UpdateConfig = UpdateConfig()
            .setDataSourceType(TypeConfig.DATA_SOURCE_TYPE_MODEL)
            .setShowNotification(true) //配置更新的过程中是否在通知栏显示进度
            .setNotificationIconRes(R.mipmap.download_icon) //配置通知栏显示的图标
            .setUiThemeType(TypeConfig.UI_THEME_L)
            .setAutoDownloadBackground(false)
            .setNeedFileMD5Check(false)
            .setCustomDownloadConnectionCreator(OkHttp3Connection.Creator(builder))
            .setModelClass(cangjie.scale.sorting.entity.UpdateModel())
        AppUpdateUtils.init(this, updateConfig)
    }

    companion object {
        private var sInstance: Application? = null


        @Synchronized
        fun setApplication(application: Application) {
            sInstance = application
        }

        val instance: Application?
            get() {
                if (sInstance == null) {
                    throw NullPointerException("please inherit Application or call setApplication.")
                }
                return sInstance
            }
    }
}