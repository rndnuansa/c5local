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
import com.rscja.deviceapi.interfaces.IUHF
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
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

    // [BARU] State untuk menyimpan hasil pembacaan reserved bank dan mengontrol dialog
    var reservedBankData by mutableStateOf<String?>(null)
        private set
    var isReadingData by mutableStateOf(false)
        private set

    // [BARU] State untuk fitur trigger terus-menerus
    var isContinuousTriggerActive by mutableStateOf(false)
        private set
    var continuousTriggerTargetEpc by mutableStateOf<String?>(null)
        private set
    private var continuousTriggerJob: Job? = null

    // [BARU] State untuk fitur tulis data
    var showWriteSheet by mutableStateOf(false)
        private set
    var tagToWrite by mutableStateOf<RfidTag?>(null)
        private set
    var writeStatus by mutableStateOf<String?>(null)
        private set
    var isWriting by mutableStateOf(false)
        private set

    init{
        sliderValue = rfidUseCases.getRfidPowerUseCase()
//        initUHF()
//        initBarcode()
    }


    // [BARU] Fungsi untuk menampilkan/menyembunyikan bottom sheet
    fun onWriteTagClicked(tag: RfidTag) {
        stopContinuousTrigger() // Matikan LED jika sedang menyala
        tagToWrite = tag
        writeStatus = null // Hapus status lama
        showWriteSheet = true
    }

    fun onDismissWriteSheet() {
        showWriteSheet = false
    }

    // [BARU] Fungsi untuk menulis ulang EPC
    fun writeNewEpc(oldEpc: String, newEpc: String) {
        if (newEpc.length != 24) {
            writeStatus = "Error: EPC baru harus terdiri dari 24 karakter Heksadesimal."
            return
        }
        if (isWriting) return
        isWriting = true
        writeStatus = "Menulis EPC baru..."

        viewModelScope.launch(Dispatchers.IO) {
            val result: Boolean
            var message: String

            try {
                // Gunakan parameter filter yang sudah terbukti
                val filterPtr = 32 // dalam BIT
                val filterCnt = 96 // dalam BIT

                // Panggil fungsi SDK yang benar untuk menulis EPC
                result = mReader?.writeDataToEpc(
                    "00000000",
                    IUHF.Bank_EPC,
                    filterPtr,
                    filterCnt,
                    oldEpc,
                    newEpc
                ) ?: false

                message = if (result) "SUKSES: EPC berhasil diubah." else "GAGAL: Penulisan EPC gagal."

            } catch (e: Exception) {
                message = "ERROR: ${e.message}"
            }

            withContext(Dispatchers.Main) {
                writeStatus = message
                isWriting = false
                // Mungkin perlu refresh list setelah ini
            }
        }
    }

    /**
     * [DIUBAH] Fungsi ini sekarang menggunakan `writeData` yang benar, bukan `blockWriteData`.
     */
    fun writeUserData(targetEpc: String, id: String, status: String) {
        if (isWriting) return
        isWriting = true
        writeStatus = "Menulis User Data..."

        viewModelScope.launch(Dispatchers.IO) {
            val result: Boolean
            var message: String

            try {
                // Buat JSON dari input field
                val jsonData = "{\"id\":\"$id\",\"st\":\"$status\"}"
                val hexData = jsonData.toByteArray(StandardCharsets.UTF_8).joinToString("") { "%02x".format(it) }
                val byteCount = jsonData.toByteArray(StandardCharsets.UTF_8).size
                val wordCount = (byteCount + 1) / 2
                if (wordCount == 0) throw IllegalArgumentException("Data tidak boleh kosong.")

                // Gunakan parameter filter yang sudah terbukti
                val filterPtr = 32 // dalam BIT
                val filterCnt = 96 // dalam BIT

                // Panggil fungsi SDK yang benar untuk menulis ke bank USER
                result = mReader?.writeData(
                    "00000000",
                    IUHF.Bank_EPC,
                    filterPtr,
                    filterCnt,
                    targetEpc,
                    IUHF.Bank_USER, // Tulis ke bank USER
                    0,              // Mulai dari alamat 0
                    wordCount,      // Jumlah word yang akan ditulis
                    hexData         // Data dalam format Heksadesimal
                ) ?: false

                message = if (result) "SUKSES: User Data berhasil ditulis." else "GAGAL: Penulisan User Data gagal."

            } catch (e: Exception) {
                message = "ERROR: ${e.message}"
            }

            withContext(Dispatchers.Main) {
                writeStatus = message
                isWriting = false
            }
        }
    }

    fun clearWriteStatus() {
        writeStatus = null
    }


    fun setRfid(value: String){
        rfidResult = value
        scannedRfid = value
    }


    // [BARU] Fungsi utama untuk toggle On/Off trigger
    fun toggleContinuousTrigger(epc: String) {
        // Jika job sedang berjalan, hentikan.
        if (continuousTriggerJob?.isActive == true) {
            stopContinuousTrigger()
        } else {
            // Jika tidak ada job, mulai yang baru.
            startContinuousTrigger(epc)
        }
    }

    private fun startContinuousTrigger(epc: String) {
        // Hentikan trigger lama jika ada, untuk memastikan hanya satu yang berjalan
        stopContinuousTrigger()

        isContinuousTriggerActive = true
        continuousTriggerTargetEpc = epc

        // Mulai coroutine baru untuk loop
        continuousTriggerJob = viewModelScope.launch(Dispatchers.IO) {
            Log.d("ContinuousTrigger", "Memulai trigger untuk EPC: $epc")
            while (isActive) { // Loop akan berjalan selama job ini aktif
                try {
                    // Gunakan parameter yang sudah terbukti berhasil
                    val filterPtr = 32 // dalam BIT
                    val filterCnt = 96 // dalam BIT (untuk tag 96-bit)

                    mReader?.readData(
                        "00000000",
                        IUHF.Bank_EPC,
                        filterPtr,
                        filterCnt,
                        epc,
                        IUHF.Bank_RESERVED,
                        4,
                        2
                    )
                    // Log untuk menandakan perintah dikirim
                    Log.v("ContinuousTrigger", "Perintah trigger dikirim ke $epc")

                    // Beri jeda agar tidak membanjiri reader
                    delay(200) // delay 200ms

                } catch (e: Exception) {
                    Log.e("ContinuousTrigger", "Error di dalam loop: ${e.message}")
                }
            }
        }
    }

    fun stopContinuousTrigger() {
        continuousTriggerJob?.cancel() // Batalkan job
        continuousTriggerJob = null
        isContinuousTriggerActive = false
        continuousTriggerTargetEpc = null
        Log.d("ContinuousTrigger", "Trigger dihentikan.")
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
                    mReader?.setPower(30)
                    mReader?.setFastInventoryMode(true)

                    isInitializing = false
                    initMessage = if (result) "Init berhasil" else "Init gagal"
                }
            }
        }
    }

    // Fungsi untuk memulai scan RFID
//    fun startRfidScanning() {
//        if (isScanning) return
//
//
//        stopBarcodeScan()
//
//        currentScanMode = ScanMode.RFID
//        isScanning = true
//
//        viewModelScope.launch(Dispatchers.IO) {
//            while (isScanning && currentScanMode == ScanMode.RFID) {
//                println("HEHE")
//                try {
//                    println(mReader?.power)
//                    val tag = mReader?.inventorySingleTag()
//                    tag?.let {
//                        withContext(Dispatchers.Main) {
//                            setRfid(it.epc)
//                            val existing = scannedRfidAll?.find { rfid -> rfid.epc == it.epc }
//
//                            scannedRfidAll = if (existing != null) {
//                                scannedRfidAll?.map { rfid ->
//                                    if (rfid.epc == it.epc) {
//                                        rfid.copy(
//                                            count = rfid.count + 1,
//                                            timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
//                                        )
//                                    } else rfid
//                                }
//                            } else {
//                                scannedRfidAll?.plus(
//                                    RfidTag(
//                                        epc = it.epc,
//                                        timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
//                                        count = 1
//                                    )
//                                )
//                            }
//                            println("SCANNED DITAMBAHKAN : " + (scannedRfidAll?.size ?: 0))
//
//                            scanCount++
//                            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.e("RFID", "Error during RFID scan: ${e.message}")
//                    withContext(Dispatchers.Main) {
//                        isScanning = false
//                    }
//                    return@launch
//                }
//                delay(10)
//            }
//        }
//    }

    fun startRfidScanning() {
        if (isScanning) return
        stopContinuousTrigger()
        stopBarcodeScan()
        if(mReader?.isInventorying == true){
            stopScanning()
        }
        currentScanMode = ScanMode.RFID
        isScanning = true

        // Bersihkan data
        scannedRfidAll = emptyList()
        scanCount = 0

//        mReader?.startInventoryTag()
        // Set callback async untuk tag yang terdeteksi
        mReader?.setInventoryCallback { tag ->
            tag?.let {
                viewModelScope.launch(Dispatchers.Main) {
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
        }

        // Mulai inventory
        val started = mReader?.startInventoryTag() ?: false
        if (!started) {
            isScanning = false
        }
    }



    fun clearRfidAll(){
        scannedRfidAll = emptyList()
        scanCount = 0
    }

    // Fungsi untuk memulai scan RFID sekali
    fun startRfidScanningOnce() {
//        if (isScanning) return
//
//        stopBarcodeScan()
//
//        currentScanMode = ScanMode.RFID
//        isScanning = true
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val tag = mReader?.inventorySingleTag()
//                tag?.let {
//                    withContext(Dispatchers.Main) {
//                        setRfid(it.epc)
//                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
//                    }
//                    Log.d("RFID", "RFID scanned once: ${it.epc}")
//                }
//            } catch (e: Exception) {
//                Log.e("RFID", "Error during single RFID scan: ${e.message}")
//            } finally {
//                withContext(Dispatchers.Main) {
//                    isScanning = false
//                }
//            }
//        }
        if (isScanning) return

        stopBarcodeScan()
        currentScanMode = ScanMode.RFID
        isScanning = true

        // Bersihkan data
        scannedRfidAll = emptyList()
        scanCount = 0

        // Set callback async untuk tag yang terdeteksi
        mReader?.setInventoryCallback { tag ->
            tag?.let {
                viewModelScope.launch(Dispatchers.Main) {
                    setRfid(it.epc)



//                    println("SCANNED DITAMBAHKAN : " + (scannedRfidAll?.size ?: 0))

//                    scanCount++
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                    stopScanning()
                }
            }
        }

        // Mulai inventory
        val started = mReader?.startInventoryTag() ?: false
        if (!started) {
            isScanning = false
        }
    }

    // =======================================================================
    // === FUNGSI KHUSUS UNTUK DEBUGGING PARAMETER FILTER ===
    // =======================================================================
    fun testFilterParameters(epc: String) {
        if (isReadingData || isScanning) return
        isReadingData = true
        Log.d("FilterTest", "================== MEMULAI TES FILTER ==================")
        Log.d("FilterTest", "Menggunakan EPC: $epc")

        viewModelScope.launch(Dispatchers.IO) {
            var finalMessage = "Hasil Tes Filter:\n"
            try {
                // --- PARAMETER YANG AKAN KITA UBAH-UBAH ---
                // Coba satu per satu dengan mengubah komentar (//)

                // Percobaan 1: Standar (Pointer Word 2, Panjang Bit)
//                val testFilterPtr = 2
//                val testFilterCnt = epc.length * 4

                // Percobaan 2: Pointer 0 (Smart SDK), Panjang Bit
//                 val testFilterPtr = 0
//                 val testFilterCnt = epc.length * 4

                // Percobaan 3: Pointer Word 2, Panjang Word
                 val testFilterPtr = 32
                 val testFilterCnt = 96

                // Percobaan 4: Pointer 0, Panjang Word
//                 val testFilterPtr = 0
//                 val testFilterCnt = epc.length / 4

                Log.d("FilterTest", "Menguji dengan: filterPtr = $testFilterPtr, filterCnt = $testFilterCnt")


                // --- Parameter untuk membaca EPC itu sendiri sebagai verifikasi ---
                val accessPwd = "00000000"
                val bankToRead = IUHF.Bank_RESERVED       // Kita coba baca bank EPC
                val readPtr = 4                     // Alamat awal data EPC
                val readCnt = 2         // Panjang EPC dalam word

                val readResult = mReader?.readData(
                    accessPwd,
                    IUHF.Bank_EPC, // Filter berdasarkan EPC
                    testFilterPtr,
                    testFilterCnt,
                    epc,           // Data EPC untuk filter
                    bankToRead,
                    readPtr,
                    readCnt
                )
                Log.d("FilterTest", ">>> SUKSES! Hasil Baca: $readResult")


                if (readResult != null) {
                    Log.d("FilterTest", ">>> SUKSES! Hasil Baca: $readResult")
                    if (readResult.equals(epc, ignoreCase = true)) {
                        finalMessage += "BERHASIL!\nParameter yang benar ditemukan:\nfilterPtr = $testFilterPtr\nfilterCnt = $testFilterCnt"
                        Log.d("FilterTest", ">>> VERIFIKASI BERHASIL! EPC yang dibaca sama dengan filter.")
                    } else {
                        finalMessage += "GAGAL VERIFIKASI.\nEPC yang dibaca ($readResult) tidak sama dengan EPC filter ($epc)."
                        Log.w("FilterTest", ">>> VERIFIKASI GAGAL. Hasil baca tidak sesuai.")
                    }
                } else {
                    finalMessage += "GAGAL.\nOperasi baca mengembalikan null dengan parameter:\nfilterPtr = $testFilterPtr\nfilterCnt = $testFilterCnt"
                    Log.e("FilterTest", ">>> GAGAL. readData() mengembalikan null.")
                }

            } catch (e: Exception) {
                finalMessage += "EXCEPTION: ${e.message}"
                Log.e("FilterTest", ">>> EXCEPTION: ", e)
            }

            Log.d("FilterTest", "================== TES FILTER SELESAI ==================\n")

            withContext(Dispatchers.Main) {
                reservedBankData = finalMessage // Tampilkan hasil tes di dialog
                isReadingData = false
            }
        }
    }

    // [BARU] Fungsi untuk membaca data dari reserved bank (untuk menyalakan LED)
    fun readReservedBankForTag(epc: String) {
        if (isReadingData || isScanning) return // Jangan jalankan jika sedang membaca atau scanning
        isReadingData = true

        viewModelScope.launch(Dispatchers.IO) {
            var resultMessage: String

            try {
                Log.d("ReadData", "Attempting to read data for EPC: $epc")

                val accessPwd = "00000000"
                val filterBank = IUHF.Bank_EPC
                val filterPtr = 0
                val filterCnt =  epc.length * 4
                val filterData = epc
                val bank = IUHF.Bank_RESERVED
                val ptr = 4
                val cnt = 2

                val readResult = mReader?.readData(accessPwd, filterBank, filterPtr, filterCnt, filterData, bank, ptr, cnt)
                Log.d("ReadData", "asdasd: $readResult")

                resultMessage = if (readResult != null) {
                    Log.d("ReadData", "Successfully read data: $readResult")
                    "Data untuk EPC:\n$epc\n\nHasil: $readResult"
                } else {
                    Log.e("ReadData", "Failed to read data. Method returned null.")
                    "Gagal membaca data dari tag dengan EPC:\n$epc"
                }
            } catch (e: Exception) {
                Log.e("ReadData", "Exception while reading data: ${e.message}", e)
                resultMessage = "Error: ${e.message}"
            }

            withContext(Dispatchers.Main) {
                reservedBankData = resultMessage
                isReadingData = false
            }
        }
    }

    // [BARU] Fungsi untuk menutup dialog hasil pembacaan
    fun dismissReadDataDialog() {
        reservedBankData = null
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
        mReader?.stopInventory()
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
                override fun onDecodeComplete(barcodeEntity: BarcodeEntity) {
                    try {
                        Log.d("Barcode", "Decode callback triggered - Result code: ${barcodeEntity.resultCode}")

                        val barcodeData = barcodeEntity.barcodeData ?: "null"
                        Log.d("HEHEHHE", "Scanned barcode: $barcodeData")
                        Log.d("Barcode", "Barcode scanned successfully: $barcodeData")

                        _scannedBarcode.value = barcodeEntity.barcodeData
//                        changeToBarcode(true)
                        if(barcodeData!= "null" ||barcodeData != "" || barcodeData != null){
                            changeToBarcode(false)

                        }
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
            stopContinuousTrigger()
            stopScanning()
            releaseBarcode()
            toneGenerator.release()
        } catch (e: Exception) {
            Log.e("ViewModel", "Error clearing resources: ${e.message}")
        }
    }
}