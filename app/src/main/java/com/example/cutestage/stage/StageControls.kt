package com.example.cutestage.stage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 무대 컨트롤 UI
 */
@Composable
internal fun StageControls(
    isPlaying: Boolean,
    playbackSpeed: Float,
    currentScript: TheaterScript?,
    currentSceneIndex: Int,
    onPlaybackSpeedChange: (Float) -> Unit,
    onStopPlaying: () -> Unit,
    onScenarioSelected: (StageTestScenario.ScenarioType, TheaterScript, Boolean) -> Unit,
    onShowAIDialog: () -> Unit,
    onPlay: () -> Unit,
    onScenarioSelectClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 재생 속도 조절 버튼 (왼쪽 위)
        PlaybackSpeedControl(
            playbackSpeed = playbackSpeed,
            onSpeedChange = onPlaybackSpeedChange,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // 디버그 정보 (오른쪽 위)
        currentScript?.let { script ->
            if (script.debug) {
                Text(
                    text = "Scene: ${currentSceneIndex + 1}/${script.scenes.size}",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                )
            }
        }

        // 재생 중일 때 종료 버튼
        if (isPlaying) {
            Surface(
                onClick = onStopPlaying,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp, end = 12.dp)
                    .size(32.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "종료",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        }

        // 재생 중이 아닐 때 컨트롤 버튼들
        if (!isPlaying) {
            // 음성 엔진 선택 버튼 (왼쪽 하단)
            VoiceEngineControl(
                modifier = Modifier.align(Alignment.BottomStart)
            )

            // 시나리오 선택 + 재생 버튼 (오른쪽 하단)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ScenarioSelector(
                    onScenarioSelected = onScenarioSelected,
                    onShowAIDialog = onShowAIDialog,
                    onScenarioSelectClick = onScenarioSelectClick
                )

                // 재생 버튼
                Surface(
                    onClick = onPlay,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "재생",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}

/**
 * 재생 속도 조절 UI
 */
@Composable
private fun PlaybackSpeedControl(
    playbackSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.6f),
        modifier = modifier.padding(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(1.0f, 1.5f, 2.0f).forEach { speed ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (playbackSpeed == speed) Color.White else Color.Transparent,
                    modifier = Modifier.size(width = 40.dp, height = 24.dp),
                    onClick = { onSpeedChange(speed) },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (speed == 1.0f) "1x" else if (speed == 1.5f) "1.5x" else "2x",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (playbackSpeed == speed) Color.Black else Color.White,
                        )
                    }
                }
            }
        }
    }
}

/**
 * 음성 엔진 선택 UI
 */
@Composable
private fun VoiceEngineControl(
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier.padding(bottom = 12.dp, start = 12.dp)) {
        Surface(
            onClick = { showMenu = true },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(32.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "음성 엔진 선택",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            val currentEngine = VoiceSoundManagerFactory.currentEngineType

            VoiceEngineMenuItem(
                text = "부드러운 소리 (기본)",
                engineType = VoiceSoundType.AUDIO_TRACK,
                currentEngine = currentEngine,
                onSelect = {
                    showMenu = false
                    VoiceSoundManagerFactory.currentEngineType = VoiceSoundType.AUDIO_TRACK
                }
            )

            VoiceEngineMenuItem(
                text = "레트로 비프음",
                engineType = VoiceSoundType.TONE_GENERATOR,
                currentEngine = currentEngine,
                onSelect = {
                    showMenu = false
                    VoiceSoundManagerFactory.currentEngineType = VoiceSoundType.TONE_GENERATOR
                }
            )

            VoiceEngineMenuItem(
                text = "동물의 숲 스타일 🎮",
                engineType = VoiceSoundType.ANIMAL_VOICE,
                currentEngine = currentEngine,
                onSelect = {
                    showMenu = false
                    VoiceSoundManagerFactory.currentEngineType = VoiceSoundType.ANIMAL_VOICE
                }
            )
        }
    }
}

@Composable
private fun VoiceEngineMenuItem(
    text: String,
    engineType: VoiceSoundType,
    currentEngine: VoiceSoundType,
    onSelect: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (currentEngine == engineType) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "선택됨",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Spacer(modifier = Modifier.size(20.dp))
                }
                Text(text)
            }
        },
        onClick = onSelect
    )
}

/**
 * 시나리오 선택 UI - 팝업 제거, 항상 네비게이션으로 이동
 */
@Composable
private fun ScenarioSelector(
    onScenarioSelected: (StageTestScenario.ScenarioType, TheaterScript, Boolean) -> Unit,
    onShowAIDialog: () -> Unit,
    onScenarioSelectClick: (() -> Unit)?
) {
    Surface(
        onClick = {
            onScenarioSelectClick?.invoke()
        },
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(32.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "시나리오 선택",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * 선택지 다이얼로그
 */
@Composable
internal fun ChoicesDialog(
    choices: List<Choice>,
    onChoiceSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        modifier = modifier.padding(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "선택해주세요",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            choices.forEach { choice ->
                Surface(
                    onClick = { onChoiceSelected(choice.nextSceneIndex) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = choice.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}

/**
 * AI 시나리오 생성 다이얼로그
 */
@Composable
internal fun AIGenerationDialog(
    userInput: String,
    isGenerating: Boolean,
    generationError: String?,
    onUserInputChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 시나리오 생성") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "줄거리를 입력하면 AI가 자동으로 시나리오를 생성합니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                TextField(
                    value = userInput,
                    onValueChange = onUserInputChange,
                    label = { Text("줄거리 입력") },
                    placeholder = { Text("예: 두 사람이 무대에서 만나서 서로 인사를 나눕니다.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isGenerating
                )
                if (isGenerating) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Text("AI가 시나리오를 생성중입니다...")
                    }
                }
                generationError?.let { error ->
                    Text(
                        text = "오류: $error",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onGenerate,
                enabled = !isGenerating && userInput.isNotBlank()
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("생성")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isGenerating
            ) {
                Text("취소")
            }
        }
    )
}
