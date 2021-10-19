package cangjie.scale.sorting.base.http

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/11 11:24
 */
interface ResultCallback<T> {
    fun success(result: T)
}