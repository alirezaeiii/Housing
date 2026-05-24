package com.hemnet.test.common.base

import android.content.Context
import com.hemnet.test.common.R
import com.hemnet.test.common.utils.Async
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseRepository<TYPE, QueryType, FetchType>(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val cacheDurationMs: Long = 60_000L
) {

    private var lastRefreshTime: Long = 0L

    protected abstract suspend fun query(queryValue: QueryType?): TYPE?

    protected abstract suspend fun fetch(fetchValue: FetchType?): TYPE

    protected abstract suspend fun saveFetchResult(item: TYPE)

    fun getResult(
        queryValue: QueryType? = null,
        fetchValue: FetchType? = null,
        forceRefresh: Boolean = true
    ): Flow<Async<TYPE>> =
        flow {
            emit(Async.Loading())
            val dbData = query(queryValue)

            when {
                dbData == null -> load(
                    queryValue = queryValue,
                    fetchValue = fetchValue,
                    forceRefresh = forceRefresh
                )

                dbData is List<*> && dbData.isEmpty() -> load(
                    queryValue = queryValue,
                    fetchValue = fetchValue,
                    forceRefresh = forceRefresh
                )

                else -> load(dbData, queryValue, fetchValue, forceRefresh)
            }
        }.flowOn(ioDispatcher)

    private suspend fun FlowCollector<Async<TYPE>>.load(
        dbData: TYPE? = null,
        queryValue: QueryType?,
        fetchValue: FetchType?,
        forceRefresh: Boolean = true
    ) {
        dbData?.let {
            // ****** VIEW CACHE ******
            emit(Async.Success(it))
        }
        try {
            if (forceRefresh || isCacheExpired()) {
                if (dbData != null) {
                    emit(Async.Loading(true))
                }
                // ****** MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
                refresh(fetchValue)
                lastRefreshTime = System.currentTimeMillis()
                // ****** VIEW CACHE ******
                emit(Async.Success(query(queryValue)!!))
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

    private suspend fun refresh(fetchValue: FetchType?) {
        saveFetchResult(fetch(fetchValue))
    }

    private fun isCacheExpired(): Boolean {
        return System.currentTimeMillis() - lastRefreshTime > cacheDurationMs
    }
}