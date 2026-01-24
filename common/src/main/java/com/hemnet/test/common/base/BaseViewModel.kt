package com.hemnet.test.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemnet.test.common.utils.Async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<T, S : BaseScreenState<T>, V>(
    private val repository: BaseRepository<T, V>,
    initialState: S
) : ViewModel() {

    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<S>
        get() = _state

    private val _showWarningUiEvent = MutableSharedFlow<UiEvent>()
    val showWarningUiEvent = _showWarningUiEvent

    sealed class UiEvent {
        data class ShowWarning(val message: String) : UiEvent()
    }

    protected abstract fun onSuccess(items: T, isRefreshing: Boolean)

    protected fun updateState(reducer: (S) -> S) {
        _state.update(reducer)
    }

    fun refresh(
        type: V? = null,
        isRefreshing: Boolean = false,
        showRefreshing: Boolean = true
    ) {
        repository.getResult(type).onEach { uiState ->
            when (uiState) {
                is Async.Loading -> {
                    updateState { old ->
                        reduceLoading(old, uiState.isRefreshing, showRefreshing)
                    }
                }

                is Async.Success -> onSuccess(uiState.data, isRefreshing)

                is Async.Error -> {
                    updateState { old ->
                        reduceError(old, uiState.message, uiState.isWarning)
                    }
                    if (uiState.isWarning && showRefreshing) {
                        emitWarning(uiState.message)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun reduceLoading(old: S, isRefreshing: Boolean, showRefreshing: Boolean): S =
        old.withLoading(isRefreshing, showRefreshing)


    private fun reduceError(old: S, msg: String, isWarning: Boolean): S =
        old.withError(msg, isWarning)

    private suspend fun emitWarning(message: String) {
        _showWarningUiEvent.emit(UiEvent.ShowWarning(message))
    }
}