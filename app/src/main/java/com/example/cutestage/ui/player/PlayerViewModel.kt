package com.example.cutestage.ui.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cutestage.data.scenario.TimelineToScriptConverter
import com.example.cutestage.stage.TheaterScript
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 시나리오 재생 화면의 ViewModel
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val converter: TimelineToScriptConverter,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(PlayerState())
        private set

    init {
        val scenarioId = savedStateHandle.get<String>("scenarioId")
        if (scenarioId != null) {
            loadScenario(scenarioId)
        }
    }

    private fun loadScenario(scenarioId: String) {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val script = converter.convert(scenarioId)
                state = state.copy(
                    script = script,
                    isLoading = false,
                    error = if (script == null) "시나리오를 불러올 수 없습니다" else null
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "오류가 발생했습니다"
                )
            }
        }
    }
}

data class PlayerState(
    val script: TheaterScript? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
