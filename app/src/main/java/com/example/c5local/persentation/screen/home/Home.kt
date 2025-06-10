package com.example.c5local.persentation.screen.home

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.c5local.R
import com.example.c5local.domain.model.MenuItem
import com.example.c5local.persentation.components.MenuGrid
import com.example.c5local.persentation.shared.UHFViewModel

@Composable
fun Home(navController: NavController,viewModel: UHFViewModel = hiltViewModel()) {
    val context = LocalContext.current
    MainMenuScreen { route ->
        navController.navigate(route)
    }
}

@Composable
fun MainMenuScreen(
    onNavigate: (String) -> Unit
) {

    val menuItems = remember {
        listOf(
            MenuItem(1, "Registration", R.drawable.ic_registration, "registration"),
            MenuItem(2, "Scan All Tags", R.drawable.ic_good_receive, "scan-all-tag"),
            MenuItem(3, "RFID Searching", R.drawable.ic_rfid, "rfid-searching"),
            MenuItem(4, "Good Receive", R.drawable.ic_good_receive, "good-receive"),
            MenuItem(5, "Stuffing", R.drawable.ic_stuffing, "stuffing"),
            MenuItem(6, "Stock Opname", R.drawable.ic_stock_opname, "stock-op-name"),
            MenuItem(7, "Alloc to Bin", R.drawable.ic_alloc_to_bin, "alloc-to-bin"),
            MenuItem(8, "Bin Assignment", R.drawable.ic_bin_assignment, "bin-assignment"),
            MenuItem(9, "Hold", R.drawable.ic_hold, "hold"),
            MenuItem(10, "Transfer", R.drawable.ic_transfer, "transfer"),
            MenuItem(11, "Rework", R.drawable.ic_rework, "rework"),
            MenuItem(12, "Check Location", R.drawable.ic_check_location, "check-location"),
            MenuItem(13, "Cancel Process", R.drawable.ic_cancel_process, "cancel-process"),
            MenuItem(14, "Unpair RFID", R.drawable.ic_unpair_rfid, "unpair-rfid"),
        )
    }

    MenuGrid(
        items = menuItems,
        onItemClick = { menuItem ->
            onNavigate(menuItem.route)
        }
    )
}