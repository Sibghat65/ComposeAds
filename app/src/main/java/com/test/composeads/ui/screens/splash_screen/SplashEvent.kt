package com.test.composeads.ui.screens.splash_screen

import android.content.Context
import com.test.composeads.base.UiEvent

sealed class SplashEvent : UiEvent {
    data object NextScreen : SplashEvent()
    data class RequestAd(val context: Context): SplashEvent()
}