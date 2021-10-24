package cangjie.scale.sorting.vm

import cangjie.scale.sorting.base.http.Url
import com.cangjie.frame.core.binding.BindingAction
import com.cangjie.frame.core.binding.BindingCommand
import com.cangjie.frame.core.event.MsgEvent

/**
 * @author: guruohan
 * @date: 2021/10/24
 */
class TaskViewModel : BaseScaleViewModel() {
    var finishCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(-1))
        }
    })

    fun getProjectByGoods(oid: String, cate: String) {
        loading("")
        val params = mutableMapOf<String, Any>("id" to oid, "state" to cate)
        postWithToken<Any>(Url.sorting_goods, params, 100)
    }

    override fun success(code: Int, result: Any?) {
        super.success(code, result)
        dismissLoading()
    }

    override fun error(errorCode: Int, errorMsg: String?) {
        super.error(errorCode, errorMsg)
        dismissLoading()
        toast(errorMsg!!)
    }
}