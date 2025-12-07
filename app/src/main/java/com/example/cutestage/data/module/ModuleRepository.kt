package com.example.cutestage.data.module

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 모듈 데이터 관리 Repository
 *
 * - Room DB 접근
 * - JSON 에셋 로딩
 * - 모듈 언락 관리
 */
@Singleton
class ModuleRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moduleDao: ModuleDao
) {
    private val gson = Gson()

    // ==================== ModuleType ====================

    fun getAllModuleTypes(): Flow<List<ModuleTypeEntity>> =
        moduleDao.getAllActiveModuleTypes()

    suspend fun getModuleTypeById(typeId: String): ModuleTypeEntity? =
        moduleDao.getModuleTypeById(typeId)

    // ==================== ModuleCategory ====================

    fun getCategoriesByType(typeId: String): Flow<List<ModuleCategoryEntity>> =
        moduleDao.getCategoriesByType(typeId)

    suspend fun getCategoryById(categoryId: String): ModuleCategoryEntity? =
        moduleDao.getCategoryById(categoryId)

    // ==================== ModuleItem ====================

    fun getModuleItemsByType(typeId: String): Flow<List<ModuleItemEntity>> =
        moduleDao.getModuleItemsByType(typeId)

    fun getModuleItemsByCategory(categoryId: String): Flow<List<ModuleItemEntity>> =
        moduleDao.getModuleItemsByCategory(categoryId)

    suspend fun getModuleItemById(itemId: String): ModuleItemEntity? =
        moduleDao.getModuleItemById(itemId)

    fun searchModuleItems(query: String): Flow<List<ModuleItemEntity>> =
        moduleDao.searchModuleItems(query)

    fun getAvailableModuleItems(): Flow<List<ModuleItemEntity>> =
        moduleDao.getAvailableModuleItems()

    suspend fun incrementUsageCount(itemId: String) =
        moduleDao.incrementUsageCount(itemId)

    // ==================== 언락 관리 ====================

    suspend fun isModuleUnlocked(itemId: String): Boolean =
        moduleDao.isModuleUnlocked(itemId)

    suspend fun unlockModule(itemId: String, method: UnlockMethod) {
        moduleDao.insertUnlockedModule(
            UnlockedModuleEntity(
                moduleItemId = itemId,
                unlockMethod = method
            )
        )
    }

    suspend fun unlockAllPremiumModules() {
        val premiumModules = moduleDao.getPremiumModuleItems().first()
        premiumModules.forEach { module ->
            if (!isModuleUnlocked(module.id)) {
                unlockModule(module.id, UnlockMethod.SUBSCRIPTION)
            }
        }
    }

    suspend fun clearSubscriptionUnlocks() {
        moduleDao.clearSubscriptionUnlocks()
    }

    // ==================== 초기 데이터 로딩 ====================

    /**
     * 앱 최초 실행 시 기본 모듈 데이터를 DB에 로드
     */
    suspend fun initializeDefaultModules() {
        val types = moduleDao.getAllActiveModuleTypes().first()
        if (types.isNotEmpty()) {
            // 이미 초기화됨
            return
        }

        // 기본 모듈 타입 생성
        val defaultTypes = listOf(
            ModuleTypeEntity(
                id = "dialogue",
                name = "대사",
                icon = "ic_dialogue",
                colorHex = "#FF5722",
                sortOrder = 0
            ),
            ModuleTypeEntity(
                id = "action",
                name = "동작",
                icon = "ic_action",
                colorHex = "#2196F3",
                sortOrder = 1
            ),
            ModuleTypeEntity(
                id = "scene",
                name = "장면",
                icon = "ic_scene",
                colorHex = "#4CAF50",
                sortOrder = 2
            ),
            ModuleTypeEntity(
                id = "background",
                name = "배경",
                icon = "ic_background",
                colorHex = "#9C27B0",
                sortOrder = 3
            ),
            ModuleTypeEntity(
                id = "effect",
                name = "효과",
                icon = "ic_effect",
                colorHex = "#FF9800",
                sortOrder = 4
            )
        )
        moduleDao.insertModuleTypes(defaultTypes)

        // 기본 카테고리 생성
        val defaultCategories = listOf(
            // Dialogue 카테고리
            ModuleCategoryEntity("dialogue_greeting", "dialogue", "인사", 0),
            ModuleCategoryEntity("dialogue_daily", "dialogue", "일상", 1),
            ModuleCategoryEntity("dialogue_conflict", "dialogue", "갈등", 2),
            ModuleCategoryEntity("dialogue_reconcile", "dialogue", "화해", 3),
            ModuleCategoryEntity("dialogue_romance", "dialogue", "로맨스", 4),

            // Action 카테고리
            ModuleCategoryEntity("action_move", "action", "이동", 0),
            ModuleCategoryEntity("action_emotion", "action", "감정", 1),
            ModuleCategoryEntity("action_gesture", "action", "제스처", 2),
            ModuleCategoryEntity("action_interaction", "action", "상호작용", 3),

            // Scene 카테고리
            ModuleCategoryEntity("scene_meet", "scene", "만남", 0),
            ModuleCategoryEntity("scene_conflict", "scene", "갈등", 1),
            ModuleCategoryEntity("scene_climax", "scene", "클라이맥스", 2),
            ModuleCategoryEntity("scene_ending", "scene", "결말", 3),

            // Background 카테고리
            ModuleCategoryEntity("bg_indoor", "background", "실내", 0),
            ModuleCategoryEntity("bg_outdoor", "background", "실외", 1),
            ModuleCategoryEntity("bg_nature", "background", "자연", 2),
            ModuleCategoryEntity("bg_urban", "background", "도시", 3),

            // Effect 카테고리
            ModuleCategoryEntity("effect_transition", "effect", "전환", 0),
            ModuleCategoryEntity("effect_emphasis", "effect", "강조", 1),
            ModuleCategoryEntity("effect_mood", "effect", "분위기", 2),
            ModuleCategoryEntity("effect_sound", "effect", "사운드", 3)
        )
        moduleDao.insertCategories(defaultCategories)

        // TODO: assets/modules/ 폴더에서 JSON 파일 읽어서 ModuleItem 생성
        // 지금은 샘플 데이터만 추가
        loadSampleModules()
    }

    /**
     * 샘플 모듈 데이터 로드 (개발/테스트용)
     */
    private suspend fun loadSampleModules() {
        val sampleDialogues = listOf(
            ModuleItemEntity(
                id = "dialogue_hello",
                typeId = "dialogue",
                categoryId = "dialogue_greeting",
                name = "안녕하세요",
                isPremium = false,
                contentJson = gson.toJson(
                    DialogueContent(
                        text = "안녕하세요!",
                        characterId = "hero",
                        emotion = EmotionType.HAPPY,
                        bubbleStyle = BubbleStyle.NORMAL
                    )
                ),
                tags = "[\"인사\",\"밝음\"]"
            ),
            ModuleItemEntity(
                id = "dialogue_hi",
                typeId = "dialogue",
                categoryId = "dialogue_greeting",
                name = "안녕",
                isPremium = false,
                contentJson = gson.toJson(
                    DialogueContent(
                        text = "안녕~",
                        characterId = "hero",
                        emotion = EmotionType.HAPPY,
                        bubbleStyle = BubbleStyle.NORMAL
                    )
                ),
                tags = "[\"인사\",\"친근함\"]"
            ),
            ModuleItemEntity(
                id = "dialogue_long_time",
                typeId = "dialogue",
                categoryId = "dialogue_greeting",
                name = "오랜만이에요",
                isPremium = false,
                contentJson = gson.toJson(
                    DialogueContent(
                        text = "오랜만이에요!",
                        characterId = "hero",
                        emotion = EmotionType.HAPPY,
                        bubbleStyle = BubbleStyle.NORMAL
                    )
                ),
                tags = "[\"인사\",\"재회\"]"
            ),
            ModuleItemEntity(
                id = "dialogue_love_confession",
                typeId = "dialogue",
                categoryId = "dialogue_romance",
                name = "사랑해요",
                isPremium = true,
                unlockCost = 50,
                contentJson = gson.toJson(
                    DialogueContent(
                        text = "사랑해요...",
                        characterId = "hero",
                        emotion = EmotionType.SHY,
                        bubbleStyle = BubbleStyle.WHISPER
                    )
                ),
                tags = "[\"고백\",\"로맨스\"]"
            ),
            ModuleItemEntity(
                id = "dialogue_angry",
                typeId = "dialogue",
                categoryId = "dialogue_conflict",
                name = "화났어!",
                isPremium = false,
                contentJson = gson.toJson(
                    DialogueContent(
                        text = "진짜 화났어!",
                        characterId = "hero",
                        emotion = EmotionType.ANGRY,
                        bubbleStyle = BubbleStyle.SHOUT
                    )
                ),
                tags = "[\"갈등\",\"화남\"]"
            )
        )

        val sampleActions = listOf(
            ModuleItemEntity(
                id = "action_walk",
                typeId = "action",
                categoryId = "action_move",
                name = "걷기",
                isPremium = false,
                contentJson = gson.toJson(
                    ActionContent(
                        characterId = "hero",
                        animationType = AnimationType.WALK,
                        startPositionX = 0.2f,
                        startPositionY = 0.5f,
                        endPositionX = 0.8f,
                        endPositionY = 0.5f,
                        duration = 2.0f
                    )
                ),
                tags = "[\"이동\",\"기본\"]"
            ),
            ModuleItemEntity(
                id = "action_wave",
                typeId = "action",
                categoryId = "action_gesture",
                name = "손 흔들기",
                isPremium = false,
                contentJson = gson.toJson(
                    ActionContent(
                        characterId = "hero",
                        animationType = AnimationType.WAVE,
                        startPositionX = 0.5f,
                        startPositionY = 0.5f,
                        duration = 1.0f,
                        emotion = EmotionType.HAPPY
                    )
                ),
                tags = "[\"제스처\",\"인사\"]"
            ),
            ModuleItemEntity(
                id = "action_hug",
                typeId = "action",
                categoryId = "action_interaction",
                name = "포옹하기",
                isPremium = true,
                unlockCost = 50,
                contentJson = gson.toJson(
                    ActionContent(
                        characterId = "hero",
                        animationType = AnimationType.HUG,
                        startPositionX = 0.5f,
                        startPositionY = 0.5f,
                        duration = 2.0f,
                        emotion = EmotionType.HAPPY
                    )
                ),
                tags = "[\"상호작용\",\"로맨스\"]"
            )
        )

        moduleDao.insertModuleItems(sampleDialogues + sampleActions)
    }

    /**
     * Content JSON을 파싱하여 타입별 Content 객체로 변환
     */
    inline fun <reified T> parseModuleContent(contentJson: String): T {
        val localGson = Gson()
        return localGson.fromJson(contentJson, T::class.java)
    }

    /**
     * Content 객체를 JSON으로 직렬화
     */
    fun <T> serializeModuleContent(content: T): String {
        return gson.toJson(content)
    }
}
