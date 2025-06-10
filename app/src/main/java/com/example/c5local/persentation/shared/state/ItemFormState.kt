package com.example.c5local.persentation.shared.state

data class ItemFormState(
    val name: String = "",
    val rfid: String = "",
    val description: String = "",
    val nameError: String? = null,
    val rfidError: String? = null,
    val descriptionError: String? = null,
    val isValid: Boolean = false
) {
    fun validate(): ItemFormState {
        val nameError = if (name.isBlank()) "Nama tidak boleh kosong" else null
        val rfidError = if (rfid.isBlank()) "RFID tidak boleh kosong" else null
        val keteranganError = if (description.isBlank()) "Keterangan tidak boleh kosong" else null
        
        return copy(
            nameError = nameError,
            rfidError = rfidError,
            descriptionError = keteranganError,
            isValid = nameError == null && rfidError == null && keteranganError == null
        )
    }
}