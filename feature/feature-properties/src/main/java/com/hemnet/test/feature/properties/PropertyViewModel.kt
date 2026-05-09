package com.hemnet.test.feature.properties

import com.hemnet.test.common.base.BaseRepository
import com.hemnet.test.common.base.BaseViewModel
import com.hemnet.test.common.base.ViewState
import com.hemnet.test.domain.model.Property

class PropertyViewModel(
    repository: BaseRepository<List<Property>>
) : BaseViewModel<List<Property>, PropertiesViewState>(
    repository,
    PropertiesViewState(base = ViewState(isLoading = true))
) {
    override fun onSuccess(
        items: List<Property>,
        isRefreshing: Boolean
    ) {
        TODO("Not yet implemented")
    }
}