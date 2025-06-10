package com.example.c5local.persentation.shared.state

import com.example.c5local.data.model.Item

data class ItemUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedItem: Item? = null,
    val isDialogOpen: Boolean = false,
    val dialogMode: DialogMode = DialogMode.ADD
)

enum class DialogMode {
    ADD, EDIT, VIEW
}