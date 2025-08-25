package com.example.c5local.persentation.screen.scan_all_tag

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.c5local.persentation.shared.UHFViewModel
import kotlinx.coroutines.delay


data class RfidTag(
    val id: Long = System.currentTimeMillis(),
    val epc: String,
    var timestamp: String,
    var count: Int = 1
)

object TjiwiColors {
    val Primary = Color(0xFFDC2626)
    val PrimaryLight = Color(0xFFEF4444)
    val PrimaryDark = Color(0xFFB91C1C)
    val Background = Color(0xFFFEF2F2)
    val Surface = Color(0xFFFFFFFF)
    val OnPrimary = Color.White
    val OnSurface = Color(0xFF374151)
    val SurfaceVariant = Color(0xFFF9FAFB)
    val Success = Color(0xFF10B981)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RfidScannerScreen(viewModel: UHFViewModel) {
    val isScanning = viewModel.isScanning
    val scannedTags = viewModel.scannedRfidAll
    val scanCount = viewModel.scanCount

    // Dialog tidak lagi diperlukan untuk fitur ini, jadi kita bisa hapus
    // referensinya agar kode lebih bersih.

    // State untuk Bottom Sheet
    val sheetState = rememberModalBottomSheetState()
    val showWriteSheet = viewModel.showWriteSheet


    // Kontrol untuk menampilkan/menyembunyikan sheet
    if (showWriteSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onDismissWriteSheet() },
            sheetState = sheetState
        ) {
            // Konten form akan ada di sini
            WriteDataSheetContent(viewModel = viewModel)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initUHF()
        viewModel.stopScanning()
        viewModel.changeScanModeToRfid()
        viewModel.clearRfidAll()
        viewModel.changeIsSingleScanToFalse()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        TjiwiColors.Background,
                        Color.White
                    )
                )
            )
            .padding(16.dp)
    ) {
        HeaderCard(scanCount = scanCount)
        Spacer(modifier = Modifier.height(16.dp))
        scannedTags?.let {
            ControlPanel(
                isScanning = isScanning,
                onStartScanning = { viewModel.startRfidScanning() },
                onStopScanning = { viewModel.stopScanning() },
                onClearTags = {
                    viewModel.clearRfidAll()
                    viewModel.stopContinuousTrigger() // Hentikan trigger jika clear
                },
                hasData = it.isNotEmpty()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        scannedTags?.let {
            TagsList(
                tags = it,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun HeaderCard(scanCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(TjiwiColors.Primary, TjiwiColors.PrimaryDark)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "RFID Scanner",
                        tint = TjiwiColors.OnPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "RFID Scanner",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            letterSpacing = (-1).sp,
                            color = TjiwiColors.Primary,
                        )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Total Scans",
                    fontSize = 8.sp,
                    color = Color.Gray
                )
                Text(
                    text = scanCount.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        letterSpacing = (-1).sp,
                        color = TjiwiColors.Primary,
                    )
                )
            }
        }
    }
}

@Composable
fun ControlPanel(
    isScanning: Boolean,
    onStartScanning: () -> Unit,
    onStopScanning: () -> Unit,
    onClearTags: () -> Unit,
    hasData: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start/Stop Button
                Box(modifier = Modifier.weight(1.75f)) {
                    Button(
                        onClick = if (isScanning) onStopScanning else onStartScanning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isScanning) TjiwiColors.Primary else TjiwiColors.Success,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isScanning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isScanning) "Stop Scanning" else "Start Scanning",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                letterSpacing = (-1).sp,
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        )
                    }
                }

                // Clear Button
                Box(modifier = Modifier.weight(1.25f)) {
                    OutlinedButton(
                        onClick = onClearTags,
                        enabled = hasData && !isScanning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(listOf(Color.Gray, Color.Gray))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", style = MaterialTheme.typography.bodyMedium.copy(
                            letterSpacing = (-1).sp,
                            fontSize = 14.sp,
                        ))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScanningIndicator(isScanning = isScanning)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isScanning) "Scanning..." else "Ready",
                    fontSize = 14.sp,
                    color = if (isScanning) TjiwiColors.Success else Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ScanningIndicator(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .size(12.dp)
            .background(
                color = if (isScanning) TjiwiColors.Success.copy(alpha = alpha) else Color.Gray,
                shape = CircleShape
            )
    )
}

@Composable
fun TagsList(
    tags: List<RfidTag>,
    viewModel: UHFViewModel
) {
    val isScanning = viewModel.isScanning
    val isTriggerActive = viewModel.isContinuousTriggerActive
    val activeEpc = viewModel.continuousTriggerTargetEpc
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scanned RFID Tags",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        letterSpacing = (-1).sp,
                        color = TjiwiColors.OnSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                )
                Text(
                    text = "(${tags.size})",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        letterSpacing = (-1).sp,
                        color = TjiwiColors.Primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.Gray.copy(alpha = 0.2f)
            )

            if (tags.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(tags) { index, tag ->
                        TagItem(
                            tag = tag,
                            isCurrentlyTriggered = isTriggerActive && activeEpc == tag.epc,
                            onTriggerClick = { viewModel.toggleContinuousTrigger(tag.epc) },
                            onWriteClick = { viewModel.onWriteTagClicked(tag) },
                            isActionEnabled = !viewModel.isScanning && !viewModel.isContinuousTriggerActive,
                            isScanning = isScanning
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Memory,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Belum ada tag RFID yang dipindai",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Klik \"Start Scanning\" untuk memulai.\nTekan tombol LED pada item untuk menyalakan tag.",
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TagItem(
    tag: RfidTag,
    isCurrentlyTriggered: Boolean,
    onTriggerClick: () -> Unit,
    isScanning: Boolean,
    onWriteClick: () -> Unit, // [BARU]
    isActionEnabled: Boolean // [BARU]
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TjiwiColors.SurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kolom untuk Info EPC, Count, dan Timestamp
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = tag.epc,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TjiwiColors.OnSurface,
                        lineHeight = 18.sp
                    ),
                )
                Row {
                    Text(
                        text = "Count: ${tag.count}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TjiwiColors.Primary, fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Last seen: ${tag.timestamp}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

//            // Tombol trigger LED yang baru
//            LedTriggerButton(
//                isTriggered = isCurrentlyTriggered,
//                onClick = onTriggerClick,
//                isEnabled = !isScanning
//            )
            // [DIUBAH] Gabungkan tombol aksi dalam satu kolom
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LedTriggerButton(
                    isTriggered = isCurrentlyTriggered,
                    onClick = onTriggerClick,
                    isEnabled = isActionEnabled
                )
                Spacer(modifier = Modifier.height(8.dp))
                // [BARU] Tombol untuk membuka form tulis data
                IconButton(
                    onClick = onWriteClick,
                    enabled = isActionEnabled,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Write Data",
                        tint = if (isActionEnabled) TjiwiColors.OnSurface else Color.LightGray
                    )
                }
            }
        }
    }
}

/**
 * [BARU] Composable khusus untuk tombol trigger LED yang lebih menarik.
 */
@Composable
fun LedTriggerButton(
    isTriggered: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    // Animasi untuk perubahan warna yang mulus
    val containerColor by animateColorAsState(
        targetValue = if (isTriggered) TjiwiColors.Primary else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "containerColorAnimation"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isTriggered) Color.White else Color.Gray,
        animationSpec = tween(durationMillis = 300),
        label = "contentColorAnimation"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Label untuk menjelaskan fungsi tombol
        Text(
            text = "LED",
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isEnabled) Color.Gray else Color.LightGray
            )
        )
        Spacer(Modifier.height(4.dp))

        OutlinedButton(
            onClick = onClick,
            enabled = isEnabled,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (isEnabled) Color.LightGray else Color.Gray.copy(alpha = 0.2f)
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    // Gunakan ikon yang berbeda untuk status ON dan OFF
                    imageVector = if (isTriggered) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                    contentDescription = "LED Trigger",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isTriggered) "ON" else "OFF",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}


/**
 * [BARU] Composable untuk konten form di dalam ModalBottomSheet.
 */
@Composable
fun WriteDataSheetContent(viewModel: UHFViewModel) {
    val tag = viewModel.tagToWrite ?: return // Jangan tampilkan apa-apa jika tag null

    // State lokal untuk field input
    var newEpc by remember { mutableStateOf(tag.epc) }
    var userDataId by remember { mutableStateOf("ID-123") } // Contoh data awal
    var userDataStatus by remember { mutableStateOf("OK") } // Contoh data awal

    val writeStatus = viewModel.writeStatus
    val isWriting = viewModel.isWriting

    // Hapus status pesan setelah beberapa detik
    LaunchedEffect(writeStatus) {
        if (writeStatus != null) {
            delay(4000)
            viewModel.clearWriteStatus()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Tulis Data ke Tag", style = MaterialTheme.typography.titleLarge)
        Text("Target EPC: ${tag.epc}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Divider()

        // Form untuk menulis EPC
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("1. Tulis Ulang EPC", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = newEpc,
                onValueChange = { if (it.length <= 24) newEpc = it.uppercase() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("EPC Baru (24 Karakter Hex)") },
                singleLine = true,
                supportingText = { Text("${newEpc.length} / 24") }
            )
            Button(
                onClick = { viewModel.writeNewEpc(tag.epc, newEpc) },
                enabled = !isWriting && newEpc.length == 24,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Simpan EPC")
            }
        }

        Divider()

        // Form untuk menulis User Data
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("2. Tulis Data User (JSON)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = userDataId,
                onValueChange = { userDataId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("ID") },
                singleLine = true
            )
            OutlinedTextField(
                value = userDataStatus,
                onValueChange = { userDataStatus = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Status") },
                singleLine = true
            )
            Button(
                onClick = { viewModel.writeUserData(tag.epc, userDataId, userDataStatus) },
                enabled = !isWriting,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Simpan User Data")
            }
        }

        // Tampilkan status proses penulisan
        if (writeStatus != null) {
            val statusColor = when {
                writeStatus!!.startsWith("SUKSES") -> TjiwiColors.Success
                else -> MaterialTheme.colorScheme.error
            }
            Text(writeStatus!!, color = statusColor, fontWeight = FontWeight.Bold)
        }

        if (isWriting) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}