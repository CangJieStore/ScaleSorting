package cangjie.scale.sorting.scale.message

import android.graphics.Color
import android.serialport.SerialPort
import cangjie.scale.sorting.scale.ByteUtil


class ReadMessage(serialPort: SerialPort, byteArray: ByteArray) : IMessage {
    override val message: String =ByteUtil.bytes2HexStr(byteArray)
    override val color: Int = Color.parseColor("#48B424")
}