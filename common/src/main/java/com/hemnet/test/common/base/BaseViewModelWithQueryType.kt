package com.hemnet.test.common.base

abstract class BaseViewModelWithQueryType<TYPE, STATE : BaseScreenState<TYPE>, QueryType>(
    repository: BaseRepositoryWithQueryType<TYPE, QueryType>,
    initialState: STATE
) : CoreBaseViewModel<TYPE, STATE, QueryType, Nothing>(repository, initialState)