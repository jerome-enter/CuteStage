package com.example.cutestage.stage

import android.content.Context
import android.util.Log
import com.example.cutestage.CharacterResources
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Gemini API를 사용하여 사용자 입력으로부터 연극 시나리오를 생성하는 서비스
 */
object GeminiScenarioGenerator {
    private const val API_KEY = "AIzaSyAlJVJKE_DJWNGgAjO7SUIM-Qjmf3xpgYA"
    private const val API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=$API_KEY"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * 사용자의 줄거리 입력으로부터 시나리오를 생성합니다
     *
     * @param context Android Context (리소스 읽기용)
     * @param userPlot 사용자가 입력한 줄거리
     * @return GeneratedScenario 생성된 시나리오
     * @throws IOException 네트워크 오류
     * @throws Exception 기타 오류
     */
    suspend fun generateScenario(context: Context, userPlot: String): GeneratedScenario {
        // system_prompt_scenario.md 읽기
        val systemPrompt =
            context.assets.open("system_prompt_scenario.md").bufferedReader().use { it.readText() }

        // scenario_sample.md 읽기
        val scenarioSample =
            context.assets.open("scenario_sample.md").bufferedReader().use { it.readText() }

        // CharacterResources에서 리소스 명 수집
        val availableResources = buildString {
            appendLine("[Available Resources]")
            appendLine()
            appendLine("## Female Character Resources (USE THESE FOR FEMALE CHARACTERS):")
            appendLine("IMPORTANT: When creating female characters (여자, 여성, 소녀, 딸, 엄마, 누나, 언니, etc.),")
            appendLine("you MUST use these stage_female_* resources:")
            CharacterResources.FEMALE_CHARACTER_RESOURCES.forEach { resource ->
                appendLine("- $resource")
            }
            appendLine()
            appendLine("## Male Character Resources (USE THESE FOR MALE CHARACTERS):")
            appendLine("IMPORTANT: When creating male characters (남자, 남성, 소년, 아들, 아빠, 형, 오빠, etc.),")
            appendLine("you MUST use these stage_male_* resources:")
            CharacterResources.MALE_CHARACTER_RESOURCES.forEach { resource ->
                appendLine("- $resource")
            }
            appendLine()
            appendLine("CRITICAL RULE: Match character gender with resource prefix!")
            appendLine("- Female name (SuJin, JiHye, MinJi, etc.) → stage_female_*")
            appendLine("- Male name (MinHo, TaeYang, JunSeo, etc.) → stage_male_*")
        }

        // 최종 프롬프트 구성
        val fullPrompt = buildString {
            appendLine(systemPrompt)
            appendLine()
            appendLine("---")
            appendLine()
            appendLine(availableResources)
            appendLine()
            appendLine("---")
            appendLine()
            appendLine("# Example Output Format")
            appendLine()
            appendLine(scenarioSample)
            appendLine()
            appendLine("---")
            appendLine()
            appendLine("# User's Plot Request")
            appendLine()
            appendLine(userPlot)
            appendLine()
            appendLine("=".repeat(50))
            appendLine("CRITICAL INSTRUCTIONS - READ CAREFULLY:")
            appendLine("=".repeat(50))
            appendLine()
            appendLine("1. Create a scenario with BOTH male AND female characters (unless user specifically requests one gender only)")
            appendLine()
            appendLine("2. Gender-Resource Matching (STRICTLY ENFORCE):")
            appendLine("   - Female character (여자 이름: 수진, 지혜, 민지, 은지, 유리, etc.)")
            appendLine("     → MUST use: stage_female_1_idle, stage_female_1_speak_normal, stage_female_1_walking, etc.")
            appendLine("   - Male character (남자 이름: 민호, 태양, 준서, 지훈, 성민, etc.)")
            appendLine("     → MUST use: stage_male_1_idle, stage_male_1_speak_normal, stage_male_1_walking, etc.")
            appendLine()
            appendLine("3. WRONG Examples (DO NOT DO THIS):")
            appendLine("   ❌ {\"name\": \"수진\", \"resource_id\": \"stage_male_1_idle\"}")
            appendLine("   ❌ {\"name\": \"지혜\", \"resource_id\": \"stage_male_1_speak_normal\"}")
            appendLine()
            appendLine("4. CORRECT Examples (DO THIS):")
            appendLine("   ✅ {\"name\": \"수진\", \"resource_id\": \"stage_female_1_idle\"}")
            appendLine("   ✅ {\"name\": \"지혜\", \"resource_id\": \"stage_female_1_speak_normal\"}")
            appendLine("   ✅ {\"name\": \"민호\", \"resource_id\": \"stage_male_1_idle\"}")
            appendLine()
            appendLine("5. DO NOT use narrator - only use visible stage characters")
            appendLine()
            appendLine("Output ONLY the JSON object, no other text.")
        }

        // Logging the full prompt for debugging purposes
        Log.d("GeminiScenarioGenerator", "Full Prompt: $fullPrompt")

        // Gemini API 요청 구성
        val requestBody = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = fullPrompt)
                    )
                )
            )
        )

        val jsonBody = gson.toJson(requestBody)
        val requestBodyHttp = jsonBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(API_URL)
            .post(requestBodyHttp)
            .build()

        // API 호출 실행
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: "응답 본문 없음"
            throw IOException("Gemini API 호출 실패: ${response.code} - ${response.message}\n상세: $errorBody")
        }

        val responseBody = response.body?.string() ?: throw IOException("응답 본문이 비어있습니다")

        // 응답 로깅
        Log.d("GeminiScenarioGenerator", "API Response Body: $responseBody")

        // 응답 파싱
        val geminiResponse = gson.fromJson(responseBody, GeminiResponse::class.java)
        val generatedText =
            geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("생성된 텍스트를 찾을 수 없습니다")

        // 생성된 텍스트 로깅
        Log.d("GeminiScenarioGenerator", "Generated Text: $generatedText")

        // JSON 추출 (코드 블록이나 마크다운 제거)
        val jsonText = extractJsonFromText(generatedText)

        // 추출된 JSON 로깅
        Log.d("GeminiScenarioGenerator", "Extracted JSON: $jsonText")

        // GeneratedScenario로 파싱
        val scenario = gson.fromJson(jsonText, GeneratedScenario::class.java)

        // 최종 시나리오 로깅
        Log.d(
            "GeminiScenarioGenerator",
            "Final Scenario - Title: ${scenario.title}, Scenes: ${scenario.scenes.size}"
        )
        scenario.scenes.forEachIndexed { index, scene ->
            Log.d(
                "GeminiScenarioGenerator",
                "Scene ${index + 1}: name=${scene.name}, resource=${scene.resourceId}"
            )
        }

        return scenario
    }

    /**
     * 텍스트에서 JSON을 추출합니다 (코드 블록이나 마크다운 제거)
     */
    private fun extractJsonFromText(text: String): String {
        // ```json ... ``` 형식 제거
        val codeBlockRegex = Regex("```(?:json)?\\s*([\\s\\S]*?)```")
        val match = codeBlockRegex.find(text)
        if (match != null) {
            return match.groupValues[1].trim()
        }

        // JSON 객체 직접 찾기
        val jsonRegex = Regex("\\{[\\s\\S]*\\}")
        val jsonMatch = jsonRegex.find(text)
        if (jsonMatch != null) {
            return jsonMatch.value.trim()
        }

        return text.trim()
    }
}

// ==================== Gemini API 모델 ====================

/**
 * Gemini API 요청 모델
 */
private data class GeminiRequest(
    val contents: List<Content>
)

private data class Content(
    val parts: List<Part>
)

private data class Part(
    val text: String
)

/**
 * Gemini API 응답 모델
 */
private data class GeminiResponse(
    val candidates: List<Candidate>
)

private data class Candidate(
    val content: Content
)

// ==================== 생성된 시나리오 모델 ====================

/**
 * Gemini가 생성한 시나리오
 */
data class GeneratedScenario(
    val status: String,
    val message: String,
    val title: String,
    val scenes: List<GeneratedScene>
)

/**
 * 생성된 씬
 */
data class GeneratedScene(
    val order: Int,
    @SerializedName("resource_id")
    val resourceId: String,
    val name: String,
    val position: ScenePosition?,
    val dialogue: String
)

/**
 * 씬 위치
 */
data class ScenePosition(
    val x: Int,
    val z: Int
)
