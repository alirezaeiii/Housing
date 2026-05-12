package com.hemnet.test.common.base

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher

abstract class BaseRepositoryWithQueryType<TYPE, QueryType>(
    context: Context,
    ioDispatcher: CoroutineDispatcher
) : CoreBaseRepository<TYPE, QueryType, Nothing>(context, ioDispatcher)