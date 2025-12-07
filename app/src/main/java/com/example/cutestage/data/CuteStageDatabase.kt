package com.example.cutestage.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cutestage.data.module.*

/**
 * CuteStage 앱의 메인 데이터베이스
 *
 * 버전 관리:
 * - v1: 모듈 시스템 초기 구현
 */
@Database(
    entities = [
        ModuleTypeEntity::class,
        ModuleCategoryEntity::class,
        ModuleItemEntity::class,
        UnlockedModuleEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(ModuleConverters::class)
abstract class CuteStageDatabase : RoomDatabase() {

    abstract fun moduleDao(): ModuleDao

    companion object {
        const val DATABASE_NAME = "cutestage_db"
    }
}
