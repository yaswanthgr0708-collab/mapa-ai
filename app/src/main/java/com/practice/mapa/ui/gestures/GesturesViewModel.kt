package com.practice.mapa.ui.gestures

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class GesturesUiState(
    val tapCount: Int = 0,
    val doubleTapCount: Int = 0,
    val longPressCount: Int = 0,
    val lastSwipe: String = "",
    val dragItems: List<String> = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5"),
    val zoomScale: Float = 1f
)

class GesturesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GesturesUiState())
    val uiState: StateFlow<GesturesUiState> = _uiState

    fun onTap()          = _uiState.update { it.copy(tapCount = it.tapCount + 1) }
    fun onDoubleTap()    = _uiState.update { it.copy(doubleTapCount = it.doubleTapCount + 1) }
    fun onLongPress()    = _uiState.update { it.copy(longPressCount = it.longPressCount + 1) }
    fun onSwipe(dir: String) = _uiState.update { it.copy(lastSwipe = dir) }
    fun onZoom(scale: Float) = _uiState.update { it.copy(zoomScale = scale) }

    fun moveItem(from: Int, to: Int) {
        val list = _uiState.value.dragItems.toMutableList()
        val item = list.removeAt(from)
        list.add(to, item)
        _uiState.update { it.copy(dragItems = list) }
    }
}
