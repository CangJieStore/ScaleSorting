package cangjie.scale.sorting.ui.purchase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.viewModelScope
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.ActivityPurchaseGoodsBinding
import cangjie.scale.sorting.db.OrderLabel
import cangjie.scale.sorting.entity.LabelInfo
import cangjie.scale.sorting.entity.PurchaseInfo
import cangjie.scale.sorting.entity.TaskGoodsItem
import cangjie.scale.sorting.print.Printer
import cangjie.scale.sorting.scale.*
import cangjie.scale.sorting.scale.message.IMessage
import cangjie.scale.sorting.scale.message.ReadMessage
import cangjie.scale.sorting.ui.SubmitDialogFragment
import cangjie.scale.sorting.vm.PurchaseViewModel
import coil.load
import com.blankj.utilcode.util.ViewUtils
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.CodeUtils
import com.cangjie.frame.kit.lib.ToastUtils
import com.cangjie.frame.kit.show
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar
import id.zelory.cekrek.extension.cekrekToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author CangJie
 * @date 2021/11/5 10:23
 */
class PurchaseGoodsActivity : BaseMvvmActivity<ActivityPurchaseGoodsBinding, PurchaseViewModel>() {

    private val purchaseAdapter by lazy {
        PurchaseGoodsAdapter()
    }
    private val sortedAdapter by lazy {
        PurchaseGoodsSortedAdapter()
    }

    private val labelAdapter by lazy {
        LabelAdapter()
    }

    private val stockAdapter by lazy {
        StockAdapter()
    }

    override fun layoutId(): Int = R.layout.activity_purchase_goods
    private var taskItemInfo: TaskGoodsItem? = null
    private var fromType = 0
    private val currentPurchaseLabelInfo = arrayListOf<LabelInfo>()


    override fun initVariableId(): Int = BR.purchaseModel

    override fun onStart() {
        super.onStart()
        initWeight()
        initPrinter()
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        taskItemInfo = intent.getSerializableExtra("item") as TaskGoodsItem
        fromType = intent.getIntExtra("from", 0)
        initPurchase()
        setData(taskItemInfo)
        if (fromType == 0) {
            mBinding.btnUnSort.isChecked = true
            mBinding.btnSorted.isChecked = false
        } else {
            mBinding.btnUnSort.isChecked = false
            mBinding.btnSorted.isChecked = true
        }
    }

    private fun setData(itemData: TaskGoodsItem?) {
        itemData?.let {
            viewModel.currentType.set(0)
            viewModel.currentPurchaseGoodsFiled.set(it.name)
            viewModel.getUnPurchaseTask(0, it.id, "")
            mBinding.ivGoodsImg.load(it.picture)
            calType(it.unit)
            viewModel.currentUnitFiled.set(" " + it.unit)
            viewModel.currentGoodsOrderNumFiled.set(it.quantity)
            mBinding.tvPurchaseTitle.text = "商品名称:" + it.name
        }
    }


    private fun initPurchase() {
        tabSelect(fromType)
        if (mBinding.ryPurchase.itemDecorationCount == 0) {
            dividerBuilder()
                .color(Color.parseColor("#cccccc"))
                .size(1, TypedValue.COMPLEX_UNIT_DIP)
                .showLastDivider()
                .build()
                .addTo(mBinding.ryPurchase)
        }
        if (mBinding.ryReceived.itemDecorationCount == 0) {
            dividerBuilder()
                .color(Color.parseColor("#cccccc"))
                .size(1, TypedValue.COMPLEX_UNIT_DIP)
                .showLastDivider()
                .build()
                .addTo(mBinding.ryReceived)
        }
        if (mBinding.ryBatch.itemDecorationCount == 0) {
            dividerBuilder()
                .color(Color.parseColor("#cccccc"))
                .size(1, TypedValue.COMPLEX_UNIT_DIP)
                .showLastDivider()
                .build()
                .addTo(mBinding.ryBatch)
        }
        mBinding.ryPurchase.adapter = purchaseAdapter
        mBinding.ryBatch.adapter = stockAdapter
        mBinding.ryReceived.adapter = sortedAdapter
        sortedAdapter.setOnItemClickListener { adapter, view, position -> //                val item = sortedAdapter.data[position]
            selectSortedCurrent(position)
        }
        sortedAdapter.setHandleAction(object : PurchaseGoodsSortedAdapter.HandleAction {
            override fun action(itemId: String) {
                viewModel.again(itemId)
            }
        })
        purchaseAdapter.setOnItemClickListener { _, _, position ->
            selectCurrent(position)
        }
        stockAdapter.setOnItemClickListener { _, _, position ->
            selectBatchCurrent(position)
        }
        if (mBinding.ryScaleBatch.itemDecorationCount == 0) {
            dividerBuilder()
                .color(Color.TRANSPARENT)
                .size(8, TypedValue.COMPLEX_UNIT_DIP)
                .showLastDivider()
                .showSideDividers()
                .showFirstDivider()
                .build()
                .addTo(mBinding.ryScaleBatch)
        }
        labelAdapter.setOnItemClickListener { adapter, view, position ->
            val label = labelAdapter.data[position]
            labelAdapter.selectPos(position)
            viewModel.currentLabel.set(label)
            if (mBinding.btnSorted.isChecked) {
                viewModel.thisPurchaseNumFiled.set(label.currentNum.toString())
                viewModel.currentGoodsOrderNumFiled.set(label.quantity.toString())
                viewModel.currentGoodsReceiveNumField.set(label.deliver_quantity.toString())
                if (label.deliver_quantity != null && label.quantity != null) {
                    viewModel.surplusNumFiled.set((label.quantity - label.deliver_quantity).toString())
                }
            }
            mBinding.btnNormalSort.visibility = View.GONE
            mBinding.llPrintAgain.visibility = View.VISIBLE
        }
        mBinding.ryScaleBatch.adapter = labelAdapter
    }

    private fun tabSelect(pos: Int) {
        mBinding.btnUnSort.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                viewModel.currentPurchaseType.set(0)
                mBinding.btnSorted.isChecked = false
                mBinding.etThisNum.isEnabled = true
            }
        }
        mBinding.btnSorted.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                viewModel.currentPurchaseType.set(1)
                mBinding.btnUnSort.isChecked = false
                mBinding.etThisNum.isEnabled = false
                if (sortedAdapter.data.size > 0) {
                    selectSortedCurrent(0)
                }
            }
        }
    }

    /**
     * 选中待分拣客户
     */
    private fun selectCurrent(pos: Int) {
        refreshData(pos)
        val indexData = purchaseAdapter.data[pos]
        viewModel.currentInfoFiled.set(indexData)
        if (labelAdapter.data.size > 0) {
            currentPurchaseLabelInfo.clear()
            labelAdapter.data.clear()
            labelAdapter.notifyDataSetChanged()
        }
        resetData(indexData)
    }

    /**
     * 选中已分拣客户
     */
    private fun selectSortedCurrent(pos: Int) {
        refreshSortedData(pos)
        val indexData = sortedAdapter.data[pos]
        viewModel.get(indexData.item_id)
        viewModel.currentInfoFiled.set(indexData)
        if (labelAdapter.data.size > 0) {
            currentPurchaseLabelInfo.clear()
            labelAdapter.data.clear()
            labelAdapter.notifyDataSetChanged()
        }
        resetData(indexData)
    }

    private fun selectBatchCurrent(pos: Int) {
        refreshBatchData(pos)
        val indexData = stockAdapter.data[pos]
        viewModel.currentBatchFiled.set(indexData)
    }

    private fun resetData(data: PurchaseInfo?) {
        data?.let {
            viewModel.currentUnitFiled.set(" " + it.unit)
            calType(it.unit)
            if (!viewModel.currentWeightTypeFiled.get()) {
                viewModel.thisPurchaseNumFiled.set("0.00")
            } else {
                viewModel.thisPurchaseNumFiled.set("")
            }
            viewModel.currentGoodsOrderNumFiled.set(it.quantity)
            viewModel.currentGoodsReceiveNumField.set(it.deliver_quantity)
            if (it.deliver_quantity != null && it.quantity != null) {
                viewModel.surplusNumFiled.set((it.quantity.toFloat() - it.deliver_quantity.toFloat()).toString())
            }
        }
    }

    private fun refreshData(pos: Int) {
        for (index in 0 until purchaseAdapter.data.size) {
            purchaseAdapter.data[index].isCurrent = index == pos
            purchaseAdapter.notifyItemChanged(index)
        }
    }

    private fun refreshSortedData(pos: Int) {
        for (index in 0 until sortedAdapter.data.size) {
            sortedAdapter.data[index].isCurrent = index == pos
            sortedAdapter.notifyItemChanged(index)
        }
    }

    private fun refreshBatchData(pos: Int) {
        for (index in 0 until stockAdapter.data.size) {
            stockAdapter.data[index].isCurrent = index == pos
            stockAdapter.notifyItemChanged(index)
        }
    }

    private fun calAllStock(): Float {
        var allStock = 0f
        if (stockAdapter.data.size > 0) {
            stockAdapter.data.map {
                allStock += it.stock
            }
        }
        return allStock
    }

    override fun handleEvent(msg: MsgEvent) {
        super.handleEvent(msg)
        when (msg.code) {
            -1 -> {
                finish()
            }
            1 -> {
                showInputPrice()
            }
            //打印标签
            2 -> {
                if (viewModel.currentBatchFiled.get() == null) {
                    toast("暂无库存，分拣失败")
                    return
                }
                val surplusQuantity =
                    (viewModel.thisPurchaseNumFiled.get()?.toFloat() ?: 0f) + calCurrentSurplusNum()
                val currentStock = calAllStock()
                if (surplusQuantity > currentStock) {
                    toast("库存不足，分拣失败")
                    return
                }
                viewModel.currentInfoFiled.get()?.let {
                    makeNewLabel()
                }
            }
            20 -> {
                if (viewModel.currentPrinterStatus.get() == 2) {
                    viewModel.currentLabel.get()?.let {
                        val bNo = String.format("%02d", labelAdapter.getSelect() + 1)
                        Printer.getInstance().printHistory(it, 550, bNo)
                    }
                } else {
                    toast("标签打印机未连接或打印机故障")
                }
            }
            610 -> {
                labelAdapter.selectPos(-1)
                viewModel.currentLabel.set(null)
                mBinding.btnNormalSort.visibility = View.VISIBLE
                mBinding.llPrintAgain.visibility = View.GONE
            }
            //重新分拣
            3 -> {
                if (viewModel.currentBatchFiled.get() == null) {
                    toast("暂无库存，分拣失败")
                    return
                }
                viewModel.currentInfoFiled.get()?.let {
                    viewModel.thisPurchaseNumFiled.set("0.00")
                    currentPurchaseLabelInfo.clear()
                    labelAdapter.data.clear()
                    labelAdapter.notifyDataSetChanged()
                    resetData(viewModel.currentInfoFiled.get())
                }
            }
            //完成提交
            4 -> {
                if (viewModel.currentBatchFiled.get() == null) {
                    toast("暂无库存，分拣失败")
                    return
                }
                if (labelAdapter.data.size == 0) {
                    toast("请先分拣才能完成提交")
                    return
                }
                val bundle = Bundle()
                currentPurchaseLabelInfo.forEach { da ->
                    run {
                        da.batchId = viewModel.currentBatchFiled.get()?.batch_id.toString()
                    }
                }
                checkLastLabel()
                bundle.putSerializable("info", currentPurchaseLabelInfo)
                SubmitDialogFragment.newInstance(bundle)?.setAction(object :
                    SubmitDialogFragment.SubmitAction {
                    override fun submit(quantity: Float) {
                        viewModel.submit(
                            viewModel.currentInfoFiled.get()?.item_id.toString(),
                            viewModel.currentBatchFiled.get()?.batch_id.toString(),
                            quantity.toString()
                        )
                    }
                })?.show(supportFragmentManager, "submit")
            }
            //提交完成
            5 -> {
                val labelList = arrayListOf<OrderLabel>()
                for (item in currentPurchaseLabelInfo) {
                    val itemLabel = OrderLabel(
                        itemId = viewModel.currentInfoFiled.get()?.item_id.toString(),
                        goodsName = item.goodsName.toString(),
                        quantity = item.quantity,
                        currentNum = item.currentNum,
                        deliver_quantity = item.deliver_quantity,
                        customer = item.customer.toString(),
                        unit = item.unit.toString(),
                        qrcode = item.qrcode,
                        batchId = item.batchId
                    )
                    labelList.add(itemLabel)
                }
                viewModel.add(labelList)
                toast("提交成功")
                currentPurchaseLabelInfo.clear()
                labelAdapter.data.clear()
                labelAdapter.notifyDataSetChanged()
                viewModel.getUnPurchaseTask(0, taskItemInfo!!.id, "")

            }
            //置零
            6 -> {
                returnZero()
//                updateWeight()
            }
            300 -> {
                tabSelect(0)
                viewModel.getUnPurchaseTask(0, taskItemInfo!!.id, "")
            }

        }
    }

    private fun checkLastLabel() {
        if (currentPurchaseLabelInfo.size > 0) {
            val lastItem = currentPurchaseLabelInfo[currentPurchaseLabelInfo.size - 1]
            val leftNum = lastItem.quantity - lastItem.deliver_quantity
            var isLast = false
            if (!viewModel.currentWeightTypeFiled.get()) {//计重
                val percentLeft = ((leftNum / lastItem.quantity) * 100)
                if (percentLeft <= 1.0f) {
                    isLast = true
                }
            } else {
                if (leftNum <= 0) {
                    isLast = true
                }
            }
            if (!isLast) {
                if (viewModel.currentPrinterStatus.get() == 2) {
                    val bNo = String.format("%02d", labelAdapter.data.size)
                    Printer.getInstance()
                        .printEText(lastItem,  bNo)
                }
            }
        }
    }

    private fun returnZero() {
        try {
            SerialPortManager.instance().send(ByteUtil.hexStr2bytes("5A03"))
        } catch (e: Exception) {
            ViewUtils.runOnUiThread {
                ToastUtils.show("置零失败")
            }
        }
    }

    override fun subscribeModel(model: PurchaseViewModel) {
        super.subscribeModel(model)
        model.getPurchaseData().observe(this) {
            purchaseAdapter.setList(it.filter { value -> value.deliver_quantity?.toFloat() ?: 0f == 0f })
            sortedAdapter.setList(it.filter { value -> value.deliver_quantity?.toFloat() ?: 0f > 0f })
            if (purchaseAdapter.data.size > 0) {
                selectCurrent(0)
                viewModel.getAllBatch(purchaseAdapter.data[0].item_id)
            } else {
                viewModel.currentGoodsReceiveNumField.set("0.00")
                viewModel.surplusNumFiled.set("0.00")
                viewModel.currentInfoFiled.set(null)
                if (stockAdapter.data.size > 0) {
                    stockAdapter.data.clear()
                    stockAdapter.notifyDataSetChanged()
                }
            }
            if (sortedAdapter.data.size > 0 && mBinding.btnSorted.isChecked) {
                selectSortedCurrent(0)
            }

        }
        model.getStockData().observe(this) {
            stockAdapter.setList(it)
            if (it.size > 0) {
                selectBatchCurrent(0)
            }
        }
        model.getLabelData().observe(this) {
            labelAdapter.setList(it)
        }
    }

    override fun initImmersionBar() {
        immersionBar {
            fullScreen(true)
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            statusBarDarkFont(false)
            init()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            init()
        }
    }

    /**
     * 计算计重方式
     */
    private fun calType(type: String) {
        val isWeight = if (type.contains("斤")
            || type.contains("公斤")
            || type.contains("千克")
            || type.contains("克")
            || type.contains("两")
        ) {
            0
        } else {
            1
        }
        if (isWeight == 0) {
            viewModel.scaleTypeFiled.set("计重")
            mBinding.btnRemove.visibility=View.VISIBLE
            viewModel.currentWeightTypeFiled.set(false)
        } else {
            viewModel.scaleTypeFiled.set("计数")
            mBinding.btnRemove.visibility=View.GONE
            viewModel.currentWeightTypeFiled.set(true)
        }
    }

    /** 设备过滤器 */
    private val deviceFilter = object : DeviceFilter {
        override fun allow(device: String): Boolean {
            // 不允许打开usb的串口
            return !device.contains("usb", true)
        }
    }

    private fun initWeight() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        try {
            viewModel.viewModelScope.launch {
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

    /**
     * 初始化打印机
     */
    private fun initPrinter() {
        Printer.getInstance().setStatusCallback(object : Printer.StatusCallback {
            override fun status(type: Int, msg: String?) {
                viewModel.currentPrinterStatus.set(type)
                mBinding.tvPurTitle.text = "分拣(打印机$msg)"
            }
        }).open(this@PurchaseGoodsActivity)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(message: IMessage) {
        if (message is ReadMessage) {
            getWeight(message.message)
        }
    }

    private fun getWeight(input: String) {
        val temp = input.chunked(2)
        val sb = StringBuffer()
        if (temp.size >= 14) {
            for ((index, ch) in temp.withIndex()) {
                if (index > 2 && index < temp.size - 4) {
                    sb.append(ch.toInt(16).toChar())
                }
            }
            val strWeight = sb.toString()
            if (strWeight.isNotEmpty()) {
                if (!viewModel.currentWeightTypeFiled.get()) {
                    viewModel.currentWeightValue.set(formatUnit(strWeight))
                    viewModel.thisPurchaseNumFiled.set(formatUnit(strWeight))
                } else {
                    viewModel.currentWeightValue.set("0.00")
                }
            }
        } else {
            viewModel.currentWeightValue.set("0.00")
        }
    }

    /**
     *
     */
    private fun formatUnit(currentWeight: String): String {
        return (FormatUtil.roundByScale(
            currentWeight.toDouble() * 2,
            2
        )).toString()
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        Printer.getInstance().close()
        SerialPortManager.instance().close()
        super.onDestroy()
    }

    override fun toast(notice: String?) {
        super.toast(notice)
        show(this, 2000, notice ?: "")
    }

    private fun showInputPrice() {
        EditPriceDialogFragment("本批数量", "请输入本次分拣数量...").setContentCallback(object :
            EditPriceDialogFragment.ContentCallback {
            override fun content(content: String?) {
                viewModel.thisPurchaseNumFiled.set(content)
            }
        }).show(supportFragmentManager)
    }

    /**
     * 生成标签
     */
    private fun makeNewLabel() {
        if (viewModel.currentWeightTypeFiled.get()) {
            if (viewModel.thisPurchaseNumFiled.get().isNullOrEmpty()) {
                toast("请输入本次分拣数量")
                return
            } else {
                if (viewModel.thisPurchaseNumFiled.get()?.toFloat() == 0f) {
                    toast("请输入本次分拣数量")
                    return
                }
            }
        }
        val strQuantity = viewModel.currentGoodsOrderNumFiled.get()
        val allQuantity = strQuantity?.toFloat() ?: 0f
        val surplusQuantity =
            (viewModel.thisPurchaseNumFiled.get()?.toFloat() ?: 0f) + calCurrentSurplusNum()
        val currentQuantity = viewModel.thisPurchaseNumFiled.get()?.toFloat() ?: 0f
        val labelInfo = LabelInfo(
            viewModel.currentPurchaseGoodsFiled.get(),
            allQuantity,
            currentQuantity,
            surplusQuantity,
            viewModel.currentInfoFiled.get()?.purchaser,
            viewModel.currentUnitFiled.get(),
            viewModel.currentBatchFiled.get()?.qrcode.toString()
        )
        currentPurchaseLabelInfo.add(labelInfo)
        labelAdapter.setList(currentPurchaseLabelInfo)
        viewModel.currentGoodsReceiveNumField.set(
            FormatUtil.roundByScale(
                surplusQuantity.toDouble(),
                2
            )
        )
        viewModel.surplusNumFiled.set(
            FormatUtil.roundByScale(
                ((allQuantity - surplusQuantity).toDouble()),
                2
            )
        )
        if (viewModel.currentWeightTypeFiled.get()) {
            viewModel.thisPurchaseNumFiled.set("")
        }
        if (viewModel.currentPrinterStatus.get() == 2) {
            val bNo = String.format("%02d", labelAdapter.data.size)
            Printer.getInstance()
                .printText(labelInfo, 550, bNo, viewModel.currentWeightTypeFiled.get())
        }
    }

    /**
     * 计算当前分拣累计数量
     */
    private fun calCurrentSurplusNum(): Float {
        var allQuantity = viewModel.currentInfoFiled.get()?.deliver_quantity?.toFloat() ?: 0f
        currentPurchaseLabelInfo.forEach {
            allQuantity += it.currentNum
        }
        return allQuantity
    }

    /**
     * 生成打印图片
     */
    private fun getBitmap(labelInfo: LabelInfo, batchNo: String): Bitmap {
        val v = LayoutInflater.from(this@PurchaseGoodsActivity)
            .inflate(R.layout.layout_print_item, null)
        val name = v.findViewById<AppCompatTextView>(R.id.tv_goods_title)
        val quantity = v.findViewById<AppCompatTextView>(R.id.tv_quantity)
        val batch = v.findViewById<AppCompatTextView>(R.id.tv_batch)
        val currentQu = v.findViewById<AppCompatTextView>(R.id.tv_current_num)
        val cus = v.findViewById<AppCompatTextView>(R.id.tv_customer)
        val cus2 = v.findViewById<AppCompatTextView>(R.id.tv_customer2)
        val ivQrcode = v.findViewById<AppCompatImageView>(R.id.iv_qrcode)
        ivQrcode.setImageBitmap(CodeUtils.createImage(labelInfo.qrcode, 160, 160))
        var spilt = "-"
        val leftNum = labelInfo.quantity - labelInfo.deliver_quantity
        if (leftNum <= 0) {
            spilt = "E-"
        }
        name.text = "商品名称:" + labelInfo.goodsName
        quantity.text = "订货数量:" + labelInfo.quantity.toString() + labelInfo.unit
        batch.text =
            "分拣货号:$batchNo$spilt" + FormatUtil.roundByScale(
                labelInfo.currentNum.toDouble(),
                2
            ) + "-" + FormatUtil.roundByScale(leftNum.toDouble(), 2)
        currentQu.text = "本批数量:" + labelInfo.currentNum
        val customerName = "客户名称:" + labelInfo.customer
        if (customerName.length > 14) {
            cus.text = customerName.substring(0, 14)
            cus2.visibility = View.VISIBLE
            cus2.text = customerName.substring(14, customerName.length)
        } else {
            cus.text = customerName
            cus2.visibility = View.GONE
        }
        return v.cekrekToBitmap()
    }

}