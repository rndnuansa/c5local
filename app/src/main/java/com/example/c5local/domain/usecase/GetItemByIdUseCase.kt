package com.example.c5local.domain.usecase

import com.example.c5local.data.model.Item
import com.example.c5local.domain.repository.ItemRepository

class GetItemByIdUseCase(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(id: String): Item? {
        if (id.isBlank()) return null
        return repository.getItemById(id)
    }
}
