package cangjie.scale.sorting.scale.message

import android.graphics.Color
import cangjie.scale.sorting.scale.ByteUtil

class SendMessage(byteArray: ByteArray) : IMessage {
    override val message: String =
        "发送\n${ByteUtil.bytes2HexStr(byteArray)}"
    override val color: Int = Color.parseColor("#F23434")
}