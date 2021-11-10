package cangjie.scale.sorting.print

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import cangjie.scale.sorting.base.ScaleApplication
import com.gprinter.io.PortManager
import com.gprinter.io.UsbPort
import java.io.IOException
import java.util.*
import kotlin.experimental.and


class DeviceConnFactoryManager private constructor(build: Build) {
    var mPort: PortManager? = null
    private val mUsbDevice: UsbDevice? = build.usbDevice
    private val mContext: Context? = build.context
    private var id = 0

    /**
     * 获取端口打开状态（true 打开，false 未打开）
     *
     * @return
     */
    var connState = false
    var reader: PrinterReader? = null

    /**
     * 打开端口
     *
     * @return
     */
    fun openPort() {
        deviceConnFactoryManagers[id]!!.connState = false
        sendStateBroadcast(CONN_STATE_CONNECTING)
        mPort = UsbPort(mContext, mUsbDevice)
        connState = mPort!!.openPort()
        if (connState) {
            queryCommand()
        } else {
            if (mPort != null) {
                mPort = null
            }
            sendStateBroadcast(CONN_STATE_FAILED)
        }
    }

    /**
     * 查询当前连接打印机所使用打印机指令（ESC（EscCommand.java）、TSC（LabelCommand.java））
     */
    private fun queryCommand() {
        reader = PrinterReader()
        reader!!.start()
        sendStateBroadcast(CONN_STATE_CONNECTED);
    }

    /**
     * 获取连接的USB设备信息
     *
     * @return
     */
    fun usbDevice(): UsbDevice? {
        return mUsbDevice
    }

    /**
     * 关闭端口
     */
    fun closePort(id: Int) {
        if (mPort != null) {
            if (reader != null) {
                reader!!.cancel()
                reader = null
            }
            val b = mPort!!.closePort()
            if (b) {
                mPort = null
                connState = false
            }
        }
        sendStateBroadcast(CONN_STATE_DISCONNECT)
    }

    class Build {
        var usbDevice: UsbDevice? = null
        var context: Context? = null
        var id = 0

        fun setUsbDevice(usbDevice: UsbDevice?): Build {
            this.usbDevice = usbDevice
            return this
        }

        fun setContext(context: Context?): Build {
            this.context = context
            return this
        }

        fun setId(id: Int): Build {
            this.id = id
            return this
        }

        fun build(): DeviceConnFactoryManager {
            return DeviceConnFactoryManager(this)
        }
    }

    fun sendDataImmediately(data: Vector<Byte>) {
        if (mPort == null) {
            return
        }
        try {
            mPort!!.writeDataImmediately(data, 0, data.size)
        } catch (e: Exception) { //异常中断发送
            mHandler.obtainMessage(Constant.abnormal_Disconnection).sendToTarget()
        }
    }

    fun sendByteDataImmediately(data: ByteArray) {
        if (mPort == null) {
            return
        } else {
            val datas = Vector<Byte>()
            for (i in data.indices) {
                datas.add(java.lang.Byte.valueOf(data[i]))
            }
            try {
                mPort!!.writeDataImmediately(datas, 0, datas.size)
            } catch (e: IOException) { //异常中断发送
                mHandler.obtainMessage(Constant.abnormal_Disconnection).sendToTarget()
            }
        }
    }

    @Throws(IOException::class)
    fun readDataImmediately(buffer: ByteArray?): Int {
        return mPort!!.readData(buffer)
    }

    inner class PrinterReader : Thread() {
        private var isRun = false
        private val buffer = ByteArray(100)
        override fun run() {
            try {
                while (isRun) {
                    //读取打印机返回信息,打印机没有返回纸返回-1
                    Log.e(TAG, "wait read ")
                    val len = readDataImmediately(buffer)
                    Log.e(TAG, " read $len")
                    if (len > 0) {
                        val message = Message.obtain()
                        message.what = READ_DATA
                        val bundle = Bundle()
                        bundle.putInt(READ_DATA_CNT, len) //数据长度
                        bundle.putByteArray(READ_BUFFER_ARRAY, buffer) //数据
                        message.data = bundle
                        mHandler.sendMessage(message)
                    }
                }
            } catch (e: Exception) { //异常断开
                if (deviceConnFactoryManagers[id.toInt()] != null) {
                    closePort(id.toInt())
                    mHandler.obtainMessage(Constant.abnormal_Disconnection).sendToTarget()
                }
            }
        }

        fun cancel() {
            isRun = false
        }

        init {
            isRun = true
        }
    }

    private val mHandler: Handler = object : Handler() {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constant.abnormal_Disconnection -> {
                    Log.e("info", "异常断开")
                }
                DEFAUIT_COMMAND -> {}
                READ_DATA -> {
                    val cnt = msg.data.getInt(READ_DATA_CNT) //数据长度 >0;
                    val buffer = msg.data.getByteArray(READ_BUFFER_ARRAY)
                        ?: return //数据
                    var status = "打印机连接正常"
                    if (cnt == 1) { //查询打印机实时状态
                        if (buffer[0] and TSC_STATE_PAPER_ERR.toByte() > 0) { //缺纸
                            status += " 打印机缺纸"
                        }
                        if (buffer[0] and TSC_STATE_COVER_OPEN.toByte() > 0) { //开盖
                            status += " 打印机开盖"
                        }
                        if (buffer[0] and TSC_STATE_ERR_OCCURS.toByte() > 0) { //打印机报错
                            status += " 打印机异常错误"
                        }
                        sendRunStateBroadcast(status)
                    } else {
                        val intent = Intent(ACTION_QUERY_PRINTER_STATE)
                        intent.putExtra(DEVICE_ID, id)
                        ScaleApplication.instance!!.sendBroadcast(intent)
                    }
                }
                else -> {}
            }
        }
    }

    /**
     * 发送连接状态广播
     * @param state
     */
    private fun sendStateBroadcast(state: Int) {
        val intent = Intent(ACTION_CONN_STATE)
        intent.putExtra(STATE, state)
        intent.putExtra(DEVICE_ID, id)
        ScaleApplication.instance!!.sendBroadcast(intent) //此处若报空指针错误，需要在清单文件application标签里注册此类，参考demo
    }

    /**
     * 发送运行状态广播
     */
    private fun sendRunStateBroadcast(state: String) {
        val intent = Intent(ACTION_RUN_STATE)
        intent.putExtra(STATE, state)
        intent.putExtra(DEVICE_ID, id)
        ScaleApplication.instance!!.sendBroadcast(intent)
    }

    companion object {
        private val TAG = DeviceConnFactoryManager::class.java.simpleName
        val deviceConnFactoryManagers = arrayOfNulls<DeviceConnFactoryManager>(4)

        /**
         * TSC指令查询打印机实时状态 打印机缺纸状态
         */
        private const val TSC_STATE_PAPER_ERR = 0x04

        /**
         * TSC指令查询打印机实时状态 打印机开盖状态
         */
        private const val TSC_STATE_COVER_OPEN = 0x01

        /**
         * TSC指令查询打印机实时状态 打印机出错状态
         */
        private const val TSC_STATE_ERR_OCCURS = 0x80
        private const val READ_DATA = 10000
        private const val DEFAUIT_COMMAND = 20000
        private const val READ_DATA_CNT = "read_data_cnt"
        private const val READ_BUFFER_ARRAY = "read_buffer_array"
        const val ACTION_CONN_STATE = "action_connect_state"
        const val ACTION_RUN_STATE = "action_run_state"
        const val ACTION_QUERY_PRINTER_STATE = "action_query_printer_state"
        const val STATE = "state"
        const val DEVICE_ID = "id"
        const val CONN_STATE_DISCONNECT = 0x90
        const val CONN_STATE_CONNECTING = CONN_STATE_DISCONNECT shl 1
        const val CONN_STATE_FAILED = CONN_STATE_DISCONNECT shl 2
        const val CONN_STATE_CONNECTED = CONN_STATE_DISCONNECT shl 3
        fun closeAllPort() {
            for (deviceConnFactoryManager in deviceConnFactoryManagers) {
                if (deviceConnFactoryManager != null) {
                    Log.e(TAG, "cloaseAllPort() id -> " + deviceConnFactoryManager.id)
                    deviceConnFactoryManager.closePort(deviceConnFactoryManager.id)
                    deviceConnFactoryManagers[deviceConnFactoryManager.id] = null
                }
            }
        }
    }

    init {
        id = build.id
        deviceConnFactoryManagers[id] = this
    }
}