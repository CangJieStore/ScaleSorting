package cangjie.scale.sorting.entity

/**
 * @author: guruohan
 * @date: 2021/12/5
 */
class StockInfo(
    val batch_id: String,
    val trade_no: String,
    val name: String,
    val unit: String,
    val stock: Float,
    val collect_date: String,
    var isCurrent: Boolean,
    val qrcode: String,
    val spec: String
)