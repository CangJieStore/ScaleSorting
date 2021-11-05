package cangjie.scale.sorting.entity

/**
 * @author CangJie
 * @date 2021/11/4 10:47
 */
class GoodsTaskInfo(
    val trade_no: String,
    val collect_date: String,
    val item_count: Int,
    val sorting_count: Int,
    val purchaser_count: Int,
    val goods: MutableList<TaskGoodsItem>?,
    val purchaser: MutableList<TaskGoodsItem>?
)