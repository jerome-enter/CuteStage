package com.example.cutestage.data.module

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cutestage.stage.CharacterGender

/**
 * 모듈 콘텐츠 타입별 데이터 클래스
 *
 * 각 모듈 타입마다 고유한 Content 클래스를 정의합니다.
 * JSON으로 직렬화되어 ModuleItemEntity.contentJson에 저장됩니다.
 */

// ==================== Dialogue (대사) ====================

data class DialogueContent(
    val text: String,                     // 대사 텍스트
    val characterId: String,              // 발화자 캐릭터 ID
    val emotion: EmotionType = EmotionType.NEUTRAL,
    val bubbleStyle: BubbleStyle = BubbleStyle.NORMAL,
    val typingSpeedMs: Long = 50L,        // 타이핑 속도
    val voicePitch: Float = 1.0f,         // 음성 톤 (1.0 = 보통)
    val delayMillis: Long = 0L            // 표시 전 대기 시간
)

enum class EmotionType {
    NEUTRAL,    // 평범
    HAPPY,      // 행복
    SAD,        // 슬픔
    ANGRY,      // 화남
    SURPRISED,  // 놀람
    SHY,        // 부끄러움
    EXCITED,    // 신남
    WORRIED     // 걱정
}

enum class BubbleStyle {
    NORMAL,     // 일반 말풍선
    SHOUT,      // 외침 (큰 텍스트, 진동)
    WHISPER,    // 속삭임 (작은 텍스트, 점선)
    THOUGHT     // 생각 (구름 모양)
}

// ==================== Action (동작) ====================

data class ActionContent(
    val characterId: String,
    val animationType: AnimationType,
    val startPositionX: Float,            // 시작 X 위치 (0.0 ~ 1.0, 화면 비율)
    val startPositionY: Float,            // 시작 Y 위치
    val endPositionX: Float? = null,      // 종료 X 위치 (이동 동작 시)
    val endPositionY: Float? = null,      // 종료 Y 위치
    val duration: Float = 1.0f,           // 동작 지속 시간 (초)
    val emotion: EmotionType = EmotionType.NEUTRAL
)

enum class AnimationType {
    // 이동
    WALK,           // 걷기
    RUN,            // 뛰기
    JUMP,           // 점프
    SLIDE,          // 슬라이드

    // 제스처
    WAVE,           // 손 흔들기
    BOW,            // 인사
    POINT,          // 가리키기
    CLAP,           // 박수

    // 감정 표현
    NOD,            // 고개 끄덕이기
    SHAKE_HEAD,     // 고개 젓기
    LAUGH,          // 웃기
    CRY,            // 울기
    ANGRY_STOMP,    // 발 구르기

    // 상호작용
    HUG,            // 포옹
    PUSH,           // 밀기
    PULL,           // 당기기
    GIVE,           // 주기

    // 특수
    APPEAR,         // 등장 (페이드 인)
    DISAPPEAR,      // 퇴장 (페이드 아웃)
    SPIN,           // 회전
    BOUNCE          // 튕기기
}

// ==================== Scene (장면) ====================

data class SceneContent(
    val backgroundId: String,             // 배경 리소스 ID
    val timeOfDay: TimeOfDay = TimeOfDay.DAY,
    val weather: Weather = Weather.CLEAR,
    val bgmId: String? = null,            // 배경음악 ID
    val ambientSound: String? = null,     // 환경음 (새소리, 바람 소리 등)
    val initialCharacters: List<CharacterPosition> = emptyList(),
    val mood: SceneMood = SceneMood.NEUTRAL
)

data class CharacterPosition(
    val characterId: String,
    val positionX: Float,                 // 0.0 ~ 1.0
    val positionY: Float,
    val scale: Float = 1.0f,
    val flipX: Boolean = false
)

enum class TimeOfDay {
    DAWN,       // 새벽
    MORNING,    // 아침
    DAY,        // 낮
    AFTERNOON,  // 오후
    EVENING,    // 저녁
    NIGHT       // 밤
}

enum class Weather {
    CLEAR,      // 맑음
    CLOUDY,     // 흐림
    RAINY,      // 비
    SNOWY,      // 눈
    WINDY       // 바람
}

enum class SceneMood {
    NEUTRAL,    // 평범
    ROMANTIC,   // 로맨틱
    TENSE,      // 긴장
    SAD,        // 슬픔
    JOYFUL,     // 즐거움
    MYSTERIOUS  // 신비로움
}

// ==================== Background (배경) ====================

data class BackgroundContent(
    val imageResourceId: String,          // 이미지 리소스 이름
    val category: BackgroundCategory,
    val timeOfDay: TimeOfDay? = null,
    val description: String = ""
)

enum class BackgroundCategory {
    INDOOR,     // 실내
    OUTDOOR,    // 실외
    NATURE,     // 자연
    URBAN,      // 도시
    FANTASY     // 판타지
}

// ==================== Effect (효과) ====================

data class EffectContent(
    val effectType: EffectType,
    val duration: Float = 1.0f,           // 효과 지속 시간 (초)
    val intensity: Float = 1.0f,          // 강도 (0.0 ~ 1.0)
    val soundId: String? = null,          // 효과음 ID
    val color: String? = null             // 색상 (hex, 일부 효과에만 사용)
)

enum class EffectType {
    // 화면 전환
    FADE_IN,        // 페이드 인
    FADE_OUT,       // 페이드 아웃
    SLIDE_LEFT,     // 왼쪽으로 슬라이드
    SLIDE_RIGHT,    // 오른쪽으로 슬라이드
    ZOOM_IN,        // 줌 인
    ZOOM_OUT,       // 줌 아웃

    // 강조
    FLASH,          // 플래시
    SPOTLIGHT,      // 스포트라이트
    GLOW,           // 빛나기

    // 감정
    HEART,          // 하트 이펙트
    STAR,           // 별 이펙트
    SWEAT,          // 땀 이펙트
    ANGER_MARK,     // 화남 마크

    // 날씨/환경
    RAIN,           // 비
    SNOW,           // 눈
    LEAVES,         // 낙엽
    SPARKLE,        // 반짝임

    // 사운드 전용
    SOUND_ONLY      // 사운드만 (화면 효과 없음)
}

// ==================== 유틸리티 ====================

/**
 * 화면 비율 위치를 Dp로 변환하는 헬퍼 함수
 * @param ratio 0.0 ~ 1.0 사이의 비율
 * @param screenSize 화면 크기 (dp)
 */
fun ratioToDp(ratio: Float, screenSize: Dp): Dp {
    return screenSize * ratio
}

/**
 * Dp를 화면 비율로 변환하는 헬퍼 함수
 */
fun dpToRatio(dp: Dp, screenSize: Dp): Float {
    return (dp / screenSize)
}
