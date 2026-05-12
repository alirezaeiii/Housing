package com.hemnet.test.common.base

abstract class BaseViewModel<TYPE, STATE : BaseScreenState<TYPE>>(
    repository: BaseRepository<TYPE>,
    initialState: STATE
) : CoreBaseViewModel<TYPE, STATE, Nothing, Nothing>(repository, initialState)