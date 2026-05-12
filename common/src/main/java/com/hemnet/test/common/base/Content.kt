package com.hemnet.test.common.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.hemnet.test.common.ui.common.ErrorScreen
import com.hemnet.test.common.ui.common.ProgressScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <TYPE, STATE : BaseScreenState<TYPE>> Content(
    viewModel: BaseViewModel<TYPE, STATE>,
    scaffoldState: ScaffoldState,
    mainContent: @Composable (STATE) -> Unit
) {
    UIContent(viewModel, scaffoldState, mainContent)
}

@Composable
fun <TYPE, STATE : BaseScreenState<TYPE>, QueryType> Content(
    viewModel: BaseViewModelWithQueryType<TYPE, STATE, QueryType>,
    scaffoldState: ScaffoldState,
    mainContent: @Composable (STATE) -> Unit
) {
    UIContent(viewModel, scaffoldState, mainContent)
}

@Composable
fun <TYPE, STATE : BaseScreenState<TYPE>, FetchType> Content(
    viewModel: BaseViewModelWithFetchType<TYPE, STATE, FetchType>,
    scaffoldState: ScaffoldState,
    mainContent: @Composable (STATE) -> Unit
) {
    UIContent(viewModel, scaffoldState, mainContent)
}

@Composable
private fun <TYPE, STATE : BaseScreenState<TYPE>, QueryType, FetchType> UIContent(
    viewModel: CoreBaseViewModel<TYPE, STATE, QueryType, FetchType>,
    scaffoldState: ScaffoldState,
    mainContent: @Composable (STATE) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.base.isLoading -> ProgressScreen()
            state.base.error.isNotEmpty() && !state.base.isWarning ->
                ErrorScreen(state.base.error) { viewModel.refresh() }

            else -> mainContent(state)
        }
        LaunchedEffect(Unit) {
            viewModel.showWarningUiEvent.collectLatest { event ->
                when (event) {
                    is CoreBaseViewModel.UiEvent.ShowWarning ->
                        scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
}