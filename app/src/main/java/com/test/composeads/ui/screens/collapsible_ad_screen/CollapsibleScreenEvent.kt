package com.test.composeads.ui.screens.collapsible_ad_screen

import com.google.android.gms.ads.AdView
import com.test.composeads.base.UiEvent

sealed class CollapsibleScreenEvent : UiEvent {
    data object CollapsibleBannerRequested: CollapsibleScreenEvent()
    data class CollapsibleAdLoaded(val adView: AdView?,val showLoadingContainer:Boolean): CollapsibleScreenEvent()
    data object GoBack: CollapsibleScreenEvent()
}