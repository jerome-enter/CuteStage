package com.example.cutestage.ui.creator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cutestage.data.scenario.ScenarioRepository
import com.example.cutestage.stage.CharacterGender
import com.example.cutestage.stage.beat.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * 레이어 기반 Beat Creator ViewModel
 */
@HiltViewModel
class LayeredBeatCreatorViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository,
    private val characterLibraryRepository: com.example.cutestage.data.character.CharacterLibraryRepository
) : ViewModel() {

    var state by mutableStateOf(LayeredBeatCreatorState())
        private set

    // 편집 모드에서 로드된 시나리오 ID (덮어쓰기 용)
    private var loadedScenarioId: String? = null

    init {
        // 캐릭터 라이브러리 로드
        viewModelScope.launch {
            characterLibraryRepository.initializeDefaultCharacters()
            characterLibraryRepository.getAllCharacters().collect { libraryCharacters ->
                state = state.copy(characterLibrary = libraryCharacters)
            }
        }
    }

    // ==================== 캐릭터 관리 ====================

    fun showCharacterLibraryDialog() {
        state = state.copy(showCharacterLibraryDialog = true)
    }

    fun dismissCharacterLibraryDialog() {
        state = state.copy(showCharacterLibraryDialog = false)
    }

    fun addCharacterFromLibrary(libraryCharacter: com.example.cutestage.data.character.CharacterLibraryEntity) {
        viewModelScope.launch {
            // 사용 기록
            characterLibraryRepository.recordUsage(libraryCharacter.id)

            // CharacterInfo로 변환하여 추가
            val characterInfo = characterLibraryRepository.toCharacterInfo(libraryCharacter)

            // 중복 체크
            if (state.characters.none { it.id == characterInfo.id }) {
                state = state.copy(
                    characters = state.characters + characterInfo
                )
            }
        }
    }

    fun addCharacter(name: String, gender: CharacterGender) {
        val characterId = "char_${UUID.randomUUID().toString().take(8)}"
        val newCharacter = CharacterInfo(
            id = characterId,
            name = name,
            gender = gender
        )

        // 라이브러리에도 저장
        viewModelScope.launch {
            characterLibraryRepository.saveCharacter(name, gender)
        }

        state = state.copy(
            characters = state.characters + newCharacter
        )
    }

    fun removeCharacter(characterId: String) {
        state = state.copy(
            characters = state.characters.filter { it.id != characterId }
        )
    }

    fun showAddCharacterDialog() {
        state = state.copy(showAddCharacterDialog = true)
    }

    fun dismissAddCharacterDialog() {
        state = state.copy(showAddCharacterDialog = false)
    }

    // ==================== Beat 관리 ====================

    fun createNewBeat() {
        val newBeat = LayeredBeat(
            id = "beat_${UUID.randomUUID().toString().take(8)}",
            name = "새 비트 ${state.beats.size + 1}",
            duration = 5f
        )
        state = state.copy(
            beats = state.beats + newBeat,
            selectedBeatIndex = state.beats.size
        )
    }

    fun selectBeat(index: Int) {
        state = state.copy(selectedBeatIndex = index)
    }

    fun removeBeat(index: Int) {
        val newBeats = state.beats.filterIndexed { i, _ -> i != index }
        val currentSelected = state.selectedBeatIndex
        val newSelectedIndex = if (currentSelected != null && currentSelected >= newBeats.size) {
            (newBeats.size - 1).coerceAtLeast(0)
        } else {
            currentSelected
        }
        state = state.copy(
            beats = newBeats,
            selectedBeatIndex = if (newBeats.isEmpty()) null else newSelectedIndex
        )
    }

    fun updateBeatName(index: Int, name: String) {
        val updatedBeats = state.beats.toMutableList()
        updatedBeats[index] = updatedBeats[index].copy(name = name)
        state = state.copy(beats = updatedBeats)
    }

    // ==================== 레이어 탭 선택 ====================

    fun selectLayerTab(tab: LayerTab) {
        state = state.copy(selectedLayerTab = tab)
    }

    // ==================== 장소 레이어 ====================

    fun updateLocation(beatIndex: Int, location: StageLocation) {
        val updatedBeats = state.beats.toMutableList()
        val beat = updatedBeats[beatIndex]
        updatedBeats[beatIndex] = beat.copy(
            locationLayer = beat.locationLayer.copy(location = location)
        )
        state = state.copy(beats = updatedBeats)
    }

    // ==================== 대사 레이어 ====================

    fun showAddDialogueDialog() {
        state = state.copy(showAddDialogueDialog = true)
    }

    fun dismissAddDialogueDialog() {
        state = state.copy(
            showAddDialogueDialog = false,
            dialogueEditState = DialogueEditState()
        )
    }

    fun updateDialogueEditCharacter(characterId: String) {
        val character = state.characters.find { it.id == characterId }
        state = state.copy(
            dialogueEditState = state.dialogueEditState.copy(
                selectedCharacterId = characterId,
                selectedCharacterName = character?.name ?: ""
            )
        )
    }

    fun updateDialogueEditText(text: String) {
        state = state.copy(
            dialogueEditState = state.dialogueEditState.copy(text = text)
        )
    }

    fun updateDialogueEditEmotion(emotion: DialogueEmotion) {
        state = state.copy(
            dialogueEditState = state.dialogueEditState.copy(emotion = emotion)
        )
    }

    fun updateDialogueEditAction(action: DialogueActionType?) {
        state = state.copy(
            dialogueEditState = state.dialogueEditState.copy(action = action)
        )
    }

    fun addDialogue() {
        val beatIndex = state.selectedBeatIndex ?: return
        val editState = state.dialogueEditState

        if (editState.selectedCharacterId.isEmpty() || editState.text.isEmpty()) {
            state = state.copy(errorMessage = "캐릭터와 대사를 입력해주세요")
            return
        }

        val updatedBeats = state.beats.toMutableList()
        val beat = updatedBeats[beatIndex]

        // 기존 대사들의 총 재생 시간 계산 (자동 타이밍)
        val previousDialogues = beat.dialogueLayer.dialogues
        val autoStartTime = if (previousDialogues.isEmpty()) {
            0.5f  // 첫 대사는 0.5초 후 시작
        } else {
            previousDialogues.sumOf { it.calculateDuration().toDouble() + 0.5 }
                .toFloat()  // 각 대사 후 0.5초 여백
        }

        val newDialogue = DialogueEntry(
            characterId = editState.selectedCharacterId,
            characterName = editState.selectedCharacterName,
            text = editState.text,
            emotion = editState.emotion,
            startTime = autoStartTime, // 자동 계산된 시작 시간
            action = editState.action
        )

        updatedBeats[beatIndex] = beat.copy(
            dialogueLayer = beat.dialogueLayer.copy(
                dialogues = beat.dialogueLayer.dialogues + newDialogue
            )
        )

        state = state.copy(
            beats = updatedBeats,
            showAddDialogueDialog = false,
            dialogueEditState = DialogueEditState()
        )
    }

    /**
     * 인라인 편집기에서 대사 추가
     */
    fun addDialogueInline(
        beatIndex: Int,
        characterId: String,
        text: String,
        emotion: DialogueEmotion,
        action: DialogueActionType?
    ) {
        if (characterId.isEmpty() || text.isEmpty()) {
            state = state.copy(errorMessage = "캐릭터와 대사를 입력해주세요")
            return
        }

        val character = state.characters.find { it.id == characterId } ?: return

        val updatedBeats = state.beats.toMutableList()
        val beat = updatedBeats[beatIndex]

        // 기존 대사들의 총 재생 시간 계산 (자동 타이밍)
        val previousDialogues = beat.dialogueLayer.dialogues
        val autoStartTime = if (previousDialogues.isEmpty()) { 0.5f } else { previousDialogues.sumOf { it.calculateDuration().toDouble() + 0.5 }.toFloat() }

        val newDialogue = DialogueEntry(
            characterId = characterId,
            characterName = character.name,
            text = text,
            emotion = emotion,
            startTime = autoStartTime, // 자동 계산된 시작 시간
            action = action
        )

        updatedBeats[beatIndex] = beat.copy(
            dialogueLayer = beat.dialogueLayer.copy(
                dialogues = beat.dialogueLayer.dialogues + newDialogue
            )
        )

        state = state.copy(beats = updatedBeats)
    }

    fun removeDialogue(beatIndex: Int, dialogueId: String) {
        val updatedBeats = state.beats.toMutableList()
        val beat = updatedBeats[beatIndex]

        // 삭제 후 남은 대사들의 시작 시간 재계산
        val filteredDialogues = beat.dialogueLayer.dialogues
            .filter { it.id != dialogueId }
            .sortedBy { it.startTime }
        
        // 시작 시간 재계산
        var cumulativeTime = 0f
        val recalculatedDialogues = filteredDialogues.map { dialogue ->
            val updated = dialogue.copy(startTime = cumulativeTime)
            cumulativeTime += dialogue.calculateDuration()
            updated
        }

        updatedBeats[beatIndex] = beat.copy(
            dialogueLayer = beat.dialogueLayer.copy(
                dialogues = recalculatedDialogues
            )
        )
        state = state.copy(beats = updatedBeats)
    }

    // ==================== 이동 레이어 ====================

    fun showAddMovementDialog() {
        state = state.copy(showAddMovementDialog = true)
    }

    fun dismissAddMovementDialog() {
        state = state.copy(
            showAddMovementDialog = false,
            movementEditState = MovementEditState()
        )
    }

    fun updateMovementEditCharacter(characterId: String) {
        state = state.copy(
            movementEditState = state.movementEditState.copy(selectedCharacterId = characterId)
        )
    }

    fun updateMovementEditPosition(position: StagePosition) {
        state = state.copy(
            movementEditState = state.movementEditState.copy(position = position)
        )
    }

    fun updateMovementEditStartTime(startTime: Float) {
        state = state.copy(
            movementEditState = state.movementEditState.copy(startTime = startTime)
        )
    }

    fun addMovement() {
        val beatIndex = state.selectedBeatIndex ?: return
        val editState = state.movementEditState

        if (editState.selectedCharacterId.isEmpty()) {
            state = state.copy(errorMessage = "캐릭터를 선택해주세요")
            return
        }

        val newMovement = MovementEntry(
            characterId = editState.selectedCharacterId,
            fromPosition = null, // 다이얼로그에서는 fromPosition 미지원 (향후 추가 가능)
            toPosition = editState.position,
            startTime = editState.startTime,
            endTime = editState.startTime + 1f, // 기본 1초 소요
            autoWalk = true
        )

        val updatedBeats = state.beats.toMutableList()
        val beat = updatedBeats[beatIndex]
        updatedBeats[beatIndex] = beat.copy(
            movementLayer = beat.movementLayer.copy(
                movements = beat.movementLayer.movements + newMovement
            )
        )

        state = state.copy(
            beats = updatedBeats,
            showAddMovementDialog = false,
            movementEditState = MovementEditState()
        )
    }

    /**
     * 인라인 편집기에서 이동 추가
     */
    fun addMovementInline(
        beatIndex: Int,
        characterId: String,
        fromPosition: StagePosition?,
        toPosition: StagePosition,
        startTime: Float,
        endTime: Float,
        facingDirection: FacingDirection = FacingDirection.RIGHT
    ) {
        if (characterId.isEmpty()) {
            state = state.copy(errorMessage = "캐릭터를 선택해주세요")
            return
        }

        if (endTime <= startTime) {
            state = state.copy(errorMessage = "끝 시간이 시작 시간보다 늦어야 합니다")
            return
        }

        val newMovement = MovementEntry(
            characterId = characterId,
            fromPosition = fromPosition,
            toPosition = toPosition,
            startTime = startTime,
            endTime = endTime,
            autoWalk = true,
            facingDirection = facingDirection
        )

        val updatedBeats = state.beats.toMutableList()
        val beat = updatedBeats[beatIndex]
        updatedBeats[beatIndex] = beat.copy(
            movementLayer = beat.movementLayer.copy(
                movements = beat.movementLayer.movements + newMovement
            )
        )

        state = state.copy(beats = updatedBeats)
    }

    fun removeMovement(beatIndex: Int, movementId: String) {
        val updatedBeats = state.beats.toMutableList()
        val beat = updatedBeats[beatIndex]
        updatedBeats[beatIndex] = beat.copy(
            movementLayer = beat.movementLayer.copy(
                movements = beat.movementLayer.movements.filter { it.id != movementId }
            )
        )
        state = state.copy(beats = updatedBeats)
    }

    // ==================== 저장 ====================

    fun showSaveDialog() {
        state = state.copy(showSaveDialog = true)
    }

    fun dismissSaveDialog() {
        state = state.copy(
            showSaveDialog = false,
            saveDialogTitle = "",
            saveDialogDescription = ""
        )
    }

    fun updateSaveDialogTitle(title: String) {
        state = state.copy(saveDialogTitle = title)
    }

    fun updateSaveDialogDescription(description: String) {
        state = state.copy(saveDialogDescription = description)
    }

    fun saveScenario(overwrite: Boolean = false, onSuccess: (String) -> Unit) {
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
                // ✅ LayeredBeat를 직접 JSON으로 직렬화 (Classic Beat 변환 없음!)
                val gson = com.google.gson.Gson()
                val layeredBeatsJson = gson.toJson(state.beats)
                val charactersJson = gson.toJson(state.characters)

                val beatData = mapOf(
                    "type" to "layered_beat_v2",  // ✅ 새 버전 표시
                    "description" to state.saveDialogDescription,
                    "layeredBeats" to layeredBeatsJson,  // ✅ LayeredBeat 직접 저장
                    "characters" to charactersJson
                )
                val descriptionWithBeatData = gson.toJson(beatData)

                // 덮어쓰기면 기존 ID 사용, 새로저장이면 새 ID 생성
                val scenarioId = if (overwrite && loadedScenarioId != null) {
                    loadedScenarioId!!
                } else {
                    UUID.randomUUID().toString()
                }

                val scenario = com.example.cutestage.data.scenario.ScenarioEntity(
                    id = scenarioId,
                    title = title,
                    description = descriptionWithBeatData,
                    moduleCount = state.beats.size,
                    estimatedDuration = state.beats.sumOf { it.duration.toInt() },
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                scenarioRepository.saveBeatScenario(scenario)

                state = state.copy(
                    isSaving = false,
                    showSaveDialog = false
                )

                onSuccess(scenarioId)
            } catch (e: Exception) {
                state = state.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "저장 중 오류가 발생했습니다"
                )
            }
        }
    }

    // ==================== 시나리오 로드 (편집 모드) ====================

    fun loadScenario(scenarioId: String) {
        // 편집 모드이므로 시나리오 ID 저장 (덮어쓰기 용)
        loadedScenarioId = scenarioId

        viewModelScope.launch {
            try {
                val scenario = scenarioRepository.getScenarioById(scenarioId) ?: return@launch

                // description에서 Beat 데이터 파싱
                val gson = com.google.gson.Gson()
                val beatDataMap = try {
                    gson.fromJson(
                        scenario.description,
                        Map::class.java
                    ) as? Map<String, Any>
                } catch (e: Exception) {
                    null
                }

                if (beatDataMap == null) {
                    state = state.copy(errorMessage = "시나리오 데이터를 읽을 수 없습니다")
                    return@launch
                }

                val dataType = beatDataMap["type"] as? String

                // 캐릭터 복원
                val charactersJson = beatDataMap["characters"] as? String ?: "[]"
                val characters = try {
                    gson.fromJson(
                        charactersJson,
                        Array<CharacterInfo>::class.java
                    ).toList()
                } catch (e: Exception) {
                    emptyList()
                }

                // Beat 복원 (버전에 따라 다르게 처리)
                val layeredBeats: List<LayeredBeat> = when (dataType) {
                    "layered_beat_v2" -> {
                        // ✅ 새 버전: LayeredBeat 직접 로드
                        val layeredBeatsJson = beatDataMap["layeredBeats"] as? String ?: "[]"
                        println("Debug_Load v2 로드 중...")
                        try {
                            val result = gson.fromJson(
                                layeredBeatsJson,
                                Array<LayeredBeat>::class.java
                            ).toList()
                            println("Debug_Load v2 성공: ${result.size}개 비트")
                            result.forEachIndexed { index, beat ->
                                println("Debug_Load 비트[$index]: 이동 ${beat.movementLayer.movements.size}개, 대사 ${beat.dialogueLayer.dialogues.size}개")
                            }
                            result
                        } catch (e: Exception) {
                            println("Debug_Load v2 실패: ${e.message}")
                            e.printStackTrace()
                            state = state.copy(errorMessage = "v2 로드 실패: ${e.message}")
                            return@launch
                        }
                    }

                    "layered_beat" -> {
                        // ❌ 구 버전: Classic Beat 경유 (데이터 손실 있음)
                        println("Debug_Load v1 로드 (구버전)")
                        val beatsJson = beatDataMap["beats"] as? String ?: "[]"
                        val classicBeats = BeatJsonHelper.toBeatList(beatsJson)
                        LayeredBeatConverter.fromClassicBeats(classicBeats, characters)
                    }

                    else -> {
                        println("Debug_Load 알 수 없는 타입: $dataType")
                        state = state.copy(errorMessage = "알 수 없는 시나리오 타입: $dataType")
                        return@launch
                    }
                }

                // State 업데이트
                state = state.copy(
                    characters = characters,
                    beats = layeredBeats,
                    saveDialogTitle = scenario.title,
                    saveDialogDescription = beatDataMap["description"] as? String ?: "",
                    selectedBeatIndex = if (layeredBeats.isNotEmpty()) 0 else null
                )

            } catch (e: Exception) {
                state = state.copy(errorMessage = "시나리오 로드 중 오류: ${e.message}")
            }
        }
    }

    // ==================== 에러 관리 ====================

    fun clearError() {
        state = state.copy(errorMessage = null)
    }

    // ==================== 섹션 접기/펼치기 ====================

    fun toggleCharacterSection() {
        state = state.copy(isCharacterSectionExpanded = !state.isCharacterSectionExpanded)
    }

    fun toggleBeatTimelineSection() {
        state = state.copy(isBeatTimelineSectionExpanded = !state.isBeatTimelineSectionExpanded)
    }
}

// ==================== State ====================

data class LayeredBeatCreatorState(
    // 캐릭터
    val characters: List<CharacterInfo> = emptyList(),
    val characterLibrary: List<com.example.cutestage.data.character.CharacterLibraryEntity> = emptyList(),
    val showAddCharacterDialog: Boolean = false,
    val showCharacterLibraryDialog: Boolean = false,

    // Beat 리스트
    val beats: List<LayeredBeat> = emptyList(),
    val selectedBeatIndex: Int? = null,

    // 레이어 탭
    val selectedLayerTab: LayerTab = LayerTab.LOCATION,

    // 섹션 접기/펼치기
    val isCharacterSectionExpanded: Boolean = true,
    val isBeatTimelineSectionExpanded: Boolean = true,

    // 대사 편집
    val showAddDialogueDialog: Boolean = false,
    val dialogueEditState: DialogueEditState = DialogueEditState(),

    // 이동 편집
    val showAddMovementDialog: Boolean = false,
    val movementEditState: MovementEditState = MovementEditState(),

    // 저장
    val showSaveDialog: Boolean = false,
    val saveDialogTitle: String = "",
    val saveDialogDescription: String = "",
    val isSaving: Boolean = false,

    // 에러
    val errorMessage: String? = null
)

enum class LayerTab(val displayName: String, val emoji: String) {
    LOCATION("장소", "📍"),
    DIALOGUE("대사", "💬"),
    MOVEMENT("이동", "🚶")
}

data class DialogueEditState(
    val selectedCharacterId: String = "",
    val selectedCharacterName: String = "",
    val text: String = "",
    val emotion: DialogueEmotion = DialogueEmotion.CALM,
    val action: DialogueActionType? = null
)

data class MovementEditState(
    val selectedCharacterId: String = "",
    val position: StagePosition = StagePosition.CENTER,
    val startTime: Float = 0f
)
