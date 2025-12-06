package com.example.cutestage.stage.repository

import android.content.Context
import com.example.cutestage.stage.GeminiScenarioGenerator
import com.example.cutestage.stage.ScenarioConverter
import com.example.cutestage.stage.TheaterScript
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 시나리오 생성 및 관리를 담당하는 Repository
 *
 * AI 생성, 변환, 캐싱 등의 로직을 캡슐화합니다.
 */
@Singleton
class ScenarioRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    init {
        // ScenarioConverter 초기화
        ScenarioConverter.init(context)
    }

    /**
     * AI로부터 시나리오 생성
     *
     * @param userInput 사용자 입력 줄거리
     * @return 생성된 TheaterScript
     * @throws Exception AI 생성 실패 시
     */
    suspend fun generateFromAI(userInput: String): TheaterScript {
        return withContext(Dispatchers.IO) {
            val generatedScenario = GeminiScenarioGenerator.generateScenario(context, userInput)

            if (generatedScenario.status == "error") {
                throw Exception(generatedScenario.message)
            }

            ScenarioConverter.convertToTheaterScript(generatedScenario)
        }
    }
}
