package com.example.cutestage.stage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cutestage.stage.repository.ScenarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * StageView의 상태와 비즈니스 로직을 관리하는 ViewModel
 *
 * Hilt를 사용하여 의존성을 주입받습니다.
 */
@HiltViewModel
class StageViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository
) : ViewModel() {

    var state by mutableStateOf(
        StageState(
            currentScript = StageTestScenario.createTestScript() // PLAYGROUND로 초기화
        )
    )
        private set

    // onScriptEnd 콜백은 외부에서 설정 가능
    private var onScriptEndCallback: (() -> Unit)? = null

    init {
        // 초기 시나리오를 PLAYGROUND로 설정
        StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
    }

    fun setOnScriptEnd(callback: () -> Unit) {
        onScriptEndCallback = callback
    }

    fun setInitialScript(script: TheaterScript?) {
        if (script != null) {
            // 새 스크립트로 상태 완전 초기화
            state = state.copy(
                currentScript = script,
                playbackState = PlaybackState(),
                interactionState = InteractionState(),
                choiceState = ChoiceState(),
                aiGenerationState = AIGenerationState()
            )
        }
    }

    /**
     * 이벤트 핸들러
     */
    fun handleEvent(event: StageEvent) {
        when (event) {
            // 재생 제어
            is StageEvent.Play -> handlePlay()
            is StageEvent.Stop -> handleStop()
            is StageEvent.ChangePlaybackSpeed -> handleChangeSpeed(event.speed)

            // 시나리오 제어
            is StageEvent.LoadScenario -> handleLoadScenario(event)

            // 캐릭터 상호작용
            is StageEvent.CharacterClick -> handleCharacterClick(event.character)
            is StageEvent.DismissInteraction -> handleDismissInteraction()

            // 선택지
            is StageEvent.SelectChoice -> handleSelectChoice(event.nextSceneIndex)

            // AI 생성
            is StageEvent.ShowAIDialog -> handleShowAIDialog()
            is StageEvent.HideAIDialog -> handleHideAIDialog()
            is StageEvent.UpdateAIInput -> handleUpdateAIInput(event.input)
            is StageEvent.GenerateAIScenario -> handleGenerateAI()

            // 타임라인
            is StageEvent.AdvanceScene -> handleAdvanceScene()
            is StageEvent.ScriptEnded -> handleScriptEnded()
            is StageEvent.DetectChoice -> handleDetectChoice(event.choices)
        }
    }

    // ==================== 재생 제어 ====================

    private fun handlePlay() {
        if (state.currentScript != null) {
            state = state.copy(
                playbackState = state.playbackState.copy(
                    currentSceneIndex = 0,
                    isPlaying = true
                )
            )
        }
    }

    private fun handleStop() {
        state = state.copy(
            playbackState = state.playbackState.copy(isPlaying = false)
        )
        // PLAYGROUND로 복귀
        StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
        state = state.copy(
            currentScript = StageTestScenario.createTestScript(),
            playbackState = PlaybackState()
        )
    }

    private fun handleChangeSpeed(speed: Float) {
        state = state.copy(
            playbackState = state.playbackState.copy(speed = speed)
        )
    }

    // ==================== 시나리오 제어 ====================

    private fun handleLoadScenario(event: StageEvent.LoadScenario) {
        state = state.copy(
            currentScript = event.script,
            playbackState = PlaybackState(
                isPlaying = event.shouldPlay,
                speed = state.playbackState.speed
            )
        )
    }

    // ==================== 캐릭터 상호작용 ====================

    private fun handleCharacterClick(character: CharacterState) {
        if (state.playbackState.isPlaying) return

        val currentTime = System.currentTimeMillis()
        var interaction = state.interactionState

        // 5초 이상 지났으면 리셋
        if (currentTime - interaction.lastClickTime > 5000) {
            interaction = InteractionState()
        }

        // 성별 판단
        val isMale = character.spriteAnimation?.gender == CharacterGender.MALE ||
                character.id.contains("male", ignoreCase = true) ||
                character.name.contains("상철", ignoreCase = true)

        // 클릭 횟수 증가
        val newMaleCount =
            if (isMale) interaction.maleClickCount + 1 else interaction.maleClickCount
        val newFemaleCount =
            if (!isMale) interaction.femaleClickCount + 1 else interaction.femaleClickCount

        // 감정 시스템
        val clickCount = if (isMale) newMaleCount else newFemaleCount
        val emotionalDialogue = CharacterInteractionSystem.getEmotionalDialogue(
            clickCount = clickCount,
            isMale = isMale
        )

        // 화난 상태 추적
        var newMaleAngryCount = interaction.maleAngryCount
        var newFemaleAngryCount = interaction.femaleAngryCount

        if (emotionalDialogue.emotion == CharacterInteractionSystem.EmotionType.ANGRY) {
            if (isMale) {
                newMaleAngryCount++
                if (newMaleAngryCount >= 3) {
                    state = state.copy(
                        interactionState = interaction.copy(
                            maleClickCount = 0,
                            maleAngryCount = 0
                        )
                    )
                    return
                }
            } else {
                newFemaleAngryCount++
                if (newFemaleAngryCount >= 3) {
                    state = state.copy(
                        interactionState = interaction.copy(
                            femaleClickCount = 0,
                            femaleAngryCount = 0
                        )
                    )
                    return
                }
            }
        }

        state = state.copy(
            interactionState = InteractionState(
                dialogue = emotionalDialogue.text,
                characterId = character.id,
                emotion = emotionalDialogue.emotion,
                maleClickCount = newMaleCount,
                femaleClickCount = newFemaleCount,
                lastClickTime = currentTime,
                maleAngryCount = newMaleAngryCount,
                femaleAngryCount = newFemaleAngryCount
            )
        )
    }

    private fun handleDismissInteraction() {
        state = state.copy(
            interactionState = state.interactionState.copy(
                dialogue = null,
                characterId = null
            )
        )
    }

    // ==================== 선택지 ====================

    private fun handleSelectChoice(nextSceneIndex: Int) {
        state = state.copy(
            playbackState = state.playbackState.copy(currentSceneIndex = nextSceneIndex),
            choiceState = ChoiceState()
        )
    }

    private fun handleDetectChoice(choices: List<Choice>) {
        state = state.copy(
            choiceState = ChoiceState(
                isWaiting = true,
                choices = choices
            )
        )
    }

    // ==================== AI 생성 ====================

    private fun handleShowAIDialog() {
        state = state.copy(
            aiGenerationState = state.aiGenerationState.copy(showDialog = true)
        )
    }

    private fun handleHideAIDialog() {
        state = state.copy(
            aiGenerationState = AIGenerationState()
        )
    }

    private fun handleUpdateAIInput(input: String) {
        state = state.copy(
            aiGenerationState = state.aiGenerationState.copy(userInput = input)
        )
    }

    private fun handleGenerateAI() {
        val input = state.aiGenerationState.userInput
        if (input.isBlank()) return

        viewModelScope.launch {
            state = state.copy(
                aiGenerationState = state.aiGenerationState.copy(
                    isGenerating = true,
                    error = null
                )
            )

            try {
                // Repository를 통해 AI 생성 (완전히 캡슐화)
                val theaterScript = scenarioRepository.generateFromAI(input)

                state = state.copy(
                    currentScript = theaterScript,
                    playbackState = PlaybackState(isPlaying = true),
                    aiGenerationState = AIGenerationState()
                )
            } catch (e: Exception) {
                state = state.copy(
                    aiGenerationState = state.aiGenerationState.copy(
                        error = e.message ?: "알 수 없는 오류",
                        isGenerating = false
                    )
                )
            }
        }
    }

    // ==================== 타임라인 ====================

    private fun handleAdvanceScene() {
        val script = state.currentScript ?: return
        val currentIndex = state.playbackState.currentSceneIndex

        if (currentIndex < script.scenes.lastIndex) {
            state = state.copy(
                playbackState = state.playbackState.copy(
                    currentSceneIndex = currentIndex + 1
                )
            )
        } else {
            handleScriptEnded()
        }
    }

    private fun handleScriptEnded() {
        state = state.copy(
            playbackState = state.playbackState.copy(isPlaying = false)
        )
        onScriptEndCallback?.invoke()

        // PLAYGROUND로 복귀
        StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
        state = state.copy(
            currentScript = StageTestScenario.createTestScript(),
            playbackState = PlaybackState()
        )
    }
}
