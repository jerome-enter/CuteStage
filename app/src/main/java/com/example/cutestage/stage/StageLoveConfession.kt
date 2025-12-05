package com.example.cutestage.stage

import androidx.compose.ui.unit.dp
import com.example.cutestage.R

/**
 * 사랑고백 시나리오
 *
 * 도도한 도시녀에게 오랫동안 친구로 지내던 남자가 드디어 고백하는 이야기.
 * 사용자 선택에 따라 두 가지 결말로 분기됩니다.
 * - 사귄다: 해피 엔딩 (춤, 박수)
 * - 거절한다: 새드 엔딩 (짜증, 홀로)
 *
 * 사용하는 모든 애니메이션:
 * - IDLE, IDLE_ANNOYED
 * - SPEAK_NORMAL, SPEAK_ANGRY
 * - LISTENING
 * - WALKING
 * - ANNOYED
 * - CLAP
 * - DANCING_TYPE_A, DANCING_TYPE_B, DANCING_TYPE_C
 * - SING_NORMAL, SING_CLIMAX, SING_PITCHUP (감정 표현으로 활용)
 */
object StageLoveConfession {
    // 음성 설정
    private val maleVoice = CharacterVoice(pitch = 0.9f, speed = 80, duration = 60, enabled = true)
    private val femaleVoice =
        CharacterVoice(pitch = 1.8f, speed = 70, duration = 45, enabled = true)

    /**
     * 사랑고백 전체 시나리오
     *
     * 씬 구성:
     * 0-7: 프롤로그 ~ 고백
     * 8: 선택지 (사귄다 / 거절한다)
     * 9-14: 해피 엔딩 (사귄다 선택 시)
     * 15-20: 새드 엔딩 (거절한다 선택 시)
     */
    fun createLoveConfessionScenario() = theaterScript {
        debug(true) // === 프롤로그: 오랜 친구 사이 (씬 0-2) ===
        // 씬 0: 두 사람이 걸어 들어옴
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 4000L,
        ) { // 남자 (왼쪽에서 걸어옴)
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = (-100).dp,
                y = 150.dp,
                size = 100.dp,
                alpha = 0f,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.WALKING,
                    isAnimating = true,
                ),
                voice = maleVoice,
            ) // 여자 (오른쪽에서 걸어옴)
            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 400.dp,
                y = 150.dp,
                size = 100.dp,
                alpha = 0f,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.WALKING,
                    isAnimating = true,
                ),
                voice = femaleVoice,
            )
        } // 씬 1: 등장 - 서로 마주보는 위치로 이동
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 3000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 80.dp,
                y = 150.dp,
                size = 100.dp,
                alpha = 1f,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.IDLE,
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 220.dp,
                y = 150.dp,
                size = 100.dp,
                alpha = 1f,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.IDLE,
                    isAnimating = true,
                ),
            )
        } // 씬 2: 일상 대화 1
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 5000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 80.dp,
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 220.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.LISTENING,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "서연아, 오늘도 예쁘다~",
                x = 100.dp,
                y = 60.dp,
                speakerName = "준호",
                delayMillis = 500L,
                voice = maleVoice,
            )
        } // 씬 3: 일상 대화 2 (도도한 반응)
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 5000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 80.dp,
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.LISTENING,
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 220.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.IDLE_ANNOYED,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "뭐야, 갑자기? 어색하게 왜 그래~",
                x = 200.dp,
                y = 60.dp,
                speakerName = "서연",
                delayMillis = 500L,
                voice = femaleVoice,
            )
        } // === 고백 준비 (씬 4-5) ===
        // 씬 4: 망설임
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 5500L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 80.dp,
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    isAnimating = true,
                    frameDuration = 800, // 떨리는 느낌
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 220.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.LISTENING,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "아니... 사실은... 오늘 할 말이 있어서...",
                x = 100.dp,
                y = 60.dp,
                speakerName = "준호",
                delayMillis = 500L,
                voice = maleVoice,
            )
        } // 씬 5: 진지한 분위기
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 4500L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 80.dp,
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.LISTENING,
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 220.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "응? 무슨 말인데?",
                x = 200.dp,
                y = 60.dp,
                speakerName = "서연",
                delayMillis = 500L,
                voice = femaleVoice,
            )
        } // === 고백! (씬 6-7) ===
        // 씬 6: 앞으로 나오며 고백 준비
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 3000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 100.dp, // 앞으로
                y = 160.dp, // 살짝 앞으로
                size = 100.dp,
                scale = 1.1f, // 크게
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.SING_PITCHUP, // 감정 고조
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 200.dp, // 약간 뒤로
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.LISTENING,
                    isAnimating = true,
                ),
            )
        } // 씬 7: 고백!
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 7000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 100.dp,
                y = 160.dp,
                size = 100.dp,
                scale = 1.1f,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.SING_CLIMAX, // 절정!
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 200.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.LISTENING,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "서연아... 나 너 정말 좋아해. 사귀자!",
                x = 100.dp,
                y = 60.dp,
                speakerName = "준호",
                delayMillis = 500L,
                voice = maleVoice,
            )
        } // === 선택지 (씬 8) ===
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 10000L, // 사용자가 선택할 때까지 대기
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 100.dp,
                y = 160.dp,
                size = 100.dp,
                scale = 1.1f,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.IDLE, // 긴장
                    isAnimating = true,
                    frameDuration = 1000,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 200.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.IDLE,
                    isAnimating = true,
                    frameDuration = 800,
                ),
            )

            dialogue(
                text = "여자의 선택은?",
                x = 130.dp,
                y = 60.dp,
                speakerName = null,
                delayMillis = 1000L,
                voice = CharacterVoice(
                    pitch = 1.2f,
                    speed = 60,
                    duration = 50,
                    enabled = true
                ), // 내레이션 음성
                choices = listOf(
                    Choice(text = "사귄다 ♥", nextSceneIndex = 9), // 해피 엔딩
                    Choice(text = "거절한다", nextSceneIndex = 15), // 새드 엔딩
                ),
            )
        } // === 해피 엔딩: 사귄다 (씬 9-14) ===
        // 씬 9: 승낙!
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 5000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 100.dp,
                y = 160.dp,
                size = 100.dp,
                scale = 1.1f,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.LISTENING,
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 200.dp,
                y = 155.dp, // 앞으로
                size = 100.dp,
                scale = 1.05f,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "...응, 나도 좋아. 사귀자!",
                x = 200.dp,
                y = 60.dp,
                speakerName = "서연",
                delayMillis = 1000L,
                voice = femaleVoice,
            )
        } // 씬 10: 기쁨 폭발!
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 4000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 100.dp,
                y = 160.dp,
                size = 100.dp,
                scale = 1.2f,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.SING_CLIMAX,
                    isAnimating = true,
                    frameDuration = 300,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 200.dp,
                y = 155.dp,
                size = 100.dp,
                scale = 1.05f,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.LISTENING,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "와!!!!! 진짜?! 고마워!!!",
                x = 100.dp,
                y = 60.dp,
                speakerName = "준호",
                delayMillis = 500L,
                voice = maleVoice,
            )
        } // 씬 11: 함께 기뻐하기
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 3000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 120.dp, // 가까이
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.SING_NORMAL,
                    isAnimating = true,
                    frameDuration = 400,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 180.dp, // 가까이
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.SING_NORMAL,
                    isAnimating = true,
                    frameDuration = 400,
                ),
            )
        } // 씬 12: 춤 타입 A
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 3000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 120.dp,
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.DANCING_TYPE_A,
                    isAnimating = true,
                    frameDuration = 400,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 180.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.DANCING_TYPE_A,
                    isAnimating = true,
                    frameDuration = 400,
                ),
            )

            dialogue(
                text = "♥ 사랑이 시작되었다! ♥",
                x = 130.dp,
                y = 60.dp,
                speakerName = null,
                delayMillis = 500L,
                voice = CharacterVoice(
                    pitch = 1.2f,
                    speed = 60,
                    duration = 50,
                    enabled = true
                ), // 내레이션
            )
        } // 씬 13: 춤 타입 B
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 3000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 120.dp,
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.DANCING_TYPE_B,
                    isAnimating = true,
                    frameDuration = 300,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 180.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.DANCING_TYPE_B,
                    isAnimating = true,
                    frameDuration = 300,
                ),
            )
        } // 씬 14: 박수!
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 4000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 120.dp,
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.CLAP,
                    isAnimating = true,
                    frameDuration = 300,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 180.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.CLAP,
                    isAnimating = true,
                    frameDuration = 300,
                ),
            )

            dialogue(
                text = "♥ 해피 엔딩 ♥",
                x = 130.dp,
                y = 60.dp,
                speakerName = null,
                delayMillis = 500L,
                voice = CharacterVoice(
                    pitch = 1.2f,
                    speed = 60,
                    duration = 50,
                    enabled = true
                ), // 내레이션
            )
        } // === 새드 엔딩: 거절한다 (씬 15-20) ===
        // 씬 15: 거절
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 5500L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 100.dp,
                y = 160.dp,
                size = 100.dp,
                scale = 1.1f,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.LISTENING,
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 200.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "미안해... 난 그냥 친구로만 생각했어.",
                x = 200.dp,
                y = 60.dp,
                speakerName = "서연",
                delayMillis = 1000L,
                voice = femaleVoice,
            )
        } // 씬 16: 충격
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 4000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 90.dp, // 뒤로
                y = 150.dp,
                size = 100.dp,
                scale = 0.9f, // 작게
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.ANNOYED, // 충격
                    isAnimating = true,
                    frameDuration = 800,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 200.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.IDLE_ANNOYED,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "...그렇구나.",
                x = 100.dp,
                y = 60.dp,
                speakerName = "준호",
                delayMillis = 1000L,
                voice = maleVoice,
            )
        } // 씬 17: 서연 떠남
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 4000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 90.dp,
                y = 150.dp,
                size = 100.dp,
                scale = 0.9f,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.IDLE,
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 200.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "...그럼 난 먼저 갈게.",
                x = 200.dp,
                y = 60.dp,
                speakerName = "서연",
                delayMillis = 500L,
                voice = femaleVoice,
            )
        } // 씬 18: 서연 퇴장 (걸어서)
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 3000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 90.dp,
                y = 150.dp,
                size = 100.dp,
                scale = 0.9f,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.IDLE,
                    isAnimating = true,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "서연",
                x = 400.dp, // 오른쪽으로 퇴장
                y = 150.dp,
                size = 100.dp,
                alpha = 0f, // 사라짐
                flipX = true,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.WALKING,
                    isAnimating = true,
                ),
            )
        } // 씬 19: 홀로 남음
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 4000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 150.dp, // 중앙
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.ANNOYED,
                    isAnimating = true,
                    frameDuration = 1000,
                ),
            )

            dialogue(
                text = "...하아.",
                x = 130.dp,
                y = 60.dp,
                speakerName = "준호",
                delayMillis = 1000L,
                voice = maleVoice,
            )
        } // 씬 20: 새드 엔딩
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 4000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "준호",
                x = 150.dp,
                y = 150.dp,
                size = 100.dp,
                alpha = 0.5f, // 흐려짐
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.IDLE,
                    isAnimating = true,
                ),
            )

            dialogue(
                text = "새드 ��딩...",
                x = 130.dp,
                y = 60.dp,
                speakerName = null,
                delayMillis = 1000L,
                voice = CharacterVoice(
                    pitch = 1.2f,
                    speed = 60,
                    duration = 50,
                    enabled = true
                ), // 내레이션
            )
        }
    }
}
