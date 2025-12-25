package com.hemnet.test.feature.common

sealed class Screens(val title: String) {
    object Meals : Screens("properties_screen")
    object Details : Screens("details_screen/{$PROPERTY}")

    companion object {
        const val PROPERTY = "property"
    }
}