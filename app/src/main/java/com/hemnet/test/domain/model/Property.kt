package com.hemnet.test.domain.model

import com.hemnet.test.data.database.PropertyEntity

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

fun List<Property>.asDatabaseModel() = map(Property::asDatabaseModel)

private fun Property.asDatabaseModel() = PropertyEntity(
    area = area,
    askingPrice = askingPrice,
    daysOnHemnet = daysOnHemnet,
    id = id,
    image = image,
    livingArea = livingArea,
    monthlyFee = monthlyFee,
    municipality = municipality,
    numberOfRooms = numberOfRooms,
    streetAddress = streetAddress,
    type = PropertyType.entries.indexOf(type),
    rating = rating, averagePrice = averagePrice
)