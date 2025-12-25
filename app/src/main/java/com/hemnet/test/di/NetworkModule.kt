package com.hemnet.test.di

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
        .baseUrl("https://gist.githubusercontent.com/soulzidda/220a8305a6437f3be37eae6198f4d0db/raw/bed8d1e25b85741a4e2ff1d88230b0024ba04e13/")
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
        .create(BackendApi::class.java)
}