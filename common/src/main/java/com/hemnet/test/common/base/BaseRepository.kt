package com.hemnet.test.common.base

import android.content.Context
import com.hemnet.test.common.R
import com.hemnet.test.common.utils.Async
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseRepository<T>(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {
    protected abstract suspend fun query(type: Int?): T?

    protected abstract suspend fun fetch(): T

    protected abstract suspend fun saveFetchResult(item: T)

    fun getResult(type: Int? = null): Flow<Async<T>> = flow {
        emit(Async.Loading())
        val dbData = query(type)

        when {
            dbData == null -> load(type = type)
            dbData is List<*> && dbData.isEmpty() -> load(type = type)
            else -> load(dbData, type)
        }
    }.flowOn(ioDispatcher)

    private suspend fun FlowCollector<Async<T>>.load(dbData: T? = null, type: Int?) {
        dbData?.let {
            // ****** VIEW CACHE ******
            emit(Async.Success(it))
            emit(Async.Loading(true))
        }
        try {
            // ****** MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
            refresh()
            // ****** VIEW CACHE ******
            emit(Async.Success(query(type)!!))
        } catch (_: Throwable) {
            emit(
                Async.Error(
                    context.getString(
                        if (dbData == null) R.string.error_msg else R.string.refresh_error_msg
                    ),
                    dbData != null
                )
            )
        }
    }

    private suspend fun refresh() {
        saveFetchResult(fetch())
    }
}