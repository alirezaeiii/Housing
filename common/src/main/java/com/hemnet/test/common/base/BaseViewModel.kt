package com.hemnet.test.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemnet.test.common.utils.Async
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<TYPE, STATE : BaseScreenState<TYPE, STATE>, QueryType, FetchType>(
    private val repository: BaseRepository<TYPE, QueryType, FetchType>,
    initialState: STATE,
    loadDataOnInit: Boolean = true
) : ViewModel() {

    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _showWarningUiEvent = MutableSharedFlow<UiEvent>()
    val showWarningUiEvent = _showWarningUiEvent.asSharedFlow()

    private var job: Job? = null

    sealed class UiEvent {
        data class ShowWarning(val message: String) : UiEvent()
    }

    init {
        if (loadDataOnInit) {
            refresh()
        }
    }

    protected abstract fun onSuccess(items: TYPE)

    protected fun updateState(reducer: (STATE) -> STATE) {
        _state.update(reducer)
    }

    fun refresh(
        queryValue: QueryType? = null,
        fetchValue: FetchType? = null,
        forceRefresh: Boolean = true
    ) {
        job?.cancel()
        job = repository.getResult(queryValue, fetchValue, forceRefresh).onEach { uiState ->
            when (uiState) {
                is Async.Loading -> {
                    updateState { old ->
                        reduceLoading(old, uiState.isRefreshing)
                    }
                }

                is Async.Success -> onSuccess(uiState.data)

                is Async.Error -> {
                    updateState { old ->
                        reduceError(old, uiState.message, uiState.isWarning)
                    }
                    if (uiState.isWarning) {
                        emitWarning(uiState.message)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun reduceLoading(old: STATE, isRefreshing: Boolean): STATE =
        old.withLoading(isRefreshing)


    private fun reduceError(old: STATE, msg: String, isWarning: Boolean): STATE =
        old.withError(msg, isWarning)

    private suspend fun emitWarning(message: String) {
        _showWarningUiEvent.emit(UiEvent.ShowWarning(message))
    }
}