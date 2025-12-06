package com.example.cutestage.stage

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
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
 * @param viewModel Hilt가 자동 주입 (테스트 시 수동 주입 가능)
 */
@Composable
fun StageView(
    modifier: Modifier = Modifier,
    script: TheaterScript? = null,
    onScriptEnd: () -> Unit = {},
    viewModel: StageViewModel = hiltViewModel()
) {
    // 초기 스크립트 설정
    LaunchedEffect(script) {
        viewModel.setInitialScript(script)
        viewModel.setOnScriptEnd(onScriptEnd)
    }

    StageViewContent(
        state = viewModel.state,
        onEvent = viewModel::handleEvent,  // 완전히 일관된 API!
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
    modifier: Modifier = Modifier,
) {
    val currentScene = remember(state.currentScript, state.playbackState.currentSceneIndex) {
        state.currentScript?.scenes?.getOrNull(state.playbackState.currentSceneIndex)
    }

    Box(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
    ) {
        // 무대 배경
        key(state.playbackState.currentSceneIndex) {
            StageBackground(
                backgroundRes = currentScene?.backgroundRes ?: R.drawable.stage_floor,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // 캐릭터들
        currentScene?.characters?.forEach { character ->
            val isInteracting = state.interactionState.characterId == character.id &&
                    state.interactionState.dialogue != null
            val interactionCharacter = if (isInteracting && character.spriteAnimation != null) {
                val animationType = CharacterInteractionSystem.getAnimationForEmotion(
                    state.interactionState.emotion
                )
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
            currentScene?.dialogues?.forEachIndexed { index, dialogue ->
                key(state.playbackState.currentSceneIndex, dialogue.text, index) {
                    AnimatedSpeechBubble(
                        dialogue = dialogue,
                        sceneIndex = state.playbackState.currentSceneIndex,
                        playbackSpeed = state.playbackState.speed,
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

        // UI 컨트롤들
        StageControls(
            isPlaying = state.playbackState.isPlaying,
            playbackSpeed = state.playbackState.speed,
            currentScript = state.currentScript,
            currentSceneIndex = state.playbackState.currentSceneIndex,
            onPlaybackSpeedChange = { speed ->
                onEvent(StageEvent.ChangePlaybackSpeed(speed))
            },
            onStopPlaying = { onEvent(StageEvent.Stop) },
            onScenarioSelected = { scenario, script, shouldPlay ->
                onEvent(StageEvent.LoadScenario(scenario, script, shouldPlay))
            },
            onShowAIDialog = { onEvent(StageEvent.ShowAIDialog) },
            onPlay = { onEvent(StageEvent.Play) }
        )

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
