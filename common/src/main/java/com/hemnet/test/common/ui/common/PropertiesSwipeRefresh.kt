package com.hemnet.test.common.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hemnet.test.common.base.BaseScreenState
import com.hemnet.test.common.base.CoreBaseViewModel

@Composable
fun <TYPE, STATE : BaseScreenState<TYPE>, QueryType, FetchType> PropertiesSwipeRefresh(
    modifier: Modifier = Modifier,
    viewModel: CoreBaseViewModel<TYPE, STATE, QueryType, FetchType>,
    state: STATE,
    isRefreshing: Boolean = state.base.isRefreshing,
    refresh: () -> Unit = { viewModel.refresh(isRefreshing = true) },
    mainContent: @Composable () -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { refresh.invoke() },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state,
                trigger
            )
        },
        modifier = modifier.fillMaxSize()
    ) {
        mainContent()
    }
}