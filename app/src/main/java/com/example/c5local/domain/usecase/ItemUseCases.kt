package com.example.c5local.domain.usecase

data class ItemUseCases(
    val getAllItems: GetAllItemsUseCase,
    val getItemById: GetItemByIdUseCase,
    val insertItem: InsertItemUseCase,
    val updateItem: UpdateItemUseCase,
    val deleteItem: DeleteItemUseCase,
    val deleteAllItems: DeleteAllItemsUseCase
)