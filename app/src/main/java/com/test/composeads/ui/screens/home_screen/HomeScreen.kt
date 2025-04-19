package com.test.composeads.ui.screens.home_screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.test.composeads.ui.common.BannerAdView
import com.test.composeads.ui.common.LottieWithLoadingText
import com.test.composeads.utils.extensions.loadInterstitial
import com.test.composeads.utils.extensions.showInterstitialAd
import com.test.composeads.utils.remote_config.RemoteConfigKeys.ADAPTIVE_BANNER_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.HOME_ADAPTIVE_BANNER_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.HOME_NATIVE_BUTTON_INTERSTITIAL_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.IS_ALL_ADS_ENABLED
import com.test.composeads.utils.remote_config.RemoteConfigKeys.OTHER_INTERSTITIAL_AD_KEY


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit
) {
    val context = LocalContext.current
    if (IS_ALL_ADS_ENABLED && HOME_ADAPTIVE_BANNER_AD_CONTROL && ADAPTIVE_BANNER_AD_KEY.isNotBlank()) {
        onEvent.invoke(HomeEvent.RequestAdaptiveBanner(context))
    }
    var showAdLoading by remember { mutableStateOf(false) }
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
            if (showAdLoading) {
                LottieWithLoadingText()
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = Black
                            )
                            .clickable {
                                if (IS_ALL_ADS_ENABLED && HOME_NATIVE_BUTTON_INTERSTITIAL_AD_CONTROL && OTHER_INTERSTITIAL_AD_KEY.isNotBlank()) {
                                    showAdLoading = true
                                    (context as Activity).loadInterstitial(OTHER_INTERSTITIAL_AD_KEY,
                                        onAdLoad = {
                                            (context).showInterstitialAd(
                                                it,
                                                onShow = {},
                                                onDismiss = {
                                                    showAdLoading = false
                                                    onEvent(HomeEvent.GoNativeScreen)
                                                })
                                        },
                                        onFailed = {
                                            showAdLoading = false
                                            onEvent.invoke(HomeEvent.GoNativeScreen)
                                        })
                                } else {
                                    onEvent.invoke(HomeEvent.GoNativeScreen)
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Native Ad Screen",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = White,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Continue",
                            tint = White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = Black
                            )
                            .clickable {
                                onEvent.invoke(HomeEvent.GoCollapsibleScreen)
                            }
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Collapsible Ad Screen",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = White,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Continue",
                            tint = White
                        )
                    }
                }
                if (uiState.adaptiveBannerView != null) {
                    BannerAdView(uiState.adaptiveBannerView)
                } else if (uiState.showBannerContainer && IS_ALL_ADS_ENABLED && HOME_NATIVE_BUTTON_INTERSTITIAL_AD_CONTROL && OTHER_INTERSTITIAL_AD_KEY.isNotBlank()) {
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
            }
        }
    }
}