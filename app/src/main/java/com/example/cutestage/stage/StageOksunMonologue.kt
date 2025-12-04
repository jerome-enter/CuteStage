package com.example.cutestage.stage

import androidx.compose.ui.unit.dp
import com.example.cutestage.R

/**
 * 옥순의 혼잣말 시나리오
 *
 * 어제 슈퍼마켓에서 겪은 황당한 경험을 혼자 중얼거리는 옥순
 * 모든 애니메이션 타입 활용
 */
object StageOksunMonologue {
    private val oksunVoice = CharacterVoice(
        pitch = 1.5f,
        speed = 65,
        duration = 48,
        volume = 0.7f,
    )

    /**
     * 옥순의 혼잣말 - 슈퍼마켓 대소동
     *
     * 사용된 애니메이션:
     * - IDLE: 기본 서있기
     * - SPEAK_NORMAL: 평범하게 이야기 시작
     * - WALKING: 이리저리 돌아다니며 회상
     * - ANNOYED: 짜증나는 순간 회상
     * - SPEAK_ANGRY: 화나서 말하기
     * - IDLE_ANNOYED: 짜증나서 서있기
     * - LISTENING: 상대방 말 듣던 자세 재현
     */
    fun createMonologueScenario() =
        theaterScript {
            debug(true) // 씬 1: 혼자 등장 (IDLE)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 2000L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.IDLE,
                    ),
                )
            } // 씬 2: 이야기 시작 (SPEAK_NORMAL)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    ),
                )

                dialogue(
                    text = "아휴... 어제 슈퍼마켓 갔다가...",
                    x = 130.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 3: 왼쪽으로 이동하며 회상 (WALKING)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    animationDuration = 1000,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.WALKING,
                    ),
                )

                dialogue(
                    text = "계산대에 줄 서있는데...",
                    x = 100.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 800L,
                    voice = oksunVoice,
                )
            } // 씬 4: 상황 설명 (SPEAK_NORMAL)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    ),
                )

                dialogue(
                    text = "앞에 아저씨가 카트를 가득~",
                    x = 80.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 5: 짜증나는 기억 (ANNOYED)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.ANNOYED,
                    ),
                )

                dialogue(
                    text = "30분을 기다렸어...",
                    x = 80.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 6: 오른쪽으로 이동하며 계속 (WALKING)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
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
                    text = "드디어 내 차례가 됐는데...",
                    x = 170.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 800L,
                    voice = oksunVoice,
                )
            } // 씬 7: 황당한 순간 (SPEAK_NORMAL)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 4000L,
            ) {
                character(
                    id = "oksun",
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
                    text = "점원이 '잠시만요~' 하더니",
                    x = 180.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 8: 화났던 순간 회상 (SPEAK_ANGRY)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "oksun",
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
                    text = "휴게실 가버렸어!!",
                    x = 180.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 9: 중앙으로 이동 (WALKING)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 2500L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    animationDuration = 1000,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.WALKING,
                    ),
                )

                dialogue(
                    text = "아니 그래서...",
                    x = 140.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 800L,
                    voice = oksunVoice,
                )
            } // 씬 10: 상황 설명 (SPEAK_NORMAL)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    ),
                )

                dialogue(
                    text = "다른 점원 불렀더니...",
                    x = 140.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 11: 듣고만 있어야 했던 순간 (LISTENING)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 4000L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.LISTENING,
                    ),
                )

                dialogue(
                    text = "\"신입이라 잘 몰라요~\" 라고...",
                    x = 120.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 12: 완전 짜증 (IDLE_ANNOYED)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.IDLE_ANNOYED,
                    ),
                )

                dialogue(
                    text = "그래서 40분 기다렸어...",
                    x = 130.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 13: 왼쪽으로 이동하며 분노 (WALKING)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    animationDuration = 1000,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.WALKING,
                    ),
                )

                dialogue(
                    text = "결국 매니저 불러서...",
                    x = 90.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 800L,
                    voice = oksunVoice,
                )
            } // 씬 14: 화내며 이야기 (SPEAK_ANGRY)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 80.dp,
                    y = 150.dp,
                    size = 100.dp,
                    scale = 1.15f,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.SPEAK_ANGRY,
                    ),
                )

                dialogue(
                    text = "\"죄송합니다\"만 10번 들었어!",
                    x = 70.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 15: 오른쪽으로 이동하며 결말 (WALKING)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 220.dp,
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
                    text = "그래서 내가...",
                    x = 180.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 800L,
                    voice = oksunVoice,
                )
            } // 씬 16: 반전 (SPEAK_NORMAL)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "oksun",
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
                    text = "쿠폰 5장 받았지 뭐야~",
                    x = 180.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 17: 중앙으로 이동 (WALKING)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 2500L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    animationDuration = 1000,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.WALKING,
                    ),
                )

                dialogue(
                    text = "헤헤~",
                    x = 140.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 800L,
                    voice = oksunVoice,
                )
            } // 씬 18: 뿌듯해하기 (SPEAK_NORMAL)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3500L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    ),
                )

                dialogue(
                    text = "오늘은 그 쿠폰 쓰러 가야지!",
                    x = 120.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 19: 또 짜증 (ANNOYED)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.ANNOYED,
                    ),
                )

                dialogue(
                    text = "...아 또 줄 서야 하나...",
                    x = 120.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 300L,
                    voice = oksunVoice,
                )
            } // 씬 20: 엔딩 (IDLE)
            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = 3000L,
            ) {
                character(
                    id = "oksun",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "옥순",
                    x = 150.dp,
                    y = 150.dp,
                    size = 100.dp,
                    voice = oksunVoice,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = CharacterAnimationType.IDLE,
                    ),
                )

                dialogue(
                    text = "에이, 모르겠다!",
                    x = 130.dp,
                    y = 60.dp,
                    speakerName = "옥순",
                    delayMillis = 500L,
                    voice = oksunVoice,
                )
            }
        }
}
