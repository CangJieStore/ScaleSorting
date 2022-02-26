package cangjie.scale.sorting.vm

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cangjie.scale.sorting.base.ScaleApplication
import cangjie.scale.sorting.base.http.Url
import cangjie.scale.sorting.db.AppDatabase
import cangjie.scale.sorting.db.OrderLabel
import cangjie.scale.sorting.db.SubmitRepository
import cangjie.scale.sorting.entity.LabelInfo
import cangjie.scale.sorting.entity.MessageEvent
import cangjie.scale.sorting.entity.PurchaseInfo
import cangjie.scale.sorting.entity.StockInfo
import com.cangjie.frame.core.binding.BindingAction
import com.cangjie.frame.core.binding.BindingCommand
import com.cangjie.frame.core.event.MsgEvent
import com.cangjie.frame.kit.tab.Title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author CangJie
 * @date 2021/11/5 10:25
 */
class PurchaseViewModel : BaseScaleViewModel() {

    private val purchaseLiveData = MutableLiveData<MutableList<PurchaseInfo>>()
    private val stockLiveData = MutableLiveData<MutableList<StockInfo>>()
    private val labelLiveData = MutableLiveData<MutableList<LabelInfo>>()
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
    var currentBatchFiled = ObservableField<StockInfo>()
    var currentType = ObservableField(0)
    var currentPurchaseType = ObservableField(0)
    var currentPrinterStatus = ObservableField(0)
    var currentLabel = ObservableField<LabelInfo>()
    private val bookRepository: SubmitRepository


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
    var printAgainCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(20))
        }
    })
    var repurchaseCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(3))
        }
    })
    var submitCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(4))
        }
    })
    var resetZeroCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(6))
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
        postWithToken<MutableList<StockInfo>>(Url.purchase_batch, params, 200)
    }

    fun getSortedBatch(itemId: String) {
        loading("")
        val params = mutableMapOf<String, Any>("item_id" to itemId)
        postWithToken<MutableList<StockInfo>>(Url.sorting_batch, params, 210)
    }

    fun submit(itemId: String, batchId: String, quantity: String) {
        loading("")
        val params = mutableMapOf<String, Any>(
            "item_id" to itemId,
            "batch_id" to batchId,
            "quantity" to quantity,
            "type" to "goods"
        )
        postWithToken<String>(Url.submit_sorting, params, 201)
    }

    fun again(id: String) {
        loading("")
        val params = mutableMapOf<String, Any>("item_id" to id)
        postWithToken<Any>(Url.again, params, 204)
    }

    override fun success(code: Int, result: Any?) {
        super.success(code, result)
        dismissLoading()
        when (code) {
            100 -> {
                purchaseLiveData.postValue(result as MutableList<PurchaseInfo>)
            }
            200 -> {
                stockLiveData.postValue(result as MutableList<StockInfo>)
            }
            201 -> {
                action(MsgEvent(5))
            }
            204 -> {
                delete(currentInfoFiled.get()?.item_id.toString())
                action(MsgEvent(300))
            }
            210 -> {

            }
        }
    }

    init {
        val orderDao = AppDatabase.get(ScaleApplication.instance!!, viewModelScope).orderDao()
        bookRepository = SubmitRepository(orderDao)
    }

    fun add(book: List<OrderLabel>) = viewModelScope.launch(Dispatchers.IO) {
        bookRepository.insert(book)
    }

    fun get(itemId: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = bookRepository.get(itemId)
        val labels = mutableListOf<LabelInfo>()
        for (item in result) {
            val labelInfo = LabelInfo(
                item.goodsName,
                item.quantity,
                item.currentNum,
                item.deliver_quantity,
                item.customer,
                item.unit,
                item.qrcode,
                item.batchId
            )
            labels.add(labelInfo)
        }
        labelLiveData.postValue(labels)
    }

    fun delete(itemId: String) = viewModelScope.launch(Dispatchers.IO) {
        bookRepository.delete(itemId)
    }


    fun getPurchaseData() = purchaseLiveData
    fun getStockData() = stockLiveData
    fun getLabelData() = labelLiveData
    override fun error(errorCode: Int, errorMsg: String?) {
        super.error(errorCode, errorMsg)
        dismissLoading()
        toast(errorMsg!!)
    }

}