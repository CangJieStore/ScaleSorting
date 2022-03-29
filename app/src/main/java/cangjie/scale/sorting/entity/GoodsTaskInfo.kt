package cangjie.scale.sorting.entity

/**
 * @author CangJie
 * @date 2021/11/4 10:47
 */
class GoodsTaskInfo(
    var trade_no: String = "******",
    var collect_date: String = "****-**-**",
    var item_count: Int = 0,
    var sorting_count: Int = 0,
    var receive_count: Int,
    val purchaser_count: Int,
    val goods: MutableList<TaskGoodsItem>?,
    val purchaser: MutableList<TaskGoodsItem>?
)