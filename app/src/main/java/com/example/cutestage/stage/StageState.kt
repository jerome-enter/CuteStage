package com.example.cutestage.stage

import androidx.compose.runtime.Immutable

/**
 * StageView의 전체 상태
 */
@Immutable
data class StageState(
    val currentScript: TheaterScript? = null,
    val scenarioTitle: String? = null,
    val playbackState: PlaybackState = PlaybackState(),
    val interactionState: InteractionState = InteractionState(),
    val choiceState: ChoiceState = ChoiceState(),
    val aiGenerationState: AIGenerationState = AIGenerationState(),
    val showDebugPoints: Boolean = false,  // 디버그용 위치 점 표시
)

/**
 * 재생 관련 상태
 */
@Immutable
data class PlaybackState(
    val isPlaying: Boolean = false,
    val speed: Float = 1.0f,
    val currentSceneIndex: Int = 0,
)

/**
 * 캐릭터 상호작용 상태
 */
@Immutable
data class InteractionState(
    val dialogue: String? = null,
    val characterId: String? = null,
    val emotion: CharacterInteractionSystem.EmotionType = CharacterInteractionSystem.EmotionType.NORMAL,
    val maleClickCount: Int = 0,
    val femaleClickCount: Int = 0,
    val lastClickTime: Long = 0L,
    val maleAngryCount: Int = 0,
    val femaleAngryCount: Int = 0,
)

/**
 * 선택지 상태
 */
@Immutable
data class ChoiceState(
    val isWaiting: Boolean = false,
    val choices: List<Choice>? = null,
)

/**
 * AI 생성 상태
 */
@Immutable
data class AIGenerationState(
    val showDialog: Boolean = false,
    val userInput: String = "",
    val isGenerating: Boolean = false,
    val error: String? = null,
)
