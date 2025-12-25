package com.hemnet.test.data.repository

import android.content.Context
import com.hemnet.test.data.api.BackendApi
import com.hemnet.test.data.database.PropertyEntityDao
import com.hemnet.test.data.database.asDomainModel
import com.hemnet.test.data.response.asDomainModel
import com.hemnet.test.di.IoDispatcher
import com.hemnet.test.domain.model.Property
import com.hemnet.test.domain.model.PropertyType
import com.hemnet.test.domain.model.asDatabaseModel
import com.hemnet.test.domain.repository.BaseRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertiesRepository @Inject constructor(
    private val backendApi: BackendApi,
    private val dao: PropertyEntityDao,
    @ApplicationContext context: Context,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseRepository<List<@JvmSuppressWildcards Property>>(context, dispatcher) {

    override suspend fun query(type: Int?): List<Property> {
        return type?.let {
            dao.getFilteredProperty(it).asDomainModel()
        } ?: run {
            dao.getAll().asDomainModel()
        }
    }

    override suspend fun fetch(): List<Property> = backendApi.getProperties().list.asDomainModel()

    override suspend fun saveFetchResult(item: List<Property>) {
        dao.insertAll(item.asDatabaseModel())
    }
}