package cangjie.scale.sorting.entity

/**
 * @author CangJie
 * @date 2021/11/5 11:20
 */
class PurchaseInfo(
    val item_id: String,
    val unit: String,
    val quantity: String?,
    val deliver_quantity: String?,
    val purchaser: String?,
    val name: String?,
    val trade_no: String?,
    val picture: String?,
    var isCurrent: Boolean,
)