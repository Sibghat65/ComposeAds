package com.test.composeads.ui.screens.collapsible_ad_screen

import com.google.android.gms.ads.AdView
import com.test.composeads.base.UiState

data class CollapsibleScreenUiState(
    val isCollapsibleRequested:Boolean = false,
    val showAdContainer:Boolean = true,
    val adView: AdView? = null,
) : UiState