package com.hemnet.test.common.base

abstract class BaseViewModelWithFetchType<TYPE, STATE : BaseScreenState<TYPE>, FetchType>(
    repository: BaseRepositoryWithFetchType<TYPE, FetchType>,
    initialState: STATE
) : CoreBaseViewModel<TYPE, STATE, Nothing, FetchType>(repository, initialState)