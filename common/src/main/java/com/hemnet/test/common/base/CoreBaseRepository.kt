package com.hemnet.test.common.base

import android.content.Context
import com.hemnet.test.common.R
import com.hemnet.test.common.utils.Async
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class CoreBaseRepository<TYPE, QueryType, FetchType>(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val cacheDurationMs: Long = 60_000L
) {

    private var lastRefreshTime: Long = 0L

    protected abstract suspend fun query(queryType: QueryType?): TYPE?

    protected abstract suspend fun fetch(fetchType: FetchType?): TYPE

    protected abstract suspend fun saveFetchResult(item: TYPE)

    fun getResult(
        queryType: QueryType? = null,
        fetchType: FetchType? = null,
        forceRefresh: Boolean = true
    ): Flow<Async<TYPE>> =
        flow {
            emit(Async.Loading())
            val dbData = query(queryType)

            when {
                dbData == null -> load(
                    queryType = queryType,
                    fetchType = fetchType,
                    forceRefresh = forceRefresh
                )

                dbData is List<*> && dbData.isEmpty() -> load(
                    queryType = queryType,
                    fetchType = fetchType,
                    forceRefresh = forceRefresh
                )

                else -> load(dbData, queryType, fetchType, forceRefresh)
            }
        }.flowOn(ioDispatcher)

    private suspend fun FlowCollector<Async<TYPE>>.load(
        dbData: TYPE? = null,
        queryType: QueryType?,
        fetchType: FetchType?,
        forceRefresh: Boolean = true
    ) {
        dbData?.let {
            // ****** VIEW CACHE ******
            emit(Async.Success(it))
        }
        try {
            // ****** MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
            if (forceRefresh || isCacheExpired()) {
                if (dbData != null) {
                    emit(Async.Loading(true))
                }
                refresh(fetchType)
                lastRefreshTime = System.currentTimeMillis()
                // ****** VIEW CACHE ******
                emit(Async.Success(query(queryType)!!))
            }
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

    private suspend fun refresh(fetchType: FetchType?) {
        saveFetchResult(fetch(fetchType))
    }

    private fun isCacheExpired(): Boolean {
        return System.currentTimeMillis() - lastRefreshTime > cacheDurationMs
    }
}