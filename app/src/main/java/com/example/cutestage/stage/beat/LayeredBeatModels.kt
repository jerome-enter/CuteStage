package com.example.cutestage.stage.beat

import androidx.compose.ui.unit.DpOffset
import com.example.cutestage.R
import com.example.cutestage.stage.CharacterGender

/**
 * 레이어 기반 Beat 모델
 *
 * 각 Beat는 독립적인 4개 레이어로 구성됩니다:
 * 1. 장소 (Location)
 * 2. 대사 및 감정 (Dialogue & Emotion)
 * 3. 동작 (Action)
 * 4. 이동 동선 (Movement)
 */
data class LayeredBeat(
    val id: String,
    val name: String,
    val duration: Float = 5f, // 자동 계산 가능

    // 4개의 독립 레이어
    val locationLayer: LocationLayer = LocationLayer(),
    val dialogueLayer: DialogueLayer = DialogueLayer(),
    val actionLayer: ActionLayer = ActionLayer(),
    val movementLayer: MovementLayer = MovementLayer()
) {
    /**
     * 대사 길이 기반 자동 duration 계산
     */
    fun calculateDuration(): Float {
        val dialogueDuration = dialogueLayer.dialogues.maxOfOrNull { entry ->
            entry.startTime + (entry.text.length * 0.15f).coerceAtLeast(1.5f)
        } ?: 3f

        val movementDuration = movementLayer.movements.maxOfOrNull {
            it.startTime + 2f // 이동 시간
        } ?: 3f

        return maxOf(dialogueDuration, movementDuration, 3f)
    }
}

// ==================== 1. 장소 레이어 ====================

/**
 * 장소 레이어
 * 배경 이미지 선택
 */
data class LocationLayer(
    val location: StageLocation = StageLocation.STAGE_FLOOR
)

enum class StageLocation(
    val displayName: String,
    val emoji: String,
    val backgroundRes: Int
) {
    STAGE_FLOOR("무대", "🎭", R.drawable.stage_floor),
    SCHOOL_PLAYGROUND("학교 운동장", "🏫", R.drawable.stage_floor), // TODO: 실제 배경 추가
    CLASSROOM("교실", "📚", R.drawable.stage_floor),
    OFFICE("사무실", "🏢", R.drawable.stage_floor),
    ROOFTOP("옥상", "🏙️", R.drawable.stage_floor),
    CONVENIENCE_STORE("편의점 밖", "🏪", R.drawable.stage_floor),
    HOUSE_FRONT("집 앞", "🏠", R.drawable.stage_floor),
    SUBWAY_STATION("지하철역 앞", "🚇", R.drawable.stage_floor),
    BUS_STOP("버스정류장", "🚏", R.drawable.stage_floor),
    RESTAURANT("음식점", "🍽️", R.drawable.stage_floor),
    BEDROOM("방", "🛏️", R.drawable.stage_floor),
    LIVING_ROOM("거실", "🛋️", R.drawable.stage_floor),
    KITCHEN("주방", "🍳", R.drawable.stage_floor),
    PARK("공원", "🌳", R.drawable.stage_floor),
    STREET("거리", "🛣️", R.drawable.stage_floor);
}

// ==================== 2. 대사 레이어 ====================

/**
 * 대사 레이어
 * 순차적 대사 목록 (철수 → 영희 → 철수 ...)
 */
data class DialogueLayer(
    val dialogues: List<DialogueEntry> = emptyList()
)

data class DialogueEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val characterId: String,
    val characterName: String, // UI 표시용
    val text: String,
    val emotion: DialogueEmotion = DialogueEmotion.CALM,
    val startTime: Float = 0f, // Beat 시작 후 몇 초 (자동 계산)
    val action: DialogueActionType? = null // 대사와 함께 하는 동작 (옵션)
) {
    /**
     * 대사 재생 시간 자동 계산 (글자수 기반, 1.3배 빠르게)
     */
    fun calculateDuration(): Float {
        return ((text.length * 0.15f + 1f) / 1.3f).coerceAtLeast(1.2f)
    }
}

/**
 * 대사 감정
 */
enum class DialogueEmotion(
    val displayName: String,
    val emoji: String
) {
    CALM("차분함", "🙂"),
    HAPPY("기쁨", "😊"),
    SAD("슬픔", "😢"),
    ANGRY("화남", "😠"),
    SURPRISED("놀람", "😲"),
    FEARFUL("두려움", "😨"),
    EXCITED("흥분", "🤩"),
    NERVOUS("긴장", "😰"),
    SHY("수줍음", "😳"),
    ANNOYED("짜증", "😤");

    /**
     * EmotionType으로 변환 (기존 시스템과 호환)
     */
    fun toEmotionType(): EmotionType {
        return when (this) {
            CALM -> EmotionType.NEUTRAL
            HAPPY -> EmotionType.HAPPY
            SAD -> EmotionType.SAD
            ANGRY -> EmotionType.ANGRY
            SURPRISED -> EmotionType.SURPRISED
            FEARFUL -> EmotionType.SCARED
            EXCITED -> EmotionType.EXCITED
            NERVOUS -> EmotionType.NERVOUS
            SHY -> EmotionType.SHY
            ANNOYED -> EmotionType.ANNOYED
        }
    }
}

/**
 * 대사와 함께 하는 동작 (옵션)
 */
enum class DialogueActionType(
    val displayName: String,
    val emoji: String
) {
    NONE("없음", ""),
    CLAP("박수", "👏"),
    WAVE("손흔들기", "👋"),
    NOD("고개끄덕이기", "✅"),
    SHAKE_HEAD("고개젓기", "❌"),
    POINT("가리키기", "👉"),
    BOW("인사", "🙇");

    fun toGestureType(): GestureType? {
        return when (this) {
            NONE -> null
            CLAP -> GestureType.CLAP
            WAVE -> GestureType.WAVE
            NOD, SHAKE_HEAD, POINT -> null // 현재 미지원
            BOW -> GestureType.BOW
        }
    }
}

// ==================== 3. 동작 레이어 ====================

/**
 * 동작 레이어
 * 대사와 별개로 독립적인 동작 추가 가능
 */
data class ActionLayer(
    val actions: List<ActionEntry> = emptyList()
)

data class ActionEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val characterId: String,
    val actionType: StageActionType,
    val startTime: Float = 0f,
    val linkedDialogueId: String? = null // 특정 대사와 연결
)

enum class StageActionType(
    val displayName: String,
    val emoji: String
) {
    IDLE("대기", "🧍"),
    CLAP("박수", "👏"),
    DANCING("춤", "💃"),
    WAVE("손흔들기", "👋"),
    JUMP("점프", "🦘"),
    BOW("인사", "🙇"),
    SING("노래", "🎤");

    fun toGestureType(): GestureType? {
        return when (this) {
            IDLE -> GestureType.STAND
            CLAP -> GestureType.CLAP
            DANCING -> GestureType.DANCE
            WAVE -> GestureType.WAVE
            JUMP -> null
            BOW -> GestureType.BOW
            SING -> GestureType.SING
        }
    }
}

// ==================== 4. 이동 동선 레이어 ====================

/**
 * 이동 동선 레이어
 * 캐릭터의 위치를 타임라인 기반으로 관리
 */
data class MovementLayer(
    val movements: List<MovementEntry> = emptyList()
)

data class MovementEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val characterId: String,
    val fromPosition: StagePosition? = null, // 시작 위치 (null이면 이전 위치 또는 기본값)
    val toPosition: StagePosition, // 목표 위치
    val startTime: Float = 0f, // 이동 시작 시간
    val endTime: Float = 1f, // 이동 끝 시간 (도착 시간)
    val autoWalk: Boolean = true, // 자동으로 WALKING 애니메이션 삽입
    val linkedDialogueId: String? = null // 특정 대사 시점과 연결
) {
    /**
     * 이동 소요 시간
     */
    fun duration(): Float = endTime - startTime

    /**
     * 실제 시작 위치 계산 (자동 추론)
     */
    fun getActualFromPosition(previousPosition: StagePosition?): StagePosition {
        return fromPosition ?: previousPosition ?: StagePosition.CENTER
    }
}

/**
 * 무대 위치 (정규화된 좌표)
 */
data class StagePosition(
    val x: Float, // 0.0 ~ 1.0 (좌 ~ 우)
    val y: Float  // 0.0 ~ 1.0 (상 ~ 하)
) {
    companion object {
        // 프리셋 위치
        val LEFT = StagePosition(0.15f, 0.6f)
        val CENTER = StagePosition(0.5f, 0.6f)
        val RIGHT = StagePosition(0.85f, 0.6f)
        val LEFT_FRONT = StagePosition(0.25f, 0.7f)
        val CENTER_FRONT = StagePosition(0.5f, 0.7f)
        val RIGHT_FRONT = StagePosition(0.75f, 0.7f)
        val LEFT_BACK = StagePosition(0.25f, 0.5f)
        val CENTER_BACK = StagePosition(0.5f, 0.5f)
        val RIGHT_BACK = StagePosition(0.75f, 0.5f)

        /**
         * 스테이지 크기 기준으로 DP로 변환
         */
        fun StagePosition.toDpOffset(stageWidthDp: Float, stageHeightDp: Float): DpOffset {
            return DpOffset(
                x = androidx.compose.ui.unit.Dp(stageWidthDp * x),
                y = androidx.compose.ui.unit.Dp(stageHeightDp * y)
            )
        }
    }

    /**
     * 두 위치 간 거리 계산 (정규화된 좌표 기준)
     */
    fun distanceTo(other: StagePosition): Float {
        val dx = other.x - this.x
        val dy = other.y - this.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    /**
     * 거리 기반 이동 시간 계산 (초)
     */
    fun walkDurationTo(other: StagePosition): Float {
        val distance = distanceTo(other)
        return (distance * 3f).coerceAtLeast(0.5f) // 최소 0.5초
    }
}
