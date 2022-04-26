package io.maddevs.paginationjetpack

class DefaultPaginator<Key,Item>(
    private val initialKey : Key,
    private inline val onLoadUpdated : (Boolean) -> Unit,
    private inline val onRequest : suspend (nextKey : Key) -> Result<List<Item>>, //определяет, как мы получим следующие загрузки item
    private inline val getNextKey : suspend (List<Item>) -> Key,
    private inline val onError : suspend (Throwable?) -> Unit,
    private inline val onSucces :  suspend (items : List<Item>, newKey: Key) -> Unit
) : Paginator<Key,Item> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override suspend fun loadNextItems() {
       if (isMakingRequest){
           return
       }
        isMakingRequest = true   //если making request дальше загружаетс
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false //проистонавливаем следующ загрузку т.к загрузили
        val items = result.getOrElse {
            onError(it)  //если получили ошибку
            onLoadUpdated(false)  //обновлен загрузки делаетс false
            return //возвращаемс к следующ загрузке
        }
        //если след загруз прошла успешна
        currentKey = getNextKey(items)
        onSucces(items,currentKey)
        onLoadUpdated(false)
    }

    override fun reset() {
        currentKey = initialKey
    }
}