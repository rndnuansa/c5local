package com.example.c5local.domain.usecase

import com.example.c5local.data.model.Item
import com.example.c5local.domain.repository.ItemRepository

class InsertItemUseCase(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(item: Item): Result<Boolean> {
        return try {
            // Validasi input
            when {
                item.name.isBlank() -> Result.failure(Exception("Nama tidak boleh kosong"))
                item.rfid.isBlank() -> Result.failure(Exception("RFID tidak boleh kosong"))
                item.description.isBlank() -> Result.failure(Exception("Keterangan tidak boleh kosong"))
                else -> {
                    val success = repository.insertItem(item)
                    if (success) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception("Gagal menyimpan item"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}