package com.hemnet.test.data.api

import com.hemnet.test.data.response.PropertiesResponse
import retrofit2.http.GET

interface BackendApi {
    @GET("adverts.json")
    suspend fun getProperties(): PropertiesResponse
}