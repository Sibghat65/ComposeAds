package com.test.composeads.ui.screens.collapsible_ad_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.composeads.base.UiEffect
import com.test.composeads.base.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CollapsibleAdScreenViewModel:ViewModel() {
    private val _uiState = mutableStateOf(
        CollapsibleScreenUiState()
    )
    val uiState: State<CollapsibleScreenUiState> = _uiState

    private val _effect = MutableSharedFlow<UiEffect>()
    val effect = _effect.asSharedFlow()

    fun setEvent(event: UiEvent) {
        viewModelScope.launch {
            when (event) {
                is CollapsibleScreenEvent.CollapsibleBannerRequested -> {
                    setState { copy(isCollapsibleRequested = true) }
                }
                is CollapsibleScreenEvent.GoBack ->{
                    setEffect(CollapsibleScreenUiEffect.GoBack)
                }
                is CollapsibleScreenEvent.CollapsibleAdLoaded ->{
                    setState { copy(adView = event.adView, showAdContainer = showAdContainer) }
                }
            }
        }
    }

    private fun setEffect(effect: UiEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
    private fun setState(update: CollapsibleScreenUiState.() -> CollapsibleScreenUiState) {
        _uiState.value = _uiState.value.update()
    }


}