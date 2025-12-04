package com.example.cutestage.stage

import androidx.compose.ui.unit.dp
import com.example.cutestage.R

/**
 * "폭삭 속았수다 - 제1막 조기 밥상" 시나리오 (개정판)
 *
 * 제주도 초가집을 배경으로 펼쳐지는 가족 드라마
 * 물질을 하는 엄마 광례가 딸 애순을 데려가기 위해
 * 작은아버지 집에 쳐들어가는 이야기
 *
 * 주제: 모성애, 가족 갈등, 그리고 따뜻한 웃음
 *
 * 씬 구성:
 * - 씬 1-18: 본편 (house 배경) - 갈등과 결별
 * - 씬 19-34: 에필로그 (sea 배경) - 모녀의 대화
 * 총 36개 씬
 *
 * 등장인물:
 * - 광례 (40대, 여): 애순의 엄마, 제주 해녀
 * - 작은아버지 (40대, 남): 애순의 삼촌, 이기적
 * - 할머니 (70대, 여): 애순의 친할머니
 * - 애순 (10대, 여): 딸, 조숙한 소녀
 *
 * 리소스 명명 규칙: 인물_자세_감정_소지품.png
 * - 숫자가 붙은 리소스 (예: mother_walk1.png, mother_walk2.png)는
 *   1->2->1 순으로 반복하여 애니메이션 효과
 *
 * 애니메이션 리소스:
 * - mother_walk1/2.png: 광례 걷기 애니메이션
 * - mother_throw1/2.png: 조기 던지기 애니메이션
 * - mother_stand_angry1/2.png: 분노 애니메이션
 * - father_stand_angry1/2.png: 작은아버지 분노 애니메이션
 * - father_sit_suprise1/2.png: 놀람 애니메이션
 * - daughter_walk1/2.png: 애순 걷기 애니메이션
 *
 * 정적 리소스:
 * - daughter_sit.png, daughter_sit_suprise.png
 * - father_sit.png
 * - grandma_sit.png, grandma_sit_sad.png, grandma_sit_suprise.png
 * - house.png (초가집 배경)
 * - sea.png (바다 배경)
 */
object StageFoolishTrick {
    // 캐릭터 음성 설정
    private val gwangryeVoice = CharacterVoice(
        pitch = 1.2f,  // 중간 높이의 여성 목소리
        speed = 80,     // 빠르고 다급하게
        duration = 50,
        volume = 0.7f   // 큰 목소리
    )

    private val uncleVoice = CharacterVoice(
        pitch = 0.7f,   // 낮고 무거운 남성 목소리
        speed = 95,     // 느리고 무겁게
        duration = 55,
        volume = 0.6f
    )

    private val grandmaVoice = CharacterVoice(
        pitch = 1.1f,   // 높지만 떨리는 노인 목소리
        speed = 110,    // 느리고 힘없이
        duration = 52,
        volume = 0.4f   // 작은 목소리
    )

    private val aesunVoice = CharacterVoice(
        pitch = 1.6f,   // 높고 어린 목소리
        speed = 75,     // 빠르고 불안하게
        duration = 45,
        volume = 0.45f  // 작고 떨리는 목소리
    )

    /**
     * "폭삭 속았수다 - 제1막 조기 밥상" 시나리오
     */
    fun createFoolishTrickScenario() = theaterScript {
        debug(true)

        // 씬 1: 평화로운 초가집
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 2500L
        ) {
            // 작은아버지 - 평상에 앉아있음
            character(
                id = "uncle",
                imageRes = R.drawable.father_sit,
                name = "작은아버지",
                x = 245.dp,
                y = 170.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            // 할머니 - 평상 옆
            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            // 애순 - 수돗가에서 설거지
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            dialogue(
                text = "(제주 초가집, 저녁 무렵...)",
                x = 110.dp,
                y = 40.dp,
                delayMillis = 500L,
                typingSpeedMs = 50L
            )
        }

        // 씬 2: 엄마 등장! 조기를 들고 나타남 (왼쪽 끝에서)
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 2000L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_sit,
                name = "작은아버지",
                x = 245.dp,
                y = 170.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            // 광례 등장 - 조기를 들고 왼쪽 끝에서 시작
            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,
                name = "엄마",
                x = -20.dp,  // 왼쪽 끝에서 시작
                y = 200.dp,  // 아래쪽에서 시작 (무대 하단)
                size = 95.dp,
                scale = 1.1f,
                flipX = true,  // 오른쪽을 향하도록
                voice = gwangryeVoice
            )

            // 조기 소품 - 엄마와 함께 왼쪽에서 시작
            character(
                id = "fish",
                imageRes = R.drawable.fish,
                name = "",
                x = 5.dp,  // 엄마 오른쪽 손 위치
                y = 220.dp,  // 아래쪽에서 시작
                size = 80.dp
            )

        }

        // 씬 2-2: 엄마가 가운데로 이동
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 2000L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_sit,
                name = "작은아버지",
                x = 245.dp,
                y = 170.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            // 광례 - 가운데로 이동 (위로 올라감)
            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,
                name = "엄마",
                x = 160.dp,  // 가운데로 이동
                y = 135.dp,  // 무대 가운데로 올라옴
                size = 95.dp,
                scale = 1.15f,
                flipX = true,  // 오른쪽을 향하도록
                animationDuration = 1500,
                voice = gwangryeVoice
            )

            // 조기 - 엄마와 함께 가운데로 이동
            character(
                id = "fish",
                imageRes = R.drawable.fish,
                name = "",
                x = 185.dp,  // 엄마 오른쪽 손 위치
                y = 155.dp,  // 무대 가운데 높이
                size = 80.dp,
                animationDuration = 1500
            )

        }

        // 씬 2-3: 조기 던지기 시작 (mother_throw1)
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 1500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_sit_suprise1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 170.dp,
                size = 90.dp,
                scale = 1.05f,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_suprise,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            // 광례 - 던지기 동작 1
            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_throw1,
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.2f,
                flipX = true,  // 오른쪽을 향하도록
                voice = gwangryeVoice
            )

            // 조기 - 던지기 시작
            character(
                id = "fish",
                imageRes = R.drawable.fish,
                name = "",
                x = 185.dp,
                y = 145.dp,
                size = 80.dp,
                animationDuration = 500
            )

            dialogue(
                text = "옛다! 조구새끼!",
                x = 140.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 40L,
                voice = gwangryeVoice
            )
        }

        // 씬 2-4: 조기 던지기 완료 (mother_throw2) - 조기가 오른쪽으로 날아감
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 1500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_sit_suprise2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 170.dp,
                size = 90.dp,
                scale = 1.05f,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_suprise,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            // 광례 - 던지기 동작 2 (완료)
            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_throw2,
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.2f,
                flipX = true,  // 오른쪽을 향하도록
                voice = gwangryeVoice
            )

            // 조기 - 작은아버지/할머니 앞에 떨어짐
            character(
                id = "fish",
                imageRes = R.drawable.fish,
                name = "",
                x = 220.dp,
                y = 165.dp,  // 평상 높이로 조정
                size = 80.dp,
                animationDuration = 800
            )

            dialogue(
                text = "(평상에 조기가 떨어진다)",
                x = 140.dp,
                y = 30.dp,
                delayMillis = 300L,
                typingSpeedMs = 50L
            )
        }

        // 씬 3: 모두 놀람 / 작은아빠 항의 - 애니메이션 1
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 2000L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                scale = 1.1f,
                animationDuration = 600,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_suprise,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry1,  // 애니메이션 1
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.15f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "아니 여거봐! 정신놨소?",
                x = 170.dp,
                y = 35.dp,
                speakerName = "작은아버지",
                delayMillis = 300L,
                typingSpeedMs = 42L,
                voice = uncleVoice
            )
        }

        // 씬 3-2: 애니메이션 2
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 2000L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                scale = 1.1f,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry2,  // 애니메이션 2
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.15f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "지금 여기가 어디라고",
                x = 170.dp,
                y = 35.dp,
                speakerName = "작은아버지",
                delayMillis = 300L,
                typingSpeedMs = 42L,
                voice = uncleVoice
            )
        }

        // 씬 4: 엄마의 반문 - 애니메이션 1
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 2500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry1,  // 애니메이션 1
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.15f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "조구 애끼요?",
                x = 140.dp,
                y = 35.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 50L,
                voice = gwangryeVoice
            )
        }

        // 씬 5: 작은아빠 당황
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 2500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                scale = 1.05f,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry2,  // 애니메이션 2
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "뭐 뭐 뭐?",
                x = 200.dp,
                y = 40.dp,
                speakerName = "작은아버지",
                delayMillis = 300L,
                typingSpeedMs = 55L,
                voice = uncleVoice
            )
        }

        // 씬 6: 엄마의 연속 공격 1
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 3500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry1,  // 애니메이션 1
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.2f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "조구 애껴 떼돈 버요?",
                x = 130.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 45L,
                voice = gwangryeVoice
            )
        }

        // 씬 7: 엄마의 연속 공격 2
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 3500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry2,  // 애니메이션 2
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.2f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "엄니, 오씨 딸이요, 오씨 딸!",
                x = 130.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 45L,
                voice = gwangryeVoice
            )
        }

        // 씬 8: 엄마의 연속 공격 3
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 3500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry1,  // 애니메이션 1
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.2f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "오씨 딸이 그 애비는 안 닮았겄소?",
                x = 120.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 45L,
                voice = gwangryeVoice
            )
        }

        // 씬 9: 엄마의 연속 공격 4
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 3500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry2,  // 애니메이션 2
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.2f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "그러지 마소, 그러지 마",
                x = 130.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 45L,
                voice = gwangryeVoice
            )
        }

        // 씬 10: 엄마의 연속 공격 5 (최후의 일격)
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 4500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry1,  // 애니메이션 1
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.25f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "엄니 아덜, 서방님 큰성 구천에서 피눈물 내요",
                x = 110.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 42L,
                voice = gwangryeVoice
            )
        }

        // 씬 11: 작은아빠 반격
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 5000L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                scale = 1.15f,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry2,  // 애니메이션 2
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "남의 집 장남 잡아먹은 주제가 감히 어디서 우리 성은 운운하고..",
                x = 110.dp,
                y = 35.dp,
                speakerName = "작은아버지",
                delayMillis = 300L,
                typingSpeedMs = 40L,
                voice = uncleVoice
            )
        }

        // 씬 12: 엄마 절규!
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 3000L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry1,  // 애니메이션 1
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.3f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "아아아아아악!",
                x = 140.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 35L,
                voice = gwangryeVoice
            )
        }

        // 씬 13: 작은아빠 당황
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 3500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                scale = 1.05f,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry2,  // 애니메이션 2
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "아니, 이게 진짜 무슨 구신이 씌었나?",
                x = 140.dp,
                y = 35.dp,
                speakerName = "작은아버지",
                delayMillis = 300L,
                typingSpeedMs = 42L,
                voice = uncleVoice
            )
        }

        // 씬 14: 엄마의 역공 1
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 3500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_sad,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry1,  // 애니메이션 1
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.15f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "니 성 명 짧은게 내 탓이냐?",
                x = 130.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 45L,
                voice = gwangryeVoice
            )
        }

        // 씬 15: 엄마의 역공 2
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 4000L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_sad,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                scale = 1.1f,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit_suprise,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry2,  // 애니메이션 2
                name = "엄마",
                x = 160.dp,
                y = 135.dp,
                size = 95.dp,
                scale = 1.15f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "명도 짧은 애비 준걸 애한테 미안해나 하소",
                x = 110.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 42L,
                voice = gwangryeVoice
            )
        }

        // 씬 16: 할머니 슬퍼함 / 엄마 딸에게로 - 애니메이션 1
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 2000L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                flipX = true,  // 등 돌림
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_sad,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                scale = 1.15f,  // 슬픔 강조
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 애니메이션 1
                name = "엄마",
                x = 85.dp,  // 애순에게 다가감
                y = 150.dp,
                size = 95.dp,
                animationDuration = 1000,
                voice = gwangryeVoice
            )

            dialogue(
                text = "내 딸. 내가 찾아가요",
                x = 80.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 500L,
                typingSpeedMs = 50L,
                voice = gwangryeVoice
            )
        }

        // 씬 16-2: 애니메이션 2
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 1500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                flipX = true,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_sad,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                scale = 1.15f,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_sit,
                name = "애순",
                x = 50.dp,
                y = 180.dp,
                size = 80.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 애니메이션 2
                name = "엄마",
                x = 85.dp,
                y = 150.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "(애순에게 다가간다)",
                x = 80.dp,
                y = 30.dp,
                delayMillis = 300L,
                typingSpeedMs = 50L
            )
        }

        // 씬 17: 설거지는 우라질!
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 3500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                flipX = true,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_sad,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 50.dp,
                y = 165.dp,
                size = 80.dp,
                animationDuration = 600,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_stand_angry1,  // 애니메이션 1
                name = "엄마",
                x = 75.dp,
                y = 150.dp,
                size = 95.dp,
                scale = 1.1f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "설거지는 우라질",
                x = 70.dp,
                y = 30.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 50L,
                voice = gwangryeVoice
            )
        }

        // 씬 18: 작은아빠 황당 / 퇴장 시작 - 애니메이션 1
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 1500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry2,  // 애니메이션 2
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                scale = 1.05f,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_sad,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            // 광례와 애순 퇴장 시작
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 110.dp,
                y = 165.dp,
                size = 80.dp,
                animationDuration = 1500,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 애니메이션 1
                name = "엄마",
                x = 125.dp,
                y = 150.dp,
                size = 95.dp,
                animationDuration = 1500,
                voice = gwangryeVoice
            )

            dialogue(
                text = "아니, 저게 진짜",
                x = 200.dp,
                y = 40.dp,
                speakerName = "작은아버지",
                delayMillis = 500L,
                typingSpeedMs = 50L,
                voice = uncleVoice
            )
        }

        // 씬 18-2: 퇴장 애니메이션 2
        scene(
            backgroundRes = R.drawable.house,
            durationMillis = 1500L
        ) {
            character(
                id = "uncle",
                imageRes = R.drawable.father_stand_angry1,  // 애니메이션 1
                name = "작은아버지",
                x = 245.dp,
                y = 145.dp,
                size = 90.dp,
                voice = uncleVoice
            )

            character(
                id = "grandma",
                imageRes = R.drawable.grandma_sit_sad,
                name = "할머니",
                x = 195.dp,
                y = 175.dp,
                size = 85.dp,
                voice = grandmaVoice
            )

            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 애니메이션 2
                name = "애순",
                x = 120.dp,
                y = 165.dp,
                size = 80.dp,
                animationDuration = 1000,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 애니메이션 2
                name = "엄마",
                x = 135.dp,
                y = 150.dp,
                size = 95.dp,
                animationDuration = 1000,
                voice = gwangryeVoice
            )

        }

        // ===== 장면 이동: sea 배경 =====

        // 씬 19: 배경 전환 - 집으로 가는 길 (애니메이션 1)
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 1300L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 160.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 애니메이션 1
                name = "엄마",
                x = 130.dp,
                y = 150.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "(장면 이동 - 집으로 가는 길)",
                x = 90.dp,
                y = 40.dp,
                delayMillis = 300L,
                typingSpeedMs = 50L
            )
        }

        // 씬 19-2: 걷기 애니메이션 2
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 1200L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 애니메이션 2
                name = "애순",
                x = 165.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 애니메이션 2
                name = "엄마",
                x = 135.dp,
                y = 150.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

        }

        // 씬 20: 딸의 한마디
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 4500L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                scale = 1.05f,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 애니메이션 2
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "조구 못 읃어먹길 잘했네. 조구 덕에 엄마 집 가 살고",
                x = 80.dp,
                y = 40.dp,
                speakerName = "애순",
                delayMillis = 500L,
                typingSpeedMs = 42L,
                voice = aesunVoice
            )
        }

        // 씬 21: 엄마의 질문 1
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 3000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 애니메이션 2
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 걷기 애니메이션 1
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                scale = 1.05f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "넌 내가 좋으냐?",
                x = 120.dp,
                y = 35.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 50L,
                voice = gwangryeVoice
            )
        }

        // 씬 22: 엄마의 질문 2
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 3000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 걷기 애니메이션 2
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                scale = 1.05f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "내가 뭐가 좋아?",
                x = 120.dp,
                y = 35.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 50L,
                voice = gwangryeVoice
            )
        }

        // 씬 23: 딸의 대답 (가장 감동적인 순간)
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 4500L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 애니메이션 2
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                scale = 1.1f,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 걷기 애니메이션 1
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "엄마니까. 엄마니까 좋지. 말이라고 물어?",
                x = 90.dp,
                y = 40.dp,
                speakerName = "애순",
                delayMillis = 500L,
                typingSpeedMs = 45L,
                voice = aesunVoice
            )
        }

        // 씬 24: 배고픈 애순
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 3500L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                scale = 1.05f,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 걷기 애니메이션 2
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "그나저나 엄마... 배고파... 저녁은 뭐 먹을 거여?",
                x = 90.dp,
                y = 45.dp,
                speakerName = "애순",
                delayMillis = 500L,
                typingSpeedMs = 48L,
                voice = aesunVoice
            )
        }

        // 씬 25: 광례의 당황
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 4000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 애니메이션 2
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 걷기 애니메이션 1
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                scale = 1.05f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "어... 그게... 어멍이 오늘 조기를 다... 던져불었잖냐...",
                x = 85.dp,
                y = 35.dp,
                speakerName = "엄마",
                delayMillis = 500L,
                typingSpeedMs = 43L,
                voice = gwangryeVoice
            )
        }

        // 씬 26: 애순의 반응
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 3500L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 걷기 애니메이션 1
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                scale = 1.1f,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 걷기 애니메이션 2
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "헉! 그 비싼 조기를?! 엄마... 우리 오늘 굶는 거 아니여?",
                x = 80.dp,
                y = 40.dp,
                speakerName = "애순",
                delayMillis = 500L,
                typingSpeedMs = 40L,
                voice = aesunVoice
            )
        }

        // 씬 27: 광례의 해결책
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 4000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 걷기 애니메이션 2
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 걷기 애니메이션 1
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                scale = 1.05f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "걱정 말라! 집에 가민 라면은 있다!",
                x = 100.dp,
                y = 35.dp,
                speakerName = "엄마",
                delayMillis = 500L,
                typingSpeedMs = 45L,
                voice = gwangryeVoice
            )
        }

        // 씬 28: 애순의 실망
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 3500L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 걷기 애니메이션 1
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                scale = 0.95f,  // 시무룩
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 걷기 애니메이션 2
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "에... 또 라면이여? 어제도 라면이었는디...",
                x = 95.dp,
                y = 45.dp,
                speakerName = "애순",
                delayMillis = 500L,
                typingSpeedMs = 48L,
                voice = aesunVoice
            )
        }

        // 씬 29: 광례의 변명
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 4000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 걷기 애니메이션 2
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 걷기 애니메이션 1
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                scale = 1.08f,
                voice = gwangryeVoice
            )

            dialogue(
                text = "그래도 라면 맛있잖냐! 계란이랑 파 듬뿍 넣어불게!",
                x = 85.dp,
                y = 35.dp,
                speakerName = "엄마",
                delayMillis = 500L,
                typingSpeedMs = 42L,
                voice = gwangryeVoice
            )
        }

        // 씬 30: 애순의 폭탄 질문
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 4000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                scale = 1.05f,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 걷기 애니메이션 2
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                voice = gwangryeVoice
            )

            dialogue(
                text = "엄마... 우리 집에 계란이랑 파... 있긴 해?",
                x = 90.dp,
                y = 40.dp,
                speakerName = "애순",
                delayMillis = 500L,
                typingSpeedMs = 45L,
                voice = aesunVoice
            )
        }

        // 씬 31: 광례의 멘붕
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 3500L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 애니메이션 2
                name = "애순",
                x = 170.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 걷기 애니메이션 1
                name = "엄마",
                x = 140.dp,
                y = 150.dp,
                size = 95.dp,
                scale = 0.95f,  // 시무룩
                voice = gwangryeVoice
            )

            dialogue(
                text = "...아마도...?",
                x = 130.dp,
                y = 40.dp,
                speakerName = "엄마",
                delayMillis = 1000L,
                typingSpeedMs = 60L,
                voice = gwangryeVoice
            )
        }

        // 씬 32: 둘의 허탈한 웃음 - 애니메이션 1
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 2000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 180.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                scale = 1.08f,
                animationDuration = 800,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 애니메이션 1
                name = "엄마",
                x = 150.dp,
                y = 150.dp,
                size = 95.dp,
                scale = 1.08f,
                animationDuration = 800,
                voice = gwangryeVoice
            )

            dialogue(
                text = "라면 물이나 많이 넣어불자!",
                x = 100.dp,
                y = 35.dp,
                speakerName = "엄마",
                delayMillis = 300L,
                typingSpeedMs = 45L,
                voice = gwangryeVoice
            )
        }

        // 씬 32-2: 애니메이션 2
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 1500L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 애니메이션 2
                name = "애순",
                x = 180.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                scale = 1.08f,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 애니메이션 2
                name = "엄마",
                x = 150.dp,
                y = 150.dp,
                size = 95.dp,
                scale = 1.08f,
                voice = gwangryeVoice
            )

        }

        // 씬 33: 엔딩 - 함께 걸어가는 모녀 (애니메이션 1)
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 2000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 210.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                alpha = 0.7f,
                animationDuration = 2000,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 애니메이션 1
                name = "엄마",
                x = 180.dp,
                y = 150.dp,
                size = 95.dp,
                alpha = 0.7f,
                animationDuration = 2000,
                voice = gwangryeVoice
            )

            dialogue(
                text = "(손을 꼭 잡고 집으로 향하는 모녀)",
                x = 85.dp,
                y = 40.dp,
                delayMillis = 500L,
                typingSpeedMs = 50L
            )
        }

        // 씬 33-2: 엔딩 애니메이션 2
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 2000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk2,  // 애니메이션 2
                name = "애순",
                x = 215.dp,  // 엄마 뒤에서 따라감
                y = 160.dp,
                size = 85.dp,
                alpha = 0.5f,
                animationDuration = 1500,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk2,  // 애니메이션 2
                name = "엄마",
                x = 185.dp,
                y = 150.dp,
                size = 95.dp,
                alpha = 0.5f,
                animationDuration = 1500,
                voice = gwangryeVoice
            )

        }

        // 씬 34: 암전 (막)
        scene(
            backgroundRes = R.drawable.sea,
            durationMillis = 3000L
        ) {
            character(
                id = "aesun",
                imageRes = R.drawable.daughter_walk1,  // 애니메이션 1
                name = "애순",
                x = 220.dp,
                y = 160.dp,
                size = 85.dp,
                alpha = 0f,
                animationDuration = 1500,
                voice = aesunVoice
            )

            character(
                id = "gwangrye",
                imageRes = R.drawable.mother_walk1,  // 애니메이션 1
                name = "엄마",
                x = 195.dp,
                y = 150.dp,
                size = 95.dp,
                alpha = 0f,
                animationDuration = 1500,
                voice = gwangryeVoice
            )

            dialogue(
                text = "(막)",
                x = 150.dp,
                y = 120.dp,
                delayMillis = 1500L,
                typingSpeedMs = 60L
            )
        }
    }
}
