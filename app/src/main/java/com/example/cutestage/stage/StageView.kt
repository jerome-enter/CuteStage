package com.example.cutestage.stage

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cutestage.R
import kotlinx.coroutines.delay

/**
 * 연극 무대 컴포저블
 *
 * Hilt를 통해 ViewModel을 자동으로 주입받아 복잡한 상태를 관리합니다.
 * 여러 곳에서 독립적으로 재사용 가능하며, Configuration Change에도 안전합니다.
 *
 * @param modifier Modifier
 * @param script 실행할 스크립트 (null이면 빈 무대)
 * @param onScriptEnd 스크립트 종료 콜백
 * @param onScenarioSelectClick 시나리오 선택 버튼 클릭 시 호출
 * @param viewModel Hilt가 자동 주입 (테스트 시 수동 주입 가능)
 */
@Composable
fun StageView(
    modifier: Modifier = Modifier,
    script: TheaterScript? = null,
    onScriptEnd: () -> Unit = {},
    onScenarioSelectClick: (() -> Unit)? = null,
    viewModel: StageViewModel = hiltViewModel()
) {
    // 스크립트가 변경될 때마다 ViewModel 초기화
    LaunchedEffect(script) {
        if (script != null) {
            viewModel.setInitialScript(script)
        }
        viewModel.setOnScriptEnd(onScriptEnd)
    }

    StageViewContent(
        state = viewModel.state,
        onEvent = viewModel::handleEvent,  // 완전히 일관된 API!
        onScenarioSelectClick = onScenarioSelectClick,
        modifier = modifier
    )
}

/**
 * StageView의 실제 UI 컨텐츠 (Stateless)
 *
 * 모든 상태는 StageState로 받고, 모든 액션은 StageEvent로 전달합니다.
 * 순수 UI만 담당하여 프리뷰 테스트가 용이합니다.
 */
@Composable
internal fun StageViewContent(
    state: StageState,
    onEvent: (StageEvent) -> Unit,
    onScenarioSelectClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val currentScene = remember(state.currentScript, state.playbackState.currentSceneIndex) {
        state.currentScript?.scenes?.getOrNull(state.playbackState.currentSceneIndex)
    }

    Column(modifier = modifier) {
        // 무대 영역
        val density = androidx.compose.ui.platform.LocalDensity.current
        var actualStageSize by remember { mutableStateOf<androidx.compose.ui.unit.IntSize?>(null) }

        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .onSizeChanged { size ->
                    actualStageSize = size
                    with(density) {
                        val widthDp = size.width.toDp()
                        val heightDp = size.height.toDp()
                        println("Debug_StageView 실제 크기: width=${size.width}px (${widthDp}) height=${size.height}px (${heightDp})")
                    }
                }
        ) {
        // 무대 배경
        key(state.playbackState.currentSceneIndex) {
            StageBackground(
                backgroundRes = currentScene?.backgroundRes ?: R.drawable.stage_floor,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // 캐릭터들 (실제 크기에 맞춰 스케일링)
            currentScene?.characters?.forEach { character ->
                // BeatConverter에서 360dp 기준으로 계산된 위치를 실제 크기로 스케일링
                val scaledCharacter = if (actualStageSize != null) {
                    with(density) {
                        val actualWidthDp = actualStageSize!!.width.toDp()
                        val actualHeightDp = actualStageSize!!.height.toDp()
                        val baseWidth = 360.dp  // BeatConverter의 기준 크기
                        val baseHeight = 300.dp

                        val scaleX = actualWidthDp / baseWidth
                        val scaleY = actualHeightDp / baseHeight

                        character.copy(
                            position = DpOffset(
                                x = character.position.x * scaleX,
                                y = character.position.y * scaleY
                            ),
                            size = character.size * scaleY  // 높이 기준으로 스케일
                        )
                    }
                } else {
                    character
                }

                val isInteracting = state.interactionState.characterId == character.id &&
                        state.interactionState.dialogue != null
                val interactionCharacter =
                    if (isInteracting && scaledCharacter.spriteAnimation != null) {
                        val animationType = CharacterInteractionSystem.getAnimationForEmotion(
                            state.interactionState.emotion
                        )
                        scaledCharacter.copy(
                            position = DpOffset(
                                scaledCharacter.position.x,
                                scaledCharacter.position.y - 10.dp
                            ),
                            scale = 1.15f,
                            spriteAnimation = scaledCharacter.spriteAnimation.copy(
                                currentAnimation = animationType,
                                isAnimating = true,
                            ),
                        )
                    } else {
                        scaledCharacter
            }

            key(character.id) {
                AnimatedCharacter(
                    character = interactionCharacter,
                    sceneIndex = state.playbackState.currentSceneIndex,
                    playbackSpeed = state.playbackState.speed,
                    isInteractive = !state.playbackState.isPlaying,
                    onCharacterClick = { clickedCharacter ->
                        if (!state.playbackState.isPlaying) {
                            onEvent(StageEvent.CharacterClick(clickedCharacter))
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // 말풍선들 (재생 중일 때만)
        if (state.playbackState.isPlaying) {
            // 스케일링된 캐릭터 리스트 생성
            val scaledCharacters = if (actualStageSize != null && currentScene != null) {
                with(density) {
                    val actualWidthDp = actualStageSize!!.width.toDp()
                    val actualHeightDp = actualStageSize!!.height.toDp()
                    val baseWidth = 360.dp
                    val baseHeight = 300.dp

                    val scaleX = actualWidthDp / baseWidth
                    val scaleY = actualHeightDp / baseHeight

                    currentScene.characters.map { char ->
                        char.copy(
                            position = DpOffset(
                                x = char.position.x * scaleX,
                                y = char.position.y * scaleY
                            ),
                            size = char.size * scaleY
                        )
                    }
                }
            } else {
                currentScene?.characters ?: emptyList()
            }

            currentScene?.dialogues?.forEachIndexed { index, dialogue ->
                key(state.playbackState.currentSceneIndex, dialogue.id) {
                    // 스케일링된 캐릭터에서 찾기
                    val speakingCharacter = dialogue.speakerName?.let { name ->
                        scaledCharacters.find { it.name == name }
                    }

                    AnimatedSpeechBubble(
                        dialogue = dialogue,
                        sceneIndex = state.playbackState.currentSceneIndex,
                        playbackSpeed = state.playbackState.speed,
                        character = speakingCharacter,  // 스케일링된 위치
                        showDebugPoints = state.showDebugPoints,
                        allCharacters = scaledCharacters,  // 스케일링된 리스트
                        stageWidthDp = if (actualStageSize != null) {
                            with(density) { actualStageSize!!.width.toDp() }
                        } else {
                            260.dp
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        // 상호작용 대사 말풍선
        state.interactionState.dialogue?.let { text ->
            val character = currentScene?.characters?.find {
                it.id == state.interactionState.characterId
            }
            character?.let { char ->
                val isMale = char.spriteAnimation?.gender == CharacterGender.MALE ||
                        char.id.contains("male", ignoreCase = true) ||
                        char.name.contains("상철", ignoreCase = true)

                val voice = if (isMale) {
                    CharacterInteractionSystem.getMaleVoiceForEmotion(state.interactionState.emotion)
                } else {
                    CharacterInteractionSystem.getFemaleVoiceForEmotion(state.interactionState.emotion)
                }

                InteractionSpeechBubble(
                    text = text,
                    character = char,
                    voice = voice,
                    onDismiss = { onEvent(StageEvent.DismissInteraction) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // 디버그 점 표시 (캐릭터 위치만)
            if (state.showDebugPoints) {
                // 스케일링된 캐릭터 리스트 (말풍선과 동일)
                val scaledCharactersForDebug =
                    if (actualStageSize != null && currentScene != null) {
                        with(density) {
                            val actualWidthDp = actualStageSize!!.width.toDp()
                            val actualHeightDp = actualStageSize!!.height.toDp()
                            val baseWidth = 360.dp
                            val baseHeight = 300.dp

                            val scaleX = actualWidthDp / baseWidth
                            val scaleY = actualHeightDp / baseHeight

                            currentScene.characters.map { char ->
                                char.copy(
                                    position = DpOffset(
                                        x = char.position.x * scaleX,
                                        y = char.position.y * scaleY
                                    ),
                                    size = char.size * scaleY
                                )
                            }
                        }
                    } else {
                        currentScene?.characters ?: emptyList()
                    }

                DebugPointsOverlay(
                    characters = scaledCharactersForDebug,
                    dialogues = emptyList(),  // 말풍선 점은 AnimatedSpeechBubble 내부에서 처리
                    modifier = Modifier.fillMaxSize()
                )
        }

        // (UI 컨트롤들은 하단 패널로 이동)

        // 선택지 UI
        if (state.playbackState.isPlaying &&
            state.choiceState.isWaiting &&
            state.choiceState.choices != null
        ) {
            ChoicesDialog(
                choices = state.choiceState.choices!!,
                onChoiceSelected = { nextSceneIndex ->
                    onEvent(StageEvent.SelectChoice(nextSceneIndex))
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

        // 하단 컨트롤 패널
        StageControlPanel(
            state = state,
            onEvent = onEvent,
            onScenarioSelectClick = onScenarioSelectClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    // 재생 시작 시 상호작용 초기화
    LaunchedEffect(state.playbackState.isPlaying) {
        if (state.playbackState.isPlaying) {
            onEvent(StageEvent.DismissInteraction)
        }
    }

    // 선택지 감지
    LaunchedEffect(currentScene, state.playbackState.isPlaying, state.playbackState.speed) {
        if (state.playbackState.isPlaying && currentScene != null) {
            val choicesDialogue = currentScene.dialogues.firstOrNull { it.choices != null }
            if (choicesDialogue != null && choicesDialogue.choices != null) {
                delay(
                    calculateSafeDelay(
                        choicesDialogue.delayMillis + 2000,
                        state.playbackState.speed
                    )
                )
                onEvent(StageEvent.DetectChoice(choicesDialogue.choices!!))
            }
        }
    }

    // 스크립트 타임라인 진행
    LaunchedEffect(
        state.currentScript,
        state.playbackState.currentSceneIndex,
        state.playbackState.isPlaying,
        state.playbackState.speed,
        state.choiceState.isWaiting
    ) {
        if (state.playbackState.isPlaying &&
            state.currentScript != null &&
            !state.choiceState.isWaiting
        ) {

            currentScene?.let { scene ->
                delay(calculateSafeDelay(scene.durationMillis, state.playbackState.speed))

                if (scene.isEnding ||
                    state.playbackState.currentSceneIndex >= state.currentScript!!.scenes.lastIndex
                ) {
                    onEvent(StageEvent.ScriptEnded)
                } else {
                    onEvent(StageEvent.AdvanceScene)
                }
            }
        }
    }

    // AI 생성 다이얼로그와 로직
    if (state.aiGenerationState.showDialog) {
        val context = LocalContext.current

        AIGenerationDialog(
            userInput = state.aiGenerationState.userInput,
            isGenerating = state.aiGenerationState.isGenerating,
            generationError = state.aiGenerationState.error,
            onUserInputChange = { input ->
                onEvent(StageEvent.UpdateAIInput(input))
            },
            onDismiss = {
                if (!state.aiGenerationState.isGenerating) {
                    onEvent(StageEvent.HideAIDialog)
                }
            },
            onGenerate = {
                // GenerateAIScenario는 내부 트리거만, 실제 호출은 여기서
                onEvent(StageEvent.GenerateAIScenario)
            }
        )
    }
}

/**
 * 무대 배경
 */
@Composable
private fun StageBackground(
    @DrawableRes backgroundRes: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(Color.Black)) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black),
        )
    }
}

/**
 * 하단 컨트롤 패널
 * 왼쪽: 속도 조절, 중앙: 씬 번호, 오른쪽: 재생/목록/음성
 */
@Composable
private fun StageControlPanel(
    state: StageState,
    onEvent: (StageEvent) -> Unit,
    onScenarioSelectClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 재생 속도 조절
            SpeedControl(
                speed = state.playbackState.speed,
                onSpeedChange = { speed -> onEvent(StageEvent.ChangePlaybackSpeed(speed)) }
            )

            // 중앙: 씬 번호
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                SceneIndicator(
                    currentScene = state.playbackState.currentSceneIndex + 1,
                    totalScenes = state.currentScript?.scenes?.size ?: 1
                )
            }

            // 오른쪽: 컨트롤 버튼들
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 디버그 점 토글 버튼
                IconButton(
                    onClick = { onEvent(StageEvent.ToggleDebugPoints) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "디버그 점",
                        modifier = Modifier.size(18.dp),
                        tint = if (state.showDebugPoints)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }

                // 음성 엔진 설정
                VoiceEngineButton()

                // 목록 이동 버튼 (있을 때만)
                if (onScenarioSelectClick != null) {
                    IconButton(
                        onClick = onScenarioSelectClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "목록",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 재생/정지 버튼 (2/3 크기, 흰색 배경, 검정 아이콘)
                FilledIconButton(
                    onClick = {
                        if (state.playbackState.isPlaying) {
                            onEvent(StageEvent.Stop)
                        } else {
                            onEvent(StageEvent.Play)
                        }
                    },
                    modifier = Modifier.size(28.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = if (state.playbackState.isPlaying)
                            Icons.Filled.Close
                        else
                            Icons.Filled.PlayArrow,
                        contentDescription = if (state.playbackState.isPlaying) "정지" else "재생",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

/**
 * 재생 속도 조절 버튼 (팝업 방식)
 */
@Composable
private fun SpeedControl(
    speed: Float,
    onSpeedChange: (Float) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        // 현재 속도 표시 버튼
        Surface(
            onClick = { showMenu = true },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.height(28.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${speed}x",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "속도 선택",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // 속도 선택 드롭다운
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            val speeds = listOf(0.5f, 1f, 1.5f, 2f, 2.5f, 3f)

            speeds.forEach { s ->
                SpeedMenuItem(
                    speed = s,
                    currentSpeed = speed,
                    onSelect = {
                        onSpeedChange(s)
                        showMenu = false
                    }
                )
            }
        }
    }
}

/**
 * 속도 메뉴 아이템
 */
@Composable
private fun SpeedMenuItem(
    speed: Float,
    currentSpeed: Float,
    onSelect: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (currentSpeed == speed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Spacer(modifier = Modifier.size(16.dp))
                }
                Text("${speed}x")
            }
        },
        onClick = onSelect
    )
}

/**
 * 음성 엔진 설정 버튼
 */
@Composable
private fun VoiceEngineButton() {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { showMenu = true },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "음성 엔진",
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
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

/**
 * 음성 엔진 메뉴 아이템
 */
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (currentEngine == engineType) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Spacer(modifier = Modifier.size(16.dp))
                }
                Text(text)
            }
        },
        onClick = onSelect
    )
}

/**
 * 씬 번호 표시 (Scenes 레이블 포함, 작은 텍스트)
 */
@Composable
private fun SceneIndicator(
    currentScene: Int,
    totalScenes: Int
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Scenes",
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = "$currentScene/$totalScenes",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
