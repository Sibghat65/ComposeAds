package com.test.composeads.ui.screens.native_ad_screen

import com.google.android.gms.ads.nativead.NativeAd
import com.test.composeads.base.UiState

data class NativeScreenUiState(
    val isNativeAdRequested:Boolean = false,
    val showNativeAdContainer:Boolean = true,
    val nativeAd: NativeAd? = null,
) : UiState