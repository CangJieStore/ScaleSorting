package cangjie.scale.sorting.vm

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
    var purchaseNameFiled = ObservableField("")
    var handleFiled = ObservableField("")

    var finishCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(-1))
        }
    })

    fun getUnPurchaseTask(taskType: Int, taskId: String) {
        loading("")
        val params = mutableMapOf<String, Any>("id" to taskId)
        if (taskType == 0) {
            postWithToken<MutableList<PurchaseInfo>>(Url.goods_sorting, params, 100)
        } else {
            postWithToken<MutableList<PurchaseInfo>>(Url.customer_sorting, params, 100)
        }

    }

    override fun success(code: Int, result: Any?) {
        super.success(code, result)
        dismissLoading()
        when (code) {
            100 -> {
                purchaseLiveData.postValue(result as MutableList<PurchaseInfo>)
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