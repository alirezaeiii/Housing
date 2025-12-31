package com.hemnet.test.common.ui.common

sealed class Screens(val title: String) {
    object Properties : Screens("properties_screen")
    object Details : Screens("details_screen/{$PROPERTY}")

    companion object {
        const val PROPERTY = "property"
    }
}