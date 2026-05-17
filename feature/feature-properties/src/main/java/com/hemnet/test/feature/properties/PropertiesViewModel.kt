package com.hemnet.test.feature.properties

import com.hemnet.test.common.base.BaseRepositoryWithQueryType
import com.hemnet.test.common.base.BaseViewModelWithQueryType
import com.hemnet.test.common.base.ViewState
import com.hemnet.test.domain.model.Property
import com.hemnet.test.domain.model.PropertyType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    repository: BaseRepositoryWithQueryType<List<Property>, Int>
) : BaseViewModelWithQueryType<List<Property>, PropertiesViewState, Int>(
    repository,
    PropertiesViewState(base = ViewState(isLoading = true))
) {

    init {
        refresh()
    }

    fun onFilterChanged(type: PropertyType?) {
        updateState { old ->
            old.copy(propertyType = type)
        }
        refresh(
            queryType = _state.value.propertyType?.ordinal,
            isRefreshing = true,
            showRefreshing = false,
            forceRefresh = false
        )
    }

    override fun onSuccess(items: List<Property>, isRefreshing: Boolean) {
        if (isRefreshing) {
            submitQuery(_state.value.query, items)
        } else {
            _state.value = PropertiesViewState(
                base = ViewState(
                    items = items,
                ),
                filteredProperties = items,
                propertyType = _state.value.propertyType
            )
        }
    }

    fun submitQuery(query: String?, items: List<Property>? = null) {
        val properties = items ?: _state.value.base.items!!

        val filtered = if (query.isNullOrBlank()) properties
        else properties.filter { it.area.contains(query.trim(), ignoreCase = true) }

        _state.value = PropertiesViewState(
            query = query,
            filteredProperties = filtered,
            propertyType = _state.value.propertyType,
            base = ViewState(
                items = properties
            )
        )
    }
}
