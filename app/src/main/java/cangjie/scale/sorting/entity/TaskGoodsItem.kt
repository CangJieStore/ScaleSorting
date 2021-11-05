package cangjie.scale.sorting.entity

import java.io.Serializable

/**
 * @author CangJie
 * @date 2021/11/4 10:49
 */
class TaskGoodsItem(
    val id: String,
    val goods_id: String,
    val name: String?,
    val spec: String,
    val unit: String,
    val quantity: String,
    val receive_quantity: String,
    val purchaser_count: Int,
    val picture: String,
    val sorting_id: String,
    val purchaser_id: String,
    val item_count: Int,
    val sorting_count: Int,
    val purchaser_name: String
) : Serializable