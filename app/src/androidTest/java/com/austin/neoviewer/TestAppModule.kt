package com.austin.neoviewer

import android.content.Context
import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.di.AppModule
import com.austin.neoviewer.network.FakeNeoService
import com.austin.neoviewer.network.NeoService
import com.austin.neoviewer.repository.FakeNeoRepository
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.runner.manipulation.Ordering
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Singleton
    @Provides
    fun provideFakeNeoService(): NeoService = FakeNeoService()

    @Singleton
    @Provides
    fun provideInMemoryNeoDatabase(@ApplicationContext context: Context): NeoDatabase =
        NeoDatabase.getInMemoryInstance(context)

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = UnconfinedTestDispatcher()
}