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
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.ActivityPurchaseGoodsBinding
import cangjie.scale.sorting.entity.LabelInfo
import cangjie.scale.sorting.entity.TaskGoodsItem
import cangjie.scale.sorting.print.Printer
import cangjie.scale.sorting.scale.FormatUtil
import cangjie.scale.sorting.scale.ScaleModule
import cangjie.scale.sorting.scale.SerialPortUtilForScale
import cangjie.scale.sorting.vm.PurchaseViewModel
import coil.load
import com.blankj.utilcode.util.ViewUtils
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.lib.ToastUtils
import com.cangjie.frame.kit.show
import com.cangjie.frame.kit.tab.Title
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar

/**
 * @author CangJie
 * @date 2021/11/5 10:23
 */
class PurchaseGoodsActivity : BaseMvvmActivity<ActivityPurchaseGoodsBinding, PurchaseViewModel>() {

    private val purchaseAdapter by lazy {
        PurchaseGoodsAdapter()
    }

    private val labelAdapter by lazy {
        LabelAdapter()
    }

    override fun layoutId(): Int = R.layout.activity_purchase_goods
    private var taskItemInfo: TaskGoodsItem? = null
    private var readDataReceiver: ReadDataReceiver? = null
    private val currentPurchaseLabelInfo = mutableListOf<LabelInfo>()


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
        dividerBuilder()
            .color(Color.parseColor("#cccccc"))
            .size(1, TypedValue.COMPLEX_UNIT_DIP)
            .showLastDivider()
            .build()
            .addTo(mBinding.ryPurchase)
        mBinding.ryPurchase.adapter = purchaseAdapter
        purchaseAdapter.setOnItemClickListener { _, _, position ->
            selectCurrent(position)
        }
        if (mBinding.ryScaleBatch.itemDecorationCount == 0) {
            dividerBuilder()
                .color(Color.TRANSPARENT)
                .size(10, TypedValue.COMPLEX_UNIT_DIP)
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
        viewModel.currentUnitFiled.set(" " + indexData.unit)
        calType(indexData.unit)
        viewModel.thisPurchaseNumFiled.set("")
        viewModel.currentGoodsOrderNumFiled.set(indexData.quantity)
        viewModel.currentGoodsReceiveNumField.set(indexData.deliver_quantity)
        if (indexData.deliver_quantity != null && indexData.quantity != null) {
            viewModel.surplusNumFiled.set((indexData.quantity.toFloat() - indexData.deliver_quantity.toFloat()).toString())
        }
    }

    private fun refreshData(pos: Int) {
        for (index in 0 until purchaseAdapter.data.size) {
            purchaseAdapter.data[index].isCurrent = index == pos
            purchaseAdapter.notifyItemChanged(index)
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
            2 -> {
                if (viewModel.currentPrinterStatus.get() == 2) {
                    makeNewLabel()
                } else {
                    toast("未连接打印机或打印机故障")
                }

            }
        }
    }

    override fun subscribeModel(model: PurchaseViewModel) {
        super.subscribeModel(model)
        model.getPurchaseData().observe(this, {
            purchaseAdapter.setList(it)
            if (purchaseAdapter.data.size > 0) {
                selectCurrent(0)
                viewModel.getAllBatch(purchaseAdapter.data[0].item_id)
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
                viewModel.currentWeightValue.set(currentWeight)
                viewModel.thisPurchaseNumFiled.set(currentWeight)
            } else {
                viewModel.currentWeightValue.set("0.00")
            }

        } catch (ee: java.lang.Exception) {
            ee.printStackTrace()
            ToastUtils.show(ee.message!!)
        }
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
            }
        }
        val goodsName = viewModel.currentPurchaseGoodsFiled.get()
        val strQuantity = viewModel.currentGoodsOrderNumFiled.get()
        val allQuantity = strQuantity?.toFloat() ?: 0f
        val surplusQuantity =
            viewModel.thisPurchaseNumFiled.get()?.toFloat() ?: 0f + calCurrentSurplusNum()
        val currentQuantity = viewModel.thisPurchaseNumFiled.get()?.toFloat() ?: 0f
        val labelInfo = LabelInfo(
            goodsName,
            allQuantity,
            currentQuantity,
            surplusQuantity,
            taskItemInfo?.purchaser_name,
            viewModel.currentUnitFiled.get(),
            "www.baidu.com"
        )
        currentPurchaseLabelInfo.add(labelInfo)
        labelAdapter.setList(currentPurchaseLabelInfo)
        viewModel.currentGoodsReceiveNumField.set(surplusQuantity.toString())
        Printer.getInstance().printBitmap(
            getBitmap(
                this@PurchaseGoodsActivity,
                labelInfo,
                "0" + labelAdapter.data.size
            ), 460, labelInfo.qrcode, 440, 16
        )
    }

    /**
     * 计算当前分拣累计数量
     */
    private fun calCurrentSurplusNum(): Float {
        var allQuantity = viewModel.currentGoodsReceiveNumField.get()?.toFloat() ?: 0f
        labelAdapter.data.forEach {
            allQuantity += it.currentNum
        }
        return allQuantity
    }

    /**
     * 生成打印图片
     */
    private fun getBitmap(mContext: Context?, labelInfo: LabelInfo, batchNo: String): Bitmap {
        val v = View.inflate(
            mContext,
            R.layout.layout_print_item,
            null
        )
        val name = v.findViewById<AppCompatTextView>(R.id.tv_goods_title)
        val quantity = v.findViewById<AppCompatTextView>(R.id.tv_quantity)
        val batch = v.findViewById<AppCompatTextView>(R.id.tv_batch)
        val currentQu = v.findViewById<AppCompatTextView>(R.id.tv_current_num)
        val cus = v.findViewById<AppCompatTextView>(R.id.tv_customer)
        name.text = "商品名称:" + labelInfo.goodsName
        quantity.text = "订货数量:" + labelInfo.quantity.toString() + labelInfo.unit
        batch.text =
            "分拣货号:" + batchNo + "-" + labelInfo.currentNum + "-" + (labelInfo.quantity - labelInfo.deliver_quantity)
        currentQu.text = "本批数量:" + labelInfo.currentNum
        cus.text = "客户名称:" + labelInfo.customer
        return Printer.getInstance().convertViewToBitmap(v)
    }

}