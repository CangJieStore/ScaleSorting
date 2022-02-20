package cangjie.scale.sorting.entity

import java.io.Serializable

/**
 * @author: guruohan
 * @date: 2021/11/6
 */
class LabelInfo(
    val goodsName: String?,//商品名称
    val quantity: Float,//商品分拣总量
    val currentNum: Float,//本批数量
    val deliver_quantity: Float,//已分拣数量
    val customer: String?,//客户名称
    val unit: String?,//配送单位
    val qrcode: String,//二维码链接
    var batchId: String = ""//采购批次id
):Serializable