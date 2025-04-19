package com.test.composeads.ui.screens.collapsible_ad_screen

import com.test.composeads.base.UiEffect


sealed class CollapsibleScreenUiEffect(
) : UiEffect{
    data object GoBack: CollapsibleScreenUiEffect()
}