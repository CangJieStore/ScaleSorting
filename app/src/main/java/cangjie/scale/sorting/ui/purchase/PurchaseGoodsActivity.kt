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
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.ActivityPurchaseGoodsBinding
import cangjie.scale.sorting.entity.LabelInfo
import cangjie.scale.sorting.entity.PurchaseInfo
import cangjie.scale.sorting.entity.TaskGoodsItem
import cangjie.scale.sorting.print.Printer
import cangjie.scale.sorting.scale.FormatUtil
import cangjie.scale.sorting.scale.ScaleModule
import cangjie.scale.sorting.ui.SubmitDialogFragment
import cangjie.scale.sorting.vm.PurchaseViewModel
import coil.load
import com.blankj.utilcode.util.ViewUtils
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.CodeUtils
import com.cangjie.frame.kit.lib.ToastUtils
import com.cangjie.frame.kit.show
import com.cangjie.frame.kit.tab.Title
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar
import id.zelory.cekrek.Cekrek
import id.zelory.cekrek.extension.cekrekToBitmap

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
    private var readDataReceiver: ReadDataReceiver? = null
    private val currentPurchaseLabelInfo = arrayListOf<LabelInfo>()


    override fun initVariableId(): Int = BR.purchaseModel

    override fun onStart() {
        super.onStart()
        initWeight()
        initPrinter()
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        taskItemInfo = intent.getSerializableExtra("item") as TaskGoodsItem
        initPurchase()
        setData(taskItemInfo)
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
        val list = arrayListOf(
            Title("待分拣", "待分拣", "待分拣"),
            Title("已分拣", "已分拣", "已分拣")
        )
        mBinding.tabPurchase.setTitles(list)
        mBinding.tabPurchase.onSelectChangeListener = {
            viewModel.currentPurchaseType.set(it)
        }
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
        sortedAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item = sortedAdapter.data[position]

            }
        })
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
        mBinding.ryScaleBatch.adapter = labelAdapter
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

    private fun refreshBatchData(pos: Int) {
        for (index in 0 until stockAdapter.data.size) {
            stockAdapter.data[index].isCurrent = index == pos
            stockAdapter.notifyItemChanged(index)
        }
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
                viewModel.currentInfoFiled.get()?.let {
                    makeNewLabel()
                }
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
                toast("提交成功")
                currentPurchaseLabelInfo.clear()
                labelAdapter.data.clear()
                labelAdapter.notifyDataSetChanged()
                viewModel.getUnPurchaseTask(0, taskItemInfo!!.id, "")

            }
            //置零
            6 -> {
                returnZero()
                updateWeight()
            }
            300 -> {
                mBinding.tabPurchase.select(0)
                viewModel.getUnPurchaseTask(0, taskItemInfo!!.id, "")
            }

        }
    }

    private fun returnZero() {
        try {
            ScaleModule.Instance(this).ZeroClear()
        } catch (e: Exception) {
            ViewUtils.runOnUiThread {
                ToastUtils.show("置零失败")
            }
        }
    }

    override fun subscribeModel(model: PurchaseViewModel) {
        super.subscribeModel(model)
        model.getPurchaseData().observe(this, {
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
        })
        model.getStockData().observe(this, {
            stockAdapter.setList(it)
            if (it.size > 0) {
                selectBatchCurrent(0)
            }
        })
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
            viewModel.currentWeightTypeFiled.set(false)
        } else {
            viewModel.scaleTypeFiled.set("计数")
            viewModel.currentWeightTypeFiled.set(true)
        }
    }

    private fun initWeight() {
        try {
            ScaleModule.Instance(this) //初始化称重模块
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ViewUtils.runOnUiThread {
                ToastUtils.show("初始化称重主板错误！")
            }
        }
        readDataReceiver = ReadDataReceiver()
        registerReceiver(
            readDataReceiver,
            IntentFilter(ScaleModule.WeightValueChanged)
        )
        registerReceiver(
            readDataReceiver,
            IntentFilter(ScaleModule.ERROR)
        )
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

    inner class ReadDataReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ScaleModule.ERROR == intent.action) {
                val error = intent.getStringExtra("error")
                ToastUtils.show(error)
            } else {
                updateWeight()
            }
        }
    }

    private fun updateWeight() {
        try {
            val currentWeight = FormatUtil.roundByScale(
                ScaleModule.Instance(this@PurchaseGoodsActivity).RawValue - ScaleModule.Instance(
                    this@PurchaseGoodsActivity
                ).TareWeight,
                ScaleModule.Instance(this@PurchaseGoodsActivity).SetDotPoint
            )
            if (!viewModel.currentWeightTypeFiled.get()) {
                viewModel.currentWeightValue.set(formatUnit(currentWeight))
                viewModel.thisPurchaseNumFiled.set(formatUnit(currentWeight))
            } else {
                viewModel.currentWeightValue.set("0.00")
            }

        } catch (ee: java.lang.Exception) {
            ee.printStackTrace()
            ToastUtils.show(ee.message!!)
        }
    }

    /**
     *
     */
    private fun formatUnit(currentWeight: String): String {
        viewModel.currentInfoFiled.get()?.let {
            return when (it.unit) {
                "斤" -> {
                    (FormatUtil.roundByScale(
                        currentWeight.toDouble() * 2,
                        2
                    )).toString()
                }
                "克" -> {
                    (FormatUtil.roundByScale(
                        currentWeight.toDouble() * 1000,
                        2
                    ))
                        .toString()
                }
                else -> {
                    currentWeight
                }
            }
        }
        return currentWeight
    }

    override fun onDestroy() {
        super.onDestroy()
        readDataReceiver?.let { unregisterReceiver(it) }
        Printer.getInstance().close()

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
        viewModel.currentGoodsReceiveNumField.set((surplusQuantity).toString())
        viewModel.surplusNumFiled.set((allQuantity - surplusQuantity).toString())
        if (viewModel.currentWeightTypeFiled.get()) {
            viewModel.thisPurchaseNumFiled.set("")
        }
        if (viewModel.currentPrinterStatus.get() == 2) {
            Printer.getInstance().printText(labelInfo, 550, "0" + labelAdapter.data.size)
//            Printer.getInstance().printBitmap(
//                getBitmap(
//                    labelInfo,
//                    "0" + labelAdapter.data.size
//                ), 550
//            )
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
            "分拣货号:" + batchNo + spilt + labelInfo.currentNum + "-" + leftNum.toString()
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