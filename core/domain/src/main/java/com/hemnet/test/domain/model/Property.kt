package com.hemnet.test.domain.model

data class Property(
    val area: String,
    val askingPrice: String?,
    val daysOnHemnet: Int?,
    val id: String,
    val image: String,
    val livingArea: Int?,
    val monthlyFee: String?,
    val municipality: String?,
    val numberOfRooms: Int?,
    val streetAddress: String?,
    val type: PropertyType,
    val rating: String?,
    val averagePrice: String?
)