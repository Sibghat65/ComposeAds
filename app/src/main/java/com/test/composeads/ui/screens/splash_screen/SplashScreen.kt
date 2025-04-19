package com.test.composeads.ui.screens.splash_screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.composeads.BaseApplication
import com.test.composeads.admob_ads.AdsInitialization.initializeMobileAdsSdk
import com.test.composeads.utils.extensions.showConsentForm
import com.test.composeads.utils.extensions.showInterstitialAd
import com.test.composeads.utils.remote_config.RemoteConfigKeys.IS_ALL_ADS_ENABLED
import com.test.composeads.utils.remote_config.RemoteConfigKeys.SPLASH_INTERSTITIAL_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.SPLASH_INTERSTITIAL_AD_KEY


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    uiState: SplashUiState,
    onEvent: (SplashEvent) -> Unit
) {
    val context = LocalContext.current
    var showScreenOverlay by remember { mutableStateOf(false) }
    LaunchedEffect(BaseApplication.isRemoteConfigFetched) {
        BaseApplication.isRemoteConfigFetched.collect { remoteFetched ->
            if (remoteFetched) {
                (context as Activity).showConsentForm {consentGiven->
                    if (consentGiven) {
                        if (IS_ALL_ADS_ENABLED){
                            context.initializeMobileAdsSdk()
                            if (SPLASH_INTERSTITIAL_AD_CONTROL && SPLASH_INTERSTITIAL_AD_KEY.isNotBlank()){
                                onEvent(SplashEvent.RequestAd(context))
                            }else{
                                onEvent(SplashEvent.NextScreen)
                            }
                        }else{
                            onEvent(SplashEvent.NextScreen)
                        }
                    }else{
                        onEvent(SplashEvent.NextScreen)
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        if (showScreenOverlay) {
            Box(modifier = Modifier.fillMaxSize().background(color = Black))
        }else{
            if (uiState.showGetStarted){
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            color = Black
                        )
                        .clickable {
                            if (uiState.splashInterstitialAd != null) {
                                showScreenOverlay = true
                                (context as Activity).showInterstitialAd(uiState.splashInterstitialAd, onShow = {}, onDismiss = {
                                    onEvent(SplashEvent.NextScreen)
                                })
                            } else {
                                onEvent.invoke(SplashEvent.NextScreen)
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Get Started",
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
            }else{
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = Black,
                        strokeWidth = 4.dp
                    )
                }
                Text(
                    text = "Please wait...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Black,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSplashScreen(){
    SplashScreen(uiState = SplashUiState(), onEvent = { })
}


