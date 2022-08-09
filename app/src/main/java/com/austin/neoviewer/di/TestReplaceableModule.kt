package com.austin.neoviewer.di

import android.content.Context
import androidx.room.Room
import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.network.BASE_URL
import com.austin.neoviewer.network.NeoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestReplaceableModule {

    @Singleton
    @Provides
    fun provideNeoService(): NeoService {
        val okhttpClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient)
            .build()
            .create(NeoService::class.java)
    }

    @Singleton
    @Provides
    fun provideNeoDatabase(@ApplicationContext context: Context): NeoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            NeoDatabase::class.java,
            "NeoDatabase.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}