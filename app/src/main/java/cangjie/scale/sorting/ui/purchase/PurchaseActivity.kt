package cangjie.scale.sorting.ui.purchase

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import cangjie.scale.sorting.BR
import cangjie.scale.sorting.R
import cangjie.scale.sorting.databinding.ActivityPurchaseBinding
import cangjie.scale.sorting.entity.TaskGoodsItem
import cangjie.scale.sorting.vm.PurchaseViewModel
import coil.load
import com.cangjie.frame.core.BaseMvvmActivity
import com.cangjie.frame.core.event.MsgEvent
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar

/**
 * @author CangJie
 * @date 2021/11/5 10:23
 */
class PurchaseActivity : BaseMvvmActivity<ActivityPurchaseBinding, PurchaseViewModel>() {

    private val purchaseAdapter by lazy {
        PurchaseAdapter()
    }

    override fun layoutId(): Int = R.layout.activity_purchase
    private var taskItemInfo: TaskGoodsItem? = null
    override fun initVariableId(): Int = BR.purchaseModel

    override fun initActivity(savedInstanceState: Bundle?) {
        taskItemInfo = intent.getSerializableExtra("item") as TaskGoodsItem
        initPurchase()
        taskItemInfo?.let {
            var showTitle = ""
            if (it.name != null) {
                viewModel.purchaseNameFiled.set("客户名称")
                viewModel.handleFiled.set("操作")
                showTitle = "商品名称:" + it.name
                viewModel.getUnPurchaseTask(0, it.id)
                mBinding.ivGoodsImg.load(it.picture)
            } else {
                showTitle = "客户名称:" + it.purchaser_name
                viewModel.purchaseNameFiled.set("商品名称")
                viewModel.handleFiled.set("商品批次")
                viewModel.getUnPurchaseTask(1, it.sorting_id)
            }
            mBinding.tvPurchaseTitle.text = showTitle
        }
        mBinding.tabPurcahse.apply {
            configTabLayoutConfig {
                onSelectIndexChange = { _, selectIndexList, _, _ ->
                    val toIndex = selectIndexList.first()
                    viewModel.showStatusFiled.set(toIndex)
                }
            }
        }
    }


    private fun initPurchase() {
        dividerBuilder()
            .color(Color.parseColor("#cccccc"))
            .size(1, TypedValue.COMPLEX_UNIT_DIP)
            .showLastDivider()
            .build()
            .addTo(mBinding.ryPurchase)
        mBinding.ryPurchase.adapter = purchaseAdapter
    }

    override fun handleEvent(msg: MsgEvent) {
        super.handleEvent(msg)
        when (msg.code) {
            -1 -> {
                finish()
            }
        }
    }

    override fun subscribeModel(model: PurchaseViewModel) {
        super.subscribeModel(model)
        model.getPurchaseData().observe(this, {
            purchaseAdapter.setList(it)
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
}