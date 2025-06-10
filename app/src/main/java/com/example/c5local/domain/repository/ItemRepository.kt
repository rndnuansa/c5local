package com.example.c5local.domain.repository

import com.example.c5local.data.model.Item

interface ItemRepository {
    suspend fun getAllItems(): List<Item>
    suspend fun getItemById(id: String): Item?
    suspend fun insertItem(item: Item): Boolean
    suspend fun updateItem(item: Item): Boolean
    suspend fun deleteItem(id: String): Boolean
    suspend fun deleteAllItems(): Boolean
}
