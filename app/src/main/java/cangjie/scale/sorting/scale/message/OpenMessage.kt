package cangjie.scale.sorting.scale.message

import android.graphics.Color
import android.serialport.SerialPort


class OpenMessage(deviceList: List<SerialPort>) : IMessage {

    private val stringBuilder = StringBuilder()

    init {
        deviceList.forEach {
            stringBuilder.append("\n").append(it.device)
        }
    }


    override val message: String = stringBuilder.toString()
    override val color: Int = Color.parseColor("#158EE5")
}