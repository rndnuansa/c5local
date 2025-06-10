package com.example.c5local.persentation.screen.alloctobin

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.c5local.persentation.shared.UHFViewModel
import com.example.c5local.persentation.theme.DMSans

data class AllocToBinItem(
    val id: String,
    val name: String,
    val category: String,
    val currentBin: String,
    val targetBin: String,
    val quantity: Int,
    val rfidTag: String? = null,
    val isAllocated: Boolean = false
)

object TjiwiColors {
    val Primary = Color(0xFFE53E3E)
    val PrimaryDark = Color(0xFFB91C1C)
    val Secondary = Color(0xFF4A5568)
    val Background = Color(0xFFF7FAFC)
    val Surface = Color(0xFFFFFFFF)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF2D3748)
    val Success = Color(0xFF38A169)
    val Warning = Color(0xFFD69E2E)
    val Error = Color(0xFFE53E3E)
}

@Composable
private fun ItemsList(
    items: List<AllocToBinItem>,
    onItemToggle: (AllocToBinItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val filteredItems = items.filter { it.rfidTag != null }
        items(filteredItems) { item ->
            ItemCardWithCheckbox(
                item = item,
                onItemClick = { onItemToggle(item) }
            )
        }
    }
}

@Composable
private fun ItemCardWithCheckbox(
    item: AllocToBinItem,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (item.isAllocated) Modifier.border(
                    width = 2.dp,
                    color = TjiwiColors.Primary,
                    shape = RoundedCornerShape(12.dp) // Sesuaikan bentuk card
                ) else Modifier
            )
            .clickable(
                indication = rememberRipple(
                    color = TjiwiColors.Primary,
                    bounded = true
                ),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onItemClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = TjiwiColors.Surface // tidak berubah walaupun allocated
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (item.isAllocated) 4.dp else 2.dp
        ),
        shape = RoundedCornerShape(12.dp) // pastikan shape sama dengan border
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox di sebelah kiri
            Checkbox(
                checked = item.isAllocated,
                onCheckedChange = { onItemClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = TjiwiColors.Primary,
                    uncheckedColor = TjiwiColors.Secondary.copy(alpha = 0.6f),
                    checkmarkColor = Color.White
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color =TjiwiColors.Primary
                    )
                )
                Text(
                    text = "ID: ${item.id} | ${item.category}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color =  TjiwiColors.Secondary
                    )
                )
                Text(
                    text = "From: ${item.currentBin}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TjiwiColors.Warning,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "Quantity: ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TjiwiColors.Secondary
                    )
                )
                if (item.rfidTag != null) {
                    Text(
                        text = "RFID: ${item.rfidTag}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TjiwiColors.Success,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Status icon
            Icon(
                imageVector = if (item.isAllocated) Icons.Default.LocationOn else Icons.Default.LocationOff,
                contentDescription = null,
                tint = if (item.isAllocated) TjiwiColors.Success else TjiwiColors.Secondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun HeaderSection(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                letterSpacing = (-1).sp,
                color = TjiwiColors.Primary,

            )
        )
        HorizontalDivider()
        Text(
            text = subtitle,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                letterSpacing = (-1).sp,
                color = TjiwiColors.Secondary,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllocToBinFormScreen(
    allocToBinItems: List<AllocToBinItem>,
    onBack: () -> Unit,
    onSubmit: (List<AllocToBinItem>) -> Unit
) {
    val allocatedItems = allocToBinItems.filter { it.isAllocated }
    var selectedLocation by remember { mutableStateOf("") }
    var showAlert by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    // Data dummy untuk lokasi tujuan
    val destinationLocations = listOf(
        "BIN-GA-Z1", // Gudang A - Zona 1
        "BIN-GA-Z2", // Gudang A - Zona 2
        "BIN-GB-Z1", // Gudang B - Zona 1
        "BIN-GB-Z2", // Gudang B - Zona 2
        "BIN-GC-MS", // Gudang C - Main Storage
        "BIN-PA-L1", // Area Packing - Line 1
        "BIN-PA-L2", // Area Packing - Line 2
        "BIN-QC-AR"  // Quality Control Area
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TjiwiColors.Background)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TjiwiColors.Primary
                )
            }

            Text(
                text = "Form Allocation Bin",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = (-1).sp,
                    color = TjiwiColors.Primary,

                    )
            )

        }
        HorizontalDivider()


        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Allocation Summary",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Primary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Items Allocate", color = TjiwiColors.Secondary)
                    Text(
                        text = allocatedItems.size.toString(),
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Success
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Quantity", color = TjiwiColors.Secondary)
                    Text(
                        text = allocatedItems.sumOf { it.quantity }.toString(),
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Success
                    )
                }
            }
        }

        // Location Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Destination Location",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Primary
                    )
                )

                // Dropdown untuk lokasi tujuan
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedLocation,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Select Destination Location") },
                        placeholder = { Text("eg: Bin A - Zona 1") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TjiwiColors.Primary,
                            focusedLabelColor = TjiwiColors.Primary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        destinationLocations.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location) },
                                onClick = {
                                    selectedLocation = location
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Save Button
        Button(
            onClick = {
                if (selectedLocation.isNotEmpty() && allocatedItems.isNotEmpty()) {
                    showAlert = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TjiwiColors.Primary,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            enabled = selectedLocation.isNotEmpty() && allocatedItems.isNotEmpty()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save"
                )
                Text(
                    text = "Save Allocation",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    // Custom Sweet Alert Dialog
    if (showAlert) {
        CustomSweetAlert(
            title = "Save Confirmation",
            message = "Are you sure you want to save this allocation to the \"$selectedLocation\" location?",
            onConfirm = {
                onSubmit(allocatedItems)
                showAlert = false
            },
            onDismiss = { showAlert = false }
        )
    }
}

@Composable
private fun CustomSweetAlert(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = TjiwiColors.Primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QuestionMark,
                        contentDescription = "Question",
                        tint = TjiwiColors.Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Primary
                    ),
                    textAlign = TextAlign.Center
                )

                // Message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TjiwiColors.Secondary
                    ),
                    textAlign = TextAlign.Center
                )

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TjiwiColors.Secondary
                        ),
                        border = BorderStroke(1.dp, TjiwiColors.Secondary)
                    ) {
                        Text("Cancel")
                    }

                    // Confirm Button
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TjiwiColors.Primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun AllocToBinScanScreen(
    allocToBinItems: List<AllocToBinItem>,
    viewModel: UHFViewModel,
    onProceedToForm: () -> Unit,
    onClearAll: () -> Unit,
    onToggleItem: (AllocToBinItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TjiwiColors.Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderSection(
            title = "Alloc to Bin - Scan Items",
            subtitle = "Scan the RFID to proceed with item allocation to the designated bin."
        )

        ItemsList(
            items = allocToBinItems,
            modifier = Modifier.weight(1f),
            onItemToggle = onToggleItem,
        )

        Button(
            onClick = onProceedToForm,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = allocToBinItems.any { it.isAllocated },
            colors = ButtonDefaults.buttonColors(
                containerColor = TjiwiColors.Primary,
                contentColor = TjiwiColors.OnPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Next",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun AllocToBinScreen(
    navController: NavController,
    viewModel: UHFViewModel
) {
    val context = LocalContext.current

    // Dummy data untuk Alloc to Bin items
    var allocToBinItems by remember {
        mutableStateOf(
            listOf(
                AllocToBinItem("001", "Roll HVS 70gsm 90cm", "d Material", "RM001", "Gudang A - Zona 1", 2),
                AllocToBinItem("002", "Roll NCR Putih 60cm", "Raw Material", "RM002", "Gudang A - Zona 2", 3),
                AllocToBinItem("003", "Roll Thermal 80mm", "Raw Material", "RM003", "Gudang B - Zona 1", 4),
                AllocToBinItem("004", "Lem Perekat Akrilik", "Chemical", "CH001", "Gudang B - Zona 2", 10),
                AllocToBinItem("005", "Core Tube 3 inch", "Packaging", "PK001", "Gudang C - Main Storage", 100),
                AllocToBinItem("006", "Stretch Film", "Packaging", "PK002", "Area Packing - Line 1", 20),
                AllocToBinItem("007", "Label Barcode", "Label", "LB001", "Area Packing - Line 2", 500),
                AllocToBinItem("008", "Pallet Kayu", "Logistic", "LG001", "Quality Control Area", 15)
            )
        )
    }

    // Observe scanned RFID
    val scannedRfid = viewModel.scannedRfid

    // State untuk menampilkan form Alloc to Bin
    var showAllocToBinForm by remember { mutableStateOf(false) }

    var showSuccessAlert by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }


    // Initialize scanning mode untuk RFID
    LaunchedEffect(Unit) {
        viewModel.initUHF()
        viewModel.stopScanning()
        viewModel.changeScanModeToRfid()
        viewModel.changeIsSingleScanToFalse()

    }

    // Handle RFID scan result
    LaunchedEffect(scannedRfid) {
        if (scannedRfid.isNotEmpty()) {
            // Cek apakah RFID sudah pernah di-scan sebelumnya
            val isRfidAlreadyAllocated = allocToBinItems.any { it.rfidTag == scannedRfid }

            if (isRfidAlreadyAllocated) {
                // RFID sudah pernah di-scan, tampilkan pesan error
//                Toast.makeText(
//                    context,
//                    "RFID $scannedRfid sudah pernah dialokasi!",
//                    Toast.LENGTH_SHORT
//                ).show()
                viewModel.clearRfidResult()
                return@LaunchedEffect
            }

            // Cari item pertama yang belum dialokasi untuk di-assign RFID ini
            val firstUnallocatedItemIndex =
                allocToBinItems.indexOfFirst { !it.isAllocated && it.rfidTag == null }

            if (firstUnallocatedItemIndex != -1) {
                // Update item pertama yang belum dialokasi
                val updatedItems = allocToBinItems.toMutableList()
                updatedItems[firstUnallocatedItemIndex] =
                    updatedItems[firstUnallocatedItemIndex].copy(
                        rfidTag = scannedRfid,
                        isAllocated = true
                    )

                allocToBinItems = updatedItems
//                Toast.makeText(
//                    context,
//                    "RFID $scannedRfid has been successfully allocated to ${updatedItems[firstUnallocatedItemIndex].name} (${updatedItems[firstUnallocatedItemIndex].targetBin})!",
//                    Toast.LENGTH_SHORT
//                ).show()
                viewModel.clearRfidResult()
            } else {
                // Tidak ada item yang bisa di-assign
//                Toast.makeText(
//                    context,
//                    "All items have been allocated or no items are available",
//                    Toast.LENGTH_SHORT
//                ).show()
                viewModel.clearRfidResult()
            }
        }
    }

    if (!showAllocToBinForm) {
        // Tampilan scanning RFID
        AllocToBinScanScreen(
            allocToBinItems = allocToBinItems,
            viewModel = viewModel,
            onProceedToForm = { showAllocToBinForm = true },
            onClearAll = {
                val allocatedCount = allocToBinItems.count { it.isAllocated }
                allocToBinItems = allocToBinItems.map {
                    it.copy(rfidTag = null, isAllocated = false)
                }
                viewModel.clearAllResults()

                if (allocatedCount > 0) {
//                    Toast.makeText(
//                        context,
//                        "$allocatedCount allocated RFID(s) have been removed",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            },
            onToggleItem = { toggledItem ->
                allocToBinItems = allocToBinItems.map {
                    if (it.id == toggledItem.id) it.copy(isAllocated = !it.isAllocated) else it
                }
            },
        )
    } else {
        // Tampilan form Alloc to Bin
        AllocToBinFormScreen(
            allocToBinItems = allocToBinItems,
            onBack = { showAllocToBinForm = false },
            onSubmit = { items ->
                // Handle submit logic
//                Toast.makeText(
//                    context,
//                    "Alokasi bin berhasil disubmit untuk ${items.size} item!",
//                    Toast.LENGTH_LONG
//                ).show()
                // Handle submit logic
                successMessage = "Bin allocation successfully submitted for ${items.size} item(s)!"
                showSuccessAlert = true
                // Reset data
                allocToBinItems = allocToBinItems.map {
                    it.copy(rfidTag = null, isAllocated = false)
                }
                showAllocToBinForm = false
            }
        )
    }

    // State untuk success alert
//    var showSuccessAlert by remember { mutableStateOf(false) }
//    var successMessage by remember { mutableStateOf("") }

//    AllocToBinFormScreen(
//        allocToBinItems = allocToBinItems,
//        onBack = { showAllocToBinForm = false },
//        onSubmit = { items ->
//            // Handle submit logic
//            successMessage = "Alokasi bin berhasil disubmit untuk ${items.size} item!"
//            showSuccessAlert = true
//
//            // Reset data setelah success alert ditutup akan dilakukan di callback
//        }
//    )

// Success Sweet Alert
    if (showSuccessAlert) {
        SuccessSweetAlert(
            title = "Success!",
            message = successMessage,
            onConfirm = {
                // Reset data setelah user mengkonfirmasi
                allocToBinItems = allocToBinItems.map {
                    it.copy(rfidTag = null, isAllocated = false)
                }
                showSuccessAlert = false
                showAllocToBinForm = false
            }
        )
    }
}


@Composable
private fun SuccessSweetAlert(
    title: String,
    message: String,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = { /* Prevent dismiss on outside click */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Success Icon dengan animasi
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    ),
                    textAlign = TextAlign.Center
                )

                // Message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF666666),
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )

                // OK Button
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}