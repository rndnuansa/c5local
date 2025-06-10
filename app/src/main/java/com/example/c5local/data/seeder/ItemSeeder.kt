package com.example.c5local.data.seeder

import com.example.c5local.data.model.Item
import com.example.c5local.domain.repository.ItemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemSeeder @Inject constructor(
    private val itemRepository: ItemRepository
) {
    
    suspend fun seedItems() {
        // Cek apakah sudah ada data
        val existingItems = itemRepository.getAllItems()
        if (existingItems.isNotEmpty()) {
            println("Data sudah ada, seeder tidak dijalankan")
            return
        }
        
        // Data seeder
        val seedItems = listOf(
            Item(
                id = "1",
                name = "Laptop Dell XPS 13",
                rfid = "RFID001ABC",
                description = "Laptop untuk development tim frontend"
            ),
            Item(
                id = "2",
                name = "Mouse Logitech MX Master",
                rfid = "RFID002DEF",
                description = "Mouse wireless untuk desainer"
            ),
            Item(
                id = "3",
                name = "Keyboard Mechanical RGB",
                rfid = "RFID003GHI",
                description = "Keyboard gaming untuk programmer"
            ),
            Item(
                id = "4",
                name = "Monitor Samsung 24 inch",
                rfid = "RFID004JKL",
                description = "Monitor tambahan untuk dual screen setup"
            ),
            Item(
                id = "5",
                name = "Headset Sony WH-1000XM4",
                rfid = "RFID005MNO",
                description = "Headset noise cancelling untuk meeting"
            ),
            Item(
                id = "6",
                name = "Webcam Logitech C920",
                rfid = "RFID006PQR",
                description = "Webcam HD untuk video conference"
            ),
            Item(
                id = "7",
                name = "Tablet iPad Pro 11 inch",
                rfid = "RFID007STU",
                description = "Tablet untuk presentasi dan design"
            ),
            Item(
                id = "8",
                name = "Smartphone iPhone 14",
                rfid = "RFID008VWX",
                description = "Smartphone untuk testing mobile app"
            ),
            Item(
                id = "9",
                name = "Power Bank 20000mAh",
                rfid = "RFID009YZA",
                description = "Power bank untuk device mobile"
            ),
            Item(
                id = "10",
                name = "USB Hub 7 Port",
                rfid = "RFID010BCD",
                description = "USB hub untuk koneksi multiple device"
            ),
            Item(
                id = "11",
                name = "Charger MacBook Pro",
                rfid = "RFID011EFG",
                description = "Charger cadangan untuk MacBook Pro"
            ),
            Item(
                id = "12",
                name = "Cable HDMI 2 Meter",
                rfid = "RFID012HIJ",
                description = "Kabel HDMI untuk presentasi"
            ),
            Item(
                id = "13",
                name = "SSD External 1TB",
                rfid = "RFID013KLM",
                description = "Storage eksternal untuk backup"
            ),
            Item(
                id = "14",
                name = "Printer Canon Pixma",
                rfid = "RFID014NOP",
                description = "Printer untuk dokumen kantor"
            ),
            Item(
                id = "15",
                name = "Scanner Epson V550",
                rfid = "RFID015QRS",
                description = "Scanner untuk digitalisasi dokumen"
            )
        )
        
        // Insert data seeder
        seedItems.forEach { item ->
            try {
                itemRepository.insertItem(item)
                println("✓ Seeded: ${item.name}")
            } catch (e: Exception) {
                println("✗ Failed to seed: ${item.name} - ${e.message}")
            }
        }
        
        println("Seeder completed! Total items seeded: ${seedItems.size}")
    }
    
    suspend fun clearAllData() {
        try {
            itemRepository.deleteAllItems()
            println("✓ All data cleared successfully")
        } catch (e: Exception) {
            println("✗ Failed to clear data: ${e.message}")
        }
    }
    
    suspend fun reseedData() {
        println("Reseeding data...")
        clearAllData()
        seedItems()
    }
}