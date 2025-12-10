package com.example.cutestage.data.scenario

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.cutestage.data.module.ActionContent
import com.example.cutestage.data.module.DialogueContent
import com.example.cutestage.data.module.ModuleRepository
import com.example.cutestage.stage.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 타임라인 아이템을 TheaterScript로 변환하는 컨버터
 */
@Singleton
class TimelineToScriptConverter @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val scenarioRepository: ScenarioRepository
) {

    /**
     * 시나리오 엔티티 가져오기
     */
    suspend fun getScenario(scenarioId: String): ScenarioEntity? {
        return scenarioRepository.getScenarioById(scenarioId)
    }

    /**
     * 시나리오 ID로부터 TheaterScript 생성
     */
    suspend fun convert(scenarioId: String): TheaterScript? {
        val scenario = scenarioRepository.getScenarioById(scenarioId)
            ?: return null

        // Beat 기반 시나리오인지 확인
        if (scenario.description.startsWith("{") && scenario.description.contains("\"type\"")) {
            return try {
                convertBeatScenario(scenario.description)
            } catch (e: Exception) {
                null
            }
        }

        // 기존 타임라인 기반 시나리오
        val scenarioWithTimeline = scenarioRepository.getScenarioWithTimeline(scenarioId)
            ?: return null

        if (scenarioWithTimeline.timelineItems.isEmpty()) {
            return null
        }

        // 각 타임라인 아이템을 SceneState로 변환
        val scenes = scenarioWithTimeline.timelineItems.mapNotNull { timelineItem ->
            val moduleItem = moduleRepository.getModuleItemById(timelineItem.moduleItemId)
            if (moduleItem != null) {
                buildSceneFromModule(moduleItem, timelineItem.order)
            } else null
        }

        return TheaterScript(scenes = scenes)
    }

    /**
     * Beat 기반 시나리오를 TheaterScript로 변환
     */
    private fun convertBeatScenario(descriptionJson: String): TheaterScript? {
        return try {
            val gson = com.google.gson.Gson()
            val beatData = gson.fromJson(descriptionJson, Map::class.java) as Map<*, *>

            if (beatData["type"] != "beat") {
                return null
            }

            val beatsJson = beatData["beats"] as? String ?: return null
            val beats = com.example.cutestage.stage.beat.BeatJsonHelper.toBeatList(beatsJson)

            // Beat 리스트를 TheaterScript로 변환
            com.example.cutestage.stage.beat.BeatConverter.beatsToTheaterScript(beats)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 모듈 아이템으로부터 SceneState 생성
     */
    private fun buildSceneFromModule(
        moduleItem: com.example.cutestage.data.module.ModuleItemEntity,
        order: Int
    ): SceneState {
        return when (moduleItem.typeId) {
            "dialogue" -> buildDialogueScene(moduleItem, order)
            "action" -> buildActionScene(moduleItem, order)
            "scene" -> buildFullScene(moduleItem, order)
            else -> buildDefaultScene(moduleItem, order)
        }
    }

    /**
     * 대사 모듈을 SceneState로 변환
     */
    private fun buildDialogueScene(
        moduleItem: com.example.cutestage.data.module.ModuleItemEntity,
        order: Int
    ): SceneState {
        val content = moduleRepository.parseModuleContent<DialogueContent>(moduleItem.contentJson)

        // 캐릭터 생성 (간단한 버전)
        val character = CharacterState(
            id = content.characterId,
            name = content.characterId,
            imageRes = getDefaultCharacterImage(content.characterId),
            position = DpOffset(150.dp, 300.dp),
            size = 80.dp,
            alpha = 1f
        )

        // 대사 생성
        val dialogue = DialogueState(
            text = content.text,
            position = DpOffset(120.dp, 200.dp),
            speakerName = content.characterId,
            typingSpeedMs = content.typingSpeedMs
        )

        return SceneState(
            characters = listOf(character),
            dialogues = listOf(dialogue),
            durationMillis = (content.text.length * content.typingSpeedMs) + 1000L
        )
    }

    /**
     * 동작 모듈을 SceneState로 변환
     */
    private fun buildActionScene(
        moduleItem: com.example.cutestage.data.module.ModuleItemEntity,
        order: Int
    ): SceneState {
        val content = moduleRepository.parseModuleContent<ActionContent>(moduleItem.contentJson)

        // 시작 위치
        val startX = (content.startPositionX * 400).dp
        val startY = (content.startPositionY * 600).dp

        // 종료 위치 (없으면 시작 위치와 동일)
        val endX = ((content.endPositionX ?: content.startPositionX) * 400).dp
        val endY = ((content.endPositionY ?: content.startPositionY) * 600).dp

        val character = CharacterState(
            id = content.characterId,
            name = content.characterId,
            imageRes = getDefaultCharacterImage(content.characterId),
            position = DpOffset(endX, endY),
            size = 80.dp,
            alpha = 1f,
            animationDuration = (content.duration * 1000).toInt()
        )

        return SceneState(
            characters = listOf(character),
            dialogues = emptyList(),
            durationMillis = (content.duration * 1000).toLong()
        )
    }

    /**
     * 장면 모듈을 SceneState로 변환
     */
    private fun buildFullScene(
        moduleItem: com.example.cutestage.data.module.ModuleItemEntity,
        order: Int
    ): SceneState {
        // TODO: SceneContent 파싱 및 변환
        return buildDefaultScene(moduleItem, order)
    }

    /**
     * 기본 SceneState 생성
     */
    private fun buildDefaultScene(
        moduleItem: com.example.cutestage.data.module.ModuleItemEntity,
        order: Int
    ): SceneState {
        // 간단한 알림 장면
        val dialogue = DialogueState(
            text = moduleItem.name,
            position = DpOffset(120.dp, 200.dp),
            speakerName = "시스템"
        )

        return SceneState(
            characters = emptyList(),
            dialogues = listOf(dialogue),
            durationMillis = 2000L
        )
    }

    /**
     * 캐릭터 ID에 따른 기본 이미지 리소스 반환
     * TODO: 실제 캐릭터 데이터베이스와 연동
     */
    private fun getDefaultCharacterImage(characterId: String): Int {
        return when {
            characterId.contains("male", ignoreCase = true) ||
                    characterId.contains("상철", ignoreCase = true) ||
                    characterId.contains("영수", ignoreCase = true) -> {
                com.example.cutestage.R.drawable.stage_male_1_idle_1
            }

            else -> {
                com.example.cutestage.R.drawable.stage_female_1_idle_1
            }
        }
    }
}
