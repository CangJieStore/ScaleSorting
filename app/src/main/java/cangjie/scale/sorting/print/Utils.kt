package cangjie.scale.sorting.print

import android.content.Context
import android.widget.Toast
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager

object Utils {
    fun getUsbDeviceFromName(context: Context, usbName: String): UsbDevice? {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val usbDeviceList = usbManager.deviceList
        return usbDeviceList[usbName]
    }
}