package cangjie.scale.sorting.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.MediaStore
import cangjie.scale.sorting.base.http.Url
import cangjie.scale.sorting.entity.MessageEvent
import cangjie.scale.sorting.scale.ScaleModule
import cangjie.scale.sorting.scale.SerialPortUtilForScale
import com.blankj.utilcode.util.ViewUtils.runOnUiThread
import com.cangjie.frame.core.db.CangJie
import com.cangjie.frame.kit.lib.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rxhttp.RxHttp
import rxhttp.awaitResult
import rxhttp.toStr
import java.io.File
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

/**
 * @author CangJie
 * @date 2021/9/10 10:53
 */
class InitService : Service(), CoroutineScope by MainScope() {

    //    lateinit var timer: Timer
    private val corLife = CoroutineCycle()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
//        SerialPortUtilForScale.Instance().OpenSerialPort() //打开称重串口
//        try {
//            ScaleModule.Instance(this) //初始化称重模块
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//            runOnUiThread {
//                ToastUtils.show("初始化称重主板错误！")
//            }
//        }
//        timer = fixedRateTimer("", false, 0, 60000) {
//            val single: Single<MutableList<SubmitOrder>> = Single.create { emitter ->
//                booksDao = AppDatabase.get(ScaleApplication.instance!!).orderDao()
//                val orders = booksDao!!.getUpload()
//                emitter.onSuccess(orders)
//            }
//            single.subscribe(object : SingleObserver<MutableList<SubmitOrder>> {
//                override fun onSuccess(o: MutableList<SubmitOrder>) {
//                    Log.e("orders", Gson().toJson(o))
//                    for (item in o) {
//                        upload(item)
//                    }
//                }
//
//                override fun onSubscribe(d: Disposable) {
//
//                }
//
//                override fun onError(e: Throwable) {
//                }
//            })
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.code == 501) {
            returnZero()
        }
    }

    private fun returnZero() {
        try {
            ScaleModule.Instance(this).ZeroClear()
        } catch (e: Exception) {
            runOnUiThread {
                ToastUtils.show("置零失败")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        if (CangJie.getString("token", "").isNotEmpty()) {
            SerialPortUtilForScale.Instance().CloseSerialPort()
            exitProcess(0)
        }
//        corLife.close()
//        timer.cancel()
    }
}