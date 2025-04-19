package com.test.composeads.ui.screens.splash_screen

import com.test.composeads.base.UiEffect


sealed class SplashUiEffect(
) : UiEffect{
    data object GotoHome: SplashUiEffect()
}