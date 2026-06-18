package com.hemnet.test.common.ui.common

sealed class Routes(val title: String) {
    object Properties : Routes("properties_screen")
    object Details : Routes("details_screen/{$PROPERTY}")

    companion object Companion {
        const val PROPERTY = "property"
    }
}