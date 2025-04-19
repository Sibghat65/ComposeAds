package com.test.composeads.ui.screens.native_ad_screen

import com.test.composeads.base.UiEffect


sealed class NativeScreenUiEffect(
) : UiEffect{
    data object GoBack: NativeScreenUiEffect()
}