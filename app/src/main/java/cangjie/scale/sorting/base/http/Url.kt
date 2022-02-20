package cangjie.scale.sorting.base.http

import rxhttp.wrapper.annotation.DefaultDomain

object Url {

    @JvmField
    @DefaultDomain
    var baseUrl = "https://api.shian360.com/"

    const val update = "supplier/sorting/?op=upgrade"
    const val login = "supplier/sorting/?op=login"
    const val tasks = "supplier/sorting/?op=task"
    const val sorting_goods = "supplier/sorting/?op=goods_task_item"
    const val goods_item = "supplier/sorting/?op=goods_task_receive"
    const val sorting_purchaser = "supplier/sorting/?op=purchaser_task_item"
    const val tasks_item = "supplier/sorting/?op=purchaser_task_receive"

    const val goods_sorting = "supplier/sorting/?op=goods_sorting"
    const val customer_sorting = "supplier/sorting/?op=purchaser_sorting"
    const val purchase_batch = "supplier/sorting/?op=goods_batch"
    const val submit_sorting = "supplier/sorting/?op=fill"

    const val submit = "supplier/steelyard/?op=weight"
    const val upload = "supplier/steelyard/?op=picture"
    const val again = "supplier/sorting/?op=fill_clear "
    const val clear = "supplier/steelyard/?op=clear"
    const val sorting_batch = "supplier/sorting/?op=goods_item_batch"
}