package com.test.composeads.ui.screens.splash_screen

import com.google.android.gms.ads.interstitial.InterstitialAd
import com.test.composeads.base.UiState

data class SplashUiState(
    val isInterstitialRequested:Boolean = false,
    val splashInterstitialAd: InterstitialAd? = null,
    val showGetStarted: Boolean = false,
) : UiState