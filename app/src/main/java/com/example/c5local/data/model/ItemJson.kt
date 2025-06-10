package com.example.c5local.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemJson(
    val id: String,
    val name: String,
    val rfid: String,
    val description: String
)

// Extension functions untuk konversi
fun Item.toJson(): ItemJson = ItemJson(
    id = this.id,
    name = this.name,
    rfid = this.rfid,
    description = this.description
)

fun ItemJson.toItem(): Item = Item(
    id = this.id,
    name = this.name,
    rfid = this.rfid,
    description = this.description
)