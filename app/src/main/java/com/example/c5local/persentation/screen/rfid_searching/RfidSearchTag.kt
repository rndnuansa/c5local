package com.example.c5local.persentation.screen.rfid_searching

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.c5local.persentation.screen.alloctobin.HeaderSection
import com.example.c5local.persentation.shared.UHFViewModel
import java.text.SimpleDateFormat
import java.util.*

data class RfidSearchTag(
    val id: Long = System.currentTimeMillis(),
    val epc: String,
    var timestamp: String,
    var count: Int = 1,
    val isMatched: Boolean = false
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
    val Warning = Color(0xFFF59E0B)
}

@Composable
fun RfidSearchScreen(viewModel: UHFViewModel) {
    val isScanning = viewModel.isScanning
    val scannedTags = viewModel.scannedRfidAll
    val scanCount = viewModel.scanCount
    
    var searchEpc by remember { mutableStateOf("") }
    var foundTags by remember { mutableStateOf<List<RfidSearchTag>>(emptyList()) }
    var exactMatchCount by remember { mutableStateOf(0) }

    // Filter tags based on search input
    LaunchedEffect(scannedTags, searchEpc) {
        scannedTags?.let { tags ->
            if (searchEpc.isNotEmpty()) {
                val filtered = tags.filter { tag ->
                    tag.epc.contains(searchEpc, ignoreCase = true)
                }.map { tag ->
                    RfidSearchTag(
                        id = tag.id,
                        epc = tag.epc,
                        timestamp = tag.timestamp,
                        count = tag.count,
                        isMatched = tag.epc.equals(searchEpc, ignoreCase = true)
                    )
                }
                foundTags = filtered.sortedByDescending { it.isMatched }
                exactMatchCount = filtered.count { it.isMatched }
            } else {
                foundTags = tags.map { tag ->
                    RfidSearchTag(
                        id = tag.id,
                        epc = tag.epc,
                        timestamp = tag.timestamp,
                        count = tag.count,
                        isMatched = false
                    )
                }
                exactMatchCount = 0
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initUHF()
        viewModel.stopScanning()
        viewModel.changeScanModeToRfid()
        viewModel.clearRfidAll()
        viewModel.releaseBarcode()
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


        Button(
            onClick = {viewModel.startLocation()},
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
                text = "Start Location",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = (-1).sp,
                    fontSize = 14.sp,
                    color = Color.White,
                )
            )
        }
        // Search Input Card
        SearchInputCard(
            searchEpc = searchEpc,
            onSearchEpcChange = { searchEpc = it },
            onClearSearch = { searchEpc = "" },
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Search Results List
        SearchResultsList(
            tags = foundTags,
            searchQuery = searchEpc,
            isScanning = isScanning
        )
        Spacer(modifier = Modifier.height(8.dp))

//        // Control Panel
//        scannedTags?.let {
//            SearchControlPanel(
//                isScanning = isScanning,
//                onStartScanning = { viewModel.startRfidScanning() },
//                onStopScanning = { viewModel.stopScanning() },
//                onClearTags = {
//                    viewModel.clearRfidAll()
//                    foundTags = emptyList()
//                    exactMatchCount = 0
//                },
//                hasData = it.isNotEmpty()
//            )
//        }

    }
}

@Composable
fun SearchHeaderCard(
    scanCount: Int,
    foundCount: Int,
    exactMatchCount: Int
) {
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
                        imageVector = Icons.Default.Search,
                        contentDescription = "RFID Search",
                        tint = TjiwiColors.OnPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "RFID Search",
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
                    text = "Total: $scanCount",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Found: $foundCount",
                    fontSize = 10.sp,
                    color = TjiwiColors.Success
                )
                if (exactMatchCount > 0) {
                    Text(
                        text = "Exact: $exactMatchCount",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            letterSpacing = (-1).sp,
                            color = TjiwiColors.Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SearchInputCard(
    searchEpc: String,
    onSearchEpcChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    viewModel: UHFViewModel
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
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "Search EPC",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        letterSpacing = (-1).sp,
                        color = TjiwiColors.OnSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScanningIndicator(isScanning = viewModel.isScanning)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (viewModel.isScanning) "Scanning for matches..." else "Ready to scan",
                        fontSize = 14.sp,
                        color = if (viewModel.isScanning) TjiwiColors.Success else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = searchEpc,
                onValueChange = onSearchEpcChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Enter EPC to search...",
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray.copy(alpha = 0.7f)
                    )
                },
                trailingIcon = {
                    if (searchEpc.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TjiwiColors.Primary,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
            
            if (searchEpc.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Searching for EPC containing: \"$searchEpc\"",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
            }else{
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Showing all scanned RFID tags",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun SearchControlPanel(
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
                    text = if (isScanning) "Scanning for matches..." else "Ready to scan",
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

//@Composable
//fun SearchResultsList(
//    tags: List<RfidSearchTag>,
//    searchQuery: String,
//    isScanning: Boolean
//) {
//    Card(
//        modifier = Modifier.fillMaxSize(),
//        colors = CardDefaults.cardColors(containerColor = TjiwiColors.Surface),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = RoundedCornerShape(16.dp)
//    ) {
//        Column {
//            // Header
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(20.dp)
//                    .padding(bottom = 0.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = if (searchQuery.isEmpty()) "All RFID Tags" else "Search Results",
//                    style = MaterialTheme.typography.headlineMedium.copy(
//                        letterSpacing = (-1).sp,
//                        color = TjiwiColors.OnSurface,
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.SemiBold,
//                    )
//                )
//                Text(
//                    text = "(${tags.size})",
//                    style = MaterialTheme.typography.headlineMedium.copy(
//                        letterSpacing = (-1).sp,
//                        color = TjiwiColors.Primary,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Medium,
//                    )
//                )
//            }
//
//            Divider(
//                modifier = Modifier.padding(horizontal = 20.dp),
//                color = Color.Gray.copy(alpha = 0.2f)
//            )
//
//            // List Content
//            if (searchQuery.isEmpty()) {
//                EmptySearchState()
//            } else if (tags.isEmpty()) {
//                NoResultsState(searchQuery = searchQuery)
//            } else {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    itemsIndexed(tags) { index, tag ->
//                        SearchTagItem(
//                            tag = tag,
//                            index = index + 1,
//                            searchQuery = searchQuery
//                        )
//                    }
//                }
//            }
//        }
//    }
//}



@Composable
fun SearchResultsList(
    tags: List<RfidSearchTag>,
    searchQuery: String,
    isScanning: Boolean
) {
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
                    text = if (searchQuery.isEmpty()) "All RFID Tags" else "Search Results",
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
                EmptyTagsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(tags) { index, tag ->

                        SearchTagItem(
                            tag = tag,
                            index = index + 1,
                            searchQuery = searchQuery
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTagsState() {
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
            text = "Click \"Start Scanning\" to begin scanning RFID tags",
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Enter EPC to search",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Type in the search box above to find matching RFID tags",
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NoResultsState(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No matching tags found",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Text(
            text = "No RFID tags contain \"$searchQuery\"",
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Try scanning more tags or adjust your search",
            fontSize = 12.sp,
            color = Color.Gray.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic
        )
    }
}

fun hexToString(hex: String): String {
    return hex.chunked(2)
        .mapNotNull {
            try {
                it.toInt(16).toChar()
            } catch (e: Exception) {
                null // skip karakter invalid
            }
        }
        .joinToString("")
        .trimEnd { it.code == 0 } // hilangkan padding null (hex "00")
}@Composable
fun SearchTagItem(
    tag: RfidSearchTag,
    index: Int,
    searchQuery: String
) {
    val readableEpc = remember(tag.epc) { hexToString(tag.epc) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (tag.isMatched)
                TjiwiColors.Success.copy(alpha = 0.1f)
            else
                TjiwiColors.SurfaceVariant
        ),
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
                if (tag.isMatched) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = TjiwiColors.Success,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Exact Match",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = readableEpc,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = if (tag.isMatched) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (tag.isMatched) TjiwiColors.Success else TjiwiColors.OnSurface,
                        ),
                    )
                    Text(
                        text = "Hex: ${tag.epc}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    if (tag.isMatched) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "EXACT MATCH",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = TjiwiColors.Success,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .background(
                                    color = TjiwiColors.Success.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
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
                        color = if (tag.isMatched) TjiwiColors.Success else TjiwiColors.Primary,
                    ),
                )
            }
        }
    }
}
