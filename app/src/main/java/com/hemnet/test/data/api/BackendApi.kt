package com.hemnet.test.data.api

import com.hemnet.test.data.response.PropertiesResponse
import retrofit2.http.GET

interface BackendApi {
    @GET("properties.json")
    suspend fun getProperties(): PropertiesResponse
}