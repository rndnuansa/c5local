package com.example.c5local.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.c5local.di.AppModule
import com.example.c5local.persentation.shared.ItemViewModel

//@Composable
//fun itemViewModel(): ItemViewModel {
//    val context = LocalContext.current
//    val factory = AppModule.provideItemViewModelFactory(context)
//    return viewModel(factory = factory)
//}