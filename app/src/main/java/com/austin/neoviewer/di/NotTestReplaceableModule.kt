package com.austin.neoviewer.di

import android.content.Context
import android.content.SharedPreferences
import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.feed.FeedViewModel
import com.austin.neoviewer.network.NeoService
import com.austin.neoviewer.repository.NeoRepository
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NotTestReplaceableModule {

    @Singleton
    @Provides
    fun provideNeoRepository(service: NeoService, db: NeoDatabase): NeoRepositoryInterface =
        NeoRepository(service, db)

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(FeedViewModel.PREFERENCES_KEY, Context.MODE_PRIVATE)
}