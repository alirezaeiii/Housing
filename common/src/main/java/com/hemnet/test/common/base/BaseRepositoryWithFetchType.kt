package com.hemnet.test.common.base

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher

abstract class BaseRepositoryWithFetchType<TYPE, FetchType>(
    context: Context,
    ioDispatcher: CoroutineDispatcher
) : CoreBaseRepository<TYPE, Nothing, FetchType>(context, ioDispatcher)