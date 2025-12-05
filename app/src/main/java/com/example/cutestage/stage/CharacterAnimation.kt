package com.example.cutestage.stage

import androidx.annotation.DrawableRes
import com.example.cutestage.R

/**
 * 캐릭터 애니메이션 타입
 */
enum class CharacterAnimationType {
    IDLE, // 서있기
    IDLE_ANNOYED, // 짜증나서 서있기
    SPEAK_NORMAL, // 평범하게 말하기
    SPEAK_ANGRY, // 화나서 말하기
    LISTENING, // 상대 말 듣기
    WALKING, // 걷기
    ANNOYED, // 짜증
    CLAP, // 박수
    DANCING_TYPE_A, // 춤 타입 A
    DANCING_TYPE_B, // 춤 타입 B
    DANCING_TYPE_C, // 춤 타입 C
    SING_NORMAL, // 일반 노래
    SING_CLIMAX, // 노래 클라이맥스
    SING_PITCHUP, // 높은 음
}

/**
 * 캐릭터 성별
 */
enum class CharacterGender {
    MALE,
    FEMALE,
}

/**
 * 캐릭터 애니메이션 리소스 매니저
 *
 * 각 애니메이션은 2프레임으로 구성되어 자연스러운 움직임 표현
 */
object CharacterAnimationResources {
    /**
     * 애니메이션 리소스 가져오기
     *
     * @param gender 캐릭터 성별
     * @param animation 애니메이션 타입
     * @param frame 프레임 번호 (1 or 2)
     * @return Drawable 리소스 ID
     */
    @DrawableRes
    fun getAnimationResource(
        gender: CharacterGender,
        animation: CharacterAnimationType,
        frame: Int,
    ): Int {
        val genderPrefix = when (gender) {
            CharacterGender.MALE -> "male"
            CharacterGender.FEMALE -> "female"
        }
        val animationSuffix = when (animation) {
            CharacterAnimationType.IDLE -> "idle"
            CharacterAnimationType.IDLE_ANNOYED -> "idle_annoyed"
            CharacterAnimationType.SPEAK_NORMAL -> "speak_normal"
            CharacterAnimationType.SPEAK_ANGRY -> "speak_angry"
            CharacterAnimationType.LISTENING -> "listening"
            CharacterAnimationType.WALKING -> if (gender == CharacterGender.MALE) "waking" else "walking" // 남자는 오타(waking)
            CharacterAnimationType.ANNOYED -> "annoyed"
            CharacterAnimationType.CLAP -> "clap"
            CharacterAnimationType.DANCING_TYPE_A -> "dancing_type_a"
            CharacterAnimationType.DANCING_TYPE_B -> "dancing_type_b"
            CharacterAnimationType.DANCING_TYPE_C -> "dancing_type_c"
            CharacterAnimationType.SING_NORMAL -> "sing_nomal" // 파일명 오타(nomal)
            CharacterAnimationType.SING_CLIMAX -> "sing_climax"
            CharacterAnimationType.SING_PITCHUP -> "sing_pitchup"
        } // stage_{gender}_1_{animation}_{frame}
        val resourceName = "stage_${genderPrefix}_1_${animationSuffix}_$frame"

        return when (resourceName) { // 남자 캐릭터 - 기본
            "stage_male_1_idle_1" -> R.drawable.stage_male_1_idle_1
            "stage_male_1_idle_2" -> R.drawable.stage_male_1_idle_2
            "stage_male_1_idle_annoyed_1" -> R.drawable.stage_male_1_idle_annoyed_1
            "stage_male_1_idle_annoyed_2" -> R.drawable.stage_male_1_idle_annoyed_2
            "stage_male_1_speak_normal_1" -> R.drawable.stage_male_1_speak_normal_1
            "stage_male_1_speak_normal_2" -> R.drawable.stage_male_1_speak_normal_2
            "stage_male_1_speak_angry_1" -> R.drawable.stage_male_1_speak_angry_1
            "stage_male_1_speak_angry_2" -> R.drawable.stage_male_1_speak_angry_2
            "stage_male_1_listening_1" -> R.drawable.stage_male_1_listening_1
            "stage_male_1_listening_2" -> R.drawable.stage_male_1_listening_2
            "stage_male_1_waking_1" -> R.drawable.stage_male_1_waking_1
            "stage_male_1_waking_2" -> R.drawable.stage_male_1_waking_2
            "stage_male_1_annoyed_1" -> R.drawable.stage_male_1_annoyed_1
            "stage_male_1_annoyed_2" -> R.drawable.stage_male_1_annoyed_2 // 남자 캐릭터 - 새로운 애니메이션
            "stage_male_1_clap_1" -> R.drawable.stage_male_1_clap_1
            "stage_male_1_clap_2" -> R.drawable.stage_male_1_clap_2
            "stage_male_1_dancing_type_a_1" -> R.drawable.stage_male_1_dancing_type_a_1
            "stage_male_1_dancing_type_a_2" -> R.drawable.stage_male_1_dancing_type_a_2
            "stage_male_1_dancing_type_b_1" -> R.drawable.stage_male_1_dancing_type_b_1
            "stage_male_1_dancing_type_b_2" -> R.drawable.stage_male_1_dancing_type_b_2
            "stage_male_1_dancing_type_c_1" -> R.drawable.stage_male_1_dancing_type_c_1
            "stage_male_1_dancing_type_c_2" -> R.drawable.stage_male_1_dancing_type_c_2
            "stage_male_1_sing_nomal_1" -> R.drawable.stage_male_1_sing_nomal_1
            "stage_male_1_sing_nomal_2" -> R.drawable.stage_male_1_sing_nomal_2
            "stage_male_1_sing_climax_1" -> R.drawable.stage_male_1_sing_climax_1
            "stage_male_1_sing_climax_2" -> R.drawable.stage_male_1_sing_climax_2
            "stage_male_1_sing_pitchup_1" -> R.drawable.stage_male_1_sing_pitchup_1
            "stage_male_1_sing_pitchup_2" -> R.drawable.stage_male_1_sing_pitchup_2 // 여자 캐릭터 - 기본
            "stage_female_1_idle_1" -> R.drawable.stage_female_1_idle_1
            "stage_female_1_idle_2" -> R.drawable.stage_female_1_idle_2
            "stage_female_1_idle_annoyed_1" -> R.drawable.stage_female_1_idle_annoyed_1
            "stage_female_1_idle_annoyed_2" -> R.drawable.stage_female_1_idle_annoyed_2
            "stage_female_1_speak_normal_1" -> R.drawable.stage_female_1_speak_normal_1
            "stage_female_1_speak_normal_2" -> R.drawable.stage_female_1_speak_normal_2
            "stage_female_1_speak_angry_1" -> R.drawable.stage_female_1_speak_angry_1
            "stage_female_1_speak_angry_2" -> R.drawable.stage_female_1_speak_angry_2
            "stage_female_1_listening_1" -> R.drawable.stage_female_1_listening_1
            "stage_female_1_listening_2" -> R.drawable.stage_female_1_listening_2
            "stage_female_1_walking_1" -> R.drawable.stage_female_1_walking_1
            "stage_female_1_walking_2" -> R.drawable.stage_female_1_walking_2
            "stage_female_1_annoyed_1" -> R.drawable.stage_female_1_annoyed_1
            "stage_female_1_annoyed_2" -> R.drawable.stage_female_1_annoyed_2 // 여자 캐릭터 - 새로운 애니메이션
            "stage_female_1_clap_1" -> R.drawable.stage_female_1_clap_1
            "stage_female_1_clap_2" -> R.drawable.stage_female_1_clap_2
            "stage_female_1_dancing_type_a_1" -> R.drawable.stage_female_1_dancing_type_a_1
            "stage_female_1_dancing_type_a_2" -> R.drawable.stage_female_1_dancing_type_a_2
            "stage_female_1_dancing_type_b_1" -> R.drawable.stage_female_1_dancing_type_b_1
            "stage_female_1_dancing_type_b_2" -> R.drawable.stage_female_1_dancing_type_b_2
            "stage_female_1_dancing_type_c_1" -> R.drawable.stage_female_1_dancing_type_c_1
            "stage_female_1_dancing_type_c_2" -> R.drawable.stage_female_1_dancing_type_c_2
            "stage_female_1_sing_nomal_1" -> R.drawable.stage_female_1_sing_nomal_1
            "stage_female_1_sing_nomal_2" -> R.drawable.stage_female_1_sing_nomal_2
            "stage_female_1_sing_climax_1" -> R.drawable.stage_female_1_sing_climax_1
            "stage_female_1_sing_climax_2" -> R.drawable.stage_female_1_sing_climax_2
            "stage_female_1_sing_pitchup_1" -> R.drawable.stage_female_1_sing_pitchup_1
            "stage_female_1_sing_pitchup_2" -> R.drawable.stage_female_1_sing_pitchup_2
            else -> R.drawable.stage_ch_m_1 // 기본값
        }
    }

    /**
     * 애니메이션 프레임 수
     */
    const val FRAME_COUNT = 2

    /**
     * 기본 프레임 간격 (ms)
     */
    const val DEFAULT_FRAME_DURATION = 500L
}

/**
 * 캐릭터 애니메이션 상태
 */
data class CharacterAnimationState(
    val gender: CharacterGender,
    val currentAnimation: CharacterAnimationType = CharacterAnimationType.IDLE,
    val isAnimating: Boolean = true,
    val frameDuration: Long = CharacterAnimationResources.DEFAULT_FRAME_DURATION,
)
