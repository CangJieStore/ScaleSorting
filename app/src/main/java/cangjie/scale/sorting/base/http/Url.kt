package cangjie.scale.sorting.base.http

import rxhttp.wrapper.annotation.DefaultDomain

object Url {

    @JvmField
    @DefaultDomain
    var baseUrl = "https://api.shian360.com/"

    const val update = "supplier/steelyard/?op=upgrade"
    const val login = "supplier/steelyard/?op=login"
    const val orders = "supplier/steelyard/?op=order"
    const val submit = "supplier/steelyard/?op=weight"
    const val upload = "supplier/steelyard/?op=picture"
    const val again = "supplier/steelyard/?op=repair"
    const val clear = "supplier/steelyard/?op=clear"
}