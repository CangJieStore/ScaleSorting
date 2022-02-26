package cangjie.scale.sorting.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * @author: guruohan
 * @date: 2022/2/25
 */
@Parcelize
@Entity(tableName = "t_label")
data class OrderLabel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "item_id") val itemId: String,
    @ColumnInfo(name = "goods_name") val goodsName: String,//商品名称
    @ColumnInfo(name = "quantity") val quantity: Float,//商品分拣总量
    @ColumnInfo(name = "current_num") val currentNum: Float,//本批数量
    @ColumnInfo(name = "deliver_quantity") val deliver_quantity: Float,//已分拣数量
    @ColumnInfo(name = "customer") val customer: String,//客户名称
    @ColumnInfo(name = "unit") val unit: String,//配送单位
    @ColumnInfo(name = "qrcode") val qrcode: String,//二维码链接
    @ColumnInfo(name = "batch_id") var batchId: String = ""//采购批次id
) : Parcelable