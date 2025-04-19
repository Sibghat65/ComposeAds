package com.test.composeads.ui.screens.home_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.composeads.base.UiEffect
import com.test.composeads.base.UiEvent
import com.test.composeads.ui.screens.splash_screen.SplashUiEffect
import com.test.composeads.utils.extensions.loadAdaptiveBanner
import com.test.composeads.utils.remote_config.RemoteConfigKeys.ADAPTIVE_BANNER_AD_KEY
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel:ViewModel() {
    private val _uiState = mutableStateOf(
        HomeUiState()
    )
    val uiState: State<HomeUiState> = _uiState

    private val _effect = MutableSharedFlow<UiEffect>()
    val effect = _effect.asSharedFlow()

    fun setEvent(event: UiEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeEvent.GoCollapsibleScreen ->{
                    setEffect(HomeUiEffect.GotoCollapsibleScreen)
                }
                is HomeEvent.RequestAdaptiveBanner -> {
                    if (!uiState.value.isBannerRequested){
                        setState {copy(isBannerRequested = true,showBannerContainer = true) }
                        event.context.loadAdaptiveBanner(ADAPTIVE_BANNER_AD_KEY, onLoaded = {
                            setState { copy(adaptiveBannerView = it) }
                        }, onFailed = {
                            setState { copy(showBannerContainer = false) }
                        })
                    }
                }
                is HomeEvent.GoNativeScreen -> {
                    setEffect(HomeUiEffect.GotoNativeAdScreen)
                }
            }
        }
    }

    private fun setEffect(effect: UiEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
    private fun setState(update: HomeUiState.() -> HomeUiState) {
        _uiState.value = _uiState.value.update()
    }


}