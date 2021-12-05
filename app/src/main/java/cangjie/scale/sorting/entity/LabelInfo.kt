package cangjie.scale.sorting.entity

import java.io.Serializable

/**
 * @author: guruohan
 * @date: 2021/11/6
 */
class LabelInfo(
    val goodsName: String?,
    val quantity: Float,
    val currentNum: Float,
    val deliver_quantity: Float,
    val customer: String?,
    val unit: String?,
    val qrcode: String,
    var batchId: String = ""
):Serializable