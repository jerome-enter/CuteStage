package com.example.cutestage.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cutestage.data.character.*
import com.example.cutestage.data.module.*
import com.example.cutestage.data.scenario.*

/**
 * CuteStage 앱의 메인 데이터베이스
 *
 * 버전 관리:
 * - v1: 모듈 시스템 초기 구현
 * - v2: 시나리오 저장 기능 추가
 */
@Database(
    entities = [
        ModuleTypeEntity::class,
        ModuleCategoryEntity::class,
        ModuleItemEntity::class,
        UnlockedModuleEntity::class,
        ScenarioEntity::class,
        TimelineItemEntity::class,
        CharacterLibraryEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(ModuleConverters::class)
abstract class CuteStageDatabase : RoomDatabase() {

    abstract fun moduleDao(): ModuleDao
    abstract fun scenarioDao(): ScenarioDao
    abstract fun characterLibraryDao(): CharacterLibraryDao

    companion object {
        const val DATABASE_NAME = "cutestage_db"
    }
}
