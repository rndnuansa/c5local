package com.example.c5local.persentation.screen.registration.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.c5local.persentation.shared.state.DialogMode
import com.example.c5local.persentation.shared.state.ItemFormState

//import com.example.c5local.persentation.shared.DialogMode
//import com.example.c5local.persentation.shared.FormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDialog(
    mode: DialogMode,
    formState: ItemFormState,
    onNameChange: (String) -> Unit,
    onRfidChange: (String) -> Unit,
    onKeteranganChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (mode) {
                            DialogMode.ADD -> "Tambah Item Baru"
                            DialogMode.EDIT -> "Edit Item"
                            DialogMode.VIEW -> "Detail Item"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (!isLoading) {
                        IconButton(
                            onClick = onDismiss
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup"
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Form Fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name Field
                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = onNameChange,
                        label = { Text("Nama Item") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && mode != DialogMode.VIEW,
                        readOnly = mode == DialogMode.VIEW,
                        isError = formState.nameError != null,
                        supportingText = formState.nameError?.let { error ->
                            { Text(text = error) }
                        },
                        singleLine = true
                    )
                    
                    // RFID Field
                    OutlinedTextField(
                        value = formState.rfid,
                        onValueChange = onRfidChange,
                        label = { Text("RFID") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && mode != DialogMode.VIEW,
                        readOnly = mode == DialogMode.VIEW,
                        isError = formState.rfidError != null,
                        supportingText = formState.rfidError?.let { error ->
                            { Text(text = error) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true
                    )
                    
                    // Keterangan Field
                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = onKeteranganChange,
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && mode != DialogMode.VIEW,
                        readOnly = mode == DialogMode.VIEW,
                        minLines = 3,
                        maxLines = 5
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel/Close Button
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text(
                            text = if (mode == DialogMode.VIEW) "Tutup" else "Batal"
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Save Button (not shown in VIEW mode)
                    if (mode != DialogMode.VIEW) {
                        Button(
                            onClick = onSave,
                            enabled = !isLoading && formState.isValid
                        ) {
                            if (isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Text("Menyimpan...")
                                }
                            } else {
                                Text(
                                    text = when (mode) {
                                        DialogMode.ADD -> "Tambah"
                                        DialogMode.EDIT -> "Simpan"
                                        DialogMode.VIEW -> ""
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}