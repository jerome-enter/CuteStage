package com.example.cutestage.stage

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * StageView의 상태와 비즈니스 로직을 관리하는 ViewModel
 */
class StageViewModel(
    initialScript: TheaterScript?,
    private val onScriptEnd: () -> Unit
) : ViewModel() {

    var state by mutableStateOf(
        StageState(currentScript = initialScript)
    )
        private set

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
                    interaction = interaction.copy(
                        maleClickCount = 0,
                        maleAngryCount = 0
                    )
                    return // 리셋 후 종료
                }
            } else {
                newFemaleAngryCount++
                if (newFemaleAngryCount >= 3) {
                    interaction = interaction.copy(
                        femaleClickCount = 0,
                        femaleAngryCount = 0
                    )
                    return // 리셋 후 종료
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

    // AI 생성은 외부(Composable)에서 Context와 함께 호출
    fun generateAIScenario(context: Context, input: String) {
        if (input.isBlank()) return

        viewModelScope.launch {
            state = state.copy(
                aiGenerationState = state.aiGenerationState.copy(
                    isGenerating = true,
                    error = null
                )
            )

            try {
                val generatedScenario = withContext(Dispatchers.IO) {
                    GeminiScenarioGenerator.generateScenario(context, input)
                }

                if (generatedScenario.status == "error") {
                    state = state.copy(
                        aiGenerationState = state.aiGenerationState.copy(
                            error = generatedScenario.message,
                            isGenerating = false
                        )
                    )
                    return@launch
                }

                val theaterScript = ScenarioConverter.convertToTheaterScript(generatedScenario)

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

    private fun handleGenerateAI() {
        // Context가 필요하므로 외부에서 호출하도록 변경
        // StageViewContent에서 generateAIScenario() 직접 호출
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
        onScriptEnd()

        // PLAYGROUND로 복귀
        StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
        state = state.copy(
            currentScript = StageTestScenario.createTestScript(),
            playbackState = PlaybackState()
        )
    }

    /**
     * Context가 필요한 작업을 위한 헬퍼
     */
    fun initScenarioConverter(context: Context) {
        ScenarioConverter.init(context)
    }
}

/**
 * StageViewModel Factory
 */
class StageViewModelFactory(
    private val initialScript: TheaterScript?,
    private val onScriptEnd: () -> Unit
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StageViewModel::class.java)) {
            return StageViewModel(initialScript, onScriptEnd) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
