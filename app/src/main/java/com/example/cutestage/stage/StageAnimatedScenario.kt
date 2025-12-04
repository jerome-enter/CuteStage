package com.example.cutestage.stage

import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.unit.dp
import com.example.cutestage.R

/**
 * 스프라이트 애니메이션을 활용한 유머러스한 시나리오
 *
 * "부부싸움" - 상철과 옥순의 유쾌한 일상
 */
object StageAnimatedScenario {
    // 캐릭터 음성 설정
    private val sangchulVoice = CharacterVoice(
        pitch = 0.8f,
        speed = 90,
        duration = 55,
        volume = 0.6f,
    )
    private val oksunVoice = CharacterVoice(
        pitch = 1.5f,
        speed = 65,
        duration = 48,
        volume = 0.7f,
    )

    /**
     * 유머러스한 부부싸움 시나리오
     *
     * 스프라이트 애니메이션 활용:
     * - idle: 기본 서있기
     * - speak_normal: 평범하게 말하기
     * - speak_angry: 화나서 말하기
     * - listening: 상대 말 듣기
     * - annoyed: 짜증
     * - walking: 걷기
     */
    fun createAnimatedScenario() =
        theaterScript {
            debug(true) // 씬 1: 평화로운 시작
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 2000L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.IDLE,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.IDLE,
                    ),
                )
            } // 씬 2: 상철이 말을 꺼낸다 (평범하게)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.LISTENING,
                    ),
                )

                dialogue(
                    text = "여보, 오늘 저녁 뭐 먹을까?",
                    x = 130.dp,
                    y = 60.dp,
                    speakerName = "상철",
                    delayMillis = 300L,
                    voice = sangchulVoice,
                )
            } // 씬 3: 옥순의 반응 (평범하게)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.LISTENING,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    ),
                )

                dialogue(
                    text = "글쎄... 라면?",
                    x = 180.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 4: 상철의 불만 (짜증 시작)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.ANNOYED,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.LISTENING,
                    ),
                )

                dialogue(
                    text = "어제도 라면이었잖아...",
                    x = 120.dp,
                    y = 60.dp,
                    speakerName = "상철",
                    delayMillis = 300L,
                    voice = sangchulVoice,
                )
            } // 씬 5: 옥순 짜증 (본격 시작)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.LISTENING,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.IDLE_ANNOYED,
                    ),
                )

                dialogue(
                    text = "그럼 당신이 해요!",
                    x = 180.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 6: 상철 당황 (화내기 시작)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.SPEAK_ANGRY,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.LISTENING,
                    ),
                )

                dialogue(
                    text = "내가 언제 못한다고 했어!",
                    x = 110.dp,
                    y = 60.dp,
                    speakerName = "상철",
                    delayMillis = 300L,
                    voice = sangchulVoice,
                )
            } // 씬 7: 옥순 완전 화남
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.LISTENING,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    scale = 1.1f,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.SPEAK_ANGRY,
                    ),
                )

                dialogue(
                    text = "그럼 지금 당장 해봐요!",
                    x = 170.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 8: 상철 뒤로 물러남 (walking)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 2500L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 30.dp,
                    y = 150.dp,
                    size = 100.dp,
                    animationDuration = 1000,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.WALKING,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.IDLE_ANNOYED,
                    ),
                )

                dialogue(
                    text = "앗... 잠깐...",
                    x = 80.dp,
                    y = 60.dp,
                    speakerName = "상철",
                    delayMillis = 800L,
                    voice = sangchulVoice,
                )
            } // 씬 9: 옥순 다가옴 (walking)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 2500L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 30.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.ANNOYED,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    animationDuration = 1000,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.WALKING,
                    ),
                )

                dialogue(
                    text = "왜? 못하겠어?",
                    x = 130.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 800L,
                    voice = oksunVoice,
                )
            } // 씬 10: 상철 항복 (speak_normal)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 30.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.LISTENING,
                    ),
                )

                dialogue(
                    text = "라면... 좋지... 맛있어...",
                    x = 80.dp,
                    y = 60.dp,
                    speakerName = "상철",
                    delayMillis = 300L,
                    voice = sangchulVoice,
                )
            } // 씬 11: 옥순 승리 (speak_normal)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 30.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.LISTENING,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = true,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    ),
                )

                dialogue(
                    text = "그럼 라면 끓여줄게~",
                    x = 130.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 12: 함께 중앙으로 (walking, 화해)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 120.dp,
                    y = 150.dp,
                    size = 100.dp,
                    animationDuration = 1500,
                    easing = LinearEasing,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.WALKING,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 180.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = false,
                    animationDuration = 1500,
                    easing = LinearEasing,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.WALKING,
                    ),
                )

                dialogue(
                    text = "역시 우리는 라면이 최고야!",
                    x = 110.dp,
                    y = 60.dp,
                    delayMillis = 1500L,
                    voice = sangchulVoice,
                )
            } // 씬 13: 엔딩 (idle, 함께 서있기)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "상철",
                    x = 120.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = sangchulVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = CharacterAnimationType.IDLE,
                    ),
                )

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 180.dp,
                    y = 150.dp,
                    size = 100.dp,
                    flipX = false,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.IDLE,
                    ),
                )

                dialogue(
                    text = "완!",
                    x = 140.dp,
                    y = 60.dp,
                    delayMillis = 1000L,
                    typingSpeedMs = 200L,
                )
            }
        }
}
