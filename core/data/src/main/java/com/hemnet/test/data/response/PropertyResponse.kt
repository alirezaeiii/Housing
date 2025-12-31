package com.hemnet.test.data.response

import com.hemnet.test.domain.model.Property
import com.hemnet.test.domain.model.PropertyType

data class PropertyResponse(
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
    val type: String,
    val rating: String?,
    val averagePrice: String?
)

fun List<PropertyResponse>.asDomainModel() = map(PropertyResponse::asDomainModel)

private fun PropertyResponse.asDomainModel() = Property(
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
    type = PropertyType.fromApi(type),
    rating = rating, averagePrice = averagePrice
)