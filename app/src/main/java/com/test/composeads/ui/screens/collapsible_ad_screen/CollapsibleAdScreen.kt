package com.test.composeads.ui.screens.collapsible_ad_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.test.composeads.ui.common.BannerAdView
import com.test.composeads.ui.common.CollapsibleBannerAd
import com.test.composeads.utils.extensions.isNetworkAvailable
import com.test.composeads.utils.remote_config.RemoteConfigKeys.COLLAPSIBLE_BANNER_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.IS_ALL_ADS_ENABLED

@Composable
fun CollapsibleAdScreen(
    modifier: Modifier = Modifier,
    uiState: CollapsibleScreenUiState,
    onEvent: (CollapsibleScreenEvent) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Native Ad Screen Content",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Black,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
            if (IS_ALL_ADS_ENABLED && COLLAPSIBLE_BANNER_AD_KEY.isNotBlank() && context.isNetworkAvailable()) {
                if (uiState.adView != null) {
                    BannerAdView(uiState.adView)
                } else {
                    if (uiState.showAdContainer){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = Color.Gray)
                                .height(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .placeholder(
                                        visible = true,
                                        highlight = PlaceholderHighlight.shimmer(),
                                        color = Color.Gray.copy(alpha = 0.2f)
                                    )
                            )
                            Text(
                                text = "Loading ad...",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = Black
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    if (!uiState.isCollapsibleRequested) {
                        onEvent(CollapsibleScreenEvent.CollapsibleBannerRequested)
                        CollapsibleBannerAd(adID = COLLAPSIBLE_BANNER_AD_KEY, onAdLoad = {
                            onEvent(CollapsibleScreenEvent.CollapsibleAdLoaded(it, false))
                        }, onFailedToLoad = {
                            onEvent(CollapsibleScreenEvent.CollapsibleAdLoaded(null, false))
                        }, onAdClosed = {}, onAdOpened = {})
                    }
                }
            }
        }
    }
}