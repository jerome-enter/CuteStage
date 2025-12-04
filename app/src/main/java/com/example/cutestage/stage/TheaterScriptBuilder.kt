package com.example.cutestage.stage

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/**
 * 스크립트 빌더 - DSL 스타일로 쉽게 스크립트 작성
 */
class TheaterScriptBuilder {
    private val scenes = mutableListOf<SceneState>()
    private var debug = false

    fun scene(
        backgroundRes: Int? = null,
        durationMillis: Long = 3000L,
        block: SceneBuilder.() -> Unit,
    ) {
        val builder = SceneBuilder(backgroundRes, durationMillis)
        builder.block()
        scenes.add(builder.build())
    }

    fun debug(enabled: Boolean) {
        debug = enabled
    }

    fun build(): TheaterScript =
        TheaterScript(
            scenes = scenes,
            debug = debug,
        )
}

/**
 * 씬 빌더
 */
class SceneBuilder(
    @DrawableRes private val backgroundRes: Int?,
    private val durationMillis: Long,
) {
    private val characters = mutableListOf<CharacterState>()
    private val dialogues = mutableListOf<DialogueState>()

    fun character(
        id: String,
        imageRes: Int,
        name: String = id,
        x: Dp = 0.dp,
        y: Dp = 0.dp,
        size: Dp = 80.dp,
        alpha: Float = 1f,
        scale: Float = 1f,
        rotation: Float = 0f,
        flipX: Boolean = false,
        animationDuration: Int = 500,
        easing: Easing = FastOutSlowInEasing,
        voice: CharacterVoice = CharacterVoice(),
        spriteAnimation: CharacterAnimationState? = null,
    ) {
        characters.add(
            CharacterState(
                id = id,
                name = name,
                imageRes = imageRes,
                position = DpOffset(x, y),
                size = size,
                alpha = alpha,
                scale = scale,
                rotation = rotation,
                flipX = flipX,
                animationDuration = animationDuration,
                easing = easing,
                voice = voice,
                spriteAnimation = spriteAnimation,
            ),
        )
    }

    fun dialogue(
        text: String,
        x: Dp,
        y: Dp,
        speakerName: String? = null,
        delayMillis: Long = 0L,
        typingSpeedMs: Long = 50L,
        voice: CharacterVoice? = null,
    ) {
        dialogues.add(
            DialogueState(
                text = text,
                position = DpOffset(x, y),
                speakerName = speakerName,
                delayMillis = delayMillis,
                typingSpeedMs = typingSpeedMs,
                voice = voice,
            ),
        )
    }

    fun build(): SceneState =
        SceneState(
            backgroundRes = backgroundRes,
            characters = characters,
            dialogues = dialogues,
            durationMillis = durationMillis,
        )
}

/**
 * DSL 진입점
 */
fun theaterScript(block: TheaterScriptBuilder.() -> Unit): TheaterScript {
    val builder = TheaterScriptBuilder()
    builder.block()
    return builder.build()
}
/**
 * 사용 예시:
 *
 * val script = theaterScript {
 *     debug(true)
 *
 *     scene(
 *         backgroundRes = R.drawable.forest,
 *         durationMillis = 2000L
 *     ) {
 *         character(
 *             id = "hero",
 *             imageRes = R.drawable.hero,
 *             name = "주인공",
 *             x = 50.dp,
 *             y = 100.dp
 *         )
 *
 *         dialogue(
 *             text = "모험을 떠나자!",
 *             x = 120.dp,
 *             y = 80.dp,
 *             speakerName = "주인공"
 *         )
 *     }
 *
 *     scene(
 *         backgroundRes = R.drawable.forest,
 *         durationMillis = 3000L
 *     ) {
 *         character(
 *             id = "hero",
 *             imageRes = R.drawable.hero,
 *             name = "주인공",
 *             x = 150.dp, // 이동
 *             y = 100.dp
 *         )
 *
 *         character(
 *             id = "npc",
 *             imageRes = R.drawable.npc,
 *             name = "상인",
 *             x = 250.dp,
 *             y = 100.dp,
 *             flipX = true
 *         )
 *
 *         dialogue(
 *             text = "어서오세요!",
 *             x = 220.dp,
 *             y = 80.dp,
 *             speakerName = "상인",
 *             delayMillis = 500L
 *         )
 *     }
 * }
 */
