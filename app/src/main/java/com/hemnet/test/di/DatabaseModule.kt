package com.hemnet.test.di

import android.content.Context
import androidx.room.Room
import com.hemnet.test.data.database.AppDatabase
import com.hemnet.test.data.database.PropertyEntityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "android-job-assignment")
            .build()

    @Singleton
    @Provides
    fun provideMealEntityDao(appDatabase: AppDatabase): PropertyEntityDao = appDatabase.propertyDao()
}