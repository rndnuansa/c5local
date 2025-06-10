//import android.content.Context
//import android.content.SharedPreferences
//import android.util.Log
//import com.example.c5local.data.model.Item
//import com.example.c5local.data.model.ItemJson
//import com.example.c5local.data.model.toItem
//import com.example.c5local.data.model.toJson
//import com.example.c5local.domain.repository.ItemRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
//import java.util.UUID
//
//class ItemRepositoryImpl(sharedPreferences: SharedPreferences) : ItemRepository {
//
//    private val sharedPreferences: SharedPreferences =
//        context.getSharedPreferences("item_prefs", Context.MODE_PRIVATE)
//
//    private val json = Json {
//        ignoreUnknownKeys = true
//        encodeDefaults = true
//    }
//
//    companion object {
//        private const val ITEMS_KEY = "items_list"
//    }
//
//    override suspend fun getAllItems(): List<Item> = withContext(Dispatchers.IO) {
//        try {
//            val itemsJson = sharedPreferences.getString(ITEMS_KEY, "[]") ?: "[]"
//            val itemJsonList = json.decodeFromString<List<ItemJson>>(itemsJson)
//            itemJsonList.map { it.toItem() }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
//
//    override suspend fun getItemById(id: String): Item? = withContext(Dispatchers.IO) {
//        val items = getAllItems()
//        items.find { it.id == id }
//    }
//
//    override suspend fun insertItem(item: Item): Boolean = withContext(Dispatchers.IO) {
//        try {
//            println("MASUK SINI")
//            val items = getAllItems().toMutableList()
//            println(items)
//            val newItem = item.copy(id = if (item.id.isEmpty()) UUID.randomUUID().toString() else item.id)
//            println(newItem)
//            items.add(newItem)
//            println("YEAY BERHASIL DI ADD")
//            saveItems(items)
//        } catch (e: Exception) {
//            Log.d("ERROR INSERT ITEM",e.message!!)
//            false
//        }
//    }
//
//    override suspend fun updateItem(item: Item): Boolean = withContext(Dispatchers.IO) {
//        try {
//            val items = getAllItems().toMutableList()
//            val index = items.indexOfFirst { it.id == item.id }
//            if (index != -1) {
//                items[index] = item
//                saveItems(items)
//            } else {
//                false
//            }
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    override suspend fun deleteItem(id: String): Boolean = withContext(Dispatchers.IO) {
//        try {
//            val items = getAllItems().toMutableList()
//            val removed = items.removeAll { it.id == id }
//            if (removed) {
//                saveItems(items)
//            } else {
//                false
//            }
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    override suspend fun deleteAllItems(): Boolean = withContext(Dispatchers.IO) {
//        try {
//            sharedPreferences.edit().remove(ITEMS_KEY).apply()
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    private suspend fun saveItems(items: List<Item>): Boolean = withContext(Dispatchers.IO) {
//        try {
//            val itemJsonList = items.map { it.toJson() }
//            val itemsJson = json.encodeToString(itemJsonList)
//            sharedPreferences.edit().putString(ITEMS_KEY, itemsJson).apply()
//            true
//        } catch (e: Exception) {
//            println("ERROR DI SAVE ITEMS" + e.message)
//            false
//        }
//    }
//}

package com.example.c5local.data.repository

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import com.example.c5local.data.model.Item
import com.example.c5local.data.model.ItemJson
import com.example.c5local.data.model.toItem
import com.example.c5local.domain.repository.ItemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ItemRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    companion object {
        private const val ITEMS_KEY = "items_key"
    }

//    override fun getAllItems(): Flow<List<Item>> = flow {
//        emit(loadItems())
//    }
//
//    override suspend fun getItemById(id: Int): Item? {
//        return loadItems().find { it.id == id }
//    }
    override suspend fun getAllItems(): List<Item> = withContext(Dispatchers.IO) {
        try {
            val itemsJson = sharedPreferences.getString(ITEMS_KEY, "[]") ?: "[]"
            val itemJsonList = json.decodeFromString<List<ItemJson>>(itemsJson)
            itemJsonList.map { it.toItem() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getItemById(id: String): Item? = withContext(Dispatchers.IO) {
        val items = getAllItems()
        items.find { it.id == id }
    }

    override suspend fun insertItem(item: Item): Boolean {
        val currentItems = loadItems().toMutableList()
        val newId = (currentItems.maxOfOrNull { it.id.toInt() } ?: 0) + 1
        val newItem = item.copy(id = newId.toString())
        currentItems.add(newItem)
        return saveItems(currentItems)
    }

    override suspend fun updateItem(item: Item): Boolean {
        val currentItems = loadItems().toMutableList()
        val index = currentItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            currentItems[index] = item
            return saveItems(currentItems)
        }
        return false
    }

    override suspend fun deleteItem(id: String): Boolean {
        val currentItems = loadItems().toMutableList()
        val removed = currentItems.removeIf { it.id == id }
        return if (removed) saveItems(currentItems) else false
    }

    override suspend fun deleteAllItems(): Boolean {
        return saveItems(emptyList())
    }

    private suspend fun saveItems(items: List<Item>): Boolean = withContext(Dispatchers.IO) {
        try {
            val itemsJson = json.encodeToString(items)
            sharedPreferences.edit().putString(ITEMS_KEY, itemsJson).apply()
            true
        } catch (e: Exception) {
            println("ERROR DI SAVE ITEMS: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private suspend fun loadItems(): List<Item> = withContext(Dispatchers.IO) {
        try {
            val itemsJson = sharedPreferences.getString(ITEMS_KEY, null)
            if (itemsJson.isNullOrEmpty()) {
                emptyList()
            } else {
                json.decodeFromString<List<Item>>(itemsJson)
            }
        } catch (e: Exception) {
            println("ERROR DI LOAD ITEMS: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}