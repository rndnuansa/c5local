package com.example.c5local.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: String = "",
    val name: String = "",
    val rfid: String = "",
    val description: String = ""
) {
    companion object {
        fun empty() = Item()
    }
}