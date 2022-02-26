package cangjie.scale.sorting.vm

import androidx.collection.arrayMapOf
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cangjie.scale.sorting.base.ScaleApplication
import cangjie.scale.sorting.base.http.Url
import cangjie.scale.sorting.db.AppDatabase
import cangjie.scale.sorting.db.OrderLabel
import cangjie.scale.sorting.db.SubmitOrder
import cangjie.scale.sorting.db.SubmitRepository
import cangjie.scale.sorting.entity.LoginInfo
import cangjie.scale.sorting.entity.OrderInfo
import cangjie.scale.sorting.entity.Update
import com.cangjie.frame.core.binding.BindingAction
import com.cangjie.frame.core.binding.BindingCommand
import com.cangjie.frame.core.db.CangJie
import com.cangjie.frame.core.event.MsgEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author: guruohan
 * @date: 2021/9/9
 */
class ScaleViewModel : BaseScaleViewModel() {
    var usernameFiled = ObservableField<String>()
    var passwordFiled = ObservableField<String>()
    var chooseDateFiled = ObservableField<String>()
    var currentOrder = MutableLiveData<OrderInfo>()
    var updateData = MutableLiveData<Update>()

    var showStatusFiled = ObservableField(0)

    private val bookRepository: SubmitRepository

    private val orderLiveData = MutableLiveData<MutableList<OrderInfo>>()

    var loginCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            if (usernameFiled.get().isNullOrEmpty() || passwordFiled.get().isNullOrEmpty()) {
                toast("请输入账户或密码")
            } else {
                loading("登录中...")
                val params =
                    arrayMapOf<String, Any>(
                        "username" to usernameFiled.get().toString(),
                        "password" to passwordFiled.get().toString()
                    )
                post<LoginInfo>(Url.login, params, 200)
            }
        }
    })

    var loginOut: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            CangJie.clearAll()
            action(MsgEvent(1))
        }
    })
    var chooseDate: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(2))
        }
    })
    var detailClose: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(3))
        }
    })
    var clearCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(4))
        }
    })
    var removeShellCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(119))
        }
    })
    var submitCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(5))
        }
    })
    var searchCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(6))
        }
    })
    var checkCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            showStatusFiled.set(0)
        }
    })
    var unCheckCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            showStatusFiled.set(1)
        }
    })
    var resetZeroCmd: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(700))
        }
    })

    var takePhoto: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(7))
        }
    })

    fun loadMain(date: String) {
        loading("获取订单...")
        val params = mutableMapOf<String, Any>("day" to date)
        postWithToken<MutableList<OrderInfo>>(Url.tasks, params, 201)
    }

    fun loadDetail(id: String) {
        loading("获取订单...")
        val params = mutableMapOf<String, Any>("order_id" to id)
        postWithToken<MutableList<OrderInfo>>(Url.tasks, params, 203)
    }

    fun loadUpdate() {
        postWithToken<Update>(Url.update, arrayMapOf(), 210)
    }


    fun submit(
        id: String,
        quantity: String,
        isLess: Int,
        cPrice: String,
        count: String,
        price: String,
        submitType: String
    ) {
        loading("提交中...")
        val params = mutableMapOf<String, Any>(
            "id" to id,
            "quantity" to quantity,
            "loss" to isLess,
            "price" to cPrice,
            "deliver_quantity" to count,
            "deliver_price" to price,
            "type" to submitType
        )
        postWithToken<Any>(Url.submit, params, 202)

    }

    fun clear(id: String) {
        loading("请求中...")
        val params = mutableMapOf<String, Any>("id" to id)
        postWithToken<Any>(Url.clear, params, 223)
    }

    fun again(id: String) {
        loading("请求中...")
        val params = mutableMapOf<String, Any>("id" to id)
        postWithToken<Any>(Url.again, params, 204)
    }

    override fun success(code: Int, result: Any?) {
        super.success(code, result)
        dismissLoading()
        when (code) {
            200 -> {
                val loginInfo = result as LoginInfo
                CangJie.put("token", loginInfo.access_token)
                toast("登录成功")
                action(MsgEvent(0))
            }
            201 -> {
                val orderInfo = result as MutableList<OrderInfo>
                orderLiveData.postValue(orderInfo)
            }
            202 -> {
                action(MsgEvent(200))
            }
            223 -> {
                action(MsgEvent(223))
            }
            203 -> {
                val currentOrderInfo = result as MutableList<OrderInfo>
                if (currentOrderInfo.size > 0) {
                    currentOrder.postValue(currentOrderInfo[0])
                }
            }
            204 -> {
                action(MsgEvent(300))
            }
            210 -> {
                val up = result as Update
                updateData.postValue(up)
            }

        }
    }

    fun getOrderInfo(): MutableLiveData<MutableList<OrderInfo>> = orderLiveData
    fun getUpdate(): MutableLiveData<Update> = updateData

    override fun error(errorCode: Int, errorMsg: String?) {
        super.error(errorCode, errorMsg)
        dismissLoading()
        toast(errorMsg!!)
    }

    init {
        val orderDao = AppDatabase.get(ScaleApplication.instance!!, viewModelScope).orderDao()
        bookRepository = SubmitRepository(orderDao)
    }

    fun add(book: List<OrderLabel>) = viewModelScope.launch(Dispatchers.IO) {
        bookRepository.insert(book)
    }
}