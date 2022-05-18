package com.austin.neoviewer.di

import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.network.NeoService
import com.austin.neoviewer.repository.NeoRepository
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideNeoRepository(service: NeoService, db: NeoDatabase): NeoRepositoryInterface =
        NeoRepository(service, db)
}