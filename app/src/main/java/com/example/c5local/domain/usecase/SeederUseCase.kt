package com.example.c5local.domain.usecase

import com.example.c5local.data.seeder.ItemSeeder
import javax.inject.Inject

class SeederUseCase @Inject constructor(
    private val itemSeeder: ItemSeeder
) {
    
    suspend fun runSeeder() {
        itemSeeder.seedItems()
    }
    
    suspend fun clearData() {
        itemSeeder.clearAllData()
    }
    
    suspend fun reseedData() {
        itemSeeder.reseedData()
    }
}