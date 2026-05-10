package com.hemnet.test.common.base

import android.content.Context
import com.hemnet.test.common.R
import com.hemnet.test.common.utils.Async
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class CoreBaseRepository<TYPE, QueryType>(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {
    protected abstract suspend fun query(queryType: QueryType?): TYPE?

    protected abstract suspend fun fetch(): TYPE

    protected abstract suspend fun saveFetchResult(item: TYPE)

    fun getResult(queryType: QueryType? = null): Flow<Async<TYPE>> = flow {
        emit(Async.Loading())
        val dbData = query(queryType)

        when {
            dbData == null -> load(queryType = queryType)
            dbData is List<*> && dbData.isEmpty() -> load(queryType = queryType)
            else -> load(dbData, queryType)
        }
    }.flowOn(ioDispatcher)

    private suspend fun FlowCollector<Async<TYPE>>.load(dbData: TYPE? = null, queryType: QueryType?) {
        dbData?.let {
            // ****** VIEW CACHE ******
            emit(Async.Success(it))
            emit(Async.Loading(true))
        }
        try {
            // ****** MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
            refresh()
            // ****** VIEW CACHE ******
            emit(Async.Success(query(queryType)!!))
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