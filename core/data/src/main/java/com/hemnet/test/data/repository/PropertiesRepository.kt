package com.hemnet.test.data.repository

import android.content.Context
import com.hemnet.test.common.base.BaseRepositoryWithQueryType
import com.hemnet.test.data.api.BackendApi
import com.hemnet.test.data.database.PropertyEntityDao
import com.hemnet.test.data.database.asDatabaseModel
import com.hemnet.test.data.database.asDomainModel
import com.hemnet.test.data.di.IoDispatcher
import com.hemnet.test.data.response.asDomainModel
import com.hemnet.test.domain.model.Property
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
) : BaseRepositoryWithQueryType<List<@JvmSuppressWildcards Property>, Int>(context, dispatcher) {

    override suspend fun query(queryType: Int?): List<Property> =
        (queryType?.let { dao.getFilteredProperties(it) }
            ?: dao.getAll()).asDomainModel()

    override suspend fun fetch(fetchType: Nothing?): List<Property> = backendApi.getProperties().result.asDomainModel()

    override suspend fun saveFetchResult(item: List<Property>) {
        dao.insertAll(item.asDatabaseModel())
    }
}