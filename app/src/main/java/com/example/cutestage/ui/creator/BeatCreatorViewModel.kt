package com.example.cutestage.ui.creator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cutestage.data.scenario.ScenarioRepository
import com.example.cutestage.stage.CharacterGender
import com.example.cutestage.stage.beat.Beat
import com.example.cutestage.stage.beat.BeatJsonHelper
import com.example.cutestage.stage.beat.CharacterInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Beat 기반 시나리오 생성 화면의 ViewModel
 */
@HiltViewModel
class BeatCreatorViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository
) : ViewModel() {

    // UI 상태
    var state by mutableStateOf(BeatCreatorState())
        private set

    /**
     * 캐릭터 추가
     */
    fun addCharacter(name: String, gender: CharacterGender) {
        val characterId = "char_${UUID.randomUUID().toString().take(8)}"
        val newCharacter = CharacterInfo(
            id = characterId,
            name = name,
            gender = gender
        )
        state = state.copy(
            characters = state.characters + newCharacter
        )
    }

    /**
     * 캐릭터 제거
     */
    fun removeCharacter(characterId: String) {
        state = state.copy(
            characters = state.characters.filter { it.id != characterId }
        )
    }

    /**
     * Beat 추가
     */
    fun addBeat(beat: Beat) {
        state = state.copy(
            beats = state.beats + beat
        )
    }

    /**
     * Beat 제거
     */
    fun removeBeat(index: Int) {
        state = state.copy(
            beats = state.beats.filterIndexed { i, _ -> i != index }
        )
    }

    /**
     * Beat 순서 변경
     */
    fun reorderBeats(from: Int, to: Int) {
        val newList = state.beats.toMutableList()
        val item = newList.removeAt(from)
        newList.add(to, item)
        state = state.copy(beats = newList)
    }

    /**
     * 템플릿 카테고리 선택
     */
    fun selectTemplateCategory(category: BeatTemplateCategory) {
        state = state.copy(selectedTemplateCategory = category)
    }

    /**
     * 캐릭터 추가 다이얼로그 표시
     */
    fun showAddCharacterDialog() {
        state = state.copy(showAddCharacterDialog = true)
    }

    /**
     * 캐릭터 추가 다이얼로그 닫기
     */
    fun dismissAddCharacterDialog() {
        state = state.copy(showAddCharacterDialog = false)
    }

    /**
     * 저장 다이얼로그 표시
     */
    fun showSaveDialog() {
        state = state.copy(showSaveDialog = true)
    }

    /**
     * 저장 다이얼로그 닫기
     */
    fun dismissSaveDialog() {
        state = state.copy(
            showSaveDialog = false,
            saveDialogTitle = "",
            saveDialogDescription = ""
        )
    }

    /**
     * 저장 다이얼로그 제목 업데이트
     */
    fun updateSaveDialogTitle(title: String) {
        state = state.copy(saveDialogTitle = title)
    }

    /**
     * 저장 다이얼로그 설명 업데이트
     */
    fun updateSaveDialogDescription(description: String) {
        state = state.copy(saveDialogDescription = description)
    }

    /**
     * 미리보기 표시
     */
    fun showPreview() {
        // TODO: 미리보기 기능 구현
        state = state.copy(showPreview = true)
    }

    /**
     * 시나리오 저장
     */
    fun saveScenario(onSuccess: (String) -> Unit) {
        val title = state.saveDialogTitle.trim()
        if (title.isEmpty()) {
            state = state.copy(errorMessage = "제목을 입력해주세요")
            return
        }

        if (state.beats.isEmpty()) {
            state = state.copy(errorMessage = "비트를 추가해주세요")
            return
        }

        state = state.copy(isSaving = true)
        viewModelScope.launch {
            try {
                // Beat 리스트를 JSON으로 변환하여 저장
                val beatsJson = BeatJsonHelper.fromBeatList(state.beats)
                val charactersJson = com.google.gson.Gson().toJson(state.characters)

                // Beat 데이터를 description에 임베드 (JSON 형식)
                val beatData = mapOf(
                    "type" to "beat",
                    "description" to state.saveDialogDescription,
                    "beats" to beatsJson,
                    "characters" to charactersJson
                )
                val descriptionWithBeatData = com.google.gson.Gson().toJson(beatData)

                // ScenarioEntity로 저장
                val scenarioId = UUID.randomUUID().toString()
                val scenario = com.example.cutestage.data.scenario.ScenarioEntity(
                    id = scenarioId,
                    title = title,
                    description = descriptionWithBeatData, // Beat 데이터 임베드
                    moduleCount = state.beats.size,
                    estimatedDuration = state.beats.sumOf { it.duration.toInt() },
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                // Repository를 통해 저장
                scenarioRepository.saveBeatScenario(scenario)

                state = state.copy(
                    isSaving = false,
                    showSaveDialog = false,
                    showSaveSuccess = true
                )

                // 성공 콜백 호출
                onSuccess(scenarioId)
            } catch (e: Exception) {
                state = state.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "저장 중 오류가 발생했습니다"
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
}

/**
 * Beat Creator 화면 상태
 */
data class BeatCreatorState(
    val characters: List<CharacterInfo> = emptyList(),
    val beats: List<Beat> = emptyList(),
    val selectedTemplateCategory: BeatTemplateCategory = BeatTemplateCategory.MEETING,
    val showAddCharacterDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val saveDialogTitle: String = "",
    val saveDialogDescription: String = "",
    val isSaving: Boolean = false,
    val showSaveSuccess: Boolean = false,
    val showPreview: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Beat 템플릿 카테고리
 */
enum class BeatTemplateCategory(val displayName: String, val emoji: String) {
    MEETING("만남", "👋"),
    CONFLICT("갈등", "⚡"),
    EMOTION("감정", "❤️"),
    FAREWELL("작별", "👋"),
    SOLO("단독", "🎭");
}
