package com.example.c5local.persentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.c5local.persentation.screen.alloctobin.AllocToBinScanScreen
import com.example.c5local.persentation.screen.alloctobin.AllocToBinScreen
import com.example.c5local.persentation.screen.goodreceive.GoodReceiveScreen
import com.example.c5local.persentation.screen.home.Home
import com.example.c5local.persentation.screen.registration.ItemScreen
import com.example.c5local.persentation.screen.registration.Registration
import com.example.c5local.persentation.screen.rfid_searching.RfidSearchScreen
import com.example.c5local.persentation.screen.scan_all_tag.RfidScannerScreen
import com.example.c5local.persentation.screen.stuffing.StuffingScreen
import com.example.c5local.persentation.shared.UHFViewModel

@Composable
fun AppNavHost(
    uhfViewModel: UHFViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        composable(Routes.Home) { Home(navController) }
        composable(Routes.Registration) { Registration(navController,uhfViewModel) }
        composable(Routes.ScanAllTag) { RfidScannerScreen(uhfViewModel) }
        composable(Routes.RfidSearching) { RfidSearchScreen(uhfViewModel) }
        composable(Routes.GoodReceive) { GoodReceiveScreen(navController,uhfViewModel) }
        composable(Routes.Stuffing) { StuffingScreen(navController,uhfViewModel)  }
        composable(Routes.StockOpname) {  }
        composable(Routes.AllocToBin) { AllocToBinScreen(navController,uhfViewModel) }
        composable(Routes.BinAssignment) {  }
        composable(Routes.Hold) {  }
        composable(Routes.Transfer) {  }
        composable(Routes.Rework) {  }
        composable(Routes.CheckLocation) {  }
        composable(Routes.CancelProcess) {  }
        composable(Routes.UnpairRfid) {  }
    }
}
