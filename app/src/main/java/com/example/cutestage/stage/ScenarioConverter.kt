package com.example.cutestage.stage

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.cutestage.R

/**
 * GeneratedScenario를 TheaterScript로 변환하는 유틸리티
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
    fun convertToTheaterScript(generatedScenario: GeneratedScenario): TheaterScript {
        // 씬을 order 순서대로 정렬
        val sortedScenes = generatedScenario.scenes
            .filter { it.resourceId != "narrator" } // narrator 제외
            .sortedBy { it.order }

        // order가 같은 씬들을 그룹화하여 하나의 SceneState로 합치기
        // 하지만 대부분의 경우 각 order마다 하나의 캐릭터만 있으므로,
        // 모든 등장 캐릭터를 추적하여 매 씬마다 배치
        val allCharacters = mutableMapOf<String, Pair<GeneratedScene, ScenePosition?>>()
        sortedScenes.forEach { scene ->
            val key = "${scene.name}-${scene.resourceId.substringBeforeLast("_")}" // 프레임 번호 제외
            allCharacters[key] = Pair(scene, scene.position)
        }

        Log.d("ScenarioConverter", "Total characters collected: ${allCharacters.size}")
        allCharacters.forEach { (key, pair) ->
            Log.d(
                "ScenarioConverter",
                "Character: $key, resource=${pair.first.resourceId}, pos=${pair.second}"
            )
        }

        // 씬별로 변환 (모든 캐릭터를 매 씬에 배치하되, 대사는 해당 씬의 캐릭터만)
        val scenes = sortedScenes.mapIndexed { index, speakingScene ->
            convertToSceneStateWithAllCharacters(speakingScene, allCharacters, index)
        }

        return TheaterScript(
            scenes = scenes,
            debug = false
        )
    }

    /**
     * GeneratedScene을 SceneState로 변환 (모든 캐릭터 포함)
     */
    private fun convertToSceneStateWithAllCharacters(
        speakingScene: GeneratedScene,
        allCharacters: Map<String, Pair<GeneratedScene, ScenePosition?>>,
        sceneIndex: Int
    ): SceneState {
        // 모든 캐릭터 생성
        val characters = allCharacters.map { (key, pair) ->
            val (charScene, position) = pair
            createCharacterFromResourceId(
                resourceId = charScene.resourceId,
                name = charScene.name,
                position = position
            )
        }

        Log.d(
            "ScenarioConverter",
            "Scene $sceneIndex: Placed ${characters.size} characters, speaker=${speakingScene.name}"
        )

        // 현재 말하는 캐릭터의 위치 계산
        val speakerPosition = speakingScene.position
        val xPosition = (speakerPosition?.x ?: 50).toFloat() * 2.5f // 0-100 -> 0-250dp
        val dialogueX = xPosition
        val dialogueY = 30.dp

        // 대화 생성 (현재 말하는 캐릭터만)
        val dialogues = listOf(
            DialogueState(
                text = speakingScene.dialogue,
                position = DpOffset(dialogueX.dp, dialogueY),
                speakerName = speakingScene.name,
                delayMillis = 500L,
                typingSpeedMs = 50L,
                voice = run {
                    val isMale = speakingScene.resourceId.contains("male", ignoreCase = true)
                    if (isMale) {
                        CharacterVoice(
                            enabled = true,
                            pitch = 0.5f,
                            duration = 40,
                            volume = 0.5f,
                            speed = 50
                        )
                    } else {
                        CharacterVoice(
                            enabled = true,
                            pitch = 0.9f,
                            duration = 50,
                            volume = 0.5f,
                            speed = 50
                        )
                    }
                }
            )
        )

        return SceneState(
            backgroundRes = R.drawable.stage_floor,
            characters = characters,
            dialogues = dialogues,
            durationMillis = 3000L + (speakingScene.dialogue.length * 50L)
        )
    }

    /**
     * GeneratedScene을 SceneState로 변환 (이전 버전 - 사용 안 함)
     */
    private fun convertToSceneState(generatedScene: GeneratedScene): SceneState {

        // 위치 계산 (x: 0-100 -> dp, z: 0-50 -> dp)
        val xPosition = (generatedScene.position?.x ?: 50).toFloat() * 2.5f // 0-100 -> 0-250dp
        val zPosition = (generatedScene.position?.z
            ?: 40).toFloat() * 4f // 0-50 -> 0-200dp (y축 깊이), 기본값 40으로 바닥쪽 배치

        // 캐릭터 생성 
        val characters = listOf(
            createCharacterFromResourceId(
                resourceId = generatedScene.resourceId,
                name = generatedScene.name,
                position = generatedScene.position
            )
        )

        // 말풍선 위치 계산 (캐릭터 위쪽에 배치)
        // 캐릭터가 바닥쪽(z=35-45, y=140-180dp)에 있으므로 말풍선은 더 위쪽(y=30-50dp)에 배치
        val dialogueX = xPosition // 캐릭터 x 위치와 동일
        val dialogueY = 30.dp // 고정 위치 (화면 상단 근처)

        // 대화 생성
        val dialogues = listOf(
            DialogueState(
                text = generatedScene.dialogue,
                position = DpOffset(dialogueX.dp, dialogueY), // 캐릭터 기반 위치
                speakerName = generatedScene.name,
                delayMillis = 500L,
                typingSpeedMs = 50L,
                voice = run {
                    // 캐릭터 성별 판단
                    val isMale = generatedScene.resourceId.contains("male", ignoreCase = true)
                    if (isMale) {
                        CharacterVoice(
                            enabled = true,
                            pitch = 0.5f,
                            duration = 40,
                            volume = 0.5f,
                            speed = 50
                        )
                    } else {
                        CharacterVoice(
                            enabled = true,
                            pitch = 0.9f,
                            duration = 50,
                            volume = 0.5f,
                            speed = 50
                        )
                    }
                }
            )
        )

        return SceneState(
            backgroundRes = R.drawable.stage_floor,
            characters = characters,
            dialogues = dialogues,
            durationMillis = 3000L + (generatedScene.dialogue.length * 50L) // 대사 길이에 따라 시간 조정
        )
    }

    /**
     * resource_id로부터 캐릭터 생성
     * Gemini가 준 resource_id를 직접 사용
     */
    private fun createCharacterFromResourceId(
        resourceId: String,
        name: String,
        position: ScenePosition?
    ): CharacterState {
        Log.d(
            "ScenarioConverter",
            "Creating character: name=$name, resourceId=$resourceId, position=$position"
        )

        // 위치 계산 (x: 0-100 -> dp, z: 0-50 -> dp)
        val xPosition = (position?.x ?: 50).toFloat() * 2.5f // 0-100 -> 0-250dp
        val zPosition =
            (position?.z ?: 40).toFloat() * 4f // 0-50 -> 0-200dp (y축 깊이), 기본값 40으로 바닥쪽 배치

        // 성별 판단
        val isMale = resourceId.contains("male", ignoreCase = true)
        val gender = if (isMale) CharacterGender.MALE else CharacterGender.FEMALE

        // 애니메이션 타입 추출
        val animationType = extractAnimationType(resourceId)

        // resource_id에서 직접 drawable 리소스 ID 가져오기
        val drawableResId = getDrawableResourceId(resourceId)

        return CharacterState(
            id = "$name-$resourceId",
            name = name,
            imageRes = drawableResId,
            position = DpOffset(xPosition.dp, zPosition.dp),
            size = 100.dp,
            alpha = 1f,
            scale = 1f,
            rotation = 0f,
            flipX = false,
            animationDuration = 500,
            spriteAnimation = null
        )
    }

    /**
     * resource_id 문자열에서 직접 drawable 리소스 ID 가져오기
     */
    private fun getDrawableResourceId(resourceId: String): Int {
        val context = appContext ?: return R.drawable.stage_ch_m_1

        // resource_id에서 확장자 제거 (이미 없지만 혹시 모르니)
        val cleanResourceId = resourceId.replace(".png", "")

        // Context를 통해 리소스 ID 찾기
        val resId =
            context.resources.getIdentifier(cleanResourceId, "drawable", context.packageName)

        // 로그 출력
        if (resId != 0) {
            Log.d("ScenarioConverter", "✅ Resource found: $cleanResourceId -> $resId")
        } else {
            Log.w("ScenarioConverter", "❌ Resource NOT found: $cleanResourceId, using default")
        }

        return if (resId != 0) resId else R.drawable.stage_ch_m_1
    }

    /**
     * resource_id에서 애니메이션 타입 추출
     * 예: "stage_female_1_walking_1" -> WALKING
     */
    private fun extractAnimationType(resourceId: String): CharacterAnimationType {
        return when {
            resourceId.contains("walking", ignoreCase = true) ||
                    resourceId.contains(
                        "waking",
                        ignoreCase = true
                    ) -> CharacterAnimationType.WALKING

            resourceId.contains(
                "speak_angry",
                ignoreCase = true
            ) -> CharacterAnimationType.SPEAK_ANGRY

            resourceId.contains("speak_normal", ignoreCase = true) ||
                    resourceId.contains(
                        "speak",
                        ignoreCase = true
                    ) -> CharacterAnimationType.SPEAK_NORMAL

            resourceId.contains("listening", ignoreCase = true) -> CharacterAnimationType.LISTENING

            resourceId.contains(
                "idle_annoyed",
                ignoreCase = true
            ) -> CharacterAnimationType.IDLE_ANNOYED

            resourceId.contains("annoyed", ignoreCase = true) -> CharacterAnimationType.ANNOYED
            resourceId.contains("idle", ignoreCase = true) -> CharacterAnimationType.IDLE

            resourceId.contains(
                "sing_climax",
                ignoreCase = true
            ) -> CharacterAnimationType.SING_CLIMAX

            resourceId.contains(
                "sing_pitchup",
                ignoreCase = true
            ) -> CharacterAnimationType.SING_PITCHUP

            resourceId.contains("sing_normal", ignoreCase = true) ||
                    resourceId.contains(
                        "sing",
                        ignoreCase = true
                    ) -> CharacterAnimationType.SING_NORMAL

            resourceId.contains(
                "dancing_type_a",
                ignoreCase = true
            ) -> CharacterAnimationType.DANCING_TYPE_A

            resourceId.contains(
                "dancing_type_b",
                ignoreCase = true
            ) -> CharacterAnimationType.DANCING_TYPE_B

            resourceId.contains(
                "dancing_type_c",
                ignoreCase = true
            ) -> CharacterAnimationType.DANCING_TYPE_C

            resourceId.contains(
                "dancing",
                ignoreCase = true
            ) -> CharacterAnimationType.DANCING_TYPE_A

            resourceId.contains("clap", ignoreCase = true) -> CharacterAnimationType.CLAP

            else -> CharacterAnimationType.IDLE
        }
    }
}
