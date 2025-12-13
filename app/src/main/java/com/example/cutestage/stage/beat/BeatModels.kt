package com.example.cutestage.stage.beat

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cutestage.stage.CharacterAnimationType
import com.example.cutestage.stage.CharacterGender
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Beat (비트) - 연극의 의미적 최소 단위
 *
 * 하나의 Beat는 "누가, 무엇을, 어떻게, 왜" 하는지를 담는 극적 순간입니다.
 * 예: "첫 만남", "갈등 고조", "화해" 등
 *
 * @param id 고유 ID
 * @param name 비트 이름 (예: "첫 만남", "어색한 침묵")
 * @param description 연출 의도 설명
 * @param duration 지속 시간 (초)
 * @param layers 이 Beat에서 동시에 일어나는 모든 것들
 */
data class Beat(
    val id: String,
    val name: String,
    val description: String = "",
    val duration: Float = 3f, // 초 단위
    val layers: BeatLayers = BeatLayers()
)

/**
 * Beat Layers - 동시에 일어나는 여러 레이어
 *
 * Beat 안에서 캐릭터들의 행동, 배경, 조명, 음향이 동시에 진행됩니다.
 */
data class BeatLayers(
    val characters: List<CharacterAction> = emptyList(),
    val background: BackgroundLayer? = null,
    val lighting: LightingLayer? = null,
    val sound: SoundLayer? = null,
    val dialogues: List<DialogueAction> = emptyList()
)

// ==================== 캐릭터 레이어 ====================

/**
 * 캐릭터 액션 - 한 명의 캐릭터가 이 Beat에서 하는 모든 것
 */
data class CharacterAction(
    val characterId: String,
    val characterName: String,
    val gender: CharacterGender,
    val movement: Movement,
    val emotion: Emotion,
    val gesture: Gesture? = null,
    val facingDirection: Direction = Direction.CENTER
)

/**
 * 이동 정보
 */
data class Movement(
    val type: MovementType,
    val from: Position? = null,
    val to: Position? = null,
    val speed: Speed = Speed.NORMAL
)

enum class MovementType {
    @SerializedName("enter")
    ENTER, // 등장

    @SerializedName("exit")
    EXIT, // 퇴장

    @SerializedName("move")
    MOVE, // 이동

    @SerializedName("stay")
    STAY, // 제자리

    @SerializedName("approach")
    APPROACH, // 다른 캐릭터에게 다가가기

    @SerializedName("retreat")
    RETREAT // 물러나기
}

/**
 * 위치 정보
 */
data class Position(
    val x: Float, // 0.0 ~ 1.0 (무대 좌측 ~ 우측)
    val y: Float, // 0.0 ~ 1.0 (무대 상단 ~ 하단)
    val zone: PositionZone? = null // 편의를 위한 영역 표시
) {
    companion object {
        // 프리셋 위치들
        val LEFT = Position(0.15f, 0.5f, PositionZone.LEFT)
        val CENTER = Position(0.5f, 0.5f, PositionZone.CENTER)
        val RIGHT = Position(0.85f, 0.5f, PositionZone.RIGHT)
        val LEFT_FRONT = Position(0.25f, 0.6f, PositionZone.LEFT)
        val CENTER_FRONT = Position(0.5f, 0.6f, PositionZone.CENTER)
        val RIGHT_FRONT = Position(0.75f, 0.6f, PositionZone.RIGHT)
        val OFF_STAGE_LEFT = Position(-0.1f, 0.5f, PositionZone.OFF_STAGE)
        val OFF_STAGE_RIGHT = Position(1.1f, 0.5f, PositionZone.OFF_STAGE)
    }

    /**
     * Position을 화면 DP로 변환
     * @param stageWidth 무대 전체 너비 (dp)
     * @param stageHeight 무대 전체 높이 (dp)
     */
    fun toDp(stageWidth: Dp, stageHeight: Dp): Pair<Dp, Dp> {
        return Pair(
            stageWidth * x,
            stageHeight * y
        )
    }
}

enum class PositionZone {
    OFF_STAGE,
    LEFT,
    CENTER,
    RIGHT,
    FRONT,
    BACK
}

enum class Speed {
    @SerializedName("very_slow")
    VERY_SLOW, // 아주 천천히

    @SerializedName("slow")
    SLOW, // 천천히

    @SerializedName("normal")
    NORMAL, // 보통

    @SerializedName("fast")
    FAST, // 빠르게

    @SerializedName("very_fast")
    VERY_FAST; // 매우 빠르게

    fun toMillis(): Long {
        return when (this) {
            VERY_SLOW -> 2000L
            SLOW -> 1500L
            NORMAL -> 1000L
            FAST -> 600L
            VERY_FAST -> 300L
        }
    }
}

/**
 * 감정 상태
 */
data class Emotion(
    val type: EmotionType,
    val intensity: Float = 0.5f // 0.0 ~ 1.0
)

enum class EmotionType {
    @SerializedName("neutral")
    NEUTRAL, // 중립

    @SerializedName("happy")
    HAPPY, // 기쁨

    @SerializedName("sad")
    SAD, // 슬픔

    @SerializedName("angry")
    ANGRY, // 분노

    @SerializedName("surprised")
    SURPRISED, // 놀람

    @SerializedName("scared")
    SCARED, // 두려움

    @SerializedName("disgusted")
    DISGUSTED, // 혐오

    @SerializedName("excited")
    EXCITED, // 흥분

    @SerializedName("nervous")
    NERVOUS, // 긴장

    @SerializedName("confused")
    CONFUSED, // 혼란

    @SerializedName("shy")
    SHY, // 수줍음

    @SerializedName("annoyed")
    ANNOYED; // 짜증

    /**
     * 감정을 애니메이션 타입으로 변환
     */
    fun toAnimationType(isSpeaking: Boolean = false): CharacterAnimationType {
        return when (this) {
            ANGRY, ANNOYED -> if (isSpeaking) CharacterAnimationType.SPEAK_ANGRY else CharacterAnimationType.IDLE_ANNOYED
            HAPPY, EXCITED -> CharacterAnimationType.IDLE
            SAD, SCARED -> CharacterAnimationType.LISTENING
            else -> if (isSpeaking) CharacterAnimationType.SPEAK_NORMAL else CharacterAnimationType.IDLE
        }
    }
}

/**
 * 제스처 (특정 동작)
 */
data class Gesture(
    val type: GestureType,
    val target: String? = null // 대상 캐릭터 ID (다가가기, 손흔들기 등)
)

enum class GestureType {
    @SerializedName("wave")
    WAVE, // 손흔들기

    @SerializedName("bow")
    BOW, // 인사

    @SerializedName("sit")
    SIT, // 앉기

    @SerializedName("stand")
    STAND, // 일어서기

    @SerializedName("clap")
    CLAP, // 박수

    @SerializedName("dance")
    DANCE, // 춤

    @SerializedName("sing")
    SING, // 노래

    @SerializedName("point")
    POINT, // 가리키기

    @SerializedName("hug")
    HUG, // 포옹

    @SerializedName("push")
    PUSH, // 밀기

    @SerializedName("pull")
    PULL; // 당기기

    fun toAnimationType(): CharacterAnimationType? {
        return when (this) {
            CLAP -> CharacterAnimationType.CLAP
            DANCE -> CharacterAnimationType.DANCING_TYPE_A
            SING -> CharacterAnimationType.SING_NORMAL
            else -> null
        }
    }
}

enum class Direction {
    LEFT,
    RIGHT,
    CENTER,
    FORWARD, // 관객 쪽
    BACKWARD // 뒤쪽
}

// ==================== 대사 레이어 ====================

/**
 * 대사 액션
 */
data class DialogueAction(
    val id: String = java.util.UUID.randomUUID().toString(),  // 고유 식별자
    val characterId: String,
    val text: String,
    val emotion: EmotionType = EmotionType.NEUTRAL,
    val delay: Float = 0f, // Beat 시작 후 몇 초 뒤에 나올지
    val typingSpeed: Long = 50L // 타이핑 속도 (ms)
)

// ==================== 배경 레이어 ====================

/**
 * 배경 레이어
 */
data class BackgroundLayer(
    val type: BackgroundType,
    val resourceName: String? = null, // 커스텀 배경인 경우
    val transition: TransitionType = TransitionType.NONE
)

enum class BackgroundType {
    @SerializedName("stage_floor")
    STAGE_FLOOR, // 기본 무대

    @SerializedName("forest")
    FOREST, // 숲

    @SerializedName("park")
    PARK, // 공원

    @SerializedName("indoor")
    INDOOR, // 실내

    @SerializedName("street")
    STREET, // 거리

    @SerializedName("custom")
    CUSTOM // 커스텀 배경
}

enum class TransitionType {
    NONE,
    FADE,
    SLIDE_LEFT,
    SLIDE_RIGHT
}

// ==================== 조명 레이어 ====================

/**
 * 조명 레이어
 */
data class LightingLayer(
    val brightness: Float = 1.0f, // 0.0 (어두움) ~ 1.0 (밝음)
    val color: LightingColor = LightingColor.WHITE,
    val spotlight: String? = null // 특정 캐릭터 ID에 스포트라이트
)

enum class LightingColor {
    WHITE,
    WARM, // 따뜻한 조명
    COOL, // 차가운 조명
    RED,
    BLUE,
    PURPLE
}

// ==================== 음향 레이어 ====================

/**
 * 음향 레이어
 */
data class SoundLayer(
    val bgm: String? = null, // BGM 리소스 이름
    val soundEffects: List<SoundEffect> = emptyList()
)

data class SoundEffect(
    val type: SoundEffectType,
    val volume: Float = 0.5f,
    val delay: Float = 0f // Beat 시작 후 몇 초 뒤에 재생
)

enum class SoundEffectType {
    FOOTSTEP,
    DOOR_OPEN,
    DOOR_CLOSE,
    APPLAUSE,
    LAUGHTER,
    GASP,
    SIGH
}

// ==================== JSON 직렬화 헬퍼 ====================

object BeatJsonHelper {
    private val gson = com.google.gson.GsonBuilder()
        .registerTypeAdapter(DialogueAction::class.java, DialogueActionDeserializer())
        .create()

    fun toJson(beat: Beat): String {
        return gson.toJson(beat)
    }

    fun fromJson(json: String): Beat {
        return gson.fromJson(json, Beat::class.java)
    }

    fun toBeatList(json: String): List<Beat> {
        return gson.fromJson(json, Array<Beat>::class.java).toList()
    }

    fun fromBeatList(beats: List<Beat>): String {
        return gson.toJson(beats)
    }
}

/**
 * DialogueAction Deserializer - 레거시 JSON 호환성
 * id 필드가 없는 기존 JSON도 처리 가능
 */
private class DialogueActionDeserializer : com.google.gson.JsonDeserializer<DialogueAction> {
    override fun deserialize(
        json: com.google.gson.JsonElement,
        typeOfT: java.lang.reflect.Type,
        context: com.google.gson.JsonDeserializationContext
    ): DialogueAction {
        val jsonObject = json.asJsonObject

        // id가 없으면 UUID 생성 (레거시 호환성)
        val id = if (jsonObject.has("id")) {
            jsonObject.get("id").asString
        } else {
            java.util.UUID.randomUUID().toString()
        }

        return DialogueAction(
            id = id,
            characterId = jsonObject.get("characterId").asString,
            text = jsonObject.get("text").asString,
            emotion = context.deserialize(jsonObject.get("emotion"), EmotionType::class.java),
            delay = jsonObject.get("delay").asFloat,
            typingSpeed = if (jsonObject.has("typingSpeed")) {
                jsonObject.get("typingSpeed").asLong
            } else {
                50L
            }
        )
    }
}
