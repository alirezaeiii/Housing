package com.hemnet.test.domain.repository

import android.content.Context
import com.hemnet.test.R
import com.hemnet.test.utils.Async
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
            dbData == null -> load(type)
            dbData is List<*> && dbData.isEmpty() -> load(type)
            else -> refresh(dbData, type)
        }
    }.flowOn(ioDispatcher)

    private suspend fun FlowCollector<Async<T>>.load(type: Int?) {
        try {
            // ****** MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
            refresh()
            // ****** VIEW CACHE ******
            emit(Async.Success(query(type)!!))
        } catch (_: Throwable) {
            emit(Async.Error(context.getString(R.string.error_msg)))
        }
    }

    private suspend fun FlowCollector<Async<T>>.refresh(dbData: T, type: Int?) {
        // ****** VIEW CACHE ******
        emit(Async.Success(dbData))
        emit(Async.Loading(true))
        try {
            // ****** MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
            refresh()
            // ****** VIEW CACHE ******
            emit(Async.Success(query(type)!!))
        } catch (_: Throwable) {
            emit(Async.Error(context.getString(R.string.refresh_error_msg), true))
        }
    }

    private suspend fun refresh() {
        saveFetchResult(fetch())
    }
}