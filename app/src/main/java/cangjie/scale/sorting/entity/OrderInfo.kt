package cangjie.scale.sorting.entity

import java.io.Serializable

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/13 18:16
 */
data class OrderInfo(
    val id: String,
    val trade_no: String,
    val item_count: String,
    val sorting_count: String,
    val purchaser_count: String,
    val collect_date: String
) : Serializable