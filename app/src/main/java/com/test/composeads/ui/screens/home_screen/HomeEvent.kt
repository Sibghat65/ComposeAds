package com.test.composeads.ui.screens.home_screen

import android.content.Context
import com.test.composeads.base.UiEvent

sealed class HomeEvent : UiEvent {
    data object GoNativeScreen : HomeEvent()
    data object GoCollapsibleScreen : HomeEvent()
    data class RequestAdaptiveBanner(val context: Context): HomeEvent()
}