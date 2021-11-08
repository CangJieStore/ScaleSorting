package cangjie.scale.sorting.print

import android.content.Context
import cangjie.scale.sorting.entity.LabelInfo
import com.gprinter.command.LabelCommand
import com.gprinter.command.EscCommand
import android.graphics.Bitmap
import android.view.View
import cangjie.scale.sorting.R
import cangjie.scale.sorting.base.ScaleApplication
import java.util.*

object PrintContent {
    fun getLabel(labelInfo: LabelInfo): Vector<Byte> {
        val tsc = LabelCommand()
        // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
        tsc.addSize(70, 50)
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
        tsc.addGap(2)
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL)
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON)
        // 设置原点坐标
        tsc.addReference(0, 0)
        //设置浓度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4)
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON)
        // 清除打印缓冲区
        tsc.addCls()
        val b = getBitmap(ScaleApplication.instance)
        // 绘制图片
        tsc.addBitmap(0, 0, LabelCommand.BITMAP_MODE.OVERWRITE, 460, b)
        //绘制二维码
        tsc.addQRCode(
            440,
            0,
            LabelCommand.EEC.LEVEL_L,
            5,
            LabelCommand.ROTATION.ROTATION_0,
            labelInfo.qrcode
        )
        // 打印标签
        tsc.addPrint(1, 1)
        // 打印标签后 蜂鸣器响
        tsc.addSound(2, 100)
        // 发送数据
        return tsc.command
    }

    private fun getBitmap(mContext: Context?): Bitmap {
        val v = View.inflate(mContext, R.layout.layout_print_item, null)
        return convertViewToBitmap(v)
    }

    private fun convertViewToBitmap(view: View): Bitmap {
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
}