package cangjie.scale.sorting.entity

import java.io.Serializable

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/13 18:16
 */
data class GoodsInfo(
    val id: String,
    val name: String,
    val unit: String,
    val buy_quantity: String,
    val deliver_quantity: String,
    var receive_quantity: String,
    val deliver_price: String,
    val receive_loss: String,
    val receive_price: String,
    val spec: String,
    val receive_date: String,
    val path: String,
    var batch: String,
    val repair_receive: String,
    var isRepair: Boolean,
    var is_sorting: Int,
    var isLess: Int = 0,
    var matchCount: String?,
    var matchPrice: String?,
    var costPrice: String?
) : Serializable