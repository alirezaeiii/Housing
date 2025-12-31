package com.hemnet.test.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PropertyEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyEntityDao
}