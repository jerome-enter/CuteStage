package com.example.cutestage.stage

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

/**
 * 타자기 효과를 가진 텍스트 (음성 포함, 노래 지원)
 *
 * @param text 표시할 텍스트
 * @param sceneIndex 현재 씬 인덱스 (씬 전환 감지용)
 * @param startTyping 타이핑 시작 플래그 (말풍선 애니메이션 완료 후 true)
 * @param voice 음성 설정
 * @param playbackSpeed 재생 속도 (1.0x, 1.5x, 2.0x 등)
 * @param notes 노래 음표 정보 (노래일 때만, null이면 일반 대화)
 */
@Composable
internal fun TypewriterText(
    text: String,
    sceneIndex: Int,
    startTyping: Boolean,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    typingSpeedMs: Long = 50L,
    voice: CharacterVoice? = null,
    playbackSpeed: Float = 1.0f,
    notes: List<SongNote>? = null,
) {
    // 빈 텍스트는 렌더링하지 않음 (음성 재생 방지)
    if (text.isBlank()) {
        return
    }

    // sceneIndex가 변경되면 상태 리셋
    var visibleText by remember(sceneIndex, text) { mutableStateOf("") }

    // 음성 매니저는 씬마다 새로 생성하여 리소스 충돌 방지
    val soundManager = remember(sceneIndex) {
        VoiceSoundManagerFactory.create()
    }

    // 씬이 변경되거나 컴포넌트가 사라질 때 리소스 정리
    DisposableEffect(sceneIndex) {
        onDispose {
            soundManager.release()
        }
    }

    // 타자기 효과 실행
    LaunchedEffect(sceneIndex, text, startTyping, playbackSpeed) {
        // startTyping이 false면 대기
        if (!startTyping) {
            visibleText = ""
            return@LaunchedEffect
        }

        visibleText = ""

        // 노래 모드: notes가 있으면 각 음표의 pitch로 발음
        if (notes != null && notes.isNotEmpty()) {
            var textIndex = 0
            notes.forEach { note ->
                if (note.lyric.isNotBlank() && note.pitch > 0f) {
                    // 음성 재생 (해당 음표의 정확한 pitch)
                    if (voice?.enabled != false) {
                        soundManager.playBeep(
                            pitch = note.pitch,
                            duration = (note.duration * 0.8f).toInt(),
                            volume = voice?.volume ?: 0.5f,
                        )
                    }

                    // 글자 표시
                    textIndex += note.lyric.length
                    visibleText = text.take(textIndex)

                    // 음표 길이만큼 대기
                    delay(calculateSafeDelay(note.duration.toLong(), playbackSpeed))
                } else {
                    // 쉼표인 경우 대기만
                    delay(calculateSafeDelay(note.duration.toLong(), playbackSpeed))
                }
            }
        } else {
            // 일반 대화 모드: 기존 방식
            text.forEachIndexed { index, char ->
                // 음성 재생 (공백이 아닐 때만)
                if (voice?.enabled == true && !char.isWhitespace()) {
                    soundManager.playBeep(
                        pitch = voice.pitch,
                        duration = voice.duration,
                        volume = voice.volume,
                    )
                }

                // 글자 표시 속도 (재생 속도에 따라 조정, 안전한 계산)
                val speed = voice?.speed?.toLong() ?: typingSpeedMs
                delay(calculateSafeDelay(speed, playbackSpeed))
                visibleText = text.substring(0, index + 1)
            }
        }
    }

    Text(
        text = visibleText,
        modifier = modifier,
        style = style,
    )
}
