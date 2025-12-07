package com.example.cutestage.data.scenario

import com.example.cutestage.ui.creator.TimelineModuleItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 시나리오 데이터 관리 Repository
 */
@Singleton
class ScenarioRepository @Inject constructor(
    private val scenarioDao: ScenarioDao
) {

    // ==================== Scenario 조회 ====================

    fun getAllScenarios(): Flow<List<ScenarioEntity>> =
        scenarioDao.getAllScenarios()

    fun getUserScenarios(): Flow<List<ScenarioEntity>> =
        scenarioDao.getUserScenarios()

    fun getTemplateScenarios(): Flow<List<ScenarioEntity>> =
        scenarioDao.getTemplateScenarios()

    suspend fun getScenarioById(scenarioId: String): ScenarioEntity? =
        scenarioDao.getScenarioById(scenarioId)

    suspend fun getScenarioWithTimeline(scenarioId: String): ScenarioWithTimeline? =
        scenarioDao.getScenarioWithTimeline(scenarioId)

    // ==================== 시나리오 저장 ====================

    /**
     * 새 시나리오 생성 및 저장
     */
    suspend fun createScenario(
        title: String,
        description: String,
        timelineItems: List<TimelineModuleItem>
    ): String {
        val scenarioId = UUID.randomUUID().toString()

        val scenario = ScenarioEntity(
            id = scenarioId,
            title = title,
            description = description,
            moduleCount = timelineItems.size,
            estimatedDuration = estimateDuration(timelineItems),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val timelineEntities = timelineItems.mapIndexed { index, item ->
            TimelineItemEntity(
                id = UUID.randomUUID().toString(),
                scenarioId = scenarioId,
                moduleItemId = item.moduleItem.id,
                order = index,
                customParametersJson = "{}" // TODO: 커스터마이징 파라미터 추가
            )
        }

        scenarioDao.saveScenarioWithTimeline(scenario, timelineEntities)
        return scenarioId
    }

    /**
     * 기존 시나리오 업데이트
     */
    suspend fun updateScenario(
        scenarioId: String,
        title: String,
        description: String,
        timelineItems: List<TimelineModuleItem>
    ) {
        val existingScenario = scenarioDao.getScenarioById(scenarioId) ?: return

        val updatedScenario = existingScenario.copy(
            title = title,
            description = description,
            moduleCount = timelineItems.size,
            estimatedDuration = estimateDuration(timelineItems),
            updatedAt = System.currentTimeMillis()
        )

        val timelineEntities = timelineItems.mapIndexed { index, item ->
            TimelineItemEntity(
                id = UUID.randomUUID().toString(),
                scenarioId = scenarioId,
                moduleItemId = item.moduleItem.id,
                order = index,
                customParametersJson = "{}"
            )
        }

        scenarioDao.saveScenarioWithTimeline(updatedScenario, timelineEntities)
    }

    /**
     * 시나리오 삭제
     */
    suspend fun deleteScenario(scenarioId: String) {
        scenarioDao.deleteScenarioById(scenarioId)
    }

    // ==================== 타임라인 조회 ====================

    suspend fun getTimelineItems(scenarioId: String): List<TimelineItemEntity> =
        scenarioDao.getTimelineItems(scenarioId)

    fun getTimelineItemsFlow(scenarioId: String): Flow<List<TimelineItemEntity>> =
        scenarioDao.getTimelineItemsFlow(scenarioId)

    // ==================== 헬퍼 함수 ====================

    /**
     * 타임라인 아이템으로부터 예상 재생 시간 계산
     * TODO: 각 모듈 타입별로 실제 재생 시간 계산
     */
    private fun estimateDuration(items: List<TimelineModuleItem>): Int {
        // 간단한 추정: 대사 3초, 동작 2초, 기타 1초
        var total = 0
        items.forEach { item ->
            total += when (item.moduleItem.typeId) {
                "dialogue" -> 3
                "action" -> 2
                else -> 1
            }
        }
        return total
    }

    /**
     * 통계
     */
    suspend fun getUserScenarioCount(): Int =
        scenarioDao.getUserScenarioCount()

    /**
     * 기본 템플릿 시나리오 초기화
     */
    suspend fun initializeTemplates() {
        // 이미 템플릿이 있으면 스킵
        val existingTemplates = scenarioDao.getTemplateScenarios().first()
        if (existingTemplates.isNotEmpty()) {
            return
        }

        // 기본 템플릿 시나리오 5개 생성
        val templates = listOf(
            ScenarioEntity(
                id = "template_playground",
                title = "놀이터 (캐릭터 상호작용)",
                description = "캐릭터를 클릭하면 반응합니다",
                moduleCount = 1,
                estimatedDuration = 0,
                isTemplate = true
            ),
            ScenarioEntity(
                id = "template_basic",
                title = "기본 시나리오 - 만남",
                description = "두 사람의 첫 만남 이야기",
                moduleCount = 14,
                estimatedDuration = 30,
                isTemplate = true
            ),
            ScenarioEntity(
                id = "template_couple_fight",
                title = "부부싸움",
                description = "앞치마를 두고 벌어지는 부부의 다툼",
                moduleCount = 20,
                estimatedDuration = 60,
                isTemplate = true
            ),
            ScenarioEntity(
                id = "template_oksun",
                title = "옥순의 혼잣말",
                description = "생각이 많은 옥순의 하루",
                moduleCount = 15,
                estimatedDuration = 45,
                isTemplate = true
            ),
            ScenarioEntity(
                id = "template_i_am_solo",
                title = "나는 솔로 - 첫눈에 반한 소개팅",
                description = "솔로나라에서 운명을 만나다",
                moduleCount = 19,
                estimatedDuration = 60,
                isTemplate = true
            ),
            ScenarioEntity(
                id = "template_foolish_trick",
                title = "폭삭 속았수다",
                description = "��주도 가족의 따뜻한 식사 시간",
                moduleCount = 25,
                estimatedDuration = 90,
                isTemplate = true
            ),
            ScenarioEntity(
                id = "template_song",
                title = "하얀 바다새 (듀엣)",
                description = "아름다운 노래로 펼쳐지는 이야기",
                moduleCount = 10,
                estimatedDuration = 120,
                isTemplate = true
            )
        )

        templates.forEach { template ->
            scenarioDao.insertScenario(template)
        }
    }
}
