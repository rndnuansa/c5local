package com.example.c5local

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.c5local.domain.usecase.SeederUseCase
import com.example.c5local.persentation.navigation.AppNavHost
import com.example.c5local.persentation.shared.ItemViewModel
import com.example.c5local.persentation.shared.UHFViewModel
import com.example.c5local.persentation.shared.ScanMode
import com.example.c5local.persentation.theme.C5LOCALTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var seederUseCase: SeederUseCase

    private val itemViewModel: ItemViewModel by viewModels()
    private val uhfViewModel: UHFViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Jalankan seeder saat aplikasi pertama kali dibuka
        lifecycleScope.launch {
            seederUseCase.runSeeder()
        }

        enableEdgeToEdge()
        setContent {
            C5LOCALTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    topBar = {
                        // Top bar content if needed
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                                    .background(Color.White),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = stringResource(id = R.string.logo),
                                    modifier = Modifier.size(126.dp)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_rfid),
                                        contentDescription = stringResource(id = R.string.profile),
                                        modifier = Modifier
                                            .width(32.dp)
                                            .clickable {
                                                uhfViewModel.onPowerRfidClicked()
                                            }
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.1f), // start of shadow
                                                Color.Transparent // fade out
                                            )
                                        )
                                    )
                            )
                        }

                        AppNavHost(uhfViewModel = uhfViewModel)

                        // Power RFID Modal Dialog
                        if (uhfViewModel.showModal) {
                            AlertDialog(
                                onDismissRequest = {
                                    uhfViewModel.onPowerRfidDismiss()
                                },
                                title = {
                                    Text("Choose Power RFID 1 - 30 dbm", fontSize = 14.sp)
                                },
                                text = {
                                    Column {
                                        Text("Nilai: ${uhfViewModel.sliderValue}")
                                        Slider(
                                            value = uhfViewModel.sliderValue.toFloat(),
                                            onValueChange = { uhfViewModel.onSliderValueChanged(it.toInt()) },
                                            valueRange = 1f..30f,
                                            steps = 28
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            uhfViewModel.onConfirmPowerSelection()
                                        }
                                    ) {
                                        Text("OK")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        // Key codes untuk trigger button (biasanya 139, 280, 293 untuk handheld scanner)
//        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
//            if (event?.repeatCount == 0) {
//                println("HEHEI MASUK SINI")
//                println(keyCode)
//                handleScannerTrigger()
//                return true
//            }
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    private fun handleScannerTrigger() {
//        println(uhfViewModel.isScanning)
//        if (!uhfViewModel.isScanning) {
//            // Start scanning berdasarkan mode yang dipilih
//            if (uhfViewModel.currentScanMode == ScanMode.BARCODE) {
//                // Mode barcode
////                uhfViewModel.startBarcodeScan()
//            } else {
//                // Mode RFID
//                if (uhfViewModel.isSingleScan) {
//                    uhfViewModel.startRfidScanningOnce()
//                } else {
//                    println("HEHEI MASUK START RFID SCANNING FULL")
//                    uhfViewModel.startRfidScanning()
//                }
//            }
//        } else {
//            // Stop scanning
//            uhfViewModel.sztopScanning()
//        }
//    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Key codes untuk trigger button (biasanya 139, 280, 293 untuk handheld scanner)
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            if (event?.repeatCount == 0) {
                Log.d("Scanner", "Hardware trigger pressed, keyCode: $keyCode")
                handleScannerTrigger()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun handleScannerTrigger() {
        try {
            Log.d("Scanner", "Current scanning state: ${uhfViewModel.isScanning}")
            Log.d("Scanner", "Current mode: ${uhfViewModel.currentScanMode}")
            Log.d("Scanner", "Barcode scanned: ${uhfViewModel.isBarcodeScanned}")
            Log.d("Scanner", "Is Scanning Single: ${uhfViewModel.isSingleScan}")

            // Gunakan fungsi baru dari ViewModel
            uhfViewModel.handleHardwareTrigger()

        } catch (e: Exception) {
            Log.e("Scanner", "Error handling scanner trigger: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Pastikan semua scanning dihentikan saat activity destroyed
        uhfViewModel.stopScanning()
    }

    override fun onPause() {
        super.onPause()
        // Pause scanning saat activity tidak aktif untuk menghemat battery
        if (uhfViewModel.isScanning) {
            uhfViewModel.stopScanning()
        }
    }
}