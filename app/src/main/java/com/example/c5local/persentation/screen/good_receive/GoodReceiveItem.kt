package com.example.c5local.persentation.screen.goodreceive

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.c5local.persentation.shared.ScanMode
import com.example.c5local.persentation.shared.UHFViewModel

// Data class untuk item Good Receive
data class GoodReceiveItem(
    val id: String,
    val name: String,
    val category: String,
    val expectedQty: Int,
    val receivedQty: Int = 0,
    val rfidTag: String? = null,
    val isScanned: Boolean = false
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
private fun ScannedRfidListCard(scannedRfidList: List<String>) {
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
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TjiwiColors.Success,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "RFID Terscan (${scannedRfidList.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Success
                    )
                )
            }

            // Display scanned RFIDs in a compact format
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scannedRfidList) { rfid ->
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
fun GoodReceiveScreen(
    navController: NavController,
    viewModel: UHFViewModel
) {
    val context = LocalContext.current

    // Dummy data untuk Good Receive items
    var goodReceiveItems by remember {
        mutableStateOf(
            listOf(
                GoodReceiveItem("001", "Laptop Dell", "Electronics", 5),
                GoodReceiveItem("002", "Mouse Wireless", "Electronics", 10),
                GoodReceiveItem("003", "Keyboard Mechanical", "Electronics", 8),
                GoodReceiveItem("004", "Monitor 24 inch", "Electronics", 3),
                GoodReceiveItem("005", "Printer HP", "Electronics", 2),
                GoodReceiveItem("006", "Cable HDMI", "Accessories", 15),
                GoodReceiveItem("007", "Power Bank", "Electronics", 12),
                GoodReceiveItem("008", "Headphone", "Electronics", 6)
            )
        )
    }

    // Observe scanned RFID
    val scannedRfid = viewModel.scannedRfid

    // State untuk menampilkan form Good Receive
    var showGoodReceiveForm by remember { mutableStateOf(false) }

    // Initialize scanning mode untuk RFID
    LaunchedEffect(Unit) {
        viewModel.initUHF()
//        viewModel.changeIsSingleScan()
        viewModel.stopScanning()
        viewModel.changeScanModeToRfid()
    }

    // Handle RFID scan result
    LaunchedEffect(scannedRfid) {
        if (scannedRfid.isNotEmpty()) {
            // Cek apakah RFID sudah pernah di-scan sebelumnya
            val isRfidAlreadyScanned = goodReceiveItems.any { it.rfidTag == scannedRfid }

            if (isRfidAlreadyScanned) {
                // RFID sudah pernah di-scan, tampilkan pesan error
                Toast.makeText(
                    context,
                    "RFID $scannedRfid sudah pernah di-scan!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearRfidResult()
                return@LaunchedEffect
            }

            // Cari item pertama yang belum di-scan untuk di-assign RFID ini
            val firstUnscannedItemIndex = goodReceiveItems.indexOfFirst { !it.isScanned && it.rfidTag == null }

            if (firstUnscannedItemIndex != -1) {
                // Update item pertama yang belum di-scan
                val updatedItems = goodReceiveItems.toMutableList()
                updatedItems[firstUnscannedItemIndex] = updatedItems[firstUnscannedItemIndex].copy(
                    rfidTag = scannedRfid,
                    isScanned = true,
                    receivedQty = updatedItems[firstUnscannedItemIndex].receivedQty + 1
                )

                goodReceiveItems = updatedItems
                Toast.makeText(
                    context,
                    "RFID $scannedRfid berhasil ditambahkan ke ${updatedItems[firstUnscannedItemIndex].name}!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearRfidResult()
            } else {
                // Tidak ada item yang bisa di-assign
                Toast.makeText(
                    context,
                    "Semua item sudah di-scan atau tidak ada item tersedia",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearRfidResult()
            }
        }
    }

    if (!showGoodReceiveForm) {
        // Tampilan scanning RFID
        GoodReceiveScanScreen(
            goodReceiveItems = goodReceiveItems,
            viewModel = viewModel,
            onProceedToForm = { showGoodReceiveForm = true },
            onClearAll = {
                val scannedCount = goodReceiveItems.count { it.isScanned }
                goodReceiveItems = goodReceiveItems.map {
                    it.copy(rfidTag = null, isScanned = false, receivedQty = 0)
                }
                viewModel.clearAllResults()

                if (scannedCount > 0) {
                    Toast.makeText(
                        context,
                        "$scannedCount RFID yang terscan telah dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    } else {
        // Tampilan form Good Receive
        GoodReceiveFormScreen(
            goodReceiveItems = goodReceiveItems,
            onBack = { showGoodReceiveForm = false },
            onSubmit = { items ->
                // Handle submit logic
                Toast.makeText(
                    context,
                    "Good Receive berhasil disubmit untuk ${items.size} item!",
                    Toast.LENGTH_LONG
                ).show()
                // Reset data
                goodReceiveItems = goodReceiveItems.map {
                    it.copy(rfidTag = null, isScanned = false, receivedQty = 0)
                }
                showGoodReceiveForm = false
            }
        )
    }
}

@Composable
private fun GoodReceiveScanScreen(
    goodReceiveItems: List<GoodReceiveItem>,
    viewModel: UHFViewModel,
    onProceedToForm: () -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TjiwiColors.Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        HeaderSection(
            title = "Good Receive - Scan Items",
            subtitle = "Scan RFID untuk menandai item yang diterima"
        )

        // Status Summary
        StatusSummaryCard(goodReceiveItems)

        // Scanned RFID List (jika ada)
        val scannedRfidList = goodReceiveItems.filter { it.isScanned }.map { it.rfidTag!! }
        if (scannedRfidList.isNotEmpty()) {
            ScannedRfidListCard(scannedRfidList)
        }

        // Scan Controls
        ScanControlsSection(
            viewModel = viewModel,
            onClearAll = onClearAll
        )

        // Items List
        ItemsList(
            items = goodReceiveItems,
            modifier = Modifier.weight(1f)
        )

        // Navigation Button
        Button(
            onClick = onProceedToForm,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = goodReceiveItems.any { it.isScanned },
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
                text = "LANJUT KE FORM GOOD RECEIVE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun GoodReceiveFormScreen(
    goodReceiveItems: List<GoodReceiveItem>,
    onBack: () -> Unit,
    onSubmit: (List<GoodReceiveItem>) -> Unit
) {
    val scannedItems = goodReceiveItems.filter { it.isScanned }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TjiwiColors.Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
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
                text = "Form Good Receive",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TjiwiColors.Primary
                )
            )

            Spacer(modifier = Modifier.width(48.dp)) // Balance the back button
        }

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
                    text = "Ringkasan Penerimaan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Primary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Item Diterima:", color = TjiwiColors.Secondary)
                    Text(
                        text = scannedItems.size.toString(),
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Success
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Quantity:", color = TjiwiColors.Secondary)
                    Text(
                        text = scannedItems.sumOf { it.receivedQty }.toString(),
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.Success
                    )
                }
            }
        }

        // Scanned Items List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(scannedItems) { item ->
                ScannedItemCard(item)
            }
        }

        // Submit Button
        Button(
            onClick = { onSubmit(scannedItems) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
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
                text = "SUBMIT GOOD RECEIVE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
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
private fun StatusSummaryCard(items: List<GoodReceiveItem>) {
    val totalItems = items.size
    val scannedItems = items.count { it.isScanned }
    val totalExpected = items.sumOf { it.expectedQty }
    val totalReceived = items.sumOf { it.receivedQty }

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
            StatusItem("Items", "$scannedItems/$totalItems", TjiwiColors.Primary)
            StatusItem("Quantity", "$totalReceived/$totalExpected", TjiwiColors.Success)
            StatusItem("Progress", "${(scannedItems * 100 / totalItems)}%", TjiwiColors.Warning)
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
    onClearAll: () -> Unit
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
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan RFID")
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
    items: List<GoodReceiveItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            ItemCard(item)
        }
    }
}

@Composable
private fun ItemCard(item: GoodReceiveItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isScanned) TjiwiColors.Success.copy(alpha = 0.1f) else TjiwiColors.Surface
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
                modifier = Modifier.weight(1f)
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
                    text = "Expected: ${item.expectedQty} | Received: ${item.receivedQty}",
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

            Icon(
                imageVector = if (item.isScanned) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = if (item.isScanned) TjiwiColors.Success else TjiwiColors.Secondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ScannedItemCard(item: GoodReceiveItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TjiwiColors.Success.copy(alpha = 0.1f)
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
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
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
                    text = "Qty Received: ${item.receivedQty}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TjiwiColors.Success,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TjiwiColors.Success,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}