package com.example.c5local.persentation.screen.registration

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.c5local.persentation.screen.alloctobin.HeaderSection
import com.example.c5local.persentation.shared.ScanMode
import com.example.c5local.persentation.shared.UHFViewModel

// Tjiwi Kimia Color Palette
object TjiwiColors {
    val Primary = Color(0xFFE53E3E) // Merah Tjiwi
    val PrimaryDark = Color(0xFFB91C1C)
    val Secondary = Color(0xFF4A5568) // Abu-abu gelap
    val Background = Color(0xFFF7FAFC) // Abu-abu terang
    val Surface = Color(0xFFFFFFFF)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF2D3748)
    val Success = Color(0xFF38A169)
    val Warning = Color(0xFFD69E2E)
    val Error = Color(0xFFE53E3E)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registration(navController: NavController, viewModel: UHFViewModel) {
    val context = LocalContext.current
    val scannedBarcode = viewModel.scannedBarcode

    Log.d("Composable", "Barcode in UI: ${scannedBarcode}")

    LaunchedEffect(viewModel.scannedBarcode.value) {
        if (viewModel.scannedBarcode.value.isNotEmpty()) {
            Toast.makeText(context, "Scanned: $scannedBarcode", Toast.LENGTH_SHORT).show()
            viewModel.initUHF()
            viewModel.stopScanning()
            viewModel.changeScanModeToRfid()
            viewModel.clearRfidAll()
            viewModel.releaseBarcode()
            viewModel.changeIsSingleScan()
        }else{
            viewModel.initBarcode()
            viewModel.changeToBarcode()
            viewModel.changeIsSingleScan()
            viewModel.stopScanning()
            viewModel.changeScanModeToBarcode()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.initBarcode()
        viewModel.changeToBarcode()
        viewModel.changeIsSingleScan()
        viewModel.stopScanning()
        viewModel.changeScanModeToBarcode()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TjiwiColors.Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header Section
//        HeaderSection()
        HeaderSection(
            title = "RFID Registration",
            subtitle = "Scan Barcode & RFID to register the items",
        )

        // Status Indicator
        StatusIndicator(
            currentMode = viewModel.currentScanMode,
            isScanning = viewModel.isScanning,
            isBarcodeScanned = viewModel.isBarcodeScanned
        )
        // Input Fields
        InputSection(
            viewModel = viewModel,
            rfidText = viewModel.scannedRfid,
            isBarcodeScanned = viewModel.isBarcodeScanned,
            onClearBarcode = { viewModel.clearBarcodeResult() },
            onClearRfid = { viewModel.clearRfidResult() },
            onBarcodeClick = {
                if (!viewModel.isBarcodeScanned) {
                    viewModel.changeToBarcode(true)
                }
            }
        )

        // Action Buttons
        ActionButtonsSection(
            isScanning = viewModel.isScanning,
            currentScanMode = viewModel.currentScanMode,
            isBarcodeScanned = viewModel.isBarcodeScanned,
            onScanBarcode = { viewModel.startBarcodeScan() },
            onScanRfid = {
                if (viewModel.isSingleScan) {
                    viewModel.startRfidScanningOnce()
                } else {
                    viewModel.startRfidScanning()
                }
            },
            onStopScan = { viewModel.stopScanning() },
            onClearAll = { viewModel.clearAllResults() },
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.weight(1f))

        // Submit Button
        SubmitButton(
            barcodeText = scannedBarcode.value,
            rfidText = viewModel.scannedRfid,
            onSubmit = {
                if (scannedBarcode.value.isNotEmpty() && viewModel.scannedRfid.isNotEmpty()) {
                    Toast.makeText(
                        context,
                        "Berhasil Registrasi!\nBarcode: ${viewModel.scannedBarcode}\nRFID: ${viewModel.scannedRfid}",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.clearAllResults()
                } else {
                    Toast.makeText(
                        context,
                        "Silakan scan barcode dan RFID terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        // Debug Section (Optional - untuk development)
        if (Log.isLoggable("Debug", Log.DEBUG)) {
            DebugSection(viewModel = viewModel)
        }
    }
}
@Composable
private fun StatusIndicator(
    currentMode: ScanMode,
    isScanning: Boolean,
    isBarcodeScanned: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TjiwiColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Step Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicator(
                    icon = Icons.Default.Home,
                    label = "Barcode",
                    isCompleted = isBarcodeScanned,
                    isActive = currentMode == ScanMode.BARCODE,
                    isScanning = isScanning && currentMode == ScanMode.BARCODE
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = if (isBarcodeScanned) TjiwiColors.Success else TjiwiColors.Secondary,
                    modifier = Modifier.size(16.dp)
                )

                StepIndicator(
                    icon = Icons.Default.Home,
                    label = "RFID",
                    isCompleted = false,
                    isActive = currentMode == ScanMode.RFID,
                    isScanning = isScanning && currentMode == ScanMode.RFID
                )
            }

            // Status Text
            Text(
                text = when {
                    isScanning -> "Sedang Scan..."
                    isBarcodeScanned -> "Siap Scan"
                    else -> "Siap Scan"
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isScanning) TjiwiColors.Warning else TjiwiColors.Secondary
                )
            )
        }
    }
}

@Composable
private fun StepIndicator(
    icon: ImageVector,
    label: String,
    isCompleted: Boolean,
    isActive: Boolean,
    isScanning: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when {
                        isCompleted -> TjiwiColors.Success
                        isActive -> TjiwiColors.Primary
                        else -> TjiwiColors.Secondary.copy(alpha = 0.3f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isScanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = TjiwiColors.OnPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.Check else icon,
                    contentDescription = label,
                    tint = TjiwiColors.OnPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = when {
                    isCompleted -> TjiwiColors.Success
                    isActive -> TjiwiColors.Primary
                    else -> TjiwiColors.Secondary
                }
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputSection(
    viewModel: UHFViewModel,
    rfidText: String,
    isBarcodeScanned: Boolean,
    onClearBarcode: () -> Unit,
    onClearRfid: () -> Unit,
    onBarcodeClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Barcode Input
        CustomTextField(
            viewModel = viewModel,
            value = viewModel.scannedBarcode.value,
            label = "Hasil Barcode",
            placeholder = "Tap untuk scan barcode",
            leadingIcon = Icons.Default.Home,
            isReadOnly = false,
            isCompleted = viewModel.isBarcodeScanned,
            onClear = if (rfidText.isNotEmpty()) onClearBarcode else null,
            onClick = onBarcodeClick
        )

        // RFID Input
        CustomTextField(
            viewModel = viewModel,
            value = viewModel.scannedRfid,
            label = "Hasil RFID",
            placeholder = "Otomatis terisi setelah scan RFID",
            leadingIcon = Icons.Default.Home,
            isReadOnly = true,
            isCompleted = rfidText.isNotEmpty(),
            onClear = if (rfidText.isNotEmpty()) onClearRfid else null,
            onClick = null
        )
    }
}
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTextField(
    viewModel: UHFViewModel,
    value: String,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    isReadOnly: Boolean,
    isCompleted: Boolean,
    onClear: (() -> Unit)?,
    onClick: (() -> Unit)?
) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (isCompleted) TjiwiColors.Success else TjiwiColors.Secondary
            )
        },
        trailingIcon = {
            if (onClear != null) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = TjiwiColors.Secondary
                    )
                }
            }
        },
        readOnly = isReadOnly,
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable { onClick() } else it },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isCompleted) TjiwiColors.Success else TjiwiColors.Primary,
            unfocusedBorderColor = if (isCompleted) TjiwiColors.Success else TjiwiColors.Secondary.copy(alpha = 0.5f),
            focusedLabelColor = if (isCompleted) TjiwiColors.Success else TjiwiColors.Primary,
            unfocusedLabelColor = TjiwiColors.Secondary
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ActionButtonsSection(
    viewModel: UHFViewModel,
    isScanning: Boolean,
    currentScanMode: ScanMode,
    isBarcodeScanned: Boolean,
    onScanBarcode: () -> Unit,
    onScanRfid: () -> Unit,
    onStopScan: () -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Action Buttons
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            // Barcode Button
//            ActionButton(
//                text = if (viewModel.isScanning && viewModel.currentScanMode == ScanMode.BARCODE) "Stop Barcode" else "Scan Barcode",
//                icon = Icons.Default.Home,
//                onClick = {
//                    if (viewModel.isScanning && viewModel.currentScanMode == ScanMode.BARCODE) {
//                        onStopScan()
//                    } else {
//                        onScanBarcode()
//                    }
//                },
//                enabled = !viewModel.isBarcodeScanned,
//                isLoading = viewModel.isScanning && viewModel.currentScanMode == ScanMode.BARCODE,
//                color = if (viewModel.isBarcodeScanned) TjiwiColors.Success else TjiwiColors.Primary,
//                modifier = Modifier.weight(1f)
//            )
//
//            // RFID Button
//            ActionButton(
//                text = if (viewModel.isScanning && viewModel.currentScanMode == ScanMode.RFID) "Stop RFID" else "Scan RFID",
//                icon = Icons.Default.Home,
//                onClick = {
//                    if (viewModel.isScanning && viewModel.currentScanMode == ScanMode.RFID) {
//                        onStopScan()
//                    } else {
//                        onScanRfid()
//                    }
//                },
//                enabled = viewModel.isBarcodeScanned,
//                isLoading = viewModel.isScanning && viewModel.currentScanMode == ScanMode.RFID,
//                color = TjiwiColors.Primary,
//                modifier = Modifier.weight(1f)
//            )
//        }

    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    color: Color = TjiwiColors.Primary,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = TjiwiColors.OnPrimary,
            disabledContainerColor = TjiwiColors.Secondary.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = TjiwiColors.OnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }

        if (!isLoading) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun SubmitButton(
    barcodeText: String,
    rfidText: String,
    onSubmit: () -> Unit
) {
    val isComplete = barcodeText.isNotEmpty() && rfidText.isNotEmpty()

    Button(
        onClick = onSubmit,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isComplete) TjiwiColors.Primary else TjiwiColors.Secondary.copy(alpha = 0.3f),
            contentColor = TjiwiColors.OnPrimary
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isComplete) 8.dp else 0.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isComplete) "SUBMIT REGISTRASI" else "LENGKAPI DATA",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
private fun DebugSection(viewModel: UHFViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TjiwiColors.Secondary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Debug Information",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TjiwiColors.Secondary
                )
            )

            Text(
                text = "Mode: ${viewModel.currentScanMode}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TjiwiColors.Secondary
                )
            )

            Button(
                onClick = {
                    Log.d("Debug", viewModel.debugBarcodeStatus())
                    viewModel.testBarcodeInit()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TjiwiColors.Secondary
                )
            ) {
                Text("Debug Barcode")
            }
        }
    }
}