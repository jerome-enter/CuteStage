package com.example.cutestage.stage

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.cutestage.R

/**
 * GeneratedScenario를 TheaterScript로 변환하는 유틸리티
 * gender + animation 기반으로 올바른 애니메이션 시스템 활용
 */
object ScenarioConverter {
    /**
     * Context를 저장하기 위한 변수
     */
    private var appContext: Context? = null

    /**
     * Context 설정 (Application Context 사용)
     */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Gemini가 생성한 시나리오를 TheaterScript로 변환
     *
     * @param generatedScenario Gemini가 생성한 시나리오
     * @return TheaterScript 변환된 시나리오
     */
    fun convertToTheaterScript(generatedScenario: GeneratedScenario): TheaterScript { // 씬을 order 순서대로 정렬
        val sortedScenes =
            generatedScenario.scenes.sortedBy { it.order } // order별로 그룹화 (같은 order = 같은 씬)
        val groupedScenes = sortedScenes.groupBy { it.order }

        Log.d("ScenarioConverter", "Total scene groups: ${groupedScenes.size}")
        groupedScenes.forEach { (order, scenes) ->
            Log.d("ScenarioConverter", "Order $order: ${scenes.size} characters")
            scenes.forEach { scene ->
                Log.d(
                    "ScenarioConverter",
                    "  - ${scene.name} (${scene.gender}/${scene.animation}): \"${scene.dialogue}\""
                )
            }
        } // 각 order 그룹을 하나의 SceneState로 변환
        val sceneStates = groupedScenes.map { (order, scenesInGroup) ->
            convertToSceneStateFromGroup(scenesInGroup, order)
        }

        return TheaterScript(
            scenes = sceneStates, debug = false
        )
    }

    /**
     * 같은 order를 가진 씬 그룹을 하나의 SceneState로 변환
     */
    private fun convertToSceneStateFromGroup(
        scenesInGroup: List<GeneratedScene>,
        order: Int,
    ): SceneState { // 모든 캐릭터 생성 (gender + animation 기반)
        val characters = scenesInGroup.map { scene ->
            createCharacterFromGenderAndAnimation(
                name = scene.name,
                gender = scene.gender,
                animation = scene.animation,
                position = scene.position
            )
        } // 대사가 있는 캐릭터 찾기 (빈 문자열이 아닌 경우)
        val speakingScene =
            scenesInGroup.firstOrNull { it.dialogue.isNotBlank() } // 대화 생성 (대사가 있는 경우만)
        val dialogues = if (speakingScene != null) {
            val xPosition = (speakingScene.position?.x ?: 50).toFloat() * 2.5f
            val dialogueX = xPosition
            val dialogueY = 30.dp

            listOf(
                DialogueState(
                text = speakingScene.dialogue,
                position = DpOffset(dialogueX.dp, dialogueY),
                speakerName = speakingScene.name,
                delayMillis = 500L,
                typingSpeedMs = 50L,
                voice = run {
                    val isMale = speakingScene.gender.equals("male", ignoreCase = true)
                    if (isMale) {
                        CharacterVoice(
                            enabled = true,
                            pitch = 0.9f,
                            duration = 60,
                            volume = 0.6f,
                            speed = 80
                        )
                    } else {
                        CharacterVoice(
                            enabled = true,
                            pitch = 1.8f,
                            duration = 45,
                            volume = 0.5f,
                            speed = 70
                        )
                    }
                }))
        } else {
            emptyList()
        } // 대사 길이에 따른 시간 계산
        val duration = if (speakingScene == null) {
            2000L  // 대사 없으면 2초
        } else {
            3000L + (speakingScene.dialogue.length * 80L)
        }

        Log.d(
            "ScenarioConverter",
            "Scene $order: ${characters.size} characters, speaker=${speakingScene?.name ?: "none"}, duration=${duration}ms"
        )

        return SceneState(
            backgroundRes = R.drawable.stage_floor,
            characters = characters,
            dialogues = dialogues,
            durationMillis = duration
        )
    }

    /**
     * gender + animation으로부터 캐릭터 생성
     * 애니메이션 시스템을 올바르게 활용
     */
    private fun createCharacterFromGenderAndAnimation(
        name: String,
        gender: String,
        animation: String,
        position: ScenePosition?,
    ): CharacterState {
        Log.d(
            "ScenarioConverter",
            "Creating character: name=$name, gender=$gender, animation=$animation, position=$position"
        ) // 위치 계산 (x: 0-100 -> dp, z: 0-50 -> dp)
        val xPosition = (position?.x ?: 50).toFloat() * 2.5f // 0-100 -> 0-250dp
        val zPosition = (position?.z
            ?: 40).toFloat() * 4f // 0-50 -> 0-200dp (y축 깊이), 기본값 40으로 바닥쪽 배치 // gender 문자열 → CharacterGender enum
        val characterGender = when (gender.lowercase()) {
            "male" -> CharacterGender.MALE
            "female" -> CharacterGender.FEMALE
            else -> {
                Log.w("ScenarioConverter", "Unknown gender: $gender, defaulting to MALE")
                CharacterGender.MALE
            }
        } // animation 문자열 → CharacterAnimationType enum
        val animationType = parseAnimationType(animation) // 기본 drawable (사용되지 않지만 필수)
        val drawableResId = if (characterGender == CharacterGender.MALE) {
            R.drawable.stage_ch_m_1
        } else {
            R.drawable.stage_ch_f_1
        } // 스프라이트 애니메이션 설정 (자동으로 프레임 관리)
        val spriteAnimation = CharacterAnimationState(
            gender = characterGender,
            currentAnimation = animationType,
            isAnimating = true,
            frameDuration = 500
        ) // 오른쪽 캐릭터는 왼쪽을 보도록 flip
        val shouldFlip = xPosition > 125f  // 화면 중앙(125dp) 기준으로 오른쪽이면 flip

        return CharacterState(
            id = "$name-$gender-$animation",
            name = name,
            imageRes = drawableResId,  // 애니메이션 시스템이 자동으로 교체
            position = DpOffset(xPosition.dp, zPosition.dp),
            size = 100.dp,
            alpha = 1f,
            scale = 1f,
            rotation = 0f,
            flipX = shouldFlip,  // 오른쪽 캐릭터는 왼쪽을 향함
            animationDuration = 500,
            spriteAnimation = spriteAnimation  // ✅ 올바른 애니메이션 시스템 활용!
        )
    }

    /**
     * animation 문자열을 CharacterAnimationType enum으로 변환
     */
    private fun parseAnimationType(animation: String): CharacterAnimationType {
        return when (animation.lowercase().replace("-", "_")) {
            "idle" -> CharacterAnimationType.IDLE
            "idle_annoyed" -> CharacterAnimationType.IDLE_ANNOYED
            "speak_normal" -> CharacterAnimationType.SPEAK_NORMAL
            "speak_angry" -> CharacterAnimationType.SPEAK_ANGRY
            "listening" -> CharacterAnimationType.LISTENING
            "walking" -> CharacterAnimationType.WALKING
            "annoyed" -> CharacterAnimationType.ANNOYED
            "clap" -> CharacterAnimationType.CLAP
            "dancing_type_a" -> CharacterAnimationType.DANCING_TYPE_A
            "dancing_type_b" -> CharacterAnimationType.DANCING_TYPE_B
            "dancing_type_c" -> CharacterAnimationType.DANCING_TYPE_C
            "sing_normal" -> CharacterAnimationType.SING_NORMAL
            "sing_climax" -> CharacterAnimationType.SING_CLIMAX
            "sing_pitchup" -> CharacterAnimationType.SING_PITCHUP
            else -> {
                Log.w("ScenarioConverter", "Unknown animation: $animation, defaulting to IDLE")
                CharacterAnimationType.IDLE
            }
        }
    }
}
