package com.hemnet.test.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PropertyEntityDao {
    @Query("SELECT * FROM property")
    suspend fun getAll(): List<PropertyEntity>

    @Query("SELECT * FROM property WHERE type=:type")
    suspend fun getFilteredProperties(type: Int): List<PropertyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(properties: List<PropertyEntity>)
}
