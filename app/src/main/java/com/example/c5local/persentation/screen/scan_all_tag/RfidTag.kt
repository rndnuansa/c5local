package com.example.c5local.persentation.screen.scan_all_tag

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.c5local.persentation.shared.UHFViewModel
import com.example.c5local.persentation.theme.DMSans
import java.text.SimpleDateFormat
import java.util.*


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

@Composable
fun RfidScannerScreen(viewModel: UHFViewModel) {
    val isScanning = viewModel.isScanning
    val scannedTags = viewModel.scannedRfidAll
    val scanCount = viewModel.scanCount

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
        // Header Card
        HeaderCard(scanCount = scanCount)

        Spacer(modifier = Modifier.height(16.dp))

        // Control Panel
        scannedTags?.let {
            ControlPanel(
                isScanning = isScanning,
                onStartScanning = { viewModel.startRfidScanning() },
                onStopScanning = { viewModel.stopScanning() },
                onClearTags = {
                    viewModel.clearRfidAll()
                },
                hasData = it.isNotEmpty()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        WriteTagSection(viewModel = viewModel)


        Spacer(modifier = Modifier.height(16.dp))

        // Tags List
        scannedTags?.let {
            TagsList(
                tags = it,
                isScanning = isScanning
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
                            color = com.example.c5local.persentation.screen.alloctobin.TjiwiColors.Primary,)
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
                        color = com.example.c5local.persentation.screen.alloctobin.TjiwiColors.Primary,
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
                        enabled = hasData,
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
fun WriteTagSection(
    viewModel: UHFViewModel
) {
    var inputText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Write to EPC",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TjiwiColors.OnSurface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Text to write (e.g. ALD1283)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TjiwiColors.Primary,
                    cursorColor = TjiwiColors.Primary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val result = viewModel.writeTagToEpc(inputText)
                    statusMessage = if (result) {
                        "✅ Success: $inputText written to tag"
                    } else {
                        "❌ Failed to write tag"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TjiwiColors.Primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Write EPC")
            }

            statusMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = if (it.startsWith("✅")) TjiwiColors.Success else TjiwiColors.Primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun TagsList(tags: List<RfidTag>, isScanning: Boolean) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Header
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
            
            // List Content
            if (tags.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(tags) { index, tag ->
                        TagItem(tag = tag, index = index + 1)
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
            imageVector = Icons.Default.Wifi,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No RFID tags scanned yet",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Click \"Start Scanning\" to begin",
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TagItem(tag: RfidTag, index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TjiwiColors.SurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tag.epc,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TjiwiColors.OnSurface,
                        ),
                    )
                    Text(
                        text = "Last scanned: ${tag.timestamp}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 10.sp,
                            color = Color.Gray,
                            ),
                        )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Count",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = Color.Gray,
                    ),
                )
                Text(
                    text = tag.count.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TjiwiColors.Primary,
                    ),
                )
            }
        }
    }
}