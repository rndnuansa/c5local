package com.example.c5local.persentation.screen.registration// presentation/screen/ItemScreen.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.c5local.data.model.Item
import com.example.c5local.persentation.screen.registration.components.ItemCard
import com.example.c5local.persentation.screen.registration.components.ItemDialog
import com.example.c5local.persentation.shared.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    viewModel: ItemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showDebugMenu by remember { mutableStateOf(false) }
    
    // Show error snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            // Handle error display
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Manager") },
                actions = {
                    // Debug menu button (hanya untuk development)
                    if (showDebugMenu) {
                        IconButton(
                            onClick = { showDebugMenu = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Debug Menu"
                            )
                        }
                    }

                    if (uiState.items.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.deleteAllItems()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Semua"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Item"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Error message
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { viewModel.clearError() }
                        ) {
                            Text("Tutup")
                        }
                    }
                }
            }
            
            // Content
            if (uiState.items.isEmpty() && !uiState.isLoading) {
                EmptyStateContent()
            } else {
                ItemList(
                    items = uiState.items,
                    onItemClick = { viewModel.openViewDialog(it) },
                    onEditClick = { viewModel.openEditDialog(it) },
                    onDeleteClick = { viewModel.deleteItem(it.id.toInt()) }
                )
            }
        }
        
        // Dialog
        if (uiState.isDialogOpen) {
            ItemDialog(
                mode = uiState.dialogMode,
                formState = formState,
                onNameChange = viewModel::updateName,
                onRfidChange = viewModel::updateRfid,
                onKeteranganChange = viewModel::updateKeterangan,
                onSave = viewModel::saveItem,
                onDismiss = viewModel::closeDialog,
                isLoading = uiState.isLoading
            )
        }


        // Debug Menu Dialog
        if (showDebugMenu) {
            DebugMenuDialog(
                onDismiss = { showDebugMenu = false },
                onRunSeeder = {
                    viewModel.runSeeder()
                    showDebugMenu = false
                },
                onClearData = {
                    viewModel.clearAllData()
                    showDebugMenu = false
                },
                onReseedData = {
                    viewModel.reseedData()
                    showDebugMenu = false
                },
                isLoading = uiState.isLoading
            )
        }
    }
}

@Composable
fun EmptyStateContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Belum ada item",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap tombol + untuk menambah item baru",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ItemList(
    items: List<Item>,
    onItemClick: (Item) -> Unit,
    onEditClick: (Item) -> Unit,
    onDeleteClick: (Item) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            ItemCard(
                item = item,
                onClick = { onItemClick(item) },
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item) }
            )
        }
    }
}

@Composable
fun DebugMenuDialog(
    onDismiss: () -> Unit,
    onRunSeeder: () -> Unit,
    onClearData: () -> Unit,
    onReseedData: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Debug Menu",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Development tools untuk testing",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Seeder buttons
                Button(
                    onClick = onRunSeeder,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Run Seeder")
                    }
                }

                OutlinedButton(
                    onClick = onClearData,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear All Data")
                }

                OutlinedButton(
                    onClick = onReseedData,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reseed Data")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Tutup")
            }
        }
    )
}