//package com.example.c5local.persentation.screen.stuffing
//
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.c5local.persentation.shared.ScanMode
//import com.example.c5local.persentation.shared.UHFViewModel
//
//// Data class untuk item Stuffing
//data class StuffingItem(
//    val id: String,
//    val name: String,
//    val category: String,
//    val plannedQty: Int,
//    val stuffedQty: Int = 0,
//    val rfidTag: String? = null,
//    val isStuffed: Boolean = false,
//    val containerNumber: String? = null
//)
//
//// Data class untuk Container
//data class Container(
//    val id: String,
//    val number: String,
//    val type: String,
//    val maxCapacity: Int,
//    val currentLoad: Int = 0,
//    val items: List<StuffingItem> = listOf()
//)
//
//// Tjiwi Colors (menggunakan yang sama dari Registration)
//object TjiwiColors {
//    val Primary = Color(0xFFE53E3E)
//    val PrimaryDark = Color(0xFFB91C1C)
//    val Secondary = Color(0xFF4A5568)
//    val Background = Color(0xFFF7FAFC)
//    val Surface = Color(0xFFFFFFFF)
//    val OnPrimary = Color(0xFFFFFFFF)
//    val OnSurface = Color(0xFF2D3748)
//    val Success = Color(0xFF38A169)
//    val Warning = Color(0xFFD69E2E)
//    val Error = Color(0xFFE53E3E)
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun StuffedRfidListCard(stuffedRfidList: List<String>) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = TjiwiColors.Success.copy(alpha = 0.1f)
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ShoppingCart,
//                    contentDescription = null,
//                    tint = TjiwiColors.Success,
//                    modifier = Modifier.size(20.dp)
//                )
//                Text(
//                    text = "Item Terstufikasi (${stuffedRfidList.size})",
//                    style = MaterialTheme.typography.titleSmall.copy(
//                        fontWeight = FontWeight.Bold,
//                        color = TjiwiColors.Success
//                    )
//                )
//            }
//
//            // Display stuffed RFIDs in a compact format
//            LazyRow(
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(stuffedRfidList) { rfid ->
//                    Card(
//                        colors = CardDefaults.cardColors(
//                            containerColor = TjiwiColors.Success
//                        )
//                    ) {
//                        Text(
//                            text = rfid,
//                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
//                            style = MaterialTheme.typography.bodySmall.copy(
//                                color = TjiwiColors.OnPrimary,
//                                fontWeight = FontWeight.Medium
//                            )
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ContainerSelectionCard(
//    containers: List<Container>,
//    selectedContainer: Container?,
//    onContainerSelected: (Container) -> Unit
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Share,
//                    contentDescription = null,
//                    tint = TjiwiColors.Primary,
//                    modifier = Modifier.size(20.dp)
//                )
//                Text(
//                    text = "Pilih Container",
//                    style = MaterialTheme.typography.titleMedium.copy(
//                        fontWeight = FontWeight.Bold,
//                        color = TjiwiColors.Primary
//                    )
//                )
//            }
//
//            LazyRow(
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(containers) { container ->
//                    Card(
//                        modifier = Modifier.clickable { onContainerSelected(container) },
//                        colors = CardDefaults.cardColors(
//                            containerColor = if (selectedContainer?.id == container.id)
//                                TjiwiColors.Primary else TjiwiColors.Background
//                        ),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(12.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                text = container.number,
//                                style = MaterialTheme.typography.titleSmall.copy(
//                                    fontWeight = FontWeight.Bold,
//                                    color = if (selectedContainer?.id == container.id)
//                                        TjiwiColors.OnPrimary else TjiwiColors.OnSurface
//                                )
//                            )
//                            Text(
//                                text = "${container.currentLoad}/${container.maxCapacity}",
//                                style = MaterialTheme.typography.bodySmall.copy(
//                                    color = if (selectedContainer?.id == container.id)
//                                        TjiwiColors.OnPrimary else TjiwiColors.Secondary
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//
//            if (selectedContainer != null) {
//                Divider(color = TjiwiColors.Background)
//                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
//                    Text(
//                        text = "Container Terpilih: ${selectedContainer.number}",
//                        style = MaterialTheme.typography.bodyMedium.copy(
//                            fontWeight = FontWeight.Medium,
//                            color = TjiwiColors.OnSurface
//                        )
//                    )
//                    Text(
//                        text = "Tipe: ${selectedContainer.type}",
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = TjiwiColors.Secondary
//                        )
//                    )
//                    Text(
//                        text = "Kapasitas: ${selectedContainer.currentLoad}/${selectedContainer.maxCapacity}",
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = TjiwiColors.Secondary
//                        )
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun StuffingScreen(
//    navController: NavController,
//    viewModel: UHFViewModel
//) {
//    val context = LocalContext.current
//
//    // Dummy data untuk Container
//    var containers by remember {
//        mutableStateOf(
//            listOf(
//                Container("CNT001", "TCLU-1234567", "20ft Standard", 100),
//                Container("CNT002", "MSKU-9876543", "40ft High Cube", 200),
//                Container("CNT003", "CSQU-5555555", "20ft Reefer", 80)
//            )
//        )
//    }
//
//    // Selected Container
//    var selectedContainer by remember { mutableStateOf<Container?>(null) }
//
//    // Dummy data untuk Stuffing items
//    var stuffingItems by remember {
//        mutableStateOf(
//            listOf(
//                StuffingItem("ST001", "Cardboard Box A", "Packaging", 20),
//                StuffingItem("ST002", "Electronics Package", "Electronics", 15),
//                StuffingItem("ST003", "Textile Goods", "Apparel", 30),
//                StuffingItem("ST004", "Machinery Parts", "Industrial", 8),
//                StuffingItem("ST005", "Food Products", "FMCG", 25),
//                StuffingItem("ST006", "Chemical Drums", "Chemical", 12),
//                StuffingItem("ST007", "Furniture Items", "Furniture", 10),
//                StuffingItem("ST008", "Medical Supplies", "Healthcare", 18)
//            )
//        )
//    }
//
//    // Observe scanned RFID
//    val scannedRfid = viewModel.scannedRfid
//
//    // State untuk menampilkan form Stuffing Summary
//    var showStuffingSummary by remember { mutableStateOf(false) }
//
//    // Initialize scanning mode untuk RFID
//    LaunchedEffect(Unit) {
//        viewModel.initUHF()
//        viewModel.stopScanning()
//        viewModel.changeScanModeToRfid()
//    }
//
//    // Handle RFID scan result
//    LaunchedEffect(scannedRfid) {
//        if (scannedRfid.isNotEmpty() && selectedContainer != null) {
//            // Cek apakah RFID sudah pernah di-scan sebelumnya
//            val isRfidAlreadyStuffed = stuffingItems.any { it.rfidTag == scannedRfid }
//
//            if (isRfidAlreadyStuffed) {
//                // RFID sudah pernah di-scan, tampilkan pesan error
//                Toast.makeText(
//                    context,
//                    "RFID $scannedRfid sudah pernah distuffing!",
//                    Toast.LENGTH_SHORT
//                ).show()
//                viewModel.clearRfidResult()
//                return@LaunchedEffect
//            }
//
//            // Cek kapasitas container
//            if (selectedContainer!!.currentLoad >= selectedContainer!!.maxCapacity) {
//                Toast.makeText(
//                    context,
//                    "Container ${selectedContainer!!.number} sudah penuh!",
//                    Toast.LENGTH_SHORT
//                ).show()
//                viewModel.clearRfidResult()
//                return@LaunchedEffect
//            }
//
//            // Cari item pertama yang belum di-stuff untuk di-assign RFID ini
//            val firstUnstuffedItemIndex = stuffingItems.indexOfFirst {
//                !it.isStuffed && it.rfidTag == null
//            }
//
//            if (firstUnstuffedItemIndex != -1) {
//                // Update item pertama yang belum di-stuff
//                val updatedItems = stuffingItems.toMutableList()
//                updatedItems[firstUnstuffedItemIndex] = updatedItems[firstUnstuffedItemIndex].copy(
//                    rfidTag = scannedRfid,
//                    isStuffed = true,
//                    stuffedQty = updatedItems[firstUnstuffedItemIndex].stuffedQty + 1,
//                    containerNumber = selectedContainer!!.number
//                )
//
//                stuffingItems = updatedItems
//
//                // Update container load
//                val updatedContainers = containers.toMutableList()
//                val containerIndex = containers.indexOfFirst { it.id == selectedContainer!!.id }
//                if (containerIndex != -1) {
//                    updatedContainers[containerIndex] = updatedContainers[containerIndex].copy(
//                        currentLoad = updatedContainers[containerIndex].currentLoad + 1
//                    )
//                    containers = updatedContainers
//                    selectedContainer = updatedContainers[containerIndex]
//                }
//
//                Toast.makeText(
//                    context,
//                    "RFID $scannedRfid berhasil distuffing ke ${selectedContainer!!.number}!",
//                    Toast.LENGTH_SHORT
//                ).show()
//                viewModel.clearRfidResult()
//            } else {
//                // Tidak ada item yang bisa di-assign
//                Toast.makeText(
//                    context,
//                    "Semua item sudah distuffing atau tidak ada item tersedia",
//                    Toast.LENGTH_SHORT
//                ).show()
//                viewModel.clearRfidResult()
//            }
//        } else if (scannedRfid.isNotEmpty() && selectedContainer == null) {
//            Toast.makeText(
//                context,
//                "Pilih container terlebih dahulu!",
//                Toast.LENGTH_SHORT
//            ).show()
//            viewModel.clearRfidResult()
//        }
//    }
//
//    if (!showStuffingSummary) {
//        // Tampilan scanning RFID
//        StuffingScanScreen(
//            containers = containers,
//            selectedContainer = selectedContainer,
//            onContainerSelected = { selectedContainer = it },
//            stuffingItems = stuffingItems,
//            viewModel = viewModel,
//            onProceedToSummary = { showStuffingSummary = true },
//            onClearAll = {
//                val stuffedCount = stuffingItems.count { it.isStuffed }
//                stuffingItems = stuffingItems.map {
//                    it.copy(rfidTag = null, isStuffed = false, stuffedQty = 0, containerNumber = null)
//                }
//
//                // Reset container loads
//                containers = containers.map { it.copy(currentLoad = 0) }
//                selectedContainer = selectedContainer?.copy(currentLoad = 0)
//
//                viewModel.clearAllResults()
//
//                if (stuffedCount > 0) {
//                    Toast.makeText(
//                        context,
//                        "$stuffedCount item yang terstufikasi telah dihapus",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        )
//    } else {
//        // Tampilan stuffing summary
//        StuffingSummaryScreen(
//            containers = containers,
//            stuffingItems = stuffingItems,
//            onBack = { showStuffingSummary = false },
//            onSubmit = { items ->
//                // Handle submit logic
//                Toast.makeText(
//                    context,
//                    "Stuffing berhasil diselesaikan untuk ${items.size} item!",
//                    Toast.LENGTH_LONG
//                ).show()
//                // Reset data
//                stuffingItems = stuffingItems.map {
//                    it.copy(rfidTag = null, isStuffed = false, stuffedQty = 0, containerNumber = null)
//                }
//                containers = containers.map { it.copy(currentLoad = 0) }
//                selectedContainer = null
//                showStuffingSummary = false
//            }
//        )
//    }
//}
//
//@Composable
//private fun StuffingScanScreen(
//    containers: List<Container>,
//    selectedContainer: Container?,
//    onContainerSelected: (Container) -> Unit,
//    stuffingItems: List<StuffingItem>,
//    viewModel: UHFViewModel,
//    onProceedToSummary: () -> Unit,
//    onClearAll: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(TjiwiColors.Background)
//            .padding(24.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        // Header
//        HeaderSection(
//            title = "Stuffing - Load Container",
//            subtitle = "Pilih container dan scan RFID untuk stuffing"
//        )
//
//        // Container Selection
//        ContainerSelectionCard(
//            containers = containers,
//            selectedContainer = selectedContainer,
//            onContainerSelected = onContainerSelected
//        )
//
//        // Status Summary
//        StatusSummaryCard(stuffingItems)
//
//        // Stuffed RFID List (jika ada)
//        val stuffedRfidList = stuffingItems.filter { it.isStuffed }.map { it.rfidTag!! }
//        if (stuffedRfidList.isNotEmpty()) {
//            StuffedRfidListCard(stuffedRfidList)
//        }
//
//        // Scan Controls
//        ScanControlsSection(
//            viewModel = viewModel,
//            onClearAll = onClearAll,
//            isContainerSelected = selectedContainer != null
//        )
//
//        // Items List
//        ItemsList(
//            items = stuffingItems,
//            modifier = Modifier.weight(1f)
//        )
//
//        // Navigation Button
//        Button(
//            onClick = onProceedToSummary,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            enabled = stuffingItems.any { it.isStuffed },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = TjiwiColors.Primary,
//                contentColor = TjiwiColors.OnPrimary
//            ),
//            shape = RoundedCornerShape(12.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.AccountBox,
//                contentDescription = null,
//                modifier = Modifier.size(20.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                text = "LIHAT STUFFING SUMMARY",
//                style = MaterialTheme.typography.titleMedium.copy(
//                    fontWeight = FontWeight.Bold
//                )
//            )
//        }
//    }
//}
//
//@Composable
//private fun StuffingSummaryScreen(
//    containers: List<Container>,
//    stuffingItems: List<StuffingItem>,
//    onBack: () -> Unit,
//    onSubmit: (List<StuffingItem>) -> Unit
//) {
//    val stuffedItems = stuffingItems.filter { it.isStuffed }
//    val stuffedByContainer = stuffedItems.groupBy { it.containerNumber }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(TjiwiColors.Background)
//            .padding(24.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        // Header with back button
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = onBack) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBack,
//                    contentDescription = "Back",
//                    tint = TjiwiColors.Primary
//                )
//            }
//
//            Text(
//                text = "Stuffing Summary",
//                style = MaterialTheme.typography.headlineSmall.copy(
//                    fontWeight = FontWeight.Bold,
//                    color = TjiwiColors.Primary
//                )
//            )
//
//            Spacer(modifier = Modifier.width(48.dp)) // Balance the back button
//        }
//
//        // Overall Summary Card
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
//            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Text(
//                    text = "Ringkasan Stuffing",
//                    style = MaterialTheme.typography.titleMedium.copy(
//                        fontWeight = FontWeight.Bold,
//                        color = TjiwiColors.Primary
//                    )
//                )
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text("Total Item Stuffed:", color = TjiwiColors.Secondary)
//                    Text(
//                        text = stuffedItems.size.toString(),
//                        fontWeight = FontWeight.Bold,
//                        color = TjiwiColors.Success
//                    )
//                }
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text("Container Digunakan:", color = TjiwiColors.Secondary)
//                    Text(
//                        text = stuffedByContainer.keys.size.toString(),
//                        fontWeight = FontWeight.Bold,
//                        color = TjiwiColors.Success
//                    )
//                }
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text("Total Quantity:", color = TjiwiColors.Secondary)
//                    Text(
//                        text = stuffedItems.sumOf { it.stuffedQty }.toString(),
//                        fontWeight = FontWeight.Bold,
//                        color = TjiwiColors.Success
//                    )
//                }
//            }
//        }
//
//        // Stuffed Items by Container
//        LazyColumn(
//            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            stuffedByContainer.forEach { (containerNumber, items) ->
//                item {
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(16.dp),
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Email,
//                                    contentDescription = null,
//                                    tint = TjiwiColors.Primary,
//                                    modifier = Modifier.size(20.dp)
//                                )
//                                Text(
//                                    text = "Container: $containerNumber",
//                                    style = MaterialTheme.typography.titleSmall.copy(
//                                        fontWeight = FontWeight.Bold,
//                                        color = TjiwiColors.Primary
//                                    )
//                                )
//                                Spacer(modifier = Modifier.weight(1f))
//                                Text(
//                                    text = "${items.size} items",
//                                    style = MaterialTheme.typography.bodySmall.copy(
//                                        color = TjiwiColors.Secondary
//                                    )
//                                )
//                            }
//
//                            items.forEach { item ->
//                                StuffedItemCard(item)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        // Submit Button
//        Button(
//            onClick = { onSubmit(stuffedItems) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = TjiwiColors.Success,
//                contentColor = TjiwiColors.OnPrimary
//            ),
//            shape = RoundedCornerShape(12.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.CheckCircle,
//                contentDescription = null,
//                modifier = Modifier.size(24.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = "SELESAIKAN STUFFING",
//                style = MaterialTheme.typography.titleMedium.copy(
//                    fontWeight = FontWeight.Bold
//                )
//            )
//        }
//    }
//}
//
//@Composable
//private fun HeaderSection(title: String, subtitle: String) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Text(
//            text = title,
//            style = MaterialTheme.typography.headlineLarge.copy(
//                fontWeight = FontWeight.Bold,
//                color = TjiwiColors.Primary
//            ),
//            textAlign = TextAlign.Center
//        )
//
//        Text(
//            text = subtitle,
//            style = MaterialTheme.typography.bodyMedium.copy(
//                color = TjiwiColors.Secondary
//            ),
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(top = 8.dp)
//        )
//    }
//}
//
//@Composable
//private fun StatusSummaryCard(items: List<StuffingItem>) {
//    val totalItems = items.size
//    val stuffedItems = items.count { it.isStuffed }
//    val totalPlanned = items.sumOf { it.plannedQty }
//    val totalStuffed = items.sumOf { it.stuffedQty }
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            StatusItem("Items", "$stuffedItems/$totalItems", TjiwiColors.Primary)
//            StatusItem("Quantity", "$totalStuffed/$totalPlanned", TjiwiColors.Success)
//            StatusItem("Progress", "${if (totalItems > 0) (stuffedItems * 100 / totalItems) else 0}%", TjiwiColors.Warning)
//        }
//    }
//}
//
//@Composable
//private fun StatusItem(label: String, value: String, color: Color) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = value,
//            style = MaterialTheme.typography.titleLarge.copy(
//                fontWeight = FontWeight.Bold,
//                color = color
//            )
//        )
//        Text(
//            text = label,
//            style = MaterialTheme.typography.bodySmall.copy(
//                color = TjiwiColors.Secondary
//            )
//        )
//    }
//}
//
//@Composable
//private fun ScanControlsSection(
//    viewModel: UHFViewModel,
//    onClearAll: () -> Unit,
//    isContainerSelected: Boolean
//) {
//    Row(
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        // Start/Stop RFID Scan Button
//        Button(
//            onClick = {
//                if (viewModel.isScanning) {
//                    viewModel.stopScanning()
//                } else {
//                    if (viewModel.isSingleScan) {
//                        viewModel.startRfidScanningOnce()
//                    } else {
//                        viewModel.startRfidScanning()
//                    }
//                }
//            },
//            modifier = Modifier.weight(1f),
//            enabled = isContainerSelected,
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (viewModel.isScanning) TjiwiColors.Error else TjiwiColors.Primary,
//                contentColor = TjiwiColors.OnPrimary
//            ),
//            shape = RoundedCornerShape(12.dp)
//        ) {
//            if (viewModel.isScanning) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(18.dp),
//                    color = TjiwiColors.OnPrimary,
//                    strokeWidth = 2.dp
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Stop Scan")
//            } else {
//                Icon(
//                    imageVector = Icons.Default.Close,
//                    contentDescription = null,
//                    modifier = Modifier.size(18.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(if (isContainerSelected) "Scan RFID" else "Pilih Container")
//            }
//        }
//
//        // Clear All Button
//        OutlinedButton(
//            onClick = onClearAll,
//            modifier = Modifier.weight(1f),
//            colors = ButtonDefaults.outlinedButtonColors(
//                contentColor = TjiwiColors.Secondary
//            ),
//            shape = RoundedCornerShape(12.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Refresh,
//                contentDescription = null,
//                modifier = Modifier.size(18.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Reset")
//        }
//    }
//}
//
//@Composable
//private fun ItemsList(
//    items: List<StuffingItem>,
//    modifier: Modifier = Modifier
//) {
//    LazyColumn(
//        modifier = modifier,
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(items) { item ->
//            ItemCard(item)
//        }
//    }
//}
//
//@Composable
//private fun ItemCard(item: StuffingItem) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = if (item.isStuffed) TjiwiColors.Success.copy(alpha = 0.1f) else TjiwiColors.Surface
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = item.name,
//                    style = MaterialTheme.typography.titleMedium.copy(
//                        fontWeight = FontWeight.Bold,
//                        color = TjiwiColors.OnSurface
//                    )
//                )
//                Text(
//                    text = "ID: ${item.id} | ${item.category}",
//                    style = MaterialTheme.typography.bodySmall.copy(
//                        color = TjiwiColors.Secondary
//                    )
//                )
//                Text(
//                    text = "Planned: ${item.plannedQty} | Stuffed: ${item.stuffedQty}",
//                    style = MaterialTheme.typography.bodySmall.copy(
//                        color = TjiwiColors.Secondary
//                    )
//                )
//                if (item.rfidTag != null) {
//                    Text(
//                        text = "RFID: ${item.rfidTag}",
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = TjiwiColors.Success,
//                            fontWeight = FontWeight.Medium
//                        )
//                    )
//                }
//                if (item.containerNumber != null) {
//                    Text(
//                        text = "Container: ${item.containerNumber}",
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = TjiwiColors.Primary,
//                            fontWeight = FontWeight.Medium
//                        )
//                    )
//                }
//            }
//
//            Icon(
//                imageVector = if (item.isStuffed) Icons.Default.Create else Icons.Default.CheckCircle,
//                contentDescription = null,
//                tint = if (item.isStuffed) TjiwiColors.Success else TjiwiColors.Secondary,
//                modifier = Modifier.size(24.dp)
//            )
//        }
//    }
//}
//
//@Composable
//private fun StuffedItemCard(item: StuffingItem) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = TjiwiColors.Success.copy(alpha = 0.1f)
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = item.name,
//                    style = MaterialTheme.typography.titleSmall.copy(
//                        fontWeight = FontWeight.Bold,
//                        color = TjiwiColors.OnSurface
//                    )
//                )
//                Text(
//                    text = "ID: ${item.id}",
//                    style = MaterialTheme.typography.bodySmall.copy(
//                        color = TjiwiColors.Secondary
//                    )
//                )
//                Text(
//                    text = "RFID: ${item.rfidTag}",
//                    style = MaterialTheme.typography.bodySmall.copy(
//                        color = TjiwiColors.Success,
//                        fontWeight = FontWeight.Medium
//                    )
//                )
//                Text(
//                    text = "Qty Stuffed: ${item.stuffedQty}",
//                    style = MaterialTheme.typography.bodySmall.copy(
//                        color = TjiwiColors.Success,
//                        fontWeight = FontWeight.Medium
//                    )
//                )
//            }
//
//            Icon(
//                imageVector = Icons.Default.Star,
//                contentDescription = null,
//                tint = TjiwiColors.Success,
//                modifier = Modifier.size(20.dp)
//            )
//        }
//    }
//}


package com.example.c5local.persentation.screen.stuffing

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.c5local.persentation.shared.ScanMode
import com.example.c5local.persentation.shared.UHFViewModel

// Data class untuk item Stuffing
data class StuffingItem(
    val id: String,
    val name: String,
    val category: String,
    val plannedQty: Int,
    val stuffedQty: Int = 0,
    val rfidTag: String? = null,
    val isStuffed: Boolean = false,
    val containerNumber: String? = null
)

// Data class untuk Container
data class Container(
    val id: String,
    val number: String,
    val type: String,
    val maxCapacity: Int,
    val currentLoad: Int = 0,
    val items: List<StuffingItem> = listOf()
)

// Tjiwi Colors (menggunakan yang sama dari Registration)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StuffedRfidListCard(stuffedRfidList: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TjiwiColors.Success.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = TjiwiColors.Success,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Item Terstufikasi (${stuffedRfidList.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Success
                    )
                )
            }

            // Display stuffed RFIDs in a compact format with proper scrolling
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(stuffedRfidList) { rfid ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = TjiwiColors.Success
                        )
                    ) {
                        Text(
                            text = rfid,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TjiwiColors.OnPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContainerSelectionCard(
    containers: List<Container>,
    selectedContainer: Container?,
    onContainerSelected: (Container) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = TjiwiColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Pilih Container",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Primary
                    )
                )
            }

            // Container selection with proper scrolling
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(containers) { container ->
                    Card(
                        modifier = Modifier.clickable { onContainerSelected(container) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedContainer?.id == container.id)
                                TjiwiColors.Primary else TjiwiColors.Background
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = container.number,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedContainer?.id == container.id)
                                        TjiwiColors.OnPrimary else TjiwiColors.OnSurface
                                )
                            )
                            Text(
                                text = "${container.currentLoad}/${container.maxCapacity}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (selectedContainer?.id == container.id)
                                        TjiwiColors.OnPrimary else TjiwiColors.Secondary
                                )
                            )
                        }
                    }
                }
            }

            // Selected container details
            if (selectedContainer != null) {
                HorizontalDivider(color = TjiwiColors.Background)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Container Terpilih: ${selectedContainer.number}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = TjiwiColors.OnSurface
                        )
                    )
                    Text(
                        text = "Tipe: ${selectedContainer.type}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TjiwiColors.Secondary
                        )
                    )
                    Text(
                        text = "Kapasitas: ${selectedContainer.currentLoad}/${selectedContainer.maxCapacity}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TjiwiColors.Secondary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(title: String, subtitle: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TjiwiColors.Primary
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TjiwiColors.Secondary
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun StatusSummaryCard(items: List<StuffingItem>) {
    val totalItems = items.size
    val stuffedItems = items.count { it.isStuffed }
    val totalPlanned = items.sumOf { it.plannedQty }
    val totalStuffed = items.sumOf { it.stuffedQty }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusItem("Items", "$stuffedItems/$totalItems", TjiwiColors.Primary)
            StatusItem("Quantity", "$totalStuffed/$totalPlanned", TjiwiColors.Success)
            StatusItem("Progress", "${if (totalItems > 0) (stuffedItems * 100 / totalItems) else 0}%", TjiwiColors.Warning)
        }
    }
}

@Composable
private fun StatusItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TjiwiColors.Secondary
            )
        )
    }
}

@Composable
private fun ScanControlsSection(
    viewModel: UHFViewModel,
    onClearAll: () -> Unit,
    isContainerSelected: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Start/Stop RFID Scan Button
        Button(
            onClick = {
                if (viewModel.isScanning) {
                    viewModel.stopScanning()
                } else {
                    if (viewModel.isSingleScan) {
                        viewModel.startRfidScanningOnce()
                    } else {
                        viewModel.startRfidScanning()
                    }
                }
            },
            modifier = Modifier.weight(1f),
            enabled = isContainerSelected,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.isScanning) TjiwiColors.Error else TjiwiColors.Primary,
                contentColor = TjiwiColors.OnPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (viewModel.isScanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = TjiwiColors.OnPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stop Scan")
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isContainerSelected) "Scan RFID" else "Pilih Container")
            }
        }

        // Clear All Button
        OutlinedButton(
            onClick = onClearAll,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TjiwiColors.Secondary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset")
        }
    }
}

@Composable
private fun ItemsList(
    items: List<StuffingItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(items) { item ->
            ItemCard(item)
        }
    }
}

@Composable
private fun ItemCard(item: StuffingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isStuffed) TjiwiColors.Success.copy(alpha = 0.1f) else TjiwiColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.OnSurface
                    )
                )
                Text(
                    text = "ID: ${item.id} | ${item.category}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TjiwiColors.Secondary
                    )
                )
                Text(
                    text = "Planned: ${item.plannedQty} | Stuffed: ${item.stuffedQty}",
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
                if (item.containerNumber != null) {
                    Text(
                        text = "Container: ${item.containerNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TjiwiColors.Primary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Icon(
                imageVector = if (item.isStuffed) Icons.Default.CheckCircle else Icons.Default.Check,
                contentDescription = null,
                tint = if (item.isStuffed) TjiwiColors.Success else TjiwiColors.Secondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun StuffedItemCard(item: StuffingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TjiwiColors.Success.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.OnSurface
                    )
                )
                Text(
                    text = "ID: ${item.id}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TjiwiColors.Secondary
                    )
                )
                Text(
                    text = "RFID: ${item.rfidTag}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TjiwiColors.Success,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "Qty Stuffed: ${item.stuffedQty}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TjiwiColors.Success,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = TjiwiColors.Success,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun StuffingScreen(
    navController: NavController,
    viewModel: UHFViewModel
) {
    val context = LocalContext.current

    // Dummy data untuk Container
    var containers by remember {
        mutableStateOf(
            listOf(
                Container("CNT001", "TCLU-1234567", "20ft Standard", 100),
                Container("CNT002", "MSKU-9876543", "40ft High Cube", 200),
                Container("CNT003", "CSQU-5555555", "20ft Reefer", 80)
            )
        )
    }

    // Selected Container
    var selectedContainer by remember { mutableStateOf<Container?>(null) }

    // Dummy data untuk Stuffing items
    var stuffingItems by remember {
        mutableStateOf(
            listOf(
                StuffingItem("ST001", "Cardboard Box A", "Packaging", 20),
                StuffingItem("ST002", "Electronics Package", "Electronics", 15),
                StuffingItem("ST003", "Textile Goods", "Apparel", 30),
                StuffingItem("ST004", "Machinery Parts", "Industrial", 8),
                StuffingItem("ST005", "Food Products", "FMCG", 25),
                StuffingItem("ST006", "Chemical Drums", "Chemical", 12),
                StuffingItem("ST007", "Furniture Items", "Furniture", 10),
                StuffingItem("ST008", "Medical Supplies", "Healthcare", 18)
            )
        )
    }

    // Observe scanned RFID
    val scannedRfid = viewModel.scannedRfid

    // State untuk menampilkan form Stuffing Summary
    var showStuffingSummary by remember { mutableStateOf(false) }

    // Initialize scanning mode untuk RFID
    LaunchedEffect(Unit) {
        viewModel.initUHF()
        viewModel.stopScanning()
        viewModel.changeScanModeToRfid()
    }

    // Handle RFID scan result
    LaunchedEffect(scannedRfid) {
        if (scannedRfid.isNotEmpty() && selectedContainer != null) {
            // Cek apakah RFID sudah pernah di-scan sebelumnya
            val isRfidAlreadyStuffed = stuffingItems.any { it.rfidTag == scannedRfid }

            if (isRfidAlreadyStuffed) {
                // RFID sudah pernah di-scan, tampilkan pesan error
                Toast.makeText(
                    context,
                    "RFID $scannedRfid sudah pernah distuffing!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearRfidResult()
                return@LaunchedEffect
            }

            // Cek kapasitas container
            if (selectedContainer!!.currentLoad >= selectedContainer!!.maxCapacity) {
                Toast.makeText(
                    context,
                    "Container ${selectedContainer!!.number} sudah penuh!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearRfidResult()
                return@LaunchedEffect
            }

            // Cari item pertama yang belum di-stuff untuk di-assign RFID ini
            val firstUnstuffedItemIndex = stuffingItems.indexOfFirst {
                !it.isStuffed && it.rfidTag == null
            }

            if (firstUnstuffedItemIndex != -1) {
                // Update item pertama yang belum di-stuff
                val updatedItems = stuffingItems.toMutableList()
                updatedItems[firstUnstuffedItemIndex] = updatedItems[firstUnstuffedItemIndex].copy(
                    rfidTag = scannedRfid,
                    isStuffed = true,
                    stuffedQty = updatedItems[firstUnstuffedItemIndex].stuffedQty + 1,
                    containerNumber = selectedContainer!!.number
                )

                stuffingItems = updatedItems

                // Update container load
                val updatedContainers = containers.toMutableList()
                val containerIndex = containers.indexOfFirst { it.id == selectedContainer!!.id }
                if (containerIndex != -1) {
                    updatedContainers[containerIndex] = updatedContainers[containerIndex].copy(
                        currentLoad = updatedContainers[containerIndex].currentLoad + 1
                    )
                    containers = updatedContainers
                    selectedContainer = updatedContainers[containerIndex]
                }

                Toast.makeText(
                    context,
                    "RFID $scannedRfid berhasil distuffing ke ${selectedContainer!!.number}!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearRfidResult()
            } else {
                // Tidak ada item yang bisa di-assign
                Toast.makeText(
                    context,
                    "Semua item sudah distuffing atau tidak ada item tersedia",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearRfidResult()
            }
        } else if (scannedRfid.isNotEmpty() && selectedContainer == null) {
            Toast.makeText(
                context,
                "Pilih container terlebih dahulu!",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.clearRfidResult()
        }
    }

    if (!showStuffingSummary) {
        // Tampilan scanning RFID
        StuffingScanScreen(
            containers = containers,
            selectedContainer = selectedContainer,
            onContainerSelected = { selectedContainer = it },
            stuffingItems = stuffingItems,
            viewModel = viewModel,
            onProceedToSummary = { showStuffingSummary = true },
            onClearAll = {
                val stuffedCount = stuffingItems.count { it.isStuffed }

                // Reset stuffing items
                stuffingItems = stuffingItems.map {
                    it.copy(rfidTag = null, isStuffed = false, stuffedQty = 0, containerNumber = null)
                }

                // Reset container loads
                containers = containers.map { it.copy(currentLoad = 0) }
                selectedContainer = selectedContainer?.copy(currentLoad = 0)

                viewModel.clearAllResults()

                if (stuffedCount > 0) {
                    Toast.makeText(
                        context,
                        "$stuffedCount item yang terstufikasi telah dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    } else {
        StuffingSummaryScreen(
            containers = containers,
            stuffingItems = stuffingItems,
            onBack = { showStuffingSummary = false },
            onSubmit = { items ->
                // Handle submit logic
                Toast.makeText(
                    context,
                    "Stuffing berhasil diselesaikan untuk ${items.size} item!",
                    Toast.LENGTH_LONG
                ).show()

                // Reset data
                stuffingItems = stuffingItems.map {
                    it.copy(rfidTag = null, isStuffed = false, stuffedQty = 0, containerNumber = null)
                }
                containers = containers.map { it.copy(currentLoad = 0) }
                selectedContainer = null
                showStuffingSummary = false
            }
        )
    }
}

@Composable
fun StuffingSummaryScreen(
    containers: List<Container>,
    stuffingItems: List<StuffingItem>,
    onBack: () -> Unit,
    onSubmit: (List<StuffingItem>) -> Unit
) {
    val stuffedItems = stuffingItems.filter { it.isStuffed }
    val stuffedByContainer = stuffedItems.groupBy { it.containerNumber }
    val context = LocalContext.current

    // State for confirmation dialog
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TjiwiColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TjiwiColors.Primary
                )
            }

            Text(
                text = "Stuffing Summary",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TjiwiColors.Primary
                )
            )

            Spacer(modifier = Modifier.width(48.dp)) // Balance the layout
        }

        // Overall Summary Card with more details
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
                    text = "Ringkasan Stuffing",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Primary
                    )
                )

                // Summary items in a grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryItem(
                        label = "Total Item",
                        value = stuffedItems.size.toString(),
                        icon = Icons.Default.List,
                        color = TjiwiColors.Primary
                    )
                    SummaryItem(
                        label = "Total Qty",
                        value = stuffedItems.sumOf { it.stuffedQty }.toString(),
                        icon = Icons.Default.Numbers,
                        color = TjiwiColors.Success
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryItem(
                        label = "Container",
                        value = stuffedByContainer.keys.size.toString(),
                        icon = Icons.Default.Storage,
                        color = TjiwiColors.Warning
                    )
                    SummaryItem(
                        label = "Progress",
                        value = "${if (stuffingItems.isNotEmpty()) (stuffedItems.size * 100 / stuffingItems.size) else 0}%",
                        icon = Icons.Default.Build,
                        color = TjiwiColors.Success
                    )
                }

                // Detailed breakdown
                Divider(color = TjiwiColors.Background)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Detail Per Container:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = TjiwiColors.Secondary
                        )
                    )

                    stuffedByContainer.forEach { (containerNum, items) ->
                        val container = containers.find { it.number == containerNum }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = containerNum ?: "Unknown",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "${items.size} items (${items.sumOf { it.stuffedQty }} qty)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TjiwiColors.Secondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Stuffed Items by Container - Improved with expandable sections
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stuffedByContainer.forEach { (containerNumber, items) ->
                val container = containers.find { it.number == containerNumber }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.clickable { isExpanded = !isExpanded },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = TjiwiColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Container: ${containerNumber ?: "Unknown"}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TjiwiColors.Primary
                                    )
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${items.size} items",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TjiwiColors.Secondary
                                    )
                                )
                            }

                            if (container != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tipe: ${container.type}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Kapasitas: ${container.currentLoad}/${container.maxCapacity}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            if (isExpanded) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items.forEach { item ->
                                        DetailedStuffedItemCard(item)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Back button
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TjiwiColors.Primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("KEMBALI")
            }

            // Submit button
            Button(
                onClick = { showConfirmationDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TjiwiColors.Success,
                    contentColor = TjiwiColors.OnPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "SUBMIT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Stuffing",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Anda akan menyelesaikan proses stuffing untuk:")
                    Text(
                        text = "${stuffedItems.size} item",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stuffedByContainer.keys.size} container",
                        fontWeight = FontWeight.Bold
                    )
                    Text("Pastikan semua data sudah benar.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        onSubmit(stuffedItems)
                        Toast.makeText(
                            context,
                            "Stuffing berhasil disubmit!",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TjiwiColors.Success,
                        contentColor = TjiwiColors.OnPrimary
                    )
                ) {
                    Text("KONFIRMASI")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text("BATAL")
                }
            }
        )
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TjiwiColors.Secondary
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }
}

@Composable
private fun DetailedStuffedItemCard(item: StuffingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TjiwiColors.Success.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Main info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Badge(
                    containerColor = TjiwiColors.Success.copy(alpha = 0.2f),
                    contentColor = TjiwiColors.Success
                ) {
                    Text("Qty: ${item.stuffedQty}")
                }
            }

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "ID: ${item.id}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TjiwiColors.Secondary
                        )
                    )
                    Text(
                        text = "Kategori: ${item.category}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TjiwiColors.Secondary
                        )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "RFID: ${item.rfidTag ?: "-"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TjiwiColors.Success,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Plan: ${item.plannedQty}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TjiwiColors.Secondary
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StuffingScanScreen(
    containers: List<Container>,
    selectedContainer: Container?,
    onContainerSelected: (Container) -> Unit,
    stuffingItems: List<StuffingItem>,
    viewModel: UHFViewModel,
    onProceedToSummary: () -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Filter stuffed and unstuffed items
    val stuffedItems = stuffingItems.filter { it.isStuffed }
    val unstuffedItems = stuffingItems.filter { !it.isStuffed }

    // Get unique list of scanned RFIDs
    val stuffedRfidList = stuffedItems.mapNotNull { it.rfidTag }.distinct()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TjiwiColors.Background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        HeaderSection(
            title = "Stuffing Process",
            subtitle = "Scan RFID untuk memproses stuffing barang ke container"
        )

        // Container Selection Card
        ContainerSelectionCard(
            containers = containers,
            selectedContainer = selectedContainer,
            onContainerSelected = onContainerSelected
        )

        // Status Summary Card
        StatusSummaryCard(items = stuffingItems)

        // Show scanned RFID list if there are any
        if (stuffedRfidList.isNotEmpty()) {
            StuffedRfidListCard(stuffedRfidList = stuffedRfidList)
        }

        // Scan Controls Section
        ScanControlsSection(
            viewModel = viewModel,
            onClearAll = onClearAll,
            isContainerSelected = selectedContainer != null
        )

        // Items List Section with Tabs
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Semua Item", "Belum Distuff", "Sudah Distuff")

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tabs Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = TjiwiColors.Surface,
                contentColor = TjiwiColors.Primary,
//                divider = {
//                    TabRowDefaults.Divider(color = TjiwiColors.Background)
//                },
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 3.dp,
                        color = TjiwiColors.Primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        },
                        selectedContentColor = TjiwiColors.Primary,
                        unselectedContentColor = TjiwiColors.Secondary
                    )
                }
            }

            // Items List based on selected tab
            when (selectedTabIndex) {
                0 -> ItemsList(items = stuffingItems, modifier = Modifier.weight(1f))
                1 -> ItemsList(items = unstuffedItems, modifier = Modifier.weight(1f))
                2 -> ItemsList(items = stuffedItems, modifier = Modifier.weight(1f))
            }
        }

        // Proceed Button (only visible if there are stuffed items)
        if (stuffedItems.isNotEmpty()) {
            Button(
                onClick = {
                    if (selectedContainer != null) {
                        onProceedToSummary()
                    } else {
                        Toast.makeText(
                            context,
                            "Pilih container terlebih dahulu!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TjiwiColors.Primary,
                    contentColor = TjiwiColors.OnPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedContainer != null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ListAlt,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "LANJUT KE SUMMARY (${stuffedItems.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}