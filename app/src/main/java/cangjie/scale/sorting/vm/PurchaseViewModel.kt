package cangjie.scale.sorting.vm

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import cangjie.scale.sorting.base.http.Url
import cangjie.scale.sorting.entity.PurchaseInfo
import com.cangjie.frame.core.binding.BindingAction
import com.cangjie.frame.core.binding.BindingCommand
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.tab.Title

/**
 * @author CangJie
 * @date 2021/11/5 10:25
 */
class PurchaseViewModel : BaseScaleViewModel() {

    private val purchaseLiveData = MutableLiveData<MutableList<PurchaseInfo>>()
    var showStatusFiled = ObservableField(0)
    var currentPurchaseGoodsFiled = ObservableField("")
    var currentGoodsOrderNumFiled = ObservableField("")
    var currentGoodsReceiveNumField = ObservableField("")
    var thisPurchaseNumFiled = ObservableField("0.00")
    var surplusNumFiled = ObservableField("")
    var currentUnitFiled = ObservableField("")
    var scaleTypeFiled = ObservableField("")
    var currentWeightTypeFiled = ObservableBoolean(false)
    var currentWeightValue = ObservableField("0.00")
    var currentInfoFiled = ObservableField<PurchaseInfo>()
    var currentType = ObservableField(0)
    var currentPurchaseType = ObservableField(0)


    var finishCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(-1))
        }
    })
    var editPriceCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(1))
        }
    })
    var printCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(2))
        }
    })

    fun getUnPurchaseTask(taskType: Int, taskId: String, pId: String) {
        loading("")
        val params = mutableMapOf<String, Any>()
        if (taskType == 0) {
            params["id"] = taskId
            postWithToken<MutableList<PurchaseInfo>>(Url.goods_sorting, params, 100)
        } else {
            params["sorting_id"] = taskId
            params["purchaser_id"] = pId
            postWithToken<MutableList<PurchaseInfo>>(Url.customer_sorting, params, 100)
        }
    }

    fun getAllBatch(itemId: String) {
        loading("")
        val params = mutableMapOf<String, Any>("item_id" to itemId)
        postWithToken<String>(Url.purchase_batch, params, 200)
    }

    override fun success(code: Int, result: Any?) {
        super.success(code, result)
        dismissLoading()
        when (code) {
            100 -> {
                purchaseLiveData.postValue(result as MutableList<PurchaseInfo>)
            }
            200 -> {

            }
        }
    }

    fun getPurchaseData() = purchaseLiveData

    override fun error(errorCode: Int, errorMsg: String?) {
        super.error(errorCode, errorMsg)
        dismissLoading()
        toast(errorMsg!!)
    }

}