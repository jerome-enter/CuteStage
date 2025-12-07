package com.example.cutestage.data.scenario

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 시나리오 데이터 접근 객체
 */
@Dao
interface ScenarioDao {

    // ==================== Scenario ====================

    @Query("SELECT * FROM scenarios ORDER BY updatedAt DESC")
    fun getAllScenarios(): Flow<List<ScenarioEntity>>

    @Query("SELECT * FROM scenarios WHERE isTemplate = 0 ORDER BY updatedAt DESC")
    fun getUserScenarios(): Flow<List<ScenarioEntity>>

    @Query("SELECT * FROM scenarios WHERE isTemplate = 1 ORDER BY createdAt ASC")
    fun getTemplateScenarios(): Flow<List<ScenarioEntity>>

    @Query("SELECT * FROM scenarios WHERE id = :scenarioId")
    suspend fun getScenarioById(scenarioId: String): ScenarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenario(scenario: ScenarioEntity)

    @Update
    suspend fun updateScenario(scenario: ScenarioEntity)

    @Delete
    suspend fun deleteScenario(scenario: ScenarioEntity)

    @Query("DELETE FROM scenarios WHERE id = :scenarioId")
    suspend fun deleteScenarioById(scenarioId: String)

    // ==================== TimelineItem ====================

    @Query("SELECT * FROM timeline_items WHERE scenarioId = :scenarioId ORDER BY `order` ASC")
    suspend fun getTimelineItems(scenarioId: String): List<TimelineItemEntity>

    @Query("SELECT * FROM timeline_items WHERE scenarioId = :scenarioId ORDER BY `order` ASC")
    fun getTimelineItemsFlow(scenarioId: String): Flow<List<TimelineItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineItem(item: TimelineItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineItems(items: List<TimelineItemEntity>)

    @Query("DELETE FROM timeline_items WHERE scenarioId = :scenarioId")
    suspend fun deleteTimelineItemsByScenarioId(scenarioId: String)

    // ==================== 복합 쿼리 ====================

    @Transaction
    suspend fun getScenarioWithTimeline(scenarioId: String): ScenarioWithTimeline? {
        val scenario = getScenarioById(scenarioId) ?: return null
        val timelineItems = getTimelineItems(scenarioId)
        return ScenarioWithTimeline(scenario, timelineItems)
    }

    @Transaction
    suspend fun saveScenarioWithTimeline(
        scenario: ScenarioEntity,
        timelineItems: List<TimelineItemEntity>
    ) {
        insertScenario(scenario)
        deleteTimelineItemsByScenarioId(scenario.id)
        insertTimelineItems(timelineItems)
    }

    // ==================== 통계 ====================

    @Query("SELECT COUNT(*) FROM scenarios WHERE isTemplate = 0")
    suspend fun getUserScenarioCount(): Int

    @Query("SELECT COUNT(*) FROM timeline_items WHERE scenarioId = :scenarioId")
    suspend fun getModuleCount(scenarioId: String): Int
}
