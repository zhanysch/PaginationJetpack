package io.maddevs.paginationjetpack

interface Paginator<Key,Item> {
    suspend fun loadNextItems() // загрузка следующ items
    fun reset()  // при запуске сначала items
}