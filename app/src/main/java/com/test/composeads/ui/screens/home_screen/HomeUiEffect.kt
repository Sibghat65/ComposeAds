package com.test.composeads.ui.screens.home_screen

import com.test.composeads.base.UiEffect


sealed class HomeUiEffect(
) : UiEffect{
    data object GotoNativeAdScreen: HomeUiEffect()
    data object GotoCollapsibleScreen: HomeUiEffect()
}