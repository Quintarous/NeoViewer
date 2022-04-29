package com.austin.neoviewer

import com.austin.neoviewer.di.AppModule
import com.austin.neoviewer.repository.FakeNeoRepository
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
    fun provideFakeNeoRepository(): NeoRepositoryInterface = FakeNeoRepository()

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = UnconfinedTestDispatcher()
}