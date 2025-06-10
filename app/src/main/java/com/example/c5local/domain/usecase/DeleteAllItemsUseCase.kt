package com.example.c5local.domain.usecase

import com.example.c5local.domain.repository.ItemRepository


class DeleteAllItemsUseCase(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return try {
            val success = repository.deleteAllItems()
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Gagal menghapus semua item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}