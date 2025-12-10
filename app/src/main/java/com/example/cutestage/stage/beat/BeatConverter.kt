package com.example.cutestage.stage.beat

import androidx.compose.ui.unit.dp
import com.example.cutestage.R
import com.example.cutestage.stage.*

/**
 * Beat를 TheaterScript로 변환하는 컨버터
 *
 * Beat 기반 시나리오를 기존 StageView에서 재생 가능한 TheaterScript로 변환합니다.
 */
object BeatConverter {

    /**
     * Beat 리스트를 TheaterScript로 변환
     */
    fun beatsToTheaterScript(beats: List<Beat>): TheaterScript {
        val scenes = beats.map { beat ->
            beatToScene(beat)
        }

        return TheaterScript(
            scenes = scenes,
            debug = false
        )
    }

    /**
     * 단일 Beat를 SceneState로 변환
     */
    private fun beatToScene(beat: Beat): SceneState {
        val layers = beat.layers

        // 캐릭터 변환
        val characters = layers.characters.map { action ->
            characterActionToCharacterState(action, beat.duration)
        }

        // 대사 변환
        val dialogues = layers.dialogues.map { dialogue ->
            dialogueActionToDialogueState(dialogue, characters)
        }

        // 배경 리소스 결정
        val backgroundRes = layers.background?.let {
            getBackgroundResource(it.type)
        } ?: R.drawable.stage_floor

        return SceneState(
            backgroundRes = backgroundRes,
            characters = characters,
            dialogues = dialogues,
            durationMillis = (beat.duration * 1000).toLong(),
            isEnding = false
        )
    }

    /**
     * CharacterAction을 CharacterState로 변환
     */
    private fun characterActionToCharacterState(
        action: CharacterAction,
        beatDuration: Float
    ): CharacterState {
        // 위치 계산 (무대 크기 기준)
        val stageWidth = 360.dp
        val stageHeight = 300.dp

        val position = when (action.movement.type) {
            MovementType.ENTER -> {
                // 등장: from에서 to로 이동
                action.movement.to ?: Position.CENTER
            }

            MovementType.EXIT -> {
                // 퇴장: to 위치로 이동 (보통 무대 밖)
                action.movement.to ?: Position.OFF_STAGE_RIGHT
            }

            MovementType.MOVE -> {
                // 이동: to 위치로
                action.movement.to ?: Position.CENTER
            }

            MovementType.STAY -> {
                // 제자리: from 위치에 그대로
                action.movement.from ?: Position.CENTER
            }

            MovementType.APPROACH, MovementType.RETREAT -> {
                // 다가가기/물러나기: to 위치로
                action.movement.to ?: Position.CENTER
            }
        }

        val (x, y) = position.toDp(stageWidth, stageHeight)

        // 애니메이션 타입 결정
        val animationType = when {
            action.gesture?.type == GestureType.CLAP -> CharacterAnimationType.CLAP
            action.gesture?.type == GestureType.DANCE -> CharacterAnimationType.DANCING_TYPE_A
            action.gesture?.type == GestureType.SING -> CharacterAnimationType.SING_NORMAL
            action.movement.type == MovementType.ENTER || action.movement.type == MovementType.MOVE -> CharacterAnimationType.WALKING
            else -> action.emotion.type.toAnimationType(isSpeaking = false)
        }

        // 애니메이션 상태 생성
        val spriteAnimation = CharacterAnimationState(
            gender = action.gender,
            currentAnimation = animationType,
            isAnimating = true
        )

        // 캐릭터 이미지 리소스
        val imageRes = CharacterAnimationResources.getAnimationResource(
            gender = action.gender,
            animation = CharacterAnimationType.IDLE,
            frame = 1
        )

        // 방향에 따른 flipX 결정
        val flipX = when (action.facingDirection) {
            Direction.LEFT -> true
            Direction.RIGHT -> false
            else -> false
        }

        // 투명도 (등장/퇴장 시 페이드 효과)
        val alpha = when (action.movement.type) {
            MovementType.EXIT -> 0.3f
            else -> 1f
        }

        return CharacterState(
            id = action.characterId,
            name = action.characterName,
            imageRes = imageRes,
            position = androidx.compose.ui.unit.DpOffset(x, y),
            size = 80.dp,
            alpha = alpha,
            scale = 1f,
            rotation = 0f,
            flipX = flipX,
            animationDuration = action.movement.speed.toMillis().toInt(),
            voice = CharacterVoice(), // 기본 목소리
            spriteAnimation = spriteAnimation
        )
    }

    /**
     * DialogueAction을 DialogueState로 변환
     */
    private fun dialogueActionToDialogueState(
        dialogue: DialogueAction,
        characters: List<CharacterState>
    ): DialogueState {
        // 대사를 하는 캐릭터 찾기
        val speaker = characters.find { it.id == dialogue.characterId }

        // 말풍선 위치: 캐릭터 위쪽
        val position = if (speaker != null) {
            androidx.compose.ui.unit.DpOffset(
                x = speaker.position.x + 40.dp,
                y = speaker.position.y - 60.dp
            )
        } else {
            androidx.compose.ui.unit.DpOffset(180.dp, 100.dp)
        }

        return DialogueState(
            text = dialogue.text,
            position = position,
            speakerName = speaker?.name,
            delayMillis = (dialogue.delay * 1000).toLong(),
            typingSpeedMs = dialogue.typingSpeed,
            voice = speaker?.voice
        )
    }

    /**
     * 배경 타입에 따른 리소스 ID 반환
     */
    private fun getBackgroundResource(type: BackgroundType): Int {
        return when (type) {
            BackgroundType.STAGE_FLOOR -> R.drawable.stage_floor
            BackgroundType.FOREST -> R.drawable.stage_floor // TODO: 숲 배경 추가
            BackgroundType.PARK -> R.drawable.stage_floor // TODO: 공원 배경 추가
            BackgroundType.INDOOR -> R.drawable.stage_floor // TODO: 실내 배경 추가
            BackgroundType.STREET -> R.drawable.stage_floor // TODO: 거리 배경 추가
            BackgroundType.CUSTOM -> R.drawable.stage_floor // 커스텀은 기본값
        }
    }
}

/**
 * 확장 함수: Beat 리스트를 바로 TheaterScript로 변환
 */
fun List<Beat>.toTheaterScript(): TheaterScript {
    return BeatConverter.beatsToTheaterScript(this)
}
