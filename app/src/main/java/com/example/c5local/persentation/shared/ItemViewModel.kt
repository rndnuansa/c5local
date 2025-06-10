// presentation/shared/ItemViewModel.kt
package com.example.c5local.persentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c5local.data.model.Item
import com.example.c5local.domain.usecase.ItemUseCases
import com.example.c5local.domain.usecase.SeederUseCase
import com.example.c5local.persentation.shared.state.DialogMode
import com.example.c5local.persentation.shared.state.ItemFormState
import com.example.c5local.persentation.shared.state.ItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
    private val seederUseCase: SeederUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemUiState())
    val uiState: StateFlow<ItemUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(ItemFormState())
    val formState: StateFlow<ItemFormState> = _formState.asStateFlow()

    init {
        loadItems()
        // Jalankan seeder otomatis saat ViewModel dibuat
        runSeederIfNeeded()
    }

    private fun runSeederIfNeeded() {
        viewModelScope.launch {
            try {
                seederUseCase.runSeeder()
            } catch (e: Exception) {
                // Log error tapi jangan tampilkan ke user
                println("Seeder error: ${e.message}")
            }
        }
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val items = itemUseCases.getAllItems()
                _uiState.value = _uiState.value.copy(
                    items = items,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

//    private fun loadItems() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//            try {
//                itemUseCases.getAllItems().collect { items ->
//                    _uiState.update {
//                        it.copy(
//                            items = items,
//                            isLoading = false,
//                            errorMessage = null
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        errorMessage = e.message ?: "Terjadi kesalahan"
//                    )
//                }
//            }
//        }
//    }

    // Dialog Methods
    fun openAddDialog() {
        _formState.value = ItemFormState()
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                dialogMode = DialogMode.ADD,
                selectedItem = null
            )
        }
    }

    fun openEditDialog(item: Item) {
        _formState.value = ItemFormState(
            name = item.name,
            rfid = item.rfid,
            description = item.description ?: ""
        )
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                dialogMode = DialogMode.EDIT,
                selectedItem = item
            )
        }
    }

    fun openViewDialog(item: Item) {
        _formState.value = ItemFormState(
            name = item.name,
            rfid = item.rfid,
            description = item.description ?: ""
        )
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                dialogMode = DialogMode.VIEW,
                selectedItem = item
            )
        }
    }

    fun closeDialog() {
        _uiState.update {
            it.copy(
                isDialogOpen = false,
                selectedItem = null
            )
        }
        _formState.value = ItemFormState()
    }

    // Form Methods
//    fun updateName(name: String) {
//        val currentForm = _formState.value
//        _formState.value = ItemFormState.validate(
//            name = name,
//            rfid = currentForm.rfid
//        ).copy(description = currentForm.description)
//    }
//
//    fun updateRfid(rfid: String) {
//        val currentForm = _formState.value
//        _formState.value = ItemFormState.validate(
//            name = currentForm.name,
//            rfid = rfid
//        ).copy(description = currentForm.description)
//    }
    fun updateName(name: String) {
        _formState.value = _formState.value.copy(name = name).validate()
    }

    fun updateRfid(rfid: String) {
        _formState.value = _formState.value.copy(rfid = rfid).validate()
    }

    fun updateKeterangan(description: String) {
//        _formState.update { it.copy(description = description) }
        _formState.value = _formState.value.copy(description = description).validate()

    }

    // Save Method
    fun saveItem() {
        val currentForm = _formState.value
        val currentUi = _uiState.value

        if (!currentForm.isValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                when (currentUi.dialogMode) {
                    DialogMode.ADD -> {
                        val newItem = currentForm.description.trim().takeIf { it.isNotEmpty() }?.let {
                            Item(
                                id = "0", // Auto-generated
                                name = currentForm.name.trim(),
                                rfid = currentForm.rfid.trim(),
                                description = it
                            )
                        }

                        itemUseCases.insertItem(newItem!!)
                        loadItems()
                    }
                    DialogMode.EDIT -> {
                        currentUi.selectedItem?.let { selectedItem ->
                            val updatedItem =
                                currentForm.description.trim().takeIf { it.isNotEmpty() }?.let {
                                    selectedItem.copy(
                                        name = currentForm.name.trim(),
                                        rfid = currentForm.rfid.trim(),
                                        description = it
                                    )
                                }
                            itemUseCases.updateItem(updatedItem!!)
                        }
                        loadItems()
                    }
                    DialogMode.VIEW -> {
                        // No action needed for VIEW mode
                    }
                }
                _uiState.update { it.copy(isLoading = true) }
                closeDialog()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal menyimpan item"
                    )
                }
            }
        }
    }

    // Delete Methods
    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            try {
                itemUseCases.deleteItem(itemId.toString())
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Gagal menghapus item")
                }
            }
        }
    }

    fun deleteAllItems() {
        viewModelScope.launch {
            try {
                itemUseCases.deleteAllItems()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Gagal menghapus semua item")
                }
            }
        }
    }

    // Seeder Methods (untuk development)
    fun runSeeder() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                seederUseCase.runSeeder()
                // Data akan di-refresh otomatis melalui Flow
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal menjalankan seeder: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                seederUseCase.clearData()
                // Data akan di-refresh otomatis melalui Flow
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal menghapus data: ${e.message}"
                    )
                }
            }
        }
    }

    fun reseedData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                seederUseCase.reseedData()
                // Data akan di-refresh otomatis melalui Flow
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal melakukan reseed: ${e.message}"
                    )
                }
            }
        }
    }

    // Error handling
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}