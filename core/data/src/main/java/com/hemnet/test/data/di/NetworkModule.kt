package com.hemnet.test.data.di

import com.hemnet.test.data.api.BackendApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .build()

    @Singleton
    @Provides
    fun provideBackendApi(moshi: Moshi): BackendApi = Retrofit.Builder()
        .baseUrl("https://gist.githubusercontent.com/alirezaeiii/ee5c416bb7283357f4f9777f63f68bc2/raw/65851a2f3a8a9ab01f75cfcbe02c25085aab3f91/")
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
        .create(BackendApi::class.java)
}