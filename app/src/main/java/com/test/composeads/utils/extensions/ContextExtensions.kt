package com.test.composeads.utils.extensions

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.test.composeads.BuildConfig
import com.test.composeads.admob_ads.AppOpenAd.isAppOpenAdShowing
import com.test.composeads.admob_ads.AppOpenAd.isInterstitialShowing
import com.test.composeads.utils.remote_config.RemoteConfigKeys.ADAPTIVE_BANNER_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.APP_OPEN_ON_RESUME_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.COLLAPSIBLE_BANNER_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.HOME_ADAPTIVE_BANNER_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.HOME_NATIVE_BUTTON_INTERSTITIAL_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.IS_ALL_ADS_ENABLED
import com.test.composeads.utils.remote_config.RemoteConfigKeys.NATIVE_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.NATIVE_AD_SCREEN_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.NATIVE_SCREEN_BACK_INTERSTITIAL_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.OTHER_INTERSTITIAL_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.SPLASH_INTERSTITIAL_AD_CONTROL
import com.test.composeads.utils.remote_config.RemoteConfigKeys.SPLASH_INTERSTITIAL_AD_KEY
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File

suspend fun Context.getDocumentsMediaStore(): Flow<List<File>> =
    flow {
        val documentList = mutableListOf<File>()
        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DATA
            )
            val categories = arrayOf("application/pdf")
            val selection =
                "${MediaStore.Files.FileColumns.MIME_TYPE} IN (${categories.joinToString { "?" }})"

            val uri = MediaStore.Files.getContentUri("external")
            val sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"

            val cursor = contentResolver.query(
                uri,
                projection,
                selection,
                categories,
                sortOrder
            )

            cursor?.use {
                val displayNameColumn =
                    it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)

                while (it.moveToNext()) {
                    val displayName = it.getString(displayNameColumn)
                    val size = it.getLong(sizeColumn)
                    val filePath = it.getString(dataColumn)
                    val file = File(filePath)

                    documentList.add(file)
                }
            }
        }
        emit(documentList)
    }



fun Context.isNetworkAvailable(): Boolean {
    val connManager =
        getSystemService(Activity.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connManager.activeNetwork ?: return false
    val networkCapabilities = connManager.getNetworkCapabilities(network) ?: return false
    Log.d("speed", "checkNetworkSpeed: ${networkCapabilities.linkDownstreamBandwidthKbps / 1000}")
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
fun Context.fetchRemoteConfig(
    onSuccessfulFetch: () -> Unit,
    onFailed:() -> Unit
) {
    if (BuildConfig.DEBUG){
        IS_ALL_ADS_ENABLED = true
        SPLASH_INTERSTITIAL_AD_CONTROL= true
        HOME_ADAPTIVE_BANNER_AD_CONTROL= true
        HOME_NATIVE_BUTTON_INTERSTITIAL_AD_CONTROL= true
        NATIVE_SCREEN_BACK_INTERSTITIAL_AD_CONTROL= true
        NATIVE_AD_SCREEN_AD_CONTROL= true
        SPLASH_INTERSTITIAL_AD_KEY  = "ca-app-pub-3940256099942544/1033173712"
        OTHER_INTERSTITIAL_AD_KEY  = "ca-app-pub-3940256099942544/1033173712"
        APP_OPEN_ON_RESUME_AD_KEY = "ca-app-pub-3940256099942544/9257395921"
        ADAPTIVE_BANNER_AD_KEY = "ca-app-pub-3940256099942544/9214589741"
        NATIVE_AD_KEY = "ca-app-pub-3940256099942544/2247696110"
        COLLAPSIBLE_BANNER_AD_KEY = "ca-app-pub-3940256099942544/9214589741"
        onSuccessfulFetch()
    }else{
        //for remote config in release
        /*val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
        configSettings.minimumFetchIntervalInSeconds = 0L
        remoteConfig.setConfigSettingsAsync(configSettings.build())
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                this.setRemoteData( remoteConfig) {
                    onSuccessfulFetch()
                }
            } else {
                onFailed()
            }
        }*/
    }
}
/*
fun Context.setRemoteData(
    remoteConfig: FirebaseRemoteConfig,
    onLoaded: () -> Unit
) {
    IS_ALL_ADS_ENABLED = remoteConfig.getBoolean("IS_ALL_ADS_ENABLED")
    SPLASH_INTERSTITIAL_AD_CONTROL = remoteConfig.getBoolean("SPLASH_INTERSTITIAL_AD_CONTROL")
    HOME_ADAPTIVE_BANNER_AD_CONTROL = remoteConfig.getBoolean("HOME_ADAPTIVE_BANNER_AD_CONTROL")
    HOME_NATIVE_BUTTON_INTERSTITIAL_AD_CONTROL = remoteConfig.getBoolean("HOME_NATIVE_BUTTON_INTERSTITIAL_AD_CONTROL")
    NATIVE_SCREEN_BACK_INTERSTITIAL_AD_CONTROL = remoteConfig.getBoolean("NATIVE_SCREEN_BACK_INTERSTITIAL_AD_CONTROL")
    NATIVE_AD_SCREEN_AD_CONTROL = remoteConfig.getBoolean("NATIVE_AD_SCREEN_AD_CONTROL")
    SPLASH_INTERSTITIAL_AD_KEY  = remoteConfig.getString("SPLASH_INTERSTITIAL_AD_KEY")
    OTHER_INTERSTITIAL_AD_KEY  = remoteConfig.getString("OTHER_INTERSTITIAL_AD_KEY")
    APP_OPEN_ON_RESUME_AD_KEY = remoteConfig.getString("APP_OPEN_ON_RESUME_AD_KEY")
    APP_OPEN_ON_RESUME_AD_KEY = remoteConfig.getString("APP_OPEN_ON_RESUME_AD_KEY")
    ADAPTIVE_BANNER_AD_KEY = remoteConfig.getString("ADAPTIVE_BANNER_AD_KEY")
    NATIVE_AD_KEY = remoteConfig.getString("NATIVE_AD_KEY")
    onLoaded()
}
*/

fun Activity.showConsentForm(onConsentResult: (Boolean) -> Unit) {
    val debugSettings = ConsentDebugSettings.Builder(this)
        .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
        .addTestDeviceHashedId("861BC2C6E0CBA9F0E43CED319E9CB57A")
        .build()

    val params = ConsentRequestParameters.Builder()
        .setConsentDebugSettings(debugSettings)
        .build()

    val consentInformation = UserMessagingPlatform.getConsentInformation(this)

    consentInformation.requestConsentInfoUpdate(
        this,
        params,
        {
            if (consentInformation.isConsentFormAvailable) {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this
                ) { formError ->
                    if (formError == null) {
                        val isConsentGiven = consentInformation.canRequestAds()
                        onConsentResult(isConsentGiven)
                    } else {
                        onConsentResult(true)
                    }
                }
            } else {
                onConsentResult(true)
            }
        },
        { requestError ->
            onConsentResult(true)
        }
    )
}
fun Context.loadInterstitial(
    adID: String,
    onAdLoad: (InterstitialAd) -> Unit,
    onFailed: (String) -> Unit,
) {
    if (IS_ALL_ADS_ENABLED && isNetworkAvailable()){
        val adRequest = AdRequest.Builder().build()
        val interstitialCallback = object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interAd: InterstitialAd) {
                super.onAdLoaded(interAd)
                Log.d("admob_ads", "interstitial: Loaded")
                onAdLoad(interAd)
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                Log.d("admob_ads", "interstitial: Failed to load: ${error.message}")
                onFailed("Error: ${error.message}")
            }
        }
        InterstitialAd.load(
            this,
            adID,
            adRequest,
            interstitialCallback
        )
    }else{
        onFailed("no internet or ads disabled")
    }
}
fun Activity.showInterstitialAd(
    interstitialAd: InterstitialAd,
    onShow: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            Log.d("admob_ads", "interstitial Showing")
            isInterstitialShowing = true
            onShow(true)
        }
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            Log.d("admob_ads", "interstitial Dismissed")
            isInterstitialShowing = false
            onDismiss()
        }
        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            onShow(false)
            Log.d("admob_ads", "interstitial Failed to show")
            isInterstitialShowing = false
            onDismiss()
        }
        override fun onAdImpression() {
            super.onAdImpression()
        }
        override fun onAdClicked() {
            super.onAdClicked()
        }
    }
    if (isAppOpenAdShowing){
        onDismiss()
    }else{
        interstitialAd.show(this)
    }
}

fun Context.loadAdaptiveBanner(adID: String, onLoaded:(AdView)-> Unit, onFailed: (String) -> Unit){
    if (adID.isNotBlank() && isNetworkAvailable()) {
        val adView = AdView(this).apply {
            adUnitId = adID
            setAdSize((context as Activity).getAdSize())
            loadAd(AdRequest.Builder().build())
        }
        adView.adListener = object : AdListener(){
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.d("admob_ads", "Banner ad failed to load: ${loadAdError.message}")
                onFailed("Error: ${loadAdError.message}")
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("admob_ads", "Banner ad Loaded")
                onLoaded(adView)
            }
            override fun onAdClosed() {
                super.onAdClosed()
            }
            override fun onAdClicked() {
                super.onAdClicked()
            }
        }
    } else {
        onFailed("No Internet Connection or Ad Disabled")
    }
}
fun Context.getAdSize(): AdSize {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        ?: throw IllegalStateException("WindowManager is not available in this context")
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay?.getMetrics(displayMetrics)
    val density = displayMetrics.density
    val adWidthPixels = resources.displayMetrics.widthPixels.toFloat()
    val adWidth = (adWidthPixels / density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
}
fun Context.loadNativeAd(adID:String, onLoaded: (NativeAd) -> Unit, onFailed: (String) -> Unit){
    if (adID.isNotBlank() && isNetworkAvailable()) {
        val adLoader = AdLoader.Builder(this, adID)
            .forNativeAd { ad ->
                onLoaded(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("admob_ads", "Native Ad Failed to load: ${error.message}")
                    onFailed("Error: ${error.message}")
                }
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("admob_ads", "Native Ad Loaded")
                }

            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }else{
        onFailed("no internet or ad id is empty")
    }
}