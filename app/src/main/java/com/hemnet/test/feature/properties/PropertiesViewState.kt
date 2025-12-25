package com.hemnet.test.feature.properties

import com.hemnet.test.base.BaseScreenState
import com.hemnet.test.base.ViewState
import com.hemnet.test.domain.model.Property

data class PropertiesViewState(
    override val base: ViewState<List<Property>> = ViewState(),
    val filteredProperties: List<Property> = emptyList(),
    val query: String? = null
) : BaseScreenState<List<Property>> {

    override fun copyWithBase(base: ViewState<List<Property>>) = copy(base = base)
}