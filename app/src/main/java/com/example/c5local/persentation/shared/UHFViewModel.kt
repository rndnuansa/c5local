package com.example.c5local.persentation.shared

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c5local.domain.usecase.RfidUseCases
import com.example.c5local.persentation.screen.scan_all_tag.RfidTag
import com.rscja.barcode.BarcodeDecoder
import com.rscja.barcode.BarcodeFactory
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.entity.BarcodeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class ScanMode {
    RFID,
    BARCODE
}

@HiltViewModel
class UHFViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val rfidUseCases: RfidUseCases
) : ViewModel() {

    private var mReader: RFIDWithUHFUART? = null
    private var barcodeDecoder: BarcodeDecoder? = null

    var isInitializing by mutableStateOf(false)
        private set

    var isInitializingBarcode by mutableStateOf(false)
        private set

    var initMessage by mutableStateOf<String?>(null)
        private set

    var isScanning by mutableStateOf<Boolean>(false)
        private set

    var currentScanMode by mutableStateOf(ScanMode.BARCODE)
        private set

    val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    var showModal by mutableStateOf(false)
        private set

    var sliderValue by mutableStateOf<Int>(30)
        private set

//    var scannedBarcode by mutableStateOf("")
//        private set
    private val _scannedBarcode = MutableStateFlow("")
    val scannedBarcode: StateFlow<String> = _scannedBarcode

    var scannedRfid by mutableStateOf("")
        private set

    var scannedRfidAll by mutableStateOf<List<RfidTag>?>(emptyList())
        private set

    var scanCount by  mutableStateOf(0)
        private set

    var rfidResult by mutableStateOf("")
        private set

    var isBarcode by mutableStateOf(false)
        private set

    var isSingleScan by mutableStateOf(false)
        private set

    // State untuk mengelola flow scanning
    var isBarcodeScanned by mutableStateOf(false)
        private set

    init{
        sliderValue = rfidUseCases.getRfidPowerUseCase()
//        initUHF()
//        initBarcode()
    }


    fun setRfid(value: String){
        rfidResult = value
        scannedRfid = value
    }

    fun initUHF() {
        if(mReader == null){
            try {
                mReader = RFIDWithUHFUART.getInstance()
            } catch (e: Exception) {
                initMessage = e.message
                return
            }

            if (mReader != null) {
                isInitializing = true
                viewModelScope.launch {
                    val result = withContext(Dispatchers.IO) {
                        mReader?.init(context) == true
                    }
                    isInitializing = false
                    initMessage = if (result) "Init berhasil" else "Init gagal"
                }
            }
        }
    }

    // Fungsi untuk memulai scan RFID
    fun startRfidScanning() {
        if (isScanning) return


        stopBarcodeScan()

        currentScanMode = ScanMode.RFID
        isScanning = true

        viewModelScope.launch(Dispatchers.IO) {
            while (isScanning && currentScanMode == ScanMode.RFID) {
                println("HEHE")
                try {
                    val tag = mReader?.inventorySingleTag()
                    tag?.let {
                        withContext(Dispatchers.Main) {
                            setRfid(it.epc)
                            val existing = scannedRfidAll?.find { rfid -> rfid.epc == it.epc }

                            scannedRfidAll = if (existing != null) {
                                scannedRfidAll?.map { rfid ->
                                    if (rfid.epc == it.epc) {
                                        rfid.copy(
                                            count = rfid.count + 1,
                                            timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                        )
                                    } else rfid
                                }
                            } else {
                                scannedRfidAll?.plus(
                                    RfidTag(
                                        epc = it.epc,
                                        timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
                                        count = 1
                                    )
                                )
                            }
                            println("SCANNED DITAMBAHKAN : " + (scannedRfidAll?.size ?: 0))

                            scanCount++
                            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("RFID", "Error during RFID scan: ${e.message}")
                    withContext(Dispatchers.Main) {
                        isScanning = false
                    }
                    return@launch
                }
                delay(100)
            }
        }
    }
    fun clearRfidAll(){
        scannedRfidAll = emptyList()
        scanCount = 0
    }

    // Fungsi untuk memulai scan RFID sekali
    fun startRfidScanningOnce() {
        if (isScanning) return

        stopBarcodeScan()

        currentScanMode = ScanMode.RFID
        isScanning = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tag = mReader?.inventorySingleTag()
                tag?.let {
                    withContext(Dispatchers.Main) {
                        setRfid(it.epc)
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                    }
                    Log.d("RFID", "RFID scanned once: ${it.epc}")
                }
            } catch (e: Exception) {
                Log.e("RFID", "Error during single RFID scan: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) {
                    isScanning = false
                }
            }
        }
    }

    // Fungsi untuk memulai scan barcode - DIPERBAIKI
    fun startBarcodeScan() {
        println("MASUK SANA JUGA")
        if (isScanning) return

        stopRfidScanning()

        currentScanMode = ScanMode.BARCODE
        isScanning = true

        try {
            // Pastikan decoder sudah diinisialisasi dan callback sudah di-set
            if (barcodeDecoder == null) {
                initBarcode()
            }

            // Start scan barcode
            barcodeDecoder?.startScan()
            Log.d("Barcode", "Barcode scan started")

        } catch (e: Exception) {
            Log.e("Barcode", "Error starting barcode scan: ${e.message}")
            isScanning = false
        }
    }

    // Stop semua scanning
    fun stopScanning() {
        isScanning = false
        stopRfidScanning()
        stopBarcodeScan()
    }

    // Stop RFID scanning saja
    private fun stopRfidScanning() {
        if (currentScanMode == ScanMode.RFID) {
            isScanning = false
        }
    }

    // Stop barcode scanning saja
    fun stopBarcodeScan() {
        try {
            if (currentScanMode == ScanMode.BARCODE) {
                isScanning = false
                barcodeDecoder?.stopScan()
                Log.d("Barcode", "Barcode scanning stopped")
            }
        } catch (e: Exception) {
            Log.e("Barcode", "Error stopping barcode scan: ${e.message}")
        }
    }

    fun onPowerRfidClicked() {
        showModal = true
    }

    fun changeScanModeToBarcode(){
        currentScanMode = ScanMode.BARCODE
    }

    fun changeScanModeToRfid(){
        currentScanMode = ScanMode.RFID
        isBarcodeScanned = false
    }

    fun onPowerRfidDismiss() {
        showModal = false
    }

    fun onSliderValueChanged(value: Int) {
        sliderValue = value
    }

    fun changeToBarcode(changeToTrue: Boolean = false) {
        isBarcode = if(changeToTrue) {
            true
        } else {
            !isBarcode
        }
    }

    fun changeIsSingleScan() {
        isSingleScan = true
    }

    fun changeIsSingleScanToFalse(){
        isSingleScan = false

    }

    fun onConfirmPowerSelection() {
        rfidUseCases.saveRfidPowerUseCase(sliderValue)
        mReader?.setPower(sliderValue)
        showModal = false
    }

    // Inisialisasi barcode - DIPERBAIKI
    fun initBarcode() {
        try {
            isInitializingBarcode = true

            // Release decoder lama jika ada
//            barcodeDecoder?.close()

            // Buat decoder baru
            barcodeDecoder = BarcodeFactory.getInstance().barcodeDecoder
            val openResult = barcodeDecoder?.open(context)

            Log.d("Barcode", "Barcode init - Open result: $openResult")

            // Set callback untuk menangani hasil scan
            barcodeDecoder?.setDecodeCallback(object : BarcodeDecoder.DecodeCallback {
//                override fun onDecodeComplete(barcodeEntity: BarcodeEntity) {
//                    try {
//                        Log.d("Barcode", "Decode callback triggered - Result code: ${barcodeEntity.resultCode}")
////                        Log.d("HEHEHHE",barcodeEntity.barcodeData)
//                        val barcodeData = barcodeEntity.barcodeData
//                        Log.d("Barcode", "Barcode scanned successfully: $barcodeData")
//                        scannedBarcode = barcodeEntity.barcodeData
////                        updateBarcode(barcodeEntity.barcodeData)
//                        isScanning = false
//                        isBarcodeScanned = true
//
//                        // Play beep sound
//                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
//
//                        // Stop barcode scanning dan switch ke RFID mode
////                        stopBarcodeScan()
////                        changeScanModeToRfid()
//                        // Hanya proses jika sedang dalam mode barcode scanning
////                        if (currentScanMode == ScanMode.BARCODE && isScanning) {
////                            if (barcodeEntity.resultCode == BarcodeDecoder.DECODE_SUCCESS) {
////                                val barcodeData = barcodeEntity.barcodeData
////                                Log.d("Barcode", "Barcode scanned successfully: $barcodeData")
////
////                                viewModelScope.launch(Dispatchers.Main) {
////                                    // Update barcode result
////                                    updateBarcode(barcodeData)
////                                    isBarcodeScanned = true
////
////                                    // Play beep sound
////                                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
////
////                                    // Stop barcode scanning dan switch ke RFID mode
////                                    stopBarcodeScan()
////                                    changeScanModeToRfid()
////                                }
////                            } else {
////                                Log.d("Barcode", "Barcode scan failed with result code: ${barcodeEntity.resultCode}")
////                            }
////                        } else {
////                            Log.d("Barcode", "Callback ignored - Mode: $currentScanMode, Scanning: $isScanning")
////                        }
//                    } catch (e: Exception) {
//                        Log.e("Barcode", "Error in decode callback: ${e.message}")
//                    }
//                }
                override fun onDecodeComplete(barcodeEntity: BarcodeEntity) {
                    try {
                        Log.d("Barcode", "Decode callback triggered - Result code: ${barcodeEntity.resultCode}")

                        val barcodeData = barcodeEntity.barcodeData ?: "null"
                        Log.d("HEHEHHE", "Scanned barcode: $barcodeData")
                        Log.d("Barcode", "Barcode scanned successfully: $barcodeData")

                        _scannedBarcode.value = barcodeEntity.barcodeData
                        Log.d("Barcode", "Update Success: ${_scannedBarcode.value}")

                        isScanning = false
                        isBarcodeScanned = true

                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                    } catch (e: Exception) {
                        Log.e("Barcode", "Error in decode callback", e)
                    }
                }

            })

            isInitializingBarcode = false
            Log.d("Barcode", "Barcode initialization completed")

        } catch (e: Exception) {
            Log.e("Barcode", "Error initializing barcode: ${e.message}")
            isInitializingBarcode = false
        }
    }

    fun releaseBarcode() {
        try {
            barcodeDecoder?.close()
            Log.d("Barcode", "Barcode decoder released")
        } catch (e: Exception) {
            Log.e("Barcode", "Error releasing barcode: ${e.message}")
        }
    }

    fun updateBarcode(value: String) {
        _scannedBarcode.value = value
        Log.d("Barcode", "Barcode updated to: $value")
    }

    private fun updateRfid(value: String) {
        scannedRfid = value
        rfidResult = value
    }

    // Fungsi untuk clear hasil scan
    fun clearBarcodeResult() {
        _scannedBarcode.value = ""
        isBarcodeScanned = false
    }

    fun clearRfidResult() {
        scannedRfid = ""
        rfidResult = ""
    }

    fun clearAllResults() {
//        scannedBarcode = ""
//        scannedRfid = ""
        rfidResult = ""
        isBarcodeScanned = false
        // Reset ke mode barcode setelah clear
        changeScanModeToBarcode()
    }

    // Fungsi untuk handle trigger dari hardware
    fun handleHardwareTrigger() {
        try {
            Log.d("Scanner", "Hardware trigger - Scanning: $isScanning, Mode: $currentScanMode, BarcodeScanned: $isBarcodeScanned")

            if (!isScanning) {
                if (currentScanMode ==ScanMode.RFID) {

                    // Jika barcode sudah di-scan, scan RFID
                    if (isSingleScan) {
                        startRfidScanningOnce()
                    } else {

                        startRfidScanning()
                    }
                } else {
                    Log.d("Scanner", "MASUK SANA")
                    // Jika barcode belum di-scan, scan barcode dulu
                    if (currentScanMode == ScanMode.BARCODE) {
                        startBarcodeScan()
                    }
                }
            } else {
                stopScanning()
            }
        } catch (e: Exception) {
            Log.e("Scanner", "Error handling hardware trigger: ${e.message}")
        }
    }

    // Fungsi untuk debugging
    fun debugBarcodeStatus(): String {
        return """
            Barcode Decoder Status:
            - Decoder null: ${barcodeDecoder == null}
            - Current scan mode: $currentScanMode
            - Is scanning: $isScanning
            - Is barcode mode: $isBarcode
            - Is single scan: $isSingleScan
            - Is barcode scanned: $isBarcodeScanned
            - Scanned barcode: '$scannedBarcode'
            - Scanned RFID: '$scannedRfid'
        """.trimIndent()
    }

    // Test barcode functionality
    fun testBarcodeInit() {
        Log.d("Barcode", "Testing barcode initialization...")

        try {
            val factory = BarcodeFactory.getInstance()
            Log.d("Barcode", "Factory: $factory")

            val decoder = factory.barcodeDecoder
            Log.d("Barcode", "Decoder: $decoder")

            if (decoder != null) {
                val openResult = decoder.open(context)
                Log.d("Barcode", "Open result: $openResult")
                decoder.close()
            }
        } catch (e: Exception) {
            Log.e("Barcode", "Test failed: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            stopScanning()
            releaseBarcode()
            toneGenerator.release()
        } catch (e: Exception) {
            Log.e("ViewModel", "Error clearing resources: ${e.message}")
        }
    }
}