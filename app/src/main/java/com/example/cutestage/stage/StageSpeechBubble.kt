package com.example.cutestage.stage

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * 텍스트 길이 기반으로 말풍선 너비 예측
 *
 * 말풍선 구성:
 * - padding: 12dp * 2 = 24dp
 * - 텍스트: 대략 8dp per 문자 (한글 기준)
 * - 최소: 60dp, 최대: 180dp
 */
internal fun estimateBubbleWidth(text: String, speakerName: String?): androidx.compose.ui.unit.Dp {
    // 이름이 있으면 추가 높이로 인해 너비도 영향받을 수 있음
    val hasName = !speakerName.isNullOrBlank()

    // 텍스트 길이 (한글은 2바이트로 계산)
    val textLength = text.length

    // 대략적인 너비 계산
    // - 패딩: 24dp
    // - 한글 1글자: 약 12dp (한글은 넓음)
    // - 최대 너비: 180dp
    val basePadding = 24
    val charWidth = 12
    val estimatedContentWidth = textLength * charWidth

    val totalWidth = (basePadding + estimatedContentWidth).dp

    // 최소 60dp, 최대 180dp로 제한
    return totalWidth.coerceIn(60.dp, 180.dp)
}

/**
 * 애니메이션이 적용된 말풍선
 *
 * 타이밍 구조:
 * 1. delayMillis 대기
 * 2. 말풍선과 타자기를 동시에 시작 (빈 말풍선 방지)
 * 3. 부드러운 페이드인 애니메이션
 *
 * @param character 실시간 캐릭터 위치 추적을 위한 캐릭터 상태 (null이면 dialogue.position 사용)
 */
@Composable
internal fun AnimatedSpeechBubble(
    dialogue: DialogueState,
    sceneIndex: Int,
    playbackSpeed: Float,
    modifier: Modifier = Modifier,
    character: CharacterState? = null,
    showDebugPoints: Boolean = false,
    allCharacters: List<CharacterState> = emptyList(),  // 색상 결정용
    stageWidthDp: androidx.compose.ui.unit.Dp = 260.dp,  // 실제 스테이지 너비
) {
    // 빈 대사는 렌더링하지 않음 (음성 재생 방지)
    if (dialogue.text.isBlank()) {
        return
    }

    // dialogue.id를 key로 사용하여 각 대사마다 독립적인 상태 유지
    var visible by remember(sceneIndex, dialogue.id) { mutableStateOf(false) }

    // 말풍선 등장 애니메이션 시간
    val bubbleAnimationDuration = 200

    LaunchedEffect(sceneIndex, dialogue.id, playbackSpeed) {
        // 지연 시간 대기 후 말풍선과 타자기를 동시에 시작 (재생 속도에 따라 조정, 안전한 계산)
        delay(calculateSafeDelay(dialogue.delayMillis, playbackSpeed))
        visible = true

        // 타이핑 시간 계산 (글자 수 × 타이핑 속도)
        val typingDuration = (dialogue.text.length * dialogue.typingSpeedMs)
        delay(calculateSafeDelay(typingDuration, playbackSpeed))

        // 타이핑 완료 후 말풍선 표시 시간 (1.5초)
        delay(calculateSafeDelay(1500, playbackSpeed))

        // 말풍선 사라짐
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(bubbleAnimationDuration)) + scaleIn(
            animationSpec = tween(bubbleAnimationDuration),
            initialScale = 0.9f,
        ),
        exit = fadeOut(animationSpec = tween(150)) + scaleOut(
            animationSpec = tween(150),
            targetScale = 0.95f,
        ),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp), // 대화창이 StageView 경계에서 10dp 떨어지도록
        ) {
            // 말풍선 위치 계산
            val bubbleX: androidx.compose.ui.unit.Dp
            val bubbleY: androidx.compose.ui.unit.Dp

            if (character != null) {
                // 캐릭터 바닥 중앙 X
                println("Debug_SpeechBubble 캐릭터 위치: ${character.name} position.x=${character.position.x} size=${character.size}")
                val characterCenterX = character.position.x + character.size / 2

                // 텍스트 길이로 실제 말풍선 너비 예측
                val bubbleWidth = estimateBubbleWidth(dialogue.text, dialogue.speakerName)
                val stageWidth = stageWidthDp  // 실제 스테이지 너비 (스케일링 적용됨)

                // 말풍선을 중앙에 배치했을 때 오른쪽 끝
                val bubbleRightEdge = characterCenterX + bubbleWidth / 2

                // 오른쪽 경계를 넘치는 양 (최대 60dp까지만 이동)
                val rightOverflow =
                    (bubbleRightEdge - stageWidth).coerceAtLeast(0.dp).coerceAtMost(60.dp)

                // 왼쪽 경계를 넘치는 양 (최대 60dp까지만 이동)
                val leftOverflow = (0.dp - (characterCenterX - bubbleWidth / 2)).coerceAtLeast(0.dp)
                    .coerceAtMost(60.dp)

                // 넘치는 만큼만 조정 (하지만 최대 60dp까지만)
                val adjustedCenterX = characterCenterX - rightOverflow + leftOverflow
                bubbleX = adjustedCenterX - bubbleWidth / 2
                bubbleY = 60.dp  // 고정 높이

                println(
                    "Debug_SpeechBubble 말풍선 위치: ${character.name} visible=$visible bubbleX=$bubbleX text=${
                        dialogue.text.take(
                            10
                        )
                    }"
                )
            } else {
                // 캐릭터가 없으면 기존 방식
                bubbleX = dialogue.position.x.coerceIn(0.dp, 280.dp - 180.dp)
                bubbleY = dialogue.position.y.coerceIn(0.dp, 280.dp - 100.dp)
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .offset(x = bubbleX, y = bubbleY)
                    .widthIn(max = 180.dp),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                ) {
                    // 캐릭터 이름
                    dialogue.speakerName?.let { name ->
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // 대사 - 타자기 효과
                    TypewriterText(
                        text = dialogue.text,
                        sceneIndex = sceneIndex,
                        startTyping = visible,
                        style = MaterialTheme.typography.bodyMedium,
                        typingSpeedMs = dialogue.typingSpeedMs,
                        voice = dialogue.voice,
                        playbackSpeed = playbackSpeed,
                        notes = dialogue.notes,
                    )
                }
            }

            // 디버그 점 (visible일 때만) - 말풍선 위에 그리기
            if (showDebugPoints && character != null) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    // 무지개 색상 팔레트
                    val rainbowColors = listOf(
                        androidx.compose.ui.graphics.Color(0xFFFF0000),  // 빨강
                        androidx.compose.ui.graphics.Color(0xFFFF7F00),  // 주황
                        androidx.compose.ui.graphics.Color(0xFFFFFF00),  // 노랑
                        androidx.compose.ui.graphics.Color(0xFF00FF00),  // 초록
                        androidx.compose.ui.graphics.Color(0xFF0000FF),  // 파랑
                        androidx.compose.ui.graphics.Color(0xFF4B0082),  // 남색
                        androidx.compose.ui.graphics.Color(0xFF9400D3)   // 보라
                    )

                    // 캐릭터 인덱스 찾기
                    val characterIndex = allCharacters.indexOf(character)
                    val color = if (characterIndex >= 0) {
                        rainbowColors[characterIndex % rainbowColors.size]
                    } else {
                        androidx.compose.ui.graphics.Color.White
                    }

                    // 말풍선 중앙 위치
                    val characterCenterX = character.position.x.toPx() + (character.size / 2).toPx()
                    val bubbleWidth = estimateBubbleWidth(dialogue.text, dialogue.speakerName)
                    val stageWidth = stageWidthDp.toPx()

                    val bubbleRightEdge = characterCenterX + (bubbleWidth / 2).toPx()
                    val rightOverflow = (bubbleRightEdge - stageWidth).coerceAtLeast(0f)
                        .coerceAtMost(60.dp.toPx())
                    val leftOverflow =
                        (0f - (characterCenterX - (bubbleWidth / 2).toPx())).coerceAtLeast(0f)
                            .coerceAtMost(60.dp.toPx())

                    val bubbleCenterX = characterCenterX - rightOverflow + leftOverflow
                    val bubbleCenterY = 60.dp.toPx() + 30.dp.toPx()

                    println(
                        "Debug_SpeechBubble 말풍선 점: ${character.name} [$characterIndex] ${
                            dialogue.text.take(
                                10
                            )
                        }"
                    )

                    drawCircle(
                        color = color,
                        radius = 10.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(bubbleCenterX, bubbleCenterY)
                    )
                }
            }
        }
    }
}

/**
 * 상호작용 말풍선
 * 캐릭터 클릭 시 표시되는 생동감 있는 말풍선
 * - 타자기 효과
 * - 음성 재생
 * - 캐릭터 애니메이션
 *
 * @param text 표시할 대사
 * @param character 대사를 말하는 캐릭터
 * @param voice 음성 설정
 * @param onDismiss 말풍선 닫기 콜백
 */
@Composable
internal fun InteractionSpeechBubble(
    text: String,
    character: CharacterState,
    voice: CharacterVoice,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 빈 텍스트는 렌더링하지 않음 (음성 재생 방지)
    if (text.isBlank()) {
        onDismiss()
        return
    }

    // text가 변경되면 모든 상태 리셋
    var visible by remember(text) { mutableStateOf(false) }
    var startTyping by remember(text) { mutableStateOf(false) }
    var isDismissing by remember(text) { mutableStateOf(false) }

    // 음성 매니저 (text마다 새로 생성)
    val soundManager = remember(text) {
        VoiceSoundManagerFactory.create()
    }

    DisposableEffect(text) {
        onDispose {
            soundManager.release()
        }
    }

    // 타자기 텍스트 상태 (text마다 리셋)
    var visibleText by remember(text) { mutableStateOf("") }

    // 타자기 효과 + 음성
    LaunchedEffect(text, startTyping) {
        if (!startTyping) {
            visibleText = ""
            return@LaunchedEffect
        }

        visibleText = ""
        text.forEachIndexed { index, char ->
            if (isDismissing) return@LaunchedEffect

            // 음성 재생 (공백이 아닐 때만)
            if (!char.isWhitespace()) {
                soundManager.playBeep(
                    pitch = voice.pitch,
                    duration = voice.duration,
                    volume = voice.volume,
                )
            }

            delay(voice.speed.toLong())
            visibleText = text.substring(0, index + 1)
        }

        // 타자기 완료 후 대기
        delay(2000)
        isDismissing = true
        visible = false
        delay(200)
        onDismiss()
    }

    // 말풍선 등장 애니메이션
    LaunchedEffect(text) {
        visible = false
        startTyping = false
        isDismissing = false
        delay(100)
        visible = true
        delay(200) // 말풍선 애니메이션 시간
        startTyping = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)) + scaleIn(
            animationSpec = tween(200),
            initialScale = 0.8f,
        ),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(
            animationSpec = tween(200),
            targetScale = 0.8f,
        ),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp), // 대화창이 StageView 경계에서 10dp 떨어지도록
        ) {
            // 캐릭터 위치에 맞춰 말풍선 표시 (연극할 때와 같은 위치)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .offset(
                        x = (character.position.x + character.size / 2 - 90.dp).coerceIn(
                            0.dp, 280.dp - 180.dp
                        ),
                        y = 60.dp, // 연극할 때와 같은 높이
                    )
                    .widthIn(max = 180.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        // 클릭하면 즉시 닫기
                        isDismissing = true
                        onDismiss()
                    },
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                ) {
                    // 캐릭터 이름
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // 타자기 텍스트
                    Text(
                        text = visibleText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                    )
                }
            }
        }
    }
}
