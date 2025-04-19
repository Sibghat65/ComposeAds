package com.test.composeads.ui.screens.native_ad_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.composeads.base.UiEffect
import com.test.composeads.base.UiEvent
import com.test.composeads.utils.extensions.loadNativeAd
import com.test.composeads.utils.remote_config.RemoteConfigKeys.NATIVE_AD_KEY
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NativeAdScreenViewModel:ViewModel() {
    private val _uiState = mutableStateOf(
        NativeScreenUiState()
    )
    val uiState: State<NativeScreenUiState> = _uiState

    private val _effect = MutableSharedFlow<UiEffect>()
    val effect = _effect.asSharedFlow()

    fun setEvent(event: UiEvent) {
        viewModelScope.launch {
            when (event) {
                is NativeScreenEvent.RequestNativeAd -> {
                    if (!uiState.value.isNativeAdRequested){
                        setState {copy(isNativeAdRequested = true,showNativeAdContainer = true) }
                        event.context.loadNativeAd(NATIVE_AD_KEY, onLoaded = {
                            setState { copy(nativeAd = it) }
                        }, onFailed = {
                            setState { copy(showNativeAdContainer = false) }
                        })
                    }
                }
                is NativeScreenEvent.GoBack ->{
                    setEffect(NativeScreenUiEffect.GoBack)
                }
            }
        }
    }

    private fun setEffect(effect: UiEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
    private fun setState(update: NativeScreenUiState.() -> NativeScreenUiState) {
        _uiState.value = _uiState.value.update()
    }


}