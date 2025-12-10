package com.example.cutestage.ui.scenariolist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cutestage.data.scenario.ScenarioEntity
import com.example.cutestage.data.scenario.ScenarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 시나리오 목록 화면의 ViewModel
 */
@HiltViewModel
class ScenarioListViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository
) : ViewModel() {

    // UI 상태
    var state by mutableStateOf(ScenarioListState())
        private set

    // 사용자 시나리오 목록
    val userScenarios = scenarioRepository.getUserScenarios()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 템플릿 시나리오 목록
    val templateScenarios = scenarioRepository.getTemplateScenarios()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * 삭제 다이얼로그 표시
     */
    fun showDeleteDialog(scenario: ScenarioEntity) {
        state = state.copy(
            showDeleteDialog = true,
            scenarioToDelete = scenario
        )
    }

    /**
     * 삭제 다이얼로그 닫기
     */
    fun dismissDeleteDialog() {
        state = state.copy(
            showDeleteDialog = false,
            scenarioToDelete = null
        )
    }

    /**
     * 시나리오 삭제
     */
    fun deleteScenario(scenarioId: String) {
        viewModelScope.launch {
            try {
                scenarioRepository.deleteScenario(scenarioId)
                dismissDeleteDialog()
            } catch (e: Exception) {
                state = state.copy(
                    errorMessage = e.message ?: "삭제 중 오류가 발생했습니다"
                )
            }
        }
    }

    /**
     * 에러 메시지 초기화
     */
    fun clearError() {
        state = state.copy(errorMessage = null)
    }

    /**
     * 템플릿 영역 펼침/접기 토글
     */
    fun toggleTemplateExpanded() {
        state = state.copy(isTemplateExpanded = !state.isTemplateExpanded)
    }

    /**
     * 내 시나리오 영역 펼침/접기 토글
     */
    fun toggleUserExpanded() {
        state = state.copy(isUserExpanded = !state.isUserExpanded)
    }
}

/**
 * 시나리오 목록 화면 상태
 */
data class ScenarioListState(
    val showDeleteDialog: Boolean = false,
    val scenarioToDelete: ScenarioEntity? = null,
    val errorMessage: String? = null,
    val isTemplateExpanded: Boolean = true,  // 템플릿 영역 펼침 상태
    val isUserExpanded: Boolean = true       // 내 시나리오 영역 펼침 상태
)
