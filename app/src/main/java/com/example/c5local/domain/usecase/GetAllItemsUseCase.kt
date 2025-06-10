package com.example.c5local.domain.usecase

import com.example.c5local.data.model.Item
import com.example.c5local.domain.repository.ItemRepository

class GetAllItemsUseCase(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(): List<Item> {
        return repository.getAllItems()
    }
}