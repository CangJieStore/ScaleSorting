package cangjie.scale.sorting.vm

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import cangjie.scale.sorting.base.http.Url
import cangjie.scale.sorting.entity.GoodsTaskInfo
import cangjie.scale.sorting.entity.TaskGoodsItem
import com.cangjie.frame.core.binding.BindingAction
import com.cangjie.frame.core.binding.BindingCommand
import com.cangjie.frame.core.event.MsgEvent

/**
 * @author: guruohan
 * @date: 2021/10/24
 */
class TaskViewModel : BaseScaleViewModel() {

    private val taskLiveData = MutableLiveData<GoodsTaskInfo>()
    var currentGoodsItem = ObservableField<TaskGoodsItem>()

    var finishCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(-1))
        }
    })

    fun getProjectByGoods(oid: String, cate: String, type: Int) {
        loading("")
        val params = mutableMapOf<String, Any>("id" to oid, "state" to cate)
        if (type == 0) {
            postWithToken<GoodsTaskInfo>(Url.sorting_goods, params, 100)
        } else {
            postWithToken<GoodsTaskInfo>(Url.sorting_purchaser, params, 100)
        }
    }

    fun receiveTask(taskId: String, purchaserId: String, taskType: Int) {
        loading(" ")
        val params = mutableMapOf<String, Any>()
        var url = ""
        if (taskType == 1) {
            params["sorting_id"] = taskId
            params["purchaser_id"] = purchaserId
            url = Url.tasks_item
        } else {
            params["id"] = taskId
            url = Url.goods_item
        }
        postWithToken<String>(url, params, 200)
    }

    override fun success(code: Int, result: Any?) {
        super.success(code, result)
        dismissLoading()
        when (code) {
            100 -> {
                taskLiveData.postValue(result as GoodsTaskInfo)
            }
            200 -> {
                toast("领取成功")
                action(MsgEvent(100))
            }
        }

    }

    fun getTaskData() = taskLiveData

    override fun error(errorCode: Int, errorMsg: String?) {
        super.error(errorCode, errorMsg)
        dismissLoading()
        toast(errorMsg!!)
    }
}