package cangjie.scale.sorting.print


import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cangjie.scale.sorting.R
import com.gprinter.command.EscCommand
import com.gprinter.command.LabelCommand
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author: guruohan
 * @date: 2021/11/9
 */
class Printer private constructor() {
    private var mActivity: AppCompatActivity? = null
    private var usbManager: UsbManager? = null
    private val tsc = byteArrayOf(0x1b, '!'.toByte(), '?'.toByte())
    private var threadPool = ThreadPool.instantiation
    private var mPermissionIntent: PendingIntent? = null
    private var id = 0
    private var statusCallback: StatusCallback? = null
    private var continuityprint = false
    private var printcount = 0
    private var counts = 0
    private var width = 70
    private var height = 50
    private var gapSize = 2

    companion object {
        private val TAG = Printer::class.java.simpleName
        private const val CONN_STATE_DISCONN = 0x007
        private const val PRINTER_COMMAND_ERROR = 0x008
        private const val CONN_PRINTER = 0x12
        private var INSTANCE: Printer? = null

        @Synchronized
        fun getInstance(): Printer {
            if (INSTANCE == null) {
                INSTANCE = Printer()
            }
            return INSTANCE!!
        }
    }

    fun open(activity: AppCompatActivity): Printer {
        this.mActivity = activity
        usbManager = this.mActivity?.getSystemService(USB_SERVICE) as UsbManager
        initBroadcast()
        usbDeviceList()
        return this
    }

    /**
     * 设置纸张尺寸和纸张间隙
     */
    fun setPagerSize(w: Int, h: Int, gap: Int) {
        this.width = w
        this.height = h
        this.gapSize = gap
    }

    fun printBitmap(
        bitmap: Bitmap,
        nWidth: Int,
        qrcode: String,
        codeX: Int,
        codeY: Int = 0
    ) {
        val tsc = LabelCommand()
        tsc.addSize(width, height)
        tsc.addGap(gapSize)
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL)
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON)
        tsc.addReference(0, 0)
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4)
        tsc.addTear(EscCommand.ENABLE.ON)
        tsc.addCls()
        tsc.addBitmap(0, 0, LabelCommand.BITMAP_MODE.OVERWRITE, nWidth, bitmap)
        tsc.addQRCode(
            codeX,
            codeY,
            LabelCommand.EEC.LEVEL_L,
            5,
            LabelCommand.ROTATION.ROTATION_0,
            qrcode
        )
        tsc.addPrint(1, 1)
        tsc.addSound(2, 100)
        ThreadPool.instantiation?.addSerialTask(Runnable {
            if (DeviceConnFactoryManager.deviceConnFactoryManagers[id] == null ||
                !DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.connState!!
            ) {
                return@Runnable
            }
            DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.sendDataImmediately(
                tsc.command
            )
        })
    }

    private fun initBroadcast() {
        val filter = IntentFilter(Constant.ACTION_USB_PERMISSION) //USB访问权限广播
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED) //USB线拔出
        filter.addAction(DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE) //查询打印机缓冲区状态广播，用于一票一控
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE) //与打印机连接状态
        filter.addAction(DeviceConnFactoryManager.ACTION_RUN_STATE)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED) //USB线插入
        this.mActivity?.registerReceiver(receiver, filter)
    }

    private fun checkUsbDevicePidVid(dev: UsbDevice): Boolean {
        val pid = dev.productId
        val vid = dev.vendorId
        return (vid == 34918 && pid == 256 || vid == 1137 && pid == 85
                || vid == 6790 && pid == 30084
                || vid == 26728 && pid == 256 || vid == 26728 && pid == 512
                || vid == 26728 && pid == 256 || vid == 26728 && pid == 768
                || vid == 26728 && pid == 1024 || vid == 26728 && pid == 1280
                || vid == 26728 && pid == 1536)
    }

    private fun usbDeviceList() {
        val manager =
            this.mActivity?.getSystemService(AppCompatActivity.USB_SERVICE) as UsbManager
        val devices = manager.deviceList
        val deviceIterator: Iterator<UsbDevice> = devices.values.iterator()
        val count = devices.size
        var hasUsbPrinter = false
        if (count > 0) {
            while (deviceIterator.hasNext()) {
                val device = deviceIterator.next()
                val deviceName = device.deviceName
                if (checkUsbDevicePidVid(device)) {
                    hasUsbPrinter = true
                    connectUsbPrinter(deviceName)
                }
            }
        }
        if (!hasUsbPrinter) {
            statusCallback?.status(-100, "未发现USB打印设备")
        }
    }

    private fun getUsbDeviceFromName(context: Context, usbName: String): UsbDevice? {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val usbDeviceList = usbManager.deviceList
        return usbDeviceList[usbName]
    }

    private fun connectUsbPrinter(uName: String) {
        closePort()
        val usbDevice = getUsbDeviceFromName(this.mActivity!!, uName)
        if (usbManager!!.hasPermission(usbDevice)) {
            if (usbDevice != null) {
                usbConn(usbDevice)
            }
        } else { //请求权限
            mPermissionIntent =
                PendingIntent.getBroadcast(
                    this.mActivity,
                    0,
                    Intent(Constant.ACTION_USB_PERMISSION),
                    0
                )
            usbManager!!.requestPermission(usbDevice, mPermissionIntent)
        }
    }

    /**
     * 重新连接回收上次连接的对象，避免内存泄漏
     */
    private fun closePort() {
        if (DeviceConnFactoryManager.deviceConnFactoryManagers[id] != null && DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.mPort != null) {
            DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.reader?.cancel()
            DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.mPort?.closePort()
            DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.mPort = null
        }
    }

    /**
     * usb连接
     *
     * @param usbDevice
     */
    private fun usbConn(usbDevice: UsbDevice) {
        DeviceConnFactoryManager.Build()
            .setId(id)
            .setUsbDevice(usbDevice)
            .setContext(this.mActivity)
            .build()
        DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.openPort()
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CONN_STATE_DISCONN -> {
                    val deviceConnFactoryManager =
                        DeviceConnFactoryManager.deviceConnFactoryManagers[id]
                    if (deviceConnFactoryManager != null && deviceConnFactoryManager.connState) {
                        DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.closePort(id)
                        statusCallback?.status(
                            -1,
                            mActivity?.getString(R.string.str_disconnect_success)
                        )
                    }
                }
                PRINTER_COMMAND_ERROR -> {
                    statusCallback?.status(
                        -2,
                        mActivity?.getString(R.string.str_choice_printer_command)
                    )
                }
                CONN_PRINTER -> statusCallback?.status(
                    -2,
                    mActivity?.getString(R.string.str_cann_printer)
                )
                else -> {
                }
            }
        }
    }
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constant.ACTION_USB_PERMISSION -> synchronized(this) {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) { //用户点击授权
                            usbConn(device)
                        } else {

                        }
                    } else { //用户点击不授权,则无权限访问USB
                        Log.e(TAG, "No access to USB")
                    }
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val usbDevice =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (usbDevice == DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.usbDevice()) {
                        mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget()
                    }
                }
                DeviceConnFactoryManager.ACTION_CONN_STATE -> {
                    val state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1)
                    val deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1)
                    when (state) {
                        DeviceConnFactoryManager.CONN_STATE_DISCONNECT -> if (id == deviceId) {
                            statusCallback?.status(
                                0,
                                mActivity?.getString(R.string.str_conn_state_disconnect)
                            )
                        }
                        DeviceConnFactoryManager.CONN_STATE_CONNECTING ->
                            statusCallback?.status(
                                1,
                                mActivity?.getString(R.string.str_conn_state_connecting)
                            )
                        DeviceConnFactoryManager.CONN_STATE_CONNECTED ->
                            statusCallback?.status(
                                2,
                                mActivity?.getString(R.string.str_conn_state_connected)
                            )
                        DeviceConnFactoryManager.CONN_STATE_FAILED -> {
                            statusCallback?.status(
                                3,
                                mActivity?.getString(R.string.str_conn_fail)
                            )
                        }
                        else -> {}
                    }
                }
                DeviceConnFactoryManager.ACTION_RUN_STATE -> {
                    val state = intent.getStringExtra(DeviceConnFactoryManager.STATE)
                    val deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1)
                    statusCallback?.status(
                        100,
                        state
                    )
                }
                DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE -> if (counts >= 0) {
                    if (continuityprint) {
                        printcount++
                    }
                    if (counts != 0) {
                        sendContinuityPrint()
                    } else {
                        continuityprint = false
                    }
                }
                else -> {}
            }
        }
    }

    private fun sendContinuityPrint() {
        ThreadPool.instantiation?.addSerialTask {
            if (DeviceConnFactoryManager.deviceConnFactoryManagers[id] != null
                && DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.connState!!
            ) {
                val threadFactoryBuilder = ThreadFactoryBuilder("MainActivity_sendContinuity_Timer")
                val scheduledExecutorService: ScheduledExecutorService =
                    ScheduledThreadPoolExecutor(1, threadFactoryBuilder)
                scheduledExecutorService.schedule(threadFactoryBuilder.newThread {
                    counts--
//                    DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.sendDataImmediately(
//                        PrintContent.label
//                    )
                }, 1000, TimeUnit.MILLISECONDS)
            }
        }
    }

    fun btnPrinterState() {
        //打印机状态查询
        if (DeviceConnFactoryManager.deviceConnFactoryManagers[id] == null ||
            !DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.connState!!
        ) {
            statusCallback?.status(-200, mActivity?.getString(R.string.str_cann_printer))
            return
        }
        ThreadPool.instantiation?.addSerialTask {
            DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.sendByteDataImmediately(
                tsc
            )
        }
    }

    fun convertViewToBitmap(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        return view.drawingCache
    }

    fun btnDisConn(view: View?) {
        if (DeviceConnFactoryManager.deviceConnFactoryManagers[id] == null || !DeviceConnFactoryManager.deviceConnFactoryManagers[id]?.connState!!) {
            statusCallback?.status(-200, mActivity?.getString(R.string.str_cann_printer))
            return
        }
        mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget()
    }

    fun close() {
        this.mActivity?.unregisterReceiver(receiver)
        if (usbManager != null) {
            usbManager = null
        }
        DeviceConnFactoryManager.closeAllPort()
        if (threadPool != null) {
            threadPool!!.stopThreadPool()
        }
    }

    interface StatusCallback {
        fun status(type: Int, msg: String?)
    }

    fun setStatusCallback(cb: StatusCallback): Printer {
        this.statusCallback = cb
        return this
    }
}