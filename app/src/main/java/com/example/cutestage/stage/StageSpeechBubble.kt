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
 * 애니메이션이 적용된 말풍선
 *
 * 타이밍 구조:
 * 1. delayMillis 대기
 * 2. 말풍선과 타자기를 동시에 시작 (빈 말풍선 방지)
 * 3. 부드러운 페이드인 애니메이션
 */
@Composable
internal fun AnimatedSpeechBubble(
    dialogue: DialogueState,
    sceneIndex: Int,
    playbackSpeed: Float,
    modifier: Modifier = Modifier,
) {
    // 빈 대사는 렌더링하지 않음 (음성 재생 방지)
    if (dialogue.text.isBlank()) {
        return
    }

    var visible by remember(sceneIndex) { mutableStateOf(false) }

    // 말풍선 등장 애니메이션 시간
    val bubbleAnimationDuration = 200

    LaunchedEffect(sceneIndex, playbackSpeed) {
        // 지연 시간 대기 후 말풍선과 타자기를 동시에 시작 (재생 속도에 따라 조정, 안전한 계산)
        delay(calculateSafeDelay(dialogue.delayMillis, playbackSpeed))
        visible = true
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
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White, // 대화창 배경 흰색
                shadowElevation = 4.dp,
                modifier = Modifier
                    .offset(
                        x = dialogue.position.x.coerceIn(0.dp, 280.dp - 180.dp),
                        y = dialogue.position.y.coerceIn(0.dp, 280.dp - 100.dp)
                    )
                    .widthIn(max = 180.dp),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                ) {
                    // 캐릭터 이름 (선택)
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
                        startTyping = visible, // 말풍선 표시와 동시에 타자기 시작
                        style = MaterialTheme.typography.bodyMedium,
                        typingSpeedMs = dialogue.typingSpeedMs,
                        voice = dialogue.voice,
                        playbackSpeed = playbackSpeed,
                        notes = dialogue.notes, // 노래 음표 전달
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
