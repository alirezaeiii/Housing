package com.hemnet.test.feature.properties

import com.hemnet.test.base.BaseViewModel
import com.hemnet.test.base.ViewState
import com.hemnet.test.data.repository.PropertiesRepository
import com.hemnet.test.domain.model.Property
import com.hemnet.test.domain.model.PropertyType
import com.hemnet.test.domain.repository.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    private val repository: BaseRepository<List<Property>>
) : BaseViewModel<List<Property>, PropertiesViewState>(
    repository,
    PropertiesViewState(base = ViewState(isLoading = true))
) {
    init {
        refresh()
    }

    fun onFilterChanged(type: PropertyType?) {
        (repository as PropertiesRepository).propertyType = type
        refresh(true)
    }

    override fun onSuccess(items: List<Property>, isRefreshing: Boolean) {
        if (isRefreshing) {
            submitQuery(_state.value.query, items)
        } else {
            _state.value = PropertiesViewState(
                base = ViewState(
                    items = items,
                ),
                filteredProperties = items
            )
        }
    }

    fun submitQuery(query: String?, items: List<Property>? = null) {
        val meals = items ?: _state.value.base.items!!

        val filtered = if (query.isNullOrBlank()) meals
        else meals.filter { it.area.contains(query.trim(), ignoreCase = true) }

        _state.value = PropertiesViewState(
            query = query,
            filteredProperties = filtered,
            base = ViewState(
                items = meals
            )
        )
    }
}
