package com.example.cutestage.data.scenario

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 시나리오 엔티티
 * 사용자가 만든 시나리오의 메타데이터
 */
@Entity(tableName = "scenarios")
data class ScenarioEntity(
    @PrimaryKey
    val id: String,
    val title: String,                    // "첫 만남"
    val description: String = "",         // "공원에서의 첫 만남"
    val thumbnailPath: String? = null,    // (추후) 썸네일 이미지 경로
    val moduleCount: Int = 0,             // 포함된 모듈 개수
    val estimatedDuration: Int = 0,       // 예상 재생 시간 (초)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isTemplate: Boolean = false       // 템플릿 여부 (개발자가 제공)
)

/**
 * 타임라인 아이템 엔티티
 * 시나리오에 포함된 모듈들의 순서와 커스터마이징
 */
@Entity(
    tableName = "timeline_items",
    foreignKeys = [
        ForeignKey(
            entity = ScenarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["scenarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scenarioId"), Index("order")]
)
data class TimelineItemEntity(
    @PrimaryKey
    val id: String,
    val scenarioId: String,               // 시나리오 ID (외래키)
    val moduleItemId: String,             // 모듈 아이템 ID
    val order: Int,                       // 순서 (0, 1, 2, ...)
    val customParametersJson: String = "{}" // 커스터마이징 파라미터 (JSON)
)

/**
 * 시나리오와 타임라인 아이템을 함께 조회하기 위한 데이터 클래스
 */
data class ScenarioWithTimeline(
    val scenario: ScenarioEntity,
    val timelineItems: List<TimelineItemEntity>
)
