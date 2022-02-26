package cangjie.scale.sorting.db

import androidx.room.*

/**
 * @author: guruohan
 * @date: 2022/2/25
 */
@Dao
interface OrderLabelDao {
    @Query(value = "SELECT * FROM t_label WHERE item_id =:itemId")
    suspend fun getAll(itemId: String): List<OrderLabel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(labelList: List<OrderLabel>)

    @Query(value = "DELETE FROM t_label where item_id = :itemId")
    suspend fun delete(itemId: String)
}