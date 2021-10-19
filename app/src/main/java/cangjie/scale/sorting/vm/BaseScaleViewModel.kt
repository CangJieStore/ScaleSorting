package cangjie.scale.sorting.vm


import androidx.lifecycle.viewModelScope
import cangjie.scale.sorting.base.http.HttpResultCallback
import cangjie.scale.sorting.base.http.errorCode
import cangjie.scale.sorting.base.http.errorMsg
import com.cangjie.frame.core.BaseViewModel
import com.cangjie.frame.core.db.CangJie
import kotlinx.coroutines.launch
import rxhttp.RxHttp
import rxhttp.awaitResult
import rxhttp.toResultInfo


/**
 * @author: guruohan
 * @date: 2021/9/9
 */
open class BaseScaleViewModel : BaseViewModel(), HttpResultCallback {
    inline fun <reified T : Any> post(
        url: String,
        params: MutableMap<String, Any>,
        requestCode: Int
    ) {
        viewModelScope.launch {
            start()
            RxHttp.postForm(url).addAll(params).toResultInfo<T>().awaitResult {
                success(requestCode, it)
            }.onFailure {
                error(it.errorCode, it.errorMsg)
            }
        }
    }

    inline fun <reified T : Any> postWithToken(
        url: String,
        params: MutableMap<String, Any>,
        requestCode: Int
    ) {
        params["access_token"] = CangJie.getString("token")
        viewModelScope.launch {
            RxHttp.postForm(url).addAll(params).toResultInfo<T>().awaitResult {
                success(requestCode, it)
            }.onFailure {
                error(it.errorCode, it.errorMsg)
            }
        }
    }

    override fun start() {
    }

    override fun success(code: Int, result: Any?) {
    }

    override fun error(errorCode: Int, errorMsg: String?) {
    }

    override fun complete() {
    }
}