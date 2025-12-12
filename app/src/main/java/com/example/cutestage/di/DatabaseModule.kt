package com.example.cutestage.di

import android.content.Context
import androidx.room.Room
import com.example.cutestage.data.CuteStageDatabase
import com.example.cutestage.data.character.CharacterLibraryDao
import com.example.cutestage.data.module.ModuleDao
import com.example.cutestage.data.scenario.ScenarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database 의존성 주입 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCuteStageDatabase(
        @ApplicationContext context: Context
    ): CuteStageDatabase {
        return Room.databaseBuilder(
            context,
            CuteStageDatabase::class.java,
            CuteStageDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // TODO: 프로덕션에서는 마이그레이션 전략 필요
            .build()
    }

    @Provides
    @Singleton
    fun provideModuleDao(database: CuteStageDatabase): ModuleDao {
        return database.moduleDao()
    }

    @Provides
    @Singleton
    fun provideScenarioDao(database: CuteStageDatabase): ScenarioDao {
        return database.scenarioDao()
    }

    @Provides
    @Singleton
    fun provideCharacterLibraryDao(database: CuteStageDatabase): CharacterLibraryDao {
        return database.characterLibraryDao()
    }
}
