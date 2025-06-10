package com.example.c5local.di

import android.content.Context
import android.content.SharedPreferences
import com.example.c5local.data.repository.ItemRepositoryImpl
import com.example.c5local.data.repository.RfidPreferenceRepositoryImpl
import com.example.c5local.domain.repository.ItemRepository
import com.example.c5local.domain.repository.RfidPreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object    DatabaseModule {

    private const val PREF_NAME = "item_prefs"

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideItemRepository(
        sharedPreferences: SharedPreferences
    ): ItemRepository {
        return ItemRepositoryImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideRfidRepository(
        @ApplicationContext context: Context
    ): RfidPreferenceRepository {
        return RfidPreferenceRepositoryImpl(context)
    }
}
