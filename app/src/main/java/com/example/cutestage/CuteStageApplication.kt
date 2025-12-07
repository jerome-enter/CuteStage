package com.example.cutestage

import android.app.Application
import com.example.cutestage.data.module.ModuleRepository
import com.example.cutestage.data.scenario.ScenarioRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CuteStage Application
 *
 * Hilt를 사용하여 의존성 주입을 관리합니다.
 */
@HiltAndroidApp
class CuteStageApplication : Application() {

    @Inject
    lateinit var moduleRepository: ModuleRepository

    @Inject
    lateinit var scenarioRepository: ScenarioRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // 앱 최초 실행 시 기본 데이터 초기화
        applicationScope.launch {
            // 1. 모듈 시스템 초기화
            moduleRepository.initializeDefaultModules()

            // 2. 기본 템플릿 시나리오 초기화
            scenarioRepository.initializeTemplates()
        }
    }
}
