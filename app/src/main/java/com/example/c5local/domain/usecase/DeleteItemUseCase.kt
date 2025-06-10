package com.example.c5local.domain.usecase

import com.example.c5local.domain.repository.ItemRepository

class DeleteItemUseCase(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(id: String): Result<Boolean> {
        return try {
            if (id.isBlank()) {
                Result.failure(Exception("ID tidak valid"))
            } else {
                val success = repository.deleteItem(id)
                if (success) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Gagal menghapus item"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}