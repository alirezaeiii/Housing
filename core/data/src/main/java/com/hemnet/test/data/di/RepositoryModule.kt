package com.hemnet.test.data.di

import com.hemnet.test.common.base.BaseRepository
import com.hemnet.test.data.repository.PropertiesRepository
import com.hemnet.test.domain.model.Property
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    @JvmSuppressWildcards
    internal abstract fun bindRepository(repository: PropertiesRepository): BaseRepository<List<Property>, Int>
}