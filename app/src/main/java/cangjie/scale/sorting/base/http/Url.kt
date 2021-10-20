package cangjie.scale.sorting.base.http

import rxhttp.wrapper.annotation.DefaultDomain

object Url {

    @JvmField
    @DefaultDomain
    var baseUrl = "https://ps.shian360.com/"

    const val update = "openapi/sorting/?op=upgrade"
    const val login = "openapi/sorting/?op=login"
    const val tasks = "openapi/sorting/?op=task"
    const val sorting_goods = "openapi/sorting/?op=goods_task_item"
    const val goods_item = "openapi/sorting/?op=goods_task_receive"
    const val sorting_purchaser = "openapi/sorting/?op=purchaser_task_item"
    const val tasks_item = "openapi/sorting/?op=purchaser_task_receive"

    const val submit = "supplier/steelyard/?op=weight"
    const val upload = "supplier/steelyard/?op=picture"
    const val again = "supplier/steelyard/?op=repair"
    const val clear = "supplier/steelyard/?op=clear"
}