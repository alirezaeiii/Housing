package com.hemnet.test.feature.properties

import com.hemnet.test.common.base.BaseScreenState
import com.hemnet.test.common.base.ViewState
import com.hemnet.test.domain.model.Property
import com.hemnet.test.domain.model.PropertyType

data class PropertiesViewState(
    override val base: ViewState<List<Property>> = ViewState(),
    val filteredProperties: List<Property> = emptyList(),
    val query: String? = null,
    val propertyType: PropertyType? = null
) : BaseScreenState<List<Property>> {

    override fun copyWithBase(base: ViewState<List<Property>>) = copy(base = base)
}