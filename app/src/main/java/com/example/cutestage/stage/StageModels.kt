package com.example.cutestage.stage

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

// ==================== 데이터 모델 ====================

/**
 * 연극 스크립트 (전체 시나리오)
 */
@Immutable
data class TheaterScript(
    val scenes: List<SceneState>,
    val debug: Boolean = false,
)

/**
 * 씬 (장면)
 */
@Immutable
data class SceneState(
    @DrawableRes val backgroundRes: Int? = null,
    val characters: List<CharacterState> = emptyList(),
    val dialogues: List<DialogueState> = emptyList(),
    val durationMillis: Long = 3000L,
    val isEnding: Boolean = false,
)

/**
 * 캐릭터 상태
 */
@Immutable
data class CharacterState(
    val id: String,
    val name: String,
    @DrawableRes val imageRes: Int,
    val position: DpOffset = DpOffset(0.dp, 0.dp),
    val size: Dp = 80.dp,
    val alpha: Float = 1f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val flipX: Boolean = false,
    val animationDuration: Int = 500,
    val easing: Easing = FastOutSlowInEasing,
    val voice: CharacterVoice = CharacterVoice(),
    val spriteAnimation: CharacterAnimationState? = null,
    val description: String? = null,  // 캐릭터 설명 (등장인물 소개용)
)

/**
 * 대사 상태
 */
@Immutable
data class DialogueState(
    val text: String,
    val position: DpOffset,
    val speakerName: String? = null,
    val delayMillis: Long = 0L,
    val typingSpeedMs: Long = 50L,
    val voice: CharacterVoice? = null,
    val notes: List<SongNote>? = null,
    val choices: List<Choice>? = null,
)

/**
 * 노래 음표
 */
@Immutable
data class SongNote(
    val lyric: String,
    val pitch: Float,
    val duration: Int,
)

/**
 * 선택지
 */
@Immutable
data class Choice(
    val text: String,
    val nextSceneIndex: Int,
)

// ==================== 확장 함수 ====================

fun CharacterState.moveTo(x: Dp, y: Dp, duration: Int = 500): CharacterState =
    copy(position = DpOffset(x, y), animationDuration = duration)

fun CharacterState.fadeIn(duration: Int = 500): CharacterState =
    copy(alpha = 1f, animationDuration = duration)

fun CharacterState.fadeOut(duration: Int = 500): CharacterState =
    copy(alpha = 0f, animationDuration = duration)

fun CharacterState.scaleTo(scale: Float, duration: Int = 500): CharacterState =
    copy(scale = scale, animationDuration = duration)

fun CharacterState.flip(): CharacterState = copy(flipX = !flipX)
