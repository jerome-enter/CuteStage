package com.example.cutestage.stage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 디버그 점 오버레이
 * 캐릭터 위치와 말풍선 위치를 시각화
 */
@Composable
internal fun DebugPointsOverlay(
    characters: List<CharacterState>,
    dialogues: List<DialogueState>,
    modifier: Modifier = Modifier
) {
    // 무지개 색상 팔레트: 빨주노초파남보
    val rainbowColors = listOf(
        Color(0xFFFF0000),  // 빨강
        Color(0xFFFF7F00),  // 주황
        Color(0xFFFFFF00),  // 노랑
        Color(0xFF00FF00),  // 초록
        Color(0xFF0000FF),  // 파랑
        Color(0xFF4B0082),  // 남색
        Color(0xFF9400D3)   // 보라
    )

    Box(
        modifier = modifier.padding(10.dp)  // StageView 경계와 동일하게
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 캐릭터 위치 점 그리기 (바닥 중앙)
            characters.forEachIndexed { index, character ->
                // 캐릭터 순서에 따라 무지개 색상 할당
                val color = rainbowColors[index % rainbowColors.size]

                // 캐릭터 바닥 중앙 위치
                // position = 왼쪽 위 → 바닥 중앙으로 변환
                val characterBottomCenterX =
                    character.position.x.toPx() + (character.size / 2).toPx()
                val characterBottomCenterY = character.position.y.toPx() + character.size.toPx()

                drawCircle(
                    color = color,
                    radius = 10.dp.toPx(),
                    center = Offset(characterBottomCenterX, characterBottomCenterY)
                )
            }

            // 말풍선 중앙 위치 점 그리기 (재생 중일 때만)
            dialogues.forEach { dialogue ->
                // speakerName으로 캐릭터 찾기
                val character = dialogue.speakerName?.let { name ->
                    characters.find { it.name == name }
                }

                if (character != null) {
                    // 캐릭터 인덱스 찾기
                    val characterIndex = characters.indexOf(character)
                    val color = rainbowColors[characterIndex % rainbowColors.size]

                    // 캐릭터 바닥 중앙 기준으로 말풍선 위치 계산
                    val characterBottomCenterX =
                        character.position.x.toPx() + (character.size / 2).toPx()

                    // 말풍선 위치 (바닥 중앙에서 계산)
                    val bubbleX = (characterBottomCenterX - 90.dp.toPx())
                        .coerceIn(0f, (280.dp - 180.dp).toPx())
                    val bubbleY = 60.dp.toPx()

                    // 말풍선 중앙 위치 (말풍선 너비 180dp의 중앙)
                    val bubbleCenterX = bubbleX + 90.dp.toPx()
                    val bubbleCenterY = bubbleY + 30.dp.toPx()

                    drawCircle(
                        color = color,
                        radius = 10.dp.toPx(),
                        center = Offset(bubbleCenterX, bubbleCenterY)
                    )
                }
            }
        }
    }
}
