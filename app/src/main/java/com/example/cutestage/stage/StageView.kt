package com.example.cutestage.stage

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.cutestage.R
import kotlin.math.max
import kotlinx.coroutines.delay

/**
 * 재생 속도를 적용한 안전한 delay 계산
 * 0으로 나누기, 음수, 너무 작은 값 방지
 */
private fun calculateSafeDelay(
    durationMs: Long,
    playbackSpeed: Float,
): Long { // playbackSpeed가 유효하지 않으면 기본 속도 사용
    val safeSpeed =
        if (playbackSpeed > 0.1f && playbackSpeed.isFinite()) playbackSpeed else 1.0f // 최소 1ms 보장, 최대 Long.MAX_VALUE 방지
    return max(1L, (durationMs / safeSpeed).toLong().coerceIn(1L, Long.MAX_VALUE / 2))
}

/**
 * 연극 무대 컴포저블
 * 타임라인 기반 스크립트를 실행하여 캐릭터 애니메이션과 대사를 표현
 *
 * 클릭 시 테스트 시나리오가 자동으로 실행됩니다.
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
) { // 스크립트 설정 (null이면 빈 무대)
    var currentScript by remember {
        mutableStateOf(script)
    }
    var isPlaying by remember { mutableStateOf(false) } // 재생 속도 (1.0x, 1.5x, 2.0x)
    var playbackSpeed by remember { mutableStateOf(1.0f) } // 스크립트가 변경되면 씬 인덱스를 자동으로 0으로 리셋
    var currentSceneIndex by remember(currentScript) { mutableStateOf(0) } // currentScript와 currentSceneIndex에 따라 현재 씬 계산
    val currentScene by remember(currentScript, currentSceneIndex) {
        derivedStateOf {
            currentScript?.scenes?.getOrNull(currentSceneIndex)
        }
    } // 상호작용 대사 상태
    var interactionDialogue by remember { mutableStateOf<String?>(null) }
    var interactionCharacterId by remember { mutableStateOf<String?>(null) }
    var interactionEmotion by remember { mutableStateOf(CharacterInteractionSystem.EmotionType.NORMAL) } // 클릭 횟수 추적 (캐릭터별)
    var maleClickCount by remember { mutableStateOf(0) }
    var femaleClickCount by remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) } // 화난 상태 카운트 (3번 화내면 리셋)
    var maleAngryCount by remember { mutableStateOf(0) }
    var femaleAngryCount by remember { mutableStateOf(0) } // 선택지 대기 상태
    var waitingForChoice by remember { mutableStateOf(false) }
    var pendingChoices by remember { mutableStateOf<List<Choice>?>(null) }

    Box(
        modifier = modifier
                .padding(10.dp) // StageView 상하좌우 10dp 여백
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)) // 네 모서리 16dp 라운드
                .background(Color.Black) // 검은색 배경
    ) {
        // 무대 배경 ( 씬 변경 시 recomposition 보장)
        key(currentSceneIndex) {
            StageBackground(
                backgroundRes = currentScene?.backgroundRes
                    ?: R.drawable.stage_floor, // 빈 무대일 때 기본 무대 바닥
                modifier = Modifier.fillMaxSize(),
            )
        }         // 캐릭터들 (씬 변경 시 recomposition 보장)
        currentScene?.characters?.forEachIndexed { index, character -> // 상호작용 시 애니메이션 활성화
            val isInteracting =
                interactionCharacterId == character.id && interactionDialogue != null
            val interactionCharacter =
                if (isInteracting && character.spriteAnimation != null) { // 감정에 따른 애니메이션 선택
                    val animationType =
                        CharacterInteractionSystem.getAnimationForEmotion(interactionEmotion)
                    character.copy(
                        position = DpOffset(
                            character.position.x,
                            character.position.y - 10.dp
                        ), // 앞으로 (y 감소)
                        scale = 1.15f, // 크기 증가
                        spriteAnimation = character.spriteAnimation.copy(
                            currentAnimation = animationType,
                            isAnimating = true,
                        ),
                    )
                } else {
                    character
                }

            // key에서 캐릭터 ID만 사용 (씬 변경 시에도 컴포넌트 유지로 위치 애니메이션 보간)
            key(character.id) {
                AnimatedCharacter(
                    character = interactionCharacter,
                    sceneIndex = currentSceneIndex,
                    playbackSpeed = playbackSpeed,
                    isInteractive = !isPlaying, // 재생 중이 아닐 때만 클릭 가능
                    onCharacterClick = { clickedCharacter ->
                        if (!isPlaying) { // 재생 중이 아닐 때만 반응
                            // 성별 판단
                            val isMale =
                                clickedCharacter.spriteAnimation?.gender == CharacterGender.MALE ||
                                    clickedCharacter.id.contains(
                                        "male",
                                        ignoreCase = true,
                                    ) ||
                                    clickedCharacter.name.contains(
                                        "상철",
                                        ignoreCase = true,
                                    ) // 현재 시간
                            val currentTime = System.currentTimeMillis()

                            // 5초 이상 지났으면 클릭 카운트 리셋
                            if (currentTime - lastClickTime > 5000) {
                                maleClickCount = 0
                                femaleClickCount = 0
                                maleAngryCount = 0
                                femaleAngryCount = 0
                            }

                            // 클릭 횟수 증가
                            if (isMale) {
                                maleClickCount++
                            } else {
                                femaleClickCount++
                            }
                            lastClickTime = currentTime

                            // 감정 시스템을 통해 대사와 감정 결정
                            val clickCount = if (isMale) maleClickCount else femaleClickCount
                            val emotionalDialogue = CharacterInteractionSystem.getEmotionalDialogue(
                                clickCount = clickCount,
                                isMale = isMale,
                            )

                            // 화난 상태 추적 (3번 화내면 리셋)
                            if (emotionalDialogue.emotion == CharacterInteractionSystem.EmotionType.ANGRY) {
                                if (isMale) {
                                    maleAngryCount++
                                    // 3번 화내면 다시 평범한 상태로 리셋
                                    if (maleAngryCount >= 3) {
                                        maleClickCount = 0
                                        maleAngryCount = 0
                                    }
                                } else {
                                    femaleAngryCount++
                                    // 3번 화내면 다시 평범한 상태로 리셋
                                    if (femaleAngryCount >= 3) {
                                        femaleClickCount = 0
                                        femaleAngryCount = 0
                                    }
                                }
                            }

                            interactionDialogue = emotionalDialogue.text
                            interactionEmotion = emotionalDialogue.emotion
                            interactionCharacterId = clickedCharacter.id
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } // 말풍선들 (씬 변경 시 recomposition 보장, 재생 중일 때만 표시)
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
        } // 상호작용 대사 말풍선 (재생 중이 아닐 때 캐릭터 클릭 시 표시)
        interactionDialogue?.let { text ->
            val character = currentScene?.characters?.find { it.id == interactionCharacterId }
            character?.let { char ->
                // 성별 판단
                val isMale =
                    char.spriteAnimation?.gender == CharacterGender.MALE ||
                        char.id.contains(
                            "male",
                            ignoreCase = true,
                        ) ||
                        char.name.contains("상철", ignoreCase = true) // 감정에 따른 음성 설정
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
        }         // 재생 중일 때 종료 버튼 표시 (오른쪽 하단)
        if (isPlaying) {
            Surface(
                onClick = {
                    isPlaying = false // PLAYGROUND 시나리오로 복귀
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
                    currentScript = StageTestScenario.createTestScript()
                    currentSceneIndex = 0
                },
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
        // 재생 속도 조절 버튼 (왼쪽 위)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Black.copy(alpha = 0.6f),
            modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 1.0x 버튼
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (playbackSpeed == 1.0f) Color.White else Color.Transparent,
                    modifier = Modifier.size(width = 40.dp, height = 24.dp),
                    onClick = { playbackSpeed = 1.0f },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "1x",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (playbackSpeed == 1.0f) Color.Black else Color.White,
                        )
                    }
                } // 1.5x 버튼
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (playbackSpeed == 1.5f) Color.White else Color.Transparent,
                    modifier = Modifier.size(width = 40.dp, height = 24.dp),
                    onClick = { playbackSpeed = 1.5f },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "1.5x",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (playbackSpeed == 1.5f) Color.Black else Color.White,
                        )
                    }
                } // 2.0x 버튼
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (playbackSpeed == 2.0f) Color.White else Color.Transparent,
                    modifier = Modifier.size(width = 40.dp, height = 24.dp),
                    onClick = { playbackSpeed = 2.0f },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "2x",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (playbackSpeed == 2.0f) Color.Black else Color.White,
                        )
                    }
                }
            }
        } // 디버그 정보 (개발용)
        currentScript?.let { theScript ->
            if (theScript.debug) {
                Text(
                    text = "Scene: ${currentSceneIndex + 1}/${theScript.scenes.size}",
                    modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                )
            }
        } // 재생 중이 아닐 때 컨트롤 버튼들 (오른쪽 하단)
        if (!isPlaying) {
            var showScenarioMenu by remember { mutableStateOf(false) }
            var showVoiceEngineMenu by remember { mutableStateOf(false) } // 음성 엔진 선택 버튼 (왼쪽 하단)
            Box(
                modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 12.dp, start = 12.dp),
            ) {
                Surface(
                    onClick = { showVoiceEngineMenu = true },
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
                } // 음성 엔진 드롭다운 메뉴
                DropdownMenu(
                    expanded = showVoiceEngineMenu,
                    onDismissRequest = { showVoiceEngineMenu = false },
                ) {
                    val currentEngine = VoiceSoundManagerFactory.currentEngineType // AudioTrack 항목
                    DropdownMenuItem(text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (currentEngine == VoiceSoundType.AUDIO_TRACK) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "선택됨",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                Spacer(modifier = Modifier.size(20.dp))
                            }
                            Text("부드러운 소리 (기본)")
                        }
                    }, onClick = {
                        showVoiceEngineMenu = false
                        VoiceSoundManagerFactory.currentEngineType = VoiceSoundType.AUDIO_TRACK
                    }) // ToneGenerator 항목
                    DropdownMenuItem(text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (currentEngine == VoiceSoundType.TONE_GENERATOR) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "선택됨",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                Spacer(modifier = Modifier.size(20.dp))
                            }
                            Text("레트로 비프음")
                        }
                    }, onClick = {
                        showVoiceEngineMenu = false
                        VoiceSoundManagerFactory.currentEngineType = VoiceSoundType.TONE_GENERATOR
                    }) // AnimalVoice 항목
                    DropdownMenuItem(text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (currentEngine == VoiceSoundType.ANIMAL_VOICE) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "선택됨",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                Spacer(modifier = Modifier.size(20.dp))
                            }
                            Text("동물의 숲 스타일 🎮")
                        }
                    }, onClick = {
                        showVoiceEngineMenu = false
                        VoiceSoundManagerFactory.currentEngineType = VoiceSoundType.ANIMAL_VOICE
                    })
                }
            } // 버튼들을 가로로 배치
            Row(
                modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // 시나리오 선택 버튼 (작은 크기)
                Box {
                    Surface(
                        onClick = { showScenarioMenu = true },
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
                    }                     // 드롭다운 메뉴
                    DropdownMenu(
                        expanded = showScenarioMenu,
                        onDismissRequest = { showScenarioMenu = false },
                    ) {
                        val currentScenarioType =
                            StageTestScenario.currentScenario // PLAYGROUND (대기실)
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    if (currentScenarioType == StageTestScenario.ScenarioType.PLAYGROUND) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "선택됨",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                    Text("🏠 놀이터 (대기실)")
                                }
                            },
                            onClick = {
                                showScenarioMenu = false
                                StageTestScenario.currentScenario =
                                    StageTestScenario.ScenarioType.PLAYGROUND
                                currentScript = StageTestScenario.createTestScript()
                                currentSceneIndex = 0 // 재생하지 않음 (대기실은 상호작용 모드)
                            },
                        ) // 폭삭 속았수다
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    if (currentScenarioType == StageTestScenario.ScenarioType.FOOLISH_TRICK) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "선택됨",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                    Text("폭삭 속았수다 🐟")
                                }
                            },
                            onClick = {
                                showScenarioMenu = false
                                StageTestScenario.currentScenario =
                                    StageTestScenario.ScenarioType.FOOLISH_TRICK
                                currentScript = StageFoolishTrick.createFoolishTrickScenario()
                                currentSceneIndex = 0
                                isPlaying = true
                            },
                        ) // 옥순의 혼잣말
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    if (currentScenarioType == StageTestScenario.ScenarioType.OKSUN_MONOLOGUE) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "선택됨",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                    Text("옥순의 혼잣말")
                                }
                            },
                            onClick = {
                                showScenarioMenu = false
                                StageTestScenario.currentScenario =
                                    StageTestScenario.ScenarioType.OKSUN_MONOLOGUE
                                currentScript = StageTestScenario.createTestScript()
                                currentSceneIndex = 0
                                isPlaying = true
                            },
                        ) // 부부싸움
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    if (currentScenarioType == StageTestScenario.ScenarioType.COUPLE_FIGHT) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "선택됨",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                    Text("부부싸움")
                                }
                            },
                            onClick = {
                                showScenarioMenu = false
                                StageTestScenario.currentScenario =
                                    StageTestScenario.ScenarioType.COUPLE_FIGHT
                                currentScript = StageTestScenario.createTestScript()
                                currentSceneIndex = 0
                                isPlaying = true
                            },
                        ) // 만남 (정적)
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    if (currentScenarioType == StageTestScenario.ScenarioType.BASIC) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "선택됨",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                    Text("만남 (정적)")
                                }
                            },
                            onClick = {
                                showScenarioMenu = false
                                StageTestScenario.currentScenario =
                                    StageTestScenario.ScenarioType.BASIC
                                currentScript = StageTestScenario.createTestScript()
                                currentSceneIndex = 0
                                isPlaying = true
                            },
                        ) // 나는솔로
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    if (currentScenarioType == StageTestScenario.ScenarioType.I_AM_SOLO) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "선택됨",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                    Text("나는솔로 ♥")
                                }
                            },
                            onClick = {
                                showScenarioMenu = false
                                StageTestScenario.currentScenario =
                                    StageTestScenario.ScenarioType.I_AM_SOLO
                                currentScript = StageTestScenario.createTestScript()
                                currentSceneIndex = 0
                                isPlaying = true
                            },
                        ) // 하얀 바다새 (노래)
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Spacer(modifier = Modifier.size(20.dp))
                                    Text("🎵 하얀 바다새 (듀엣)")
                                }
                            },
                            onClick = {
                                showScenarioMenu = false
                                currentScript = StageSongScenario.createWhiteSeagullScenario()
                                currentSceneIndex = 0
                                isPlaying = true
                            },
                        ) // 사랑고백 (인터랙티브)
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Spacer(modifier = Modifier.size(20.dp))
                                    Text("💕 사랑고백 (선택형)")
                                }
                            },
                            onClick = {
                                showScenarioMenu = false
                                currentScript = StageLoveConfession.createLoveConfessionScenario()
                                currentSceneIndex = 0
                                isPlaying = true
                            },
                        )
                    }
                } // 재생 버튼 (작은 크기)
                Surface(
                    onClick = {
                        // 스크립트가 있을 때만 재생
                        if (currentScript != null) {
                            currentSceneIndex = 0 // 씬 인덱스 리셋
                            isPlaying = true
                        }
                    },
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
        } // 선택지 버튼 UI (재생 중이고 선택 대기 중일 때)
        if (isPlaying && waitingForChoice && pendingChoices != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 8.dp,
                modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp),
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

                    pendingChoices?.forEach { choice ->
                        Surface(
                            onClick = { // 선택한 씬으로 이동
                                currentSceneIndex = choice.nextSceneIndex
                                waitingForChoice = false
                                pendingChoices = null
                            },
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
    } // 재생 시작 시 상호작용 대사 및 클릭 카운트 초기화
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            interactionDialogue = null
            interactionCharacterId = null
            maleClickCount = 0
            femaleClickCount = 0
            maleAngryCount = 0
            femaleAngryCount = 0
        }
    } // 선택지 감지
    LaunchedEffect(currentScene, isPlaying, playbackSpeed) {
        val scene = currentScene // 로컬 변수로 저장 (스마트 캐스팅 위해)
        if (isPlaying && scene != null) { // 현재 씬의 대화 중 선택지가 있는지 확인
            val choicesDialogue = scene.dialogues.firstOrNull { it.choices != null }
            if (choicesDialogue != null && choicesDialogue.choices != null) { // 선택지가 있으면 대기
                delay(
                    calculateSafeDelay(
                        choicesDialogue.delayMillis + 2000,
                        playbackSpeed
                    )
                ) // 대사 후 2초 대기
                waitingForChoice = true
                pendingChoices = choicesDialogue.choices
            }
        }
    } // 스크립트 타임라인 진행
    LaunchedEffect(currentScript, currentSceneIndex, isPlaying, playbackSpeed, waitingForChoice) {
        val script = currentScript // 로컬 변수에 저장하여 smart cast 가능하도록
        if (isPlaying && script != null && !waitingForChoice) { // 선택지 대기 중이 아닐 때만 진행
            currentScene?.let { scene -> // 재생 속도에 따라 지연 시간 조정 (안전한 계산)
                delay(calculateSafeDelay(scene.durationMillis, playbackSpeed))
                if (currentSceneIndex < script.scenes.lastIndex) {
                    currentSceneIndex++
                } else {
                    isPlaying = false
                    onScriptEnd() // 시나리오 종료 후 PLAYGROUND로 복귀
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
                    currentScript = StageTestScenario.createTestScript()
                    currentSceneIndex = 0
                }
            }
        }
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
    Box(
        modifier = modifier.background(Color.Black),
    ) {
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
 * 애니메이션이 적용된 캐릭터
 *
 * 스프라이트 애니메이션 지원:
 * - spriteAnimation이 null이면 정적 이미지 사용
 * - spriteAnimation이 있으면 2프레임 애니메이션 자동 재생
 * - WALKING 애니메이션 시 발걸음 소리 자동 재생
 * - 클릭 상호작용 지원 (재생 중이 아닐 때)
 */
@Composable
private fun AnimatedCharacter(
    character: CharacterState,
    sceneIndex: Int,
    playbackSpeed: Float,
    isInteractive: Boolean = false,
    onCharacterClick: (CharacterState) -> Unit = {},
    modifier: Modifier = Modifier,
) { // 위치 애니메이션
    val offsetX by animateDpAsState(
        targetValue = character.position.x,
        animationSpec = tween(
            durationMillis = character.animationDuration,
            easing = character.easing,
        ),
        label = "character_offset_x",
    )
    val offsetY by animateDpAsState(
        targetValue = character.position.y,
        animationSpec = tween(
            durationMillis = character.animationDuration,
            easing = character.easing,
        ),
        label = "character_offset_y",
    ) // 투명도 애니메이션
    val alpha by animateFloatAsState(
        targetValue = character.alpha,
        animationSpec = tween(durationMillis = 500),
        label = "character_alpha",
    ) // 크기 애니메이션
    val scale by animateFloatAsState(
        targetValue = character.scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "character_scale",
    ) // 스프라이트 프레임 전환 (1 ↔ 2)
    // character.spriteAnimation?.currentAnimation을 key로 사용하여 애니메이션 변경 감지
    var currentFrame by remember(character.spriteAnimation?.currentAnimation) {
        mutableStateOf(1)
    } // 발걸음 소리를 위한 사운드 매니저
    val footstepSoundManager = remember(character.spriteAnimation?.currentAnimation) {
        VoiceSoundManagerFactory.create()
    }

    DisposableEffect(character.spriteAnimation?.currentAnimation) {
        onDispose {
            footstepSoundManager.release()
        }
    } // 스프라이트 애니메이션 자동 재생 + 발걸음 소리
    LaunchedEffect(character.spriteAnimation?.currentAnimation, playbackSpeed) {
        character.spriteAnimation?.let { spriteAnim ->
            if (spriteAnim.isAnimating) { // WALKING 애니메이션인지 확인
                val isWalking = spriteAnim.currentAnimation == CharacterAnimationType.WALKING

                while (true) { // 재생 속도에 따라 프레임 전환 속도 조정 (안전한 계산)
                    delay(calculateSafeDelay(spriteAnim.frameDuration, playbackSpeed))
                    currentFrame =
                        if (currentFrame == 1) 2 else 1 // 발걸음 소리 재생 (WALKING 애니메이션이고 프레임 1일 때만)
                    if (isWalking && currentFrame == 1) {
                        footstepSoundManager.playBeep(
                            pitch = 0.3f, // 낮은 음 (발걸음)
                            duration = 50, // 짧은 소리
                            volume = 0.3f, // 작은 볼륨
                        )
                    }
                }
            }
        }
    } // 현재 표시할 이미지 리소스 결정
    val displayImageRes = character.spriteAnimation?.let { spriteAnim ->
        CharacterAnimationResources.getAnimationResource(
            gender = spriteAnim.gender,
            animation = spriteAnim.currentAnimation,
            frame = currentFrame,
        )
    } ?: character.imageRes

    Box(modifier = modifier) {
        Image(
            painter = painterResource(displayImageRes),
            contentDescription = character.name,
            modifier = Modifier
                    .size(character.size)
                    .offset(x = offsetX, y = offsetY)
                    .graphicsLayer {
                        scaleX = scale * if (character.flipX) -1f else 1f
                        scaleY = scale
                        this.alpha = alpha
                        rotationZ = character.rotation
                    }
                    .then(
                        if (isInteractive) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                onCharacterClick(character)
                            }
                        } else {
                            Modifier
                        },
                    ),
        )
    }
}

/**
 * 애니메이션이 적용된 말풍선
 *
 * 타이밍 구조:
 * 1. delayMillis 대기
 * 2. 말풍선과 타자기를 동시에 시작 (빈 말풍선 방지)
 * 3. 부드러운 페이드인 애니메이션
 */
@Composable
private fun AnimatedSpeechBubble(
    dialogue: DialogueState,
    sceneIndex: Int,
    playbackSpeed: Float,
    modifier: Modifier = Modifier,
) {
    var visible by remember(sceneIndex) { mutableStateOf(false) } // 말풍선 등장 애니메이션 시간
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
                    .padding(5.dp), // 대화창이 StageView 경계에서 5dp 떨어지도록
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White, // 대화창 배경 흰색
                shadowElevation = 4.dp,
                modifier = Modifier
                        .offset(x = dialogue.position.x, y = dialogue.position.y)
                        .widthIn(max = 200.dp),
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
                    } // 대사 - 타자기 효과
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
private fun InteractionSpeechBubble(
    text: String,
    character: CharacterState,
    voice: CharacterVoice,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) { // text가 변경되면 모든 상태 리셋
    var visible by remember(text) { mutableStateOf(false) }
    var startTyping by remember(text) { mutableStateOf(false) }
    var isDismissing by remember(text) { mutableStateOf(false) } // 음성 매니저 (text마다 새로 생성)
    val soundManager = remember(text) {
        VoiceSoundManagerFactory.create()
    }

    DisposableEffect(text) {
        onDispose {
            soundManager.release()
        }
    } // 타자기 텍스트 상태 (text마다 리셋)
    var visibleText by remember(text) { mutableStateOf("") } // 타자기 효과 + 음성
    LaunchedEffect(text, startTyping) {
        if (!startTyping) {
            visibleText = ""
            return@LaunchedEffect
        }

        visibleText = ""
        text.forEachIndexed { index, char ->
            if (isDismissing) return@LaunchedEffect // 음성 재생 (공백이 아닐 때만)
            if (!char.isWhitespace()) {
                soundManager.playBeep(
                    pitch = voice.pitch,
                    duration = voice.duration,
                    volume = voice.volume,
                )
            }

            delay(voice.speed.toLong())
            visibleText = text.substring(0, index + 1)
        } // 타자기 완료 후 대기
        delay(2000)
        isDismissing = true
        visible = false
        delay(200)
        onDismiss()
    } // 말풍선 등장 애니메이션
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
                    .padding(5.dp), // 대화창이 StageView 경계에서 5dp 떨어지도록
        ) {
            // 캐릭터 위치에 맞춰 말풍선 표시 (연극할 때와 같은 위치)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                        .offset(
                            x = character.position.x + character.size / 2 - 100.dp,
                            y = 60.dp, // 연극할 때와 같은 높이
                        )
                        .widthIn(max = 200.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { // 클릭하면 즉시 닫기
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
                    Spacer(modifier = Modifier.height(4.dp)) // 타자기 텍스트
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
private fun TypewriterText(
    text: String,
    sceneIndex: Int,
    startTyping: Boolean,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    typingSpeedMs: Long = 50L,
    voice: CharacterVoice? = null,
    playbackSpeed: Float = 1.0f,
    notes: List<SongNote>? = null,
) { // sceneIndex가 변경되면 상태 리셋
    var visibleText by remember(
        sceneIndex,
        text,
    ) { mutableStateOf("") } // 음성 매니저는 씬마다 새로 생성하여 리소스 충돌 방지
    val soundManager = remember(sceneIndex) {
        VoiceSoundManagerFactory.create()
    } // 씬이 변경되거나 컴포넌트가 사라질 때 리소스 정리
    DisposableEffect(sceneIndex) {
        onDispose {
            soundManager.release()
        }
    } // 타자기 효과 실행
    LaunchedEffect(sceneIndex, text, startTyping, playbackSpeed) { // startTyping이 false면 대기
        if (!startTyping) {
            visibleText = ""
            return@LaunchedEffect
        }

        visibleText = "" // 노래 모드: notes가 있으면 각 음표의 pitch로 발음
        if (notes != null && notes.isNotEmpty()) {
            var textIndex = 0
            notes.forEach { note ->
                if (note.lyric.isNotBlank() && note.pitch > 0f) { // 음성 재생 (해당 음표의 정확한 pitch)
                    if (voice?.enabled != false) {
                        soundManager.playBeep(
                            pitch = note.pitch,
                            duration = (note.duration * 0.8f).toInt(),
                            volume = voice?.volume ?: 0.5f,
                        )
                    } // 글자 표시
                    textIndex += note.lyric.length
                    visibleText = text.take(textIndex) // 음표 길이만큼 대기
                    delay(calculateSafeDelay(note.duration.toLong(), playbackSpeed))
                } else { // 쉼표인 경우 대기만
                    delay(calculateSafeDelay(note.duration.toLong(), playbackSpeed))
                }
            }
        } else { // 일반 대화 모드: 기존 방식
            text.forEachIndexed { index, char -> // 음성 재생 (공백이 아닐 때만)
                if (voice?.enabled == true && !char.isWhitespace()) {
                    soundManager.playBeep(
                        pitch = voice.pitch,
                        duration = voice.duration,
                        volume = voice.volume,
                    )
                } // 글자 표시 속도 (재생 속도에 따라 조정, 안전한 계산)
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
    val spriteAnimation: CharacterAnimationState? = null, // 스프라이트 애니메이션 (null이면 정적 이미지)
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
    val voice: CharacterVoice? = null, // 음성 설정 (null이면 기본값 사용)
    val notes: List<SongNote>? = null, // 노래 음표 정보 (노래일 때만)
    val choices: List<Choice>? = null, // 사용자 선택지 (분기할 때만)
)

/**
 * 노래 음표 (노래 전용)
 */
@Immutable
data class SongNote(
    val lyric: String, // 글자
    val pitch: Float, // 음높이
    val duration: Int, // 지속 시간 (ms)
)

/**
 * 선택지 (사용자 선택)
 */
@Immutable
data class Choice(
    val text: String, // 선택지 텍스트
    val nextSceneIndex: Int, // 선택 시 이동할 씬 인덱스
) // ==================== 편의 확장 함수 ====================

/**
 * 캐릭터 이동 액션
 */
fun CharacterState.moveTo(
    x: Dp,
    y: Dp,
    duration: Int = 500,
): CharacterState =
    copy(
        position = DpOffset(x, y),
        animationDuration = duration,
    )

/**
 * 캐릭터 등장 액션
 */
fun CharacterState.fadeIn(duration: Int = 500): CharacterState = copy(alpha = 1f, animationDuration = duration)

/**
 * 캐릭터 퇴장 액션
 */
fun CharacterState.fadeOut(duration: Int = 500): CharacterState = copy(alpha = 0f, animationDuration = duration)

/**
 * 캐릭터 크기 변경 액션
 */
fun CharacterState.scaleTo(
    scale: Float,
    duration: Int = 500,
): CharacterState = copy(scale = scale, animationDuration = duration)

/**
 * 캐릭터 좌우 반전
 */
fun CharacterState.flip(): CharacterState = copy(flipX = !flipX)
