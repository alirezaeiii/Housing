package com.hemnet.test.common.base

interface BaseScreenState<TYPE> {
    val base: ViewState<TYPE>
    fun copyWithBase(base: ViewState<TYPE>): BaseScreenState<TYPE>

    @Suppress("UNCHECKED_CAST")
    fun <STATE : BaseScreenState<TYPE>> STATE.withBase(newBase: ViewState<TYPE>): STATE =
        copyWithBase(newBase) as STATE
}

fun <TYPE, STATE : BaseScreenState<TYPE>> STATE.withLoading(isRefreshing: Boolean, showRefreshing: Boolean): STATE =
    withBase(
        base.copy(
            isLoading = !isRefreshing,
            isRefreshing = isRefreshing && showRefreshing,
            error = ""
        )
    )

fun <TYPE, STATE : BaseScreenState<TYPE>> STATE.withError(msg: String, isWarning: Boolean): STATE =
    withBase(
        base.copy(
            isLoading = false,
            isRefreshing = false,
            error = msg,
            isWarning = isWarning
        )
    )