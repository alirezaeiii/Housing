package com.hemnet.test.common.base

interface BaseScreenState<TYPE, SELF : BaseScreenState<TYPE, SELF>> {
    val base: ViewState<TYPE>
    fun copyWithBase(base: ViewState<TYPE>): SELF
}

fun <TYPE, STATE : BaseScreenState<TYPE, STATE>> STATE.withLoading(isRefreshing: Boolean): STATE =
    copyWithBase(
        base.copy(
            isLoading = !isRefreshing,
            isRefreshing = isRefreshing,
            error = ""
        )
    )

fun <TYPE, STATE : BaseScreenState<TYPE, STATE>> STATE.withError(
    msg: String,
    isWarning: Boolean
): STATE =
    copyWithBase(
        base.copy(
            isLoading = false,
            isRefreshing = false,
            error = msg,
            isWarning = isWarning
        )
    )