package com.hemnet.test.common.base

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher

abstract class BaseRepository<TYPE>(
    context: Context,
    ioDispatcher: CoroutineDispatcher
) : CoreBaseRepository<TYPE, Nothing>(context, ioDispatcher)