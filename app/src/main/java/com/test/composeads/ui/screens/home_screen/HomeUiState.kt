package com.test.composeads.ui.screens.home_screen

import com.google.android.gms.ads.AdView
import com.test.composeads.base.UiState

data class HomeUiState(
    val isBannerRequested:Boolean = false,
    val showBannerContainer:Boolean = true,
    val adaptiveBannerView: AdView? = null,
) : UiState