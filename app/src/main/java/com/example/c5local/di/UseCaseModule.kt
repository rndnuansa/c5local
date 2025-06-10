package com.example.c5local.di

import com.example.c5local.domain.repository.ItemRepository
import com.example.c5local.domain.repository.RfidPreferenceRepository
import com.example.c5local.domain.usecase.DeleteAllItemsUseCase
import com.example.c5local.domain.usecase.DeleteItemUseCase
import com.example.c5local.domain.usecase.GetAllItemsUseCase
import com.example.c5local.domain.usecase.GetItemByIdUseCase
import com.example.c5local.domain.usecase.GetRfidPowerUseCase
import com.example.c5local.domain.usecase.InsertItemUseCase
import com.example.c5local.domain.usecase.ItemUseCases
import com.example.c5local.domain.usecase.RfidUseCases
import com.example.c5local.domain.usecase.SaveRfidPowerUseCase
import com.example.c5local.domain.usecase.UpdateItemUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideItemUseCases(
        repository: ItemRepository
    ): ItemUseCases {
        return ItemUseCases(
            getAllItems = GetAllItemsUseCase(repository),
            getItemById = GetItemByIdUseCase(repository),
            insertItem = InsertItemUseCase(repository),
            updateItem = UpdateItemUseCase(repository),
            deleteItem = DeleteItemUseCase(repository),
            deleteAllItems = DeleteAllItemsUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideRfidUseCases(
        repository: RfidPreferenceRepository
    ): RfidUseCases {
        return RfidUseCases(
            getRfidPowerUseCase = GetRfidPowerUseCase(repository),
            saveRfidPowerUseCase = SaveRfidPowerUseCase(repository)
        )
    }
}