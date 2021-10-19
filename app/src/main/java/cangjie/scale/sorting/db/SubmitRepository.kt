package cangjie.scale.sorting.db

import androidx.lifecycle.LiveData

class SubmitRepository(private val orderDao: SubmitOrderDao) {

    val allOrders: LiveData<MutableList<SubmitOrder>> = orderDao.getAll()

    suspend fun insert(book: SubmitOrder) = orderDao.insert(book)

    suspend fun update(book: SubmitOrder) = orderDao.update(book)

    suspend fun delete(book: SubmitOrder) = orderDao.delete(book)

}