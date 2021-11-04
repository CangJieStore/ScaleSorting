package cangjie.scale.sorting.vm

import androidx.lifecycle.MutableLiveData
import cangjie.scale.sorting.base.http.Url
import cangjie.scale.sorting.entity.GoodsTaskInfo
import com.cangjie.frame.core.binding.BindingAction
import com.cangjie.frame.core.binding.BindingCommand
import com.cangjie.frame.core.event.MsgEvent

/**
 * @author: guruohan
 * @date: 2021/10/24
 */
class TaskViewModel : BaseScaleViewModel() {

    private val taskLiveData = MutableLiveData<GoodsTaskInfo>()

    var finishCommand: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(-1))
        }
    })

    fun getProjectByGoods(oid: String, cate: String, type: Int) {
        loading("")
        val params = mutableMapOf<String, Any>("id" to oid, "state" to cate)
        if(type==0){
            postWithToken<GoodsTaskInfo>(Url.sorting_goods, params, 100)
        }else{
            postWithToken<GoodsTaskInfo>(Url.sorting_purchaser, params, 100)
        }
    }

    override fun success(code: Int, result: Any?) {
        super.success(code, result)
        dismissLoading()
        when (code) {
            100 -> {
                taskLiveData.postValue(result as GoodsTaskInfo)
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