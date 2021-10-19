package cangjie.scale.sorting.widget

import android.content.Context
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import cangjie.scale.sorting.R
import cangjie.scale.sorting.entity.GoodsInfo
import com.lxj.xpopup.core.DrawerPopupView

/**
 * Description: 自定义带列表的Drawer弹窗
 * Create by dance, at 2019/1/9
 */
class CalLessPopView(context: Context, info: GoodsInfo, listener: LessValueListener) :
    DrawerPopupView(context) {

    private var item: GoodsInfo? = null
    private var valueListener: LessValueListener? = null

    init {
        this.item = info
        this.valueListener = listener
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_cal_less
    }

    override fun onCreate() {
        findViewById<AppCompatButton>(R.id.btn_cancel).setOnClickListener {
            valueListener?.value(null, null)
            dismiss()
        }
        val receiveCount = findViewById<AppCompatEditText>(R.id.edit_match_count)
        val receivePrice = findViewById<AppCompatEditText>(R.id.edit_receive_price)
        item?.let {
            Log.e("last", it.toString())
            findViewById<AppCompatTextView>(R.id.tv_goods_title).text = "商品名称:" + it.name
            findViewById<AppCompatTextView>(R.id.tv_goods_spec).text = "商品规格:" + it.spec
            findViewById<AppCompatTextView>(R.id.tv_buy_unit2).text = it.unit
            if (it.is_sorting == 0) {
                receivePrice.isEnabled = true
                receiveCount.isEnabled = true
            } else {
                receivePrice.setText(it.deliver_price)
                receiveCount.setText(it.deliver_quantity)
                receivePrice.isEnabled = false
                receiveCount.isEnabled = false
            }
            if (it.repair_receive == "1") {
                receivePrice.setText(it.deliver_price)
                receiveCount.setText(it.deliver_quantity)
            }
        }
        findViewById<AppCompatButton>(R.id.btn_confirm).setOnClickListener {
            valueListener?.value(
                receiveCount.text.toString().trim(),
                receivePrice.text.toString().trim()
            )
            dismiss()
        }
    }

    interface LessValueListener {
        fun value(count: String?, price: String?)
    }
}