package com.test.composeads.ui.common


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.test.composeads.R
import com.test.composeads.utils.extensions.getAdSize


@Composable
fun BannerAdView(
    adView: AdView,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = {
            (adView.parent as? ViewGroup)?.removeView(adView)
            adView
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = Color.Gray)
    )
}

@Composable
fun LottieWithLoadingText() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_anim))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading Ad...",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Normal,
                color = Black
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PopulateNativeWithMedia(nativeAd: NativeAd) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            val adView = LayoutInflater.from(context)
                .inflate(R.layout.native_with_large_media, null, false) as NativeAdView
            adView.mediaView = adView.findViewById(R.id.ad_media_view)
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body_text)
            adView.callToActionView = adView.findViewById(R.id.btn_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.mediaView?.mediaContent = nativeAd.mediaContent
            if (nativeAd.icon != null) {
                (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
            }
            if (nativeAd.callToAction != null) {
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }
            if (nativeAd.mediaContent != null) {
                (adView.mediaView as MediaView).mediaContent = nativeAd.mediaContent
            }
            if (nativeAd.headline != null) {
                (adView.headlineView as TextView).text = nativeAd.headline
            }
            if (nativeAd.body != null) {
                (adView.bodyView as TextView).text = nativeAd.body
            }
            adView.setNativeAd(nativeAd)
            adView
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color =  Color(0xFFF5F5F5))
    )
}

@Composable
fun CollapsibleBannerAd(
    modifier: Modifier = Modifier,
    adID: String,
    onAdLoad:(AdView)->Unit,
    onFailedToLoad:(String)->Unit,
    onAdOpened:()->Unit,
    onAdClosed:()->Unit
) {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(context.getAdSize())
                adUnitId = adID
                val extras = Bundle().apply {
                    putString("collapsible", "bottom")
                }
                val adRequest = AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                    .build()
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        onAdLoad(this@apply)
                    }
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        onFailedToLoad("Failed to load: ${error.message}")
                    }
                    override fun onAdOpened() {
                        onAdOpened()
                    }
                    override fun onAdClosed() {
                        onAdClosed()
                    }
                }
                loadAd(adRequest)
            }
        },
        onRelease = {
            it.destroy()
        }
    )
}

