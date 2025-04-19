package com.test.composeads.ui.screens.splash_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.composeads.base.UiEffect
import com.test.composeads.base.UiEvent
import com.test.composeads.utils.extensions.loadInterstitial
import com.test.composeads.utils.remote_config.RemoteConfigKeys.SPLASH_INTERSTITIAL_AD_KEY
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel:ViewModel() {
    private val _uiState = mutableStateOf(
        SplashUiState()
    )
    val uiState: State<SplashUiState> = _uiState

    private val _effect = MutableSharedFlow<UiEffect>()
    val effect = _effect.asSharedFlow()

    fun setEvent(event: UiEvent) {
        viewModelScope.launch {
            when (event) {
                is SplashEvent.RequestAd -> {
                    if (!uiState.value.isInterstitialRequested){
                        setState {copy(isInterstitialRequested = true) }
                        event.context.loadInterstitial(SPLASH_INTERSTITIAL_AD_KEY, onAdLoad = {
                            setState { copy(splashInterstitialAd = it, showGetStarted = true) }
                        }, onFailed = {
                            setState { copy(showGetStarted = true) }
                        })
                    }
                }
                is SplashEvent.NextScreen -> {
                    setEffect(SplashUiEffect.GotoHome)
                }
            }
        }
    }

    private fun setEffect(effect: UiEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
    private fun setState(update: SplashUiState.() -> SplashUiState) {
        _uiState.value = _uiState.value.update()
    }


}