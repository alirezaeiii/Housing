package com.hemnet.test.domain.model

enum class PropertyType(val type: String) {
    Highlighted("HighlightedProperty"),
    Property("Property"),
    Area("Area");

    companion object {
        fun fromApi(value: String): PropertyType =
            entries.firstOrNull { it.type == value }
                ?: Property
    }
}