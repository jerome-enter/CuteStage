package com.example.cutestage.stage

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.cutestage.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 연극 무대 컴포저블
 * 타임라인 기반 스크립트를 실행하여 캐릭터 애니메이션과 대사를 표현
 *
 * @param modifier Modifier
 * @param script 실행할 스크립트 (null이면 빈 무대)
 * @param onScriptEnd 스크립트 종료 콜백
 */
@Composable
fun StageView(
    modifier: Modifier = Modifier,
    script: TheaterScript? = null,
    onScriptEnd: () -> Unit = {},
) {
    var currentScript by remember { mutableStateOf(script) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var currentSceneIndex by remember(currentScript) { mutableStateOf(0) }
    val currentScene by remember(currentScript, currentSceneIndex) {
        derivedStateOf {
            currentScript?.scenes?.getOrNull(currentSceneIndex)
        }
    }

    // 상호작용 대사 상태
    var interactionDialogue by remember { mutableStateOf<String?>(null) }
    var interactionCharacterId by remember { mutableStateOf<String?>(null) }
    var interactionEmotion by remember { mutableStateOf(CharacterInteractionSystem.EmotionType.NORMAL) }

    // 클릭 횟수 추적
    var maleClickCount by remember { mutableStateOf(0) }
    var femaleClickCount by remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) }
    var maleAngryCount by remember { mutableStateOf(0) }
    var femaleAngryCount by remember { mutableStateOf(0) }

    // 선택지 상태
    var waitingForChoice by remember { mutableStateOf(false) }
    var pendingChoices by remember { mutableStateOf<List<Choice>?>(null) }

    // AI 생성 다이얼로그 상태
    var showDialog by remember { mutableStateOf(false) }
    var userInput by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var generationError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
    ) {
        // 무대 배경
        key(currentSceneIndex) {
            StageBackground(
                backgroundRes = currentScene?.backgroundRes ?: R.drawable.stage_floor,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // 캐릭터들
        currentScene?.characters?.forEach { character ->
            val isInteracting =
                interactionCharacterId == character.id && interactionDialogue != null
            val interactionCharacter = if (isInteracting && character.spriteAnimation != null) {
                val animationType =
                    CharacterInteractionSystem.getAnimationForEmotion(interactionEmotion)
                character.copy(
                    position = DpOffset(character.position.x, character.position.y - 10.dp),
                    scale = 1.15f,
                    spriteAnimation = character.spriteAnimation.copy(
                        currentAnimation = animationType,
                        isAnimating = true,
                    ),
                )
            } else {
                character
            }

            key(character.id) {
                AnimatedCharacter(
                    character = interactionCharacter,
                    sceneIndex = currentSceneIndex,
                    playbackSpeed = playbackSpeed,
                    isInteractive = !isPlaying,
                    onCharacterClick = { clickedCharacter ->
                        if (!isPlaying) {
                            handleCharacterClick(
                                character = clickedCharacter,
                                currentTime = System.currentTimeMillis(),
                                lastClickTime = lastClickTime,
                                maleClickCount = maleClickCount,
                                femaleClickCount = femaleClickCount,
                                maleAngryCount = maleAngryCount,
                                femaleAngryCount = femaleAngryCount,
                                onUpdateClick = { male, female, lastTime, maleAngry, femaleAngry, dialogue, emotion, charId ->
                                    maleClickCount = male
                                    femaleClickCount = female
                                    lastClickTime = lastTime
                                    maleAngryCount = maleAngry
                                    femaleAngryCount = femaleAngry
                                    interactionDialogue = dialogue
                                    interactionEmotion = emotion
                                    interactionCharacterId = charId
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // 말풍선들 (재생 중일 때만)
        if (isPlaying) {
            currentScene?.dialogues?.forEachIndexed { index, dialogue ->
                key(currentSceneIndex, dialogue.text, index) {
                    AnimatedSpeechBubble(
                        dialogue = dialogue,
                        sceneIndex = currentSceneIndex,
                        playbackSpeed = playbackSpeed,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        // 상호작용 대사 말풍선
        interactionDialogue?.let { text ->
            val character = currentScene?.characters?.find { it.id == interactionCharacterId }
            character?.let { char ->
                val isMale = char.spriteAnimation?.gender == CharacterGender.MALE ||
                        char.id.contains("male", ignoreCase = true) ||
                        char.name.contains("상철", ignoreCase = true)

                val voice = if (isMale) {
                    CharacterInteractionSystem.getMaleVoiceForEmotion(interactionEmotion)
                } else {
                    CharacterInteractionSystem.getFemaleVoiceForEmotion(interactionEmotion)
                }

                InteractionSpeechBubble(
                    text = text,
                    character = char,
                    voice = voice,
                    onDismiss = {
                        interactionDialogue = null
                        interactionCharacterId = null
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // UI 컨트롤들
        StageControls(
            isPlaying = isPlaying,
            playbackSpeed = playbackSpeed,
            currentScript = currentScript,
            currentSceneIndex = currentSceneIndex,
            onPlaybackSpeedChange = { playbackSpeed = it },
            onStopPlaying = {
                isPlaying = false
                StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
                currentScript = StageTestScenario.createTestScript()
                currentSceneIndex = 0
            },
            onScenarioSelected = { scenario, script, shouldPlay ->
                currentScript = script
                currentSceneIndex = 0
                if (shouldPlay) isPlaying = true
            },
            onShowAIDialog = { showDialog = true },
            onPlay = {
                if (currentScript != null) {
                    currentSceneIndex = 0
                    isPlaying = true
                }
            }
        )

        // 선택지 UI
        if (isPlaying && waitingForChoice && pendingChoices != null) {
            ChoicesDialog(
                choices = pendingChoices!!,
                onChoiceSelected = { nextSceneIndex ->
                    currentSceneIndex = nextSceneIndex
                    waitingForChoice = false
                    pendingChoices = null
                }
            )
        }
    }

    // 재생 시작 시 상호작용 초기화
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            interactionDialogue = null
            interactionCharacterId = null
            maleClickCount = 0
            femaleClickCount = 0
            maleAngryCount = 0
            femaleAngryCount = 0
        }
    }

    // 선택지 감지
    LaunchedEffect(currentScene, isPlaying, playbackSpeed) {
        val scene = currentScene
        if (isPlaying && scene != null) {
            val choicesDialogue = scene.dialogues.firstOrNull { it.choices != null }
            if (choicesDialogue != null && choicesDialogue.choices != null) {
                delay(calculateSafeDelay(choicesDialogue.delayMillis + 2000, playbackSpeed))
                waitingForChoice = true
                pendingChoices = choicesDialogue.choices
            }
        }
    }

    // 스크립트 타임라인 진행
    LaunchedEffect(currentScript, currentSceneIndex, isPlaying, playbackSpeed, waitingForChoice) {
        val script = currentScript
        if (isPlaying && script != null && !waitingForChoice) {
            currentScene?.let { scene ->
                delay(calculateSafeDelay(scene.durationMillis, playbackSpeed))

                if (scene.isEnding || currentSceneIndex >= script.scenes.lastIndex) {
                    isPlaying = false
                    onScriptEnd()
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
                    currentScript = StageTestScenario.createTestScript()
                    currentSceneIndex = 0
                } else {
                    currentSceneIndex++
                }
            }
        }
    }

    // AI 생성 다이얼로그
    if (showDialog) {
        AIGenerationDialog(
            userInput = userInput,
            isGenerating = isGenerating,
            generationError = generationError,
            onUserInputChange = { userInput = it },
            onDismiss = {
                if (!isGenerating) {
                    showDialog = false
                    userInput = ""
                    generationError = null
                }
            },
            onGenerate = {
                if (userInput.isBlank()) return@AIGenerationDialog
                coroutineScope.launch {
                    isGenerating = true
                    generationError = null
                    try {
                        ScenarioConverter.init(context)
                        val generatedScenario = withContext(Dispatchers.IO) {
                            GeminiScenarioGenerator.generateScenario(context, userInput)
                        }

                        if (generatedScenario.status == "error") {
                            generationError = generatedScenario.message
                            return@launch
                        }

                        val theaterScript =
                            ScenarioConverter.convertToTheaterScript(generatedScenario)
                        currentScript = theaterScript
                        currentSceneIndex = 0
                        isPlaying = true

                        showDialog = false
                        userInput = ""
                        isGenerating = false
                        generationError = null
                    } catch (e: Exception) {
                        generationError = e.message ?: "알 수 없는 오류"
                        isGenerating = false
                    }
                }
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
 * 캐릭터 클릭 핸들러
 */
private fun handleCharacterClick(
    character: CharacterState,
    currentTime: Long,
    lastClickTime: Long,
    maleClickCount: Int,
    femaleClickCount: Int,
    maleAngryCount: Int,
    femaleAngryCount: Int,
    onUpdateClick: (
        male: Int,
        female: Int,
        lastTime: Long,
        maleAngry: Int,
        femaleAngry: Int,
        dialogue: String,
        emotion: CharacterInteractionSystem.EmotionType,
        charId: String
    ) -> Unit
) {
    val isMale = character.spriteAnimation?.gender == CharacterGender.MALE ||
            character.id.contains("male", ignoreCase = true) ||
            character.name.contains("상철", ignoreCase = true)

    var newMaleCount = maleClickCount
    var newFemaleCount = femaleClickCount
    var newMaleAngryCount = maleAngryCount
    var newFemaleAngryCount = femaleAngryCount

    // 5초 이상 지났으면 클릭 카운트 리셋
    if (currentTime - lastClickTime > 5000) {
        newMaleCount = 0
        newFemaleCount = 0
        newMaleAngryCount = 0
        newFemaleAngryCount = 0
    }

    // 클릭 횟수 증가
    if (isMale) {
        newMaleCount++
    } else {
        newFemaleCount++
    }

    // 감정 시스템을 통해 대사와 감정 결정
    val clickCount = if (isMale) newMaleCount else newFemaleCount
    val emotionalDialogue = CharacterInteractionSystem.getEmotionalDialogue(
        clickCount = clickCount,
        isMale = isMale,
    )

    // 화난 상태 추적 (3번 화내면 리셋)
    if (emotionalDialogue.emotion == CharacterInteractionSystem.EmotionType.ANGRY) {
        if (isMale) {
            newMaleAngryCount++
            if (newMaleAngryCount >= 3) {
                newMaleCount = 0
                newMaleAngryCount = 0
            }
        } else {
            newFemaleAngryCount++
            if (newFemaleAngryCount >= 3) {
                newFemaleCount = 0
                newFemaleAngryCount = 0
            }
        }
    }

    onUpdateClick(
        newMaleCount,
        newFemaleCount,
        currentTime,
        newMaleAngryCount,
        newFemaleAngryCount,
        emotionalDialogue.text,
        emotionalDialogue.emotion,
        character.id
    )
}

// ==================== 데이터 모델 ====================

/**
 * 연극 스크립트 (전체 시나리오)
 */
@Immutable
data class TheaterScript(
    val scenes: List<SceneState>,
    val debug: Boolean = false,
)

/**
 * 씬 (장면)
 */
@Immutable
data class SceneState(
    @DrawableRes val backgroundRes: Int? = null,
    val characters: List<CharacterState> = emptyList(),
    val dialogues: List<DialogueState> = emptyList(),
    val durationMillis: Long = 3000L,
    val isEnding: Boolean = false,
)

/**
 * 캐릭터 상태
 */
@Immutable
data class CharacterState(
    val id: String,
    val name: String,
    @DrawableRes val imageRes: Int,
    val position: DpOffset = DpOffset(0.dp, 0.dp),
    val size: Dp = 80.dp,
    val alpha: Float = 1f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val flipX: Boolean = false,
    val animationDuration: Int = 500,
    val easing: Easing = FastOutSlowInEasing,
    val voice: CharacterVoice = CharacterVoice(),
    val spriteAnimation: CharacterAnimationState? = null,
)

/**
 * 대사 상태
 */
@Immutable
data class DialogueState(
    val text: String,
    val position: DpOffset,
    val speakerName: String? = null,
    val delayMillis: Long = 0L,
    val typingSpeedMs: Long = 50L,
    val voice: CharacterVoice? = null,
    val notes: List<SongNote>? = null,
    val choices: List<Choice>? = null,
)

/**
 * 노래 음표
 */
@Immutable
data class SongNote(
    val lyric: String,
    val pitch: Float,
    val duration: Int,
)

/**
 * 선택지
 */
@Immutable
data class Choice(
    val text: String,
    val nextSceneIndex: Int,
)

// ==================== 확장 함수 ====================

fun CharacterState.moveTo(x: Dp, y: Dp, duration: Int = 500): CharacterState =
    copy(position = DpOffset(x, y), animationDuration = duration)

fun CharacterState.fadeIn(duration: Int = 500): CharacterState =
    copy(alpha = 1f, animationDuration = duration)

fun CharacterState.fadeOut(duration: Int = 500): CharacterState =
    copy(alpha = 0f, animationDuration = duration)

fun CharacterState.scaleTo(scale: Float, duration: Int = 500): CharacterState =
    copy(scale = scale, animationDuration = duration)

fun CharacterState.flip(): CharacterState = copy(flipX = !flipX)
