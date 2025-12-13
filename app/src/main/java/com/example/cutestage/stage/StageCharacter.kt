package com.example.cutestage.stage

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.core.*
import kotlinx.coroutines.delay
import kotlin.math.max

/**
 * 재생 속도를 적용한 안전한 delay 계산
 * 0으로 나누기, 음수, 너무 작은 값 방지
 */
internal fun calculateSafeDelay(
    durationMs: Long,
    playbackSpeed: Float,
): Long {
    val safeSpeed =
        if (playbackSpeed > 0.1f && playbackSpeed.isFinite()) playbackSpeed else 1.0f
    return max(1L, (durationMs / safeSpeed).toLong().coerceIn(1L, Long.MAX_VALUE / 2))
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
internal fun AnimatedCharacter(
    character: CharacterState,
    sceneIndex: Int,
    playbackSpeed: Float,
    isInteractive: Boolean = false,
    onCharacterClick: (CharacterState) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // 위치 애니메이션
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
    )

    // 투명도 애니메이션
    val alpha by animateFloatAsState(
        targetValue = character.alpha,
        animationSpec = tween(durationMillis = 500),
        label = "character_alpha",
    )

    // 크기 애니메이션
    val scale by animateFloatAsState(
        targetValue = character.scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "character_scale",
    )

    // 스프라이트 프레임 전환 (1 ↔ 2)
    var currentFrame by remember(character.spriteAnimation?.currentAnimation) {
        mutableStateOf(1)
    }

    // 발걸음 소리를 위한 사운드 매니저
    val footstepSoundManager = remember(character.spriteAnimation?.currentAnimation) {
        VoiceSoundManagerFactory.create()
    }

    DisposableEffect(character.spriteAnimation?.currentAnimation) {
        onDispose {
            footstepSoundManager.release()
        }
    }

    // 스프라이트 애니메이션 자동 재생 + 발걸음 소리
    LaunchedEffect(character.spriteAnimation?.currentAnimation, playbackSpeed, sceneIndex) {
        character.spriteAnimation?.let { spriteAnim ->
            if (spriteAnim.isAnimating) {
                val isWalking = spriteAnim.currentAnimation == CharacterAnimationType.WALKING

                while (true) {
                    delay(calculateSafeDelay(spriteAnim.frameDuration, playbackSpeed))
                    currentFrame = if (currentFrame == 1) 2 else 1

                    // 발걸음 소리 재생 조건:
                    // 1. WALKING 애니메이션
                    // 2. 프레임 1일 때만 (걸음마다 한 번)
                    // 3. 재생 중(!isInteractive)
                    // 4. 캐릭터에 음성이 있을 때만 (voice != null)
                    //    → 대사가 없는 씬에서는 발걸음 소리도 없음
                    if (isWalking && currentFrame == 1 && !isInteractive && character.voice != null) {
                        footstepSoundManager.playBeep(
                            pitch = 0.3f,
                            duration = 50,
                            volume = 0.3f,
                        )
                    }
                }
            }
        }
    }

    // 현재 표시할 이미지 리소스 결정
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
