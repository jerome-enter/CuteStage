package com.example.cutestage.stage

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.unit.dp

/**
 * 샘플 연극 스크립트 모음
 * 실제 사용 시 참고할 수 있는 다양한 패턴 예시
 */
object SampleTheaterScripts {
    /**
     * 기본 대화 시나리오
     * - 두 캐릭터의 간단한 만남과 대화
     */
    fun basicConversation(
        heroRes: Int,
        npcRes: Int,
        backgroundRes: Int,
    ) = theaterScript {
        debug(false) // 씬 1: 주인공 등장
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 2500L,
        ) {
            character(
                id = "hero",
                imageRes = heroRes,
                name = "주인공",
                x = (-100).dp, // 화면 밖에서 시작
                y = 120.dp,
                alpha = 0f,
            )

            dialogue(
                text = "...",
                x = 50.dp,
                y = 80.dp,
                delayMillis = 1000L,
            )
        } // 씬 2: 주인공 화면 안으로 이동
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 2000L,
        ) {
            character(
                id = "hero",
                imageRes = heroRes,
                name = "주인공",
                x = 50.dp,
                y = 120.dp,
                alpha = 1f,
                animationDuration = 1000,
            )

            dialogue(
                text = "여기가 어디지?",
                x = 120.dp,
                y = 80.dp,
                speakerName = "주인공",
                delayMillis = 500L,
            )
        } // 씬 3: NPC 등장
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 3000L,
        ) {
            character(
                id = "hero",
                imageRes = heroRes,
                name = "주인공",
                x = 50.dp,
                y = 120.dp,
                flipX = true, // NPC를 바라봄
            )

            character(
                id = "npc",
                imageRes = npcRes,
                name = "마을 주민",
                x = 250.dp,
                y = 120.dp,
                scale = 1.1f,
            )

            dialogue(
                text = "안녕하세요! 저희 마을에 오신 걸 환영합니다.",
                x = 200.dp,
                y = 60.dp,
                speakerName = "마을 주민",
                delayMillis = 500L,
                typingSpeedMs = 40L,
            )
        } // 씬 4: 주인공 반응
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 2500L,
        ) {
            character(
                id = "hero",
                imageRes = heroRes,
                name = "주인공",
                x = 50.dp,
                y = 120.dp,
                flipX = true,
                scale = 1.05f, // 살짝 강조
            )

            character(
                id = "npc",
                imageRes = npcRes,
                name = "마을 주민",
                x = 250.dp,
                y = 120.dp,
            )

            dialogue(
                text = "고맙습니다!",
                x = 100.dp,
                y = 80.dp,
                speakerName = "주인공",
                delayMillis = 300L,
            )
        }
    }

    /**
     * 액션 시나리오
     * - 빠른 움직임과 동적인 연출
     */
    fun actionSequence(
        characterRes: Int,
        enemyRes: Int,
        backgroundRes: Int,
    ) = theaterScript {
        debug(false) // 씬 1: 대치
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 2000L,
        ) {
            character(
                id = "hero",
                imageRes = characterRes,
                name = "전사",
                x = 50.dp,
                y = 120.dp,
            )

            character(
                id = "enemy",
                imageRes = enemyRes,
                name = "적",
                x = 250.dp,
                y = 120.dp,
                flipX = true,
            )

            dialogue(
                text = "크앙!",
                x = 220.dp,
                y = 80.dp,
                speakerName = "적",
                delayMillis = 500L,
            )
        } // 씬 2: 돌진
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 1500L,
        ) {
            character(
                id = "hero",
                imageRes = characterRes,
                name = "전사",
                x = 150.dp, // 빠르게 이동
                y = 120.dp,
                animationDuration = 300,
                easing = LinearEasing,
            )

            character(
                id = "enemy",
                imageRes = enemyRes,
                name = "적",
                x = 200.dp, // 뒤로 밀림
                y = 120.dp,
                flipX = true,
                scale = 0.95f,
                animationDuration = 300,
            )

            dialogue(
                text = "받아라!",
                x = 120.dp,
                y = 80.dp,
                speakerName = "전사",
                delayMillis = 100L,
            )
        } // 씬 3: 적 퇴장
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 2000L,
        ) {
            character(
                id = "hero",
                imageRes = characterRes,
                name = "전사",
                x = 150.dp,
                y = 120.dp,
                scale = 1.1f, // 승리 포즈
            )

            character(
                id = "enemy",
                imageRes = enemyRes,
                name = "적",
                x = 350.dp, // 화면 밖으로
                y = 120.dp,
                flipX = true,
                alpha = 0f,
                animationDuration = 800,
            )

            dialogue(
                text = "승리했다!",
                x = 100.dp,
                y = 80.dp,
                speakerName = "전사",
                delayMillis = 800L,
            )
        }
    }

    /**
     * 감정 표현 시나리오
     * - 캐릭터 크기, 회전으로 감정 표현
     */
    fun emotionalScene(
        characterRes: Int,
        backgroundRes: Int,
    ) = theaterScript {
        debug(false) // 씬 1: 평온
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 2000L,
        ) {
            character(
                id = "character",
                imageRes = characterRes,
                name = "주인공",
                x = 150.dp,
                y = 120.dp,
                scale = 1f,
            )

            dialogue(
                text = "좋은 날씨네...",
                x = 120.dp,
                y = 80.dp,
                speakerName = "주인공",
            )
        } // 씬 2: 놀람! (커지고 살짝 회전)
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 1500L,
        ) {
            character(
                id = "character",
                imageRes = characterRes,
                name = "주인공",
                x = 150.dp,
                y = 100.dp,
                scale = 1.3f,
                rotation = -5f,
                animationDuration = 200,
                easing = EaseInOut,
            )

            dialogue(
                text = "앗!",
                x = 120.dp,
                y = 50.dp,
                speakerName = "주인공",
                delayMillis = 100L,
                typingSpeedMs = 30L,
            )
        } // 씬 3: 진정 (원래대로)
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 2000L,
        ) {
            character(
                id = "character",
                imageRes = characterRes,
                name = "주인공",
                x = 150.dp,
                y = 120.dp,
                scale = 1f,
                rotation = 0f,
                animationDuration = 600,
            )

            dialogue(
                text = "...놀랐잖아.",
                x = 120.dp,
                y = 80.dp,
                speakerName = "주인공",
                delayMillis = 500L,
            )
        }
    }

    /**
     * 여러 캐릭터 군중 씬
     * - 3명 이상의 캐릭터가 등장하는 복잡한 장면
     */
    fun crowdScene(
        char1Res: Int,
        char2Res: Int,
        char3Res: Int,
        backgroundRes: Int,
    ) = theaterScript {
        debug(false) // 씬 1: 3명 등장
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 3000L,
        ) {
            character(
                id = "char1",
                imageRes = char1Res,
                name = "A",
                x = 30.dp,
                y = 120.dp,
            )

            character(
                id = "char2",
                imageRes = char2Res,
                name = "B",
                x = 150.dp,
                y = 120.dp,
                scale = 1.1f, // 중앙 캐릭터 강조
            )

            character(
                id = "char3",
                imageRes = char3Res,
                name = "C",
                x = 270.dp,
                y = 120.dp,
                flipX = true,
            )

            dialogue(
                text = "모두 모였네요!",
                x = 120.dp,
                y = 60.dp,
                speakerName = "B",
                delayMillis = 500L,
            )
        } // 씬 2: 각자 대화
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 2500L,
        ) {
            character(
                id = "char1",
                imageRes = char1Res,
                name = "A",
                x = 30.dp,
                y = 120.dp,
                scale = 1.1f, // A 강조
            )

            character(
                id = "char2",
                imageRes = char2Res,
                name = "B",
                x = 150.dp,
                y = 120.dp,
            )

            character(
                id = "char3",
                imageRes = char3Res,
                name = "C",
                x = 270.dp,
                y = 120.dp,
                flipX = true,
            )

            dialogue(
                text = "출발합시다!",
                x = 80.dp,
                y = 80.dp,
                speakerName = "A",
                delayMillis = 300L,
            )
        } // 씬 3: 모두 이동
        scene(
            backgroundRes = backgroundRes,
            durationMillis = 2000L,
        ) {
            character(
                id = "char1",
                imageRes = char1Res,
                name = "A",
                x = 130.dp, // 모두 중앙으로
                y = 120.dp,
                animationDuration = 800,
            )

            character(
                id = "char2",
                imageRes = char2Res,
                name = "B",
                x = 150.dp,
                y = 120.dp,
            )

            character(
                id = "char3",
                imageRes = char3Res,
                name = "C",
                x = 170.dp,
                y = 120.dp,
                flipX = true,
                animationDuration = 800,
            )

            dialogue(
                text = "힘을 합쳐서!",
                x = 100.dp,
                y = 60.dp,
                delayMillis = 800L,
            )
        }
    }
}
/**
 * 사용 예시:
 *
 * @Composable
 * fun ExampleScreen() {
 *     val script = remember {
 *         SampleTheaterScripts.basicConversation(
 *             heroRes = R.drawable.hero,
 *             npcRes = R.drawable.npc,
 *             backgroundRes = R.drawable.forest
 *         )
 *     }
 *
 *     StageView(
 *         script = script,
 *         onScriptEnd = {
 *             // 스크립트 종료 처리
 *         }
 *     )
 * }
 */
