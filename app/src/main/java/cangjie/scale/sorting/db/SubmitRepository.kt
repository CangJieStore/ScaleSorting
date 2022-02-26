package cangjie.scale.sorting.db


class SubmitRepository(private val orderDao: OrderLabelDao) {

    suspend fun insert(book: List<OrderLabel>) = orderDao.insertList(book)

    suspend fun get(id: String) = orderDao.getAll(id)

    suspend fun delete(id:String)=orderDao.delete(id)

}