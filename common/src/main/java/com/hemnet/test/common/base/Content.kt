package com.hemnet.test.common.base

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.hemnet.test.common.ui.common.ErrorScreen
import com.hemnet.test.common.ui.common.ProgressScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T, S : BaseScreenState<T>, V> Content(
    viewModel: BaseViewModel<T, S, V>,
    mainContent: @Composable (S) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.base.isLoading -> ProgressScreen()
            state.base.error.isNotEmpty() && !state.base.isWarning ->
                ErrorScreen(state.base.error) { viewModel.refresh() }

            else -> mainContent(state)
        }

        val context = LocalContext.current
        LaunchedEffect(Unit) {
            viewModel.showWarningUiEvent.collectLatest { event ->
                when (event) {
                    is BaseViewModel.UiEvent.ShowWarning ->
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}