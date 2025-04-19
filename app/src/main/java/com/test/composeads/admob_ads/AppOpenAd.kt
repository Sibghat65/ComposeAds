package com.test.composeads.admob_ads

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.test.composeads.ui.activities.MainActivity
import com.test.composeads.utils.remote_config.RemoteConfigKeys.APP_OPEN_ON_RESUME_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.IS_ALL_ADS_ENABLED


object AppOpenAd {
    private var isAppOpenAdLoading = false
    var isAppOpenAdShowing = false
    var isInterstitialShowing = false
    private var lastAdShowTime = 0L
    private var appOpenAd: AppOpenAd? = null

    fun Activity.showAppOpenAd(onDismiss: () -> Unit) {
        if (System.currentTimeMillis() - lastAdShowTime < 500) {
            onDismiss()
        }else{
            if (appOpenAd!=null){
                appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        isAppOpenAdShowing = false
                        appOpenAd = null
                        Log.d("admob_ads","App Open dismissed")
                        onDismiss()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        isAppOpenAdShowing = false
                        appOpenAd = null
                        Log.d("admob_ads","App Open failed to show: ${adError.message}")
                        onDismiss()
                    }
                    override fun onAdShowedFullScreenContent() {
                        isAppOpenAdShowing = true
                        Log.d("admob_ads","App Open shown")
                        lastAdShowTime = System.currentTimeMillis()
                    }
                }
                appOpenAd?.show(this)
            }else{
                onDismiss()
            }
        }
    }
    fun Activity.showAppOpenOnResume(
        onAlreadyShowing: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (isAppOpenAdShowing || isAppOpenAdLoading || isInterstitialShowing) {
            Log.d("admob_ads", "App Open already showing or loading")
            onAlreadyShowing()
            return
        }

        if (appOpenAd != null) {
            Log.d("admob_ads", "App Open already loaded")
            showAppOpenAd(onDismiss)
            return
        }

        if (APP_OPEN_ON_RESUME_AD_KEY.isBlank() || !IS_ALL_ADS_ENABLED) {
            Log.d("admob_ads", "app open ad id empty or ads disabled")
            onDismiss()
            return
        }

        Log.d("admob_ads", "App Open requesting")
        isAppOpenAdLoading = true
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            this,
            APP_OPEN_ON_RESUME_AD_KEY,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isAppOpenAdLoading = false
                    isAppOpenAdShowing = false
                    appOpenAd = null
                    Log.d("admob_ads", "App Open failed to load")
                    onDismiss()
                }

                override fun onAdLoaded(ad: AppOpenAd) {
                    isAppOpenAdLoading = false
                    Log.d("admob_ads", "App Open Loaded")
                    appOpenAd = ad
                    showAppOpenAd(onDismiss)
                }
            }
        )
    }

}
