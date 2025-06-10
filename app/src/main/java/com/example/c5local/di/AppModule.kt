// di/AppModule.kt
package com.example.c5local.di

import android.content.Context
import android.content.SharedPreferences
import com.example.c5local.data.repository.ItemRepositoryImpl
import com.example.c5local.data.seeder.ItemSeeder
import com.example.c5local.domain.repository.ItemRepository
import com.example.c5local.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideSharedPreferences(
//        @ApplicationContext context: Context
//    ): SharedPreferences {
//        return context.getSharedPreferences("item_prefs", Context.MODE_PRIVATE)
//    }

//    @Provides
//    @Singleton
//    fun provideItemRepository(
//        sharedPreferences: SharedPreferences
//    ): ItemRepository {
//        return ItemRepositoryImpl(sharedPreferences)
//    }

    @Provides
    @Singleton
    fun provideGetAllItemsUseCase(
        repository: ItemRepository
    ): GetAllItemsUseCase {
        return GetAllItemsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetItemByIdUseCase(
        repository: ItemRepository
    ): GetItemByIdUseCase {
        return GetItemByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideInsertItemUseCase(
        repository: ItemRepository
    ): InsertItemUseCase {
        return InsertItemUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateItemUseCase(
        repository: ItemRepository
    ): UpdateItemUseCase {
        return UpdateItemUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteItemUseCase(
        repository: ItemRepository
    ): DeleteItemUseCase {
        return DeleteItemUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteAllItemsUseCase(
        repository: ItemRepository
    ): DeleteAllItemsUseCase {
        return DeleteAllItemsUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideItemSeeder(
        repository: ItemRepository
    ): ItemSeeder {
        return ItemSeeder(repository)
    }

    @Provides
    @Singleton
    fun provideSeederUseCase(
        itemSeeder: ItemSeeder
    ): SeederUseCase {
        return SeederUseCase(itemSeeder)
    }
}