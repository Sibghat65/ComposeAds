package com.test.composeads.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.test.composeads.admob_ads.AppOpenAd.isAppOpenAdShowing
import com.test.composeads.admob_ads.AppOpenAd.isInterstitialShowing
import com.test.composeads.admob_ads.AppOpenAd.showAppOpenOnResume
import com.test.composeads.ui.common.LottieWithLoadingText
import com.test.composeads.ui.screens.collapsible_ad_screen.CollapsibleAdScreen
import com.test.composeads.ui.screens.collapsible_ad_screen.CollapsibleAdScreenViewModel
import com.test.composeads.ui.screens.collapsible_ad_screen.CollapsibleScreenUiEffect
import com.test.composeads.ui.screens.home_screen.HomeScreen
import com.test.composeads.ui.screens.home_screen.HomeUiEffect
import com.test.composeads.ui.screens.home_screen.HomeViewModel
import com.test.composeads.ui.screens.native_ad_screen.NativeAdScreen
import com.test.composeads.ui.screens.native_ad_screen.NativeAdScreenViewModel
import com.test.composeads.ui.screens.native_ad_screen.NativeScreenUiEffect
import com.test.composeads.ui.screens.splash_screen.SplashScreen
import com.test.composeads.ui.screens.splash_screen.SplashUiEffect
import com.test.composeads.ui.screens.splash_screen.SplashViewModel
import com.test.composeads.ui.theme.ComposeAdsTheme
import com.test.composeads.utils.extensions.isNetworkAvailable
import com.test.composeads.utils.remote_config.RemoteConfigKeys.APP_OPEN_ON_RESUME_AD_KEY
import com.test.composeads.utils.remote_config.RemoteConfigKeys.IS_ALL_ADS_ENABLED
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.ext.getFullName


class MainActivity : ComponentActivity() {
    private var wasAppOnBackground: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val lifecycleOwner = LocalLifecycleOwner.current
            var showAppOpenLoading by remember { mutableStateOf(false) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_STOP -> {
                            if (!isAppOpenAdShowing && !isInterstitialShowing) {
                                val currentScreen = navBackStackEntry?.destination?.route
                                when(currentScreen){
                                    HomeScreen::class.getFullName(),CollapsibleAdScreen::class.getFullName()->{
                                        wasAppOnBackground = true
                                    }
                                }
                            }
                        }
                        Lifecycle.Event.ON_RESUME -> {
                            val currentScreen = navBackStackEntry?.destination?.route
                            //show app open on screens where you want
                            when(currentScreen){
                                HomeScreen::class.getFullName(),CollapsibleAdScreen::class.getFullName()->{
                                    if (wasAppOnBackground) {
                                        if (IS_ALL_ADS_ENABLED && APP_OPEN_ON_RESUME_AD_KEY.isNotBlank() && isNetworkAvailable()) {
                                            showAppOpenLoading = true
                                            showAppOpenOnResume(onAlreadyShowing = {
                                                showAppOpenLoading = false
                                                wasAppOnBackground = false
                                            }, onDismiss = {
                                                showAppOpenLoading = false
                                                wasAppOnBackground = false
                                            })
                                        }
                                    }
                                }
                            }
                        }

                        else -> Unit
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }

            ComposeAdsTheme {
                if(showAppOpenLoading){
                    LottieWithLoadingText()
                }else{
                    NavHost(navController = navController, startDestination = SplashScreen) {
                        composable<SplashScreen> {
                            val viewModel: SplashViewModel = koinViewModel()
                            LaunchedEffect(true) {
                                viewModel.effect.collectLatest {
                                    when (it) {
                                        SplashUiEffect.GotoHome -> {
                                            navController.navigate(HomeScreen) {
                                                popUpTo<SplashScreen> { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            }
                            SplashScreen(
                                uiState = viewModel.uiState.value,
                                onEvent = viewModel::setEvent
                            )
                        }
                        composable<HomeScreen> {
                            val viewModel: HomeViewModel = koinViewModel()
                            LaunchedEffect(true) {
                                viewModel.effect.collectLatest {
                                    when (it) {
                                        HomeUiEffect.GotoNativeAdScreen -> {
                                            navController.navigate(NativeAdScreen)
                                        }
                                        HomeUiEffect.GotoCollapsibleScreen -> {
                                            navController.navigate(CollapsibleAdScreen)
                                        }
                                    }
                                }
                            }
                            HomeScreen(
                                uiState = viewModel.uiState.value,
                                onEvent = viewModel::setEvent
                            )
                        }
                        composable<NativeAdScreen> {
                            val viewModel: NativeAdScreenViewModel = koinViewModel()
                            LaunchedEffect(true) {
                                viewModel.effect.collectLatest {
                                    when (it) {
                                        NativeScreenUiEffect.GoBack -> {
                                            navController.navigateUp()
                                        }
                                    }
                                }
                            }
                            NativeAdScreen(
                                uiState = viewModel.uiState.value,
                                onEvent = viewModel::setEvent
                            )
                        }
                        composable<CollapsibleAdScreen> {
                            val viewModel: CollapsibleAdScreenViewModel = koinViewModel()
                            LaunchedEffect(true) {
                                viewModel.effect.collectLatest {
                                    when (it) {
                                        CollapsibleScreenUiEffect.GoBack -> {
                                            navController.navigateUp()
                                        }
                                    }
                                }
                            }
                            CollapsibleAdScreen(
                                uiState = viewModel.uiState.value,
                                onEvent = viewModel::setEvent
                            )
                        }
                    }
                }
            }
        }
    }
}


@Serializable
object SplashScreen

@Serializable
object HomeScreen

@Serializable
object NativeAdScreen

@Serializable
object CollapsibleAdScreen

