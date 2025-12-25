package com.hemnet.test.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hemnet.test.domain.model.Property
import com.hemnet.test.domain.model.PropertyType

@Entity(tableName = "property")
data class PropertyEntity(
    val area: String,
    val askingPrice: String?,
    val daysOnHemnet: Int?,
    @PrimaryKey val id: String,
    val image: String,
    val livingArea: Int?,
    val monthlyFee: String?,
    val municipality: String?,
    val numberOfRooms: Int?,
    val streetAddress: String?,
    val type: Int,
    val rating: String?,
    val averagePrice: String?
)

fun List<PropertyEntity>.asDomainModel() = map(PropertyEntity::asDomainModel)

private fun PropertyEntity.asDomainModel() = Property(
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
    type = PropertyType.entries[type],
    rating = rating, averagePrice = averagePrice
)
