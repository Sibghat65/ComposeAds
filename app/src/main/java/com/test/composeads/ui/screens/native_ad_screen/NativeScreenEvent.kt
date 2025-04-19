package com.test.composeads.ui.screens.native_ad_screen

import android.content.Context
import com.test.composeads.base.UiEvent

sealed class NativeScreenEvent : UiEvent {
    data class RequestNativeAd(val context: Context): NativeScreenEvent()
    data object GoBack: NativeScreenEvent()
}