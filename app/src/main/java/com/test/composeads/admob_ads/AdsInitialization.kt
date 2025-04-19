package com.test.composeads.admob_ads

import android.content.Context
import com.google.android.gms.ads.MobileAds

object AdsInitialization {
    fun Context.initializeMobileAdsSdk() {
        MobileAds.initialize(this) {}
    }

}