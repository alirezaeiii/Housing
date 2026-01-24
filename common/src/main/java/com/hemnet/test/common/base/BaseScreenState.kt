package com.hemnet.test.common.base

interface BaseScreenState<T> {
    val base: ViewState<T>
    fun copyWithBase(base: ViewState<T>): BaseScreenState<T>

    @Suppress("UNCHECKED_CAST")
    fun <S : BaseScreenState<T>> S.withBase(newBase: ViewState<T>): S =
        copyWithBase(newBase) as S
}

fun <T, S : BaseScreenState<T>> S.withLoading(isRefreshing: Boolean, showRefreshing: Boolean): S =
    withBase(
        base.copy(
            isLoading = !isRefreshing,
            isRefreshing = isRefreshing && showRefreshing,
            error = ""
        )
    )

fun <T, S : BaseScreenState<T>> S.withError(msg: String, isWarning: Boolean): S =
    withBase(
        base.copy(
            isLoading = false,
            isRefreshing = false,
            error = msg,
            isWarning = isWarning
        )
    )