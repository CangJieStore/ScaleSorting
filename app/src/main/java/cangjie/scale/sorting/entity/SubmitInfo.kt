package cangjie.scale.sorting.entity

import java.io.Serializable

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/16 11:31
 */
data class SubmitInfo(
    val id: String,
    val batch: String,
    val batch_count: String,
    val batch_path: String,
    val name: String,
    val receive_count: String?,
    val delivery_count: String,
    val receive_unit: String,
    val isLess: Int=0,
    val matchCount: String?,
    val matchPrice: String?,
    var costPrice: String?,
):Serializable