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

    // 디버그 로그
    println("Debug_StageViewDebug 캐릭터 리스트 순서:")
    characters.forEachIndexed { index, character ->
        println("Debug_StageViewDebug   [$index] ${character.name}(${character.id})")
    }
    println("Debug_StageViewDebug 대사 리스트:")
    dialogues.forEach { dialogue ->
        println("Debug_StageViewDebug   ${dialogue.speakerName}: \"${dialogue.text}\"")
    }

    // 주의: AnimatedSpeechBubble도 padding(10.dp) 있음!
    // 디버그 점도 동일한 padding 적용
    Box(modifier = modifier) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)) {
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

                    // 캐릭터 중앙 X
                    val characterCenterX = character.position.x.toPx() + (character.size / 2).toPx()

                    // AnimatedSpeechBubble과 동일한 계산
                    val bubbleWidth = estimateBubbleWidth(dialogue.text, dialogue.speakerName)
                    val stageWidth = 260.dp.toPx()

                    // 말풍선을 중앙에 배치했을 때 오른쪽 끝
                    val bubbleRightEdge = characterCenterX + (bubbleWidth / 2).toPx()

                    // 오른쪽 경계를 넘치는 양
                    val rightOverflow = (bubbleRightEdge - stageWidth).coerceAtLeast(0f)

                    // 왼쪽 경계를 넘치는 양
                    val leftOverflow =
                        (0f - (characterCenterX - (bubbleWidth / 2).toPx())).coerceAtLeast(0f)

                    // 넘치는 만큼만 조정
                    val bubbleCenterX = characterCenterX - rightOverflow + leftOverflow
                    val bubbleCenterY = 60.dp.toPx() + 30.dp.toPx()

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
