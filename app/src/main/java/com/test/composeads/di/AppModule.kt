package com.test.composeads.di

import com.test.composeads.ui.screens.collapsible_ad_screen.CollapsibleAdScreenViewModel
import com.test.composeads.ui.screens.home_screen.HomeViewModel
import com.test.composeads.ui.screens.native_ad_screen.NativeAdScreenViewModel
import com.test.composeads.ui.screens.splash_screen.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    //Splash ViewModel
    viewModel { SplashViewModel() }

    //Home ViewModel
    viewModel { HomeViewModel() }

    //Native screen ViewModel
    viewModel { NativeAdScreenViewModel() }

    //Collapsible ad screen ViewModel
    viewModel { CollapsibleAdScreenViewModel() }
}