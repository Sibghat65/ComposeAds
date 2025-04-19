package com.test.composeads.ui.screens.native_ad_screen

import android.app.Activity
import android.view.LayoutInflater
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isInvisible
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.test.composeads.R
import com.test.composeads.ui.common.LottieWithLoadingText
import com.test.composeads.ui.common.PopulateNativeWithMedia
import com.test.composeads.utils.extensions.isNetworkAvailable
import com.test.composeads.utils.extensions.loadInterstitial
import com.test.composeads.utils.extensions.showInterstitialAd
import com.test.composeads.utils.remote_config.RemoteConfigKeys.IS_ALL_ADS_ENABLED
import com.test.composeads.utils.remote_config.RemoteConfigKeys.NATIVE_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.NATIVE_AD_SCREEN_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.NATIVE_SCREEN_BACK_INTERSTITIAL_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.OTHER_INTERSTITIAL_AD_KEY


@Composable
fun NativeAdScreen(
    modifier: Modifier = Modifier,
    uiState: NativeScreenUiState,
    onEvent: (NativeScreenEvent) -> Unit
) {
    val context = LocalContext.current
    if (IS_ALL_ADS_ENABLED && NATIVE_AD_SCREEN_AD_CONTROL && NATIVE_AD_KEY.isNotBlank()) {
        onEvent.invoke(NativeScreenEvent.RequestNativeAd(context))
    }
    var showAdLoading by remember { mutableStateOf(false) }
    BackHandler {
        if (IS_ALL_ADS_ENABLED && NATIVE_SCREEN_BACK_INTERSTITIAL_AD_CONTROL && OTHER_INTERSTITIAL_AD_KEY.isNotBlank()) {
            showAdLoading = true
            (context as Activity).loadInterstitial(
                OTHER_INTERSTITIAL_AD_KEY,
                onAdLoad = {
                    (context).showInterstitialAd(it, onShow = {}, onDismiss = {
                        showAdLoading = false
                        onEvent(NativeScreenEvent.GoBack)
                    })
                },
                onFailed = {
                    showAdLoading = false
                    onEvent(NativeScreenEvent.GoBack)
                })
        }else{
            onEvent(NativeScreenEvent.GoBack)
        }
    }
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
            }else{
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
                if (IS_ALL_ADS_ENABLED && context.isNetworkAvailable() && NATIVE_AD_SCREEN_AD_CONTROL && NATIVE_AD_KEY.isNotBlank()){
                    if (uiState.nativeAd != null){
                        PopulateNativeWithMedia(uiState.nativeAd)
                    }else if (uiState.showNativeAdContainer){
                        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            AndroidView(
                                factory = {
                                    val inflater = LayoutInflater.from(context)
                                    val view = inflater.inflate(R.layout.native_with_large_media, null)
                                    view.apply {
                                        isInvisible = true
                                    }
                                }
                            )
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
                                text = "Loading Ad...",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Gray
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}