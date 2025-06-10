package com.example.c5local.persentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.c5local.domain.usecase.ItemUseCases
import com.example.c5local.domain.usecase.SeederUseCase

class ItemViewModelFactory(
    private val itemUseCases: ItemUseCases,
    private val seederUseCases: SeederUseCase
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            return ItemViewModel(
                itemUseCases,
                seederUseCases
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}