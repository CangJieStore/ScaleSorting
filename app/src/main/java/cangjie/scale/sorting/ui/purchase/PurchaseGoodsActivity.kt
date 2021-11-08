package cangjie.scale.sorting.ui.purchase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.ActivityPurchaseGoodsBinding
import cangjie.scale.sorting.entity.TaskGoodsItem
import cangjie.scale.sorting.scale.FormatUtil
import cangjie.scale.sorting.scale.ScaleModule
import cangjie.scale.sorting.scale.SerialPortUtilForScale
import cangjie.scale.sorting.vm.PurchaseViewModel
import coil.load
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


    override fun initVariableId(): Int = BR.purchaseModel

    override fun initActivity(savedInstanceState: Bundle?) {
        taskItemInfo = intent.getSerializableExtra("item") as TaskGoodsItem
        initPurchase()
        setData(taskItemInfo)
    }

    private fun setData(itemData: TaskGoodsItem?) {
        itemData?.let {
            viewModel.currentType.set(0)
            Log.e("info", Gson().toJson(it))
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
            purchaseAdapter.data[position].isCurrent = true
            purchaseAdapter.notifyItemChanged(position)
        }
        mBinding.ryScaleBatch.adapter = labelAdapter
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
                makeNewLabel()
            }
        }
    }

    override fun subscribeModel(model: PurchaseViewModel) {
        super.subscribeModel(model)
        model.getPurchaseData().observe(this, {
            purchaseAdapter.setList(it)
            if (purchaseAdapter.data.size > 0) {
                purchaseAdapter.data[0].isCurrent = true
                val indexData = purchaseAdapter.data[0]
                viewModel.currentInfoFiled.set(indexData)
                viewModel.currentUnitFiled.set(" " + indexData.unit)
                viewModel.currentGoodsReceiveNumField.set(indexData.deliver_quantity)
                if (indexData.deliver_quantity != null && indexData.quantity != null) {
                    viewModel.surplusNumFiled.set((indexData.quantity.toFloat() - indexData.deliver_quantity.toFloat()).toString())
                }
                purchaseAdapter.notifyItemChanged(0)
                viewModel.getAllBatch(purchaseAdapter.data[0].item_id)
            }
        })
    }

    override fun initImmersionBar() {
        super.initImmersionBar()
        immersionBar {
            fullScreen(true)
            hideBar(BarHide.FLAG_HIDE_BAR)
            statusBarDarkFont(false)
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
        Thread {
            SerialPortUtilForScale.Instance().CloseSerialPort()
            SerialPortUtilForScale.Instance().OpenSerialPort() //打开称重串口
            try {
                ScaleModule.Instance(this@PurchaseGoodsActivity) //初始化称重模块
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                runOnUiThread {
                    show(this@PurchaseGoodsActivity, 2000, "初始化称重主板错误！")
                }
            }
        }.start()
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

    override fun onStart() {
        super.onStart()
        initWeight()
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
        unregisterReceiver(readDataReceiver)
        SerialPortUtilForScale.Instance().CloseSerialPort()
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
        var goodsName = ""
        goodsName = if (viewModel.currentType.get() == 0) {
            viewModel.currentPurchaseGoodsFiled.get().toString()
        } else {
            viewModel.currentInfoFiled.get()?.name.toString()
        }
        val strQuantity = viewModel.currentInfoFiled.get()?.quantity
        val quantity = strQuantity?.toFloat() ?: 0f
//        val labelInfo = LabelInfo(goodsName, quantity,viewModel.thisPurchaseNumFiled.get())
    }

}