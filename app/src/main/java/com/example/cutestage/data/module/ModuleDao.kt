package com.example.cutestage.data.module

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 모듈 데이터 접근 객체 (DAO)
 */
@Dao
interface ModuleDao {

    // ==================== ModuleType ====================

    @Query("SELECT * FROM module_types WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getAllActiveModuleTypes(): Flow<List<ModuleTypeEntity>>

    @Query("SELECT * FROM module_types WHERE id = :typeId")
    suspend fun getModuleTypeById(typeId: String): ModuleTypeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModuleType(moduleType: ModuleTypeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModuleTypes(moduleTypes: List<ModuleTypeEntity>)

    @Update
    suspend fun updateModuleType(moduleType: ModuleTypeEntity)

    @Delete
    suspend fun deleteModuleType(moduleType: ModuleTypeEntity)

    // ==================== ModuleCategory ====================

    @Query("SELECT * FROM module_categories WHERE typeId = :typeId ORDER BY sortOrder ASC")
    fun getCategoriesByType(typeId: String): Flow<List<ModuleCategoryEntity>>

    @Query("SELECT * FROM module_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): ModuleCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ModuleCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<ModuleCategoryEntity>)

    @Update
    suspend fun updateCategory(category: ModuleCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: ModuleCategoryEntity)

    // ==================== ModuleItem ====================

    @Query("SELECT * FROM module_items WHERE typeId = :typeId ORDER BY createdAt DESC")
    fun getModuleItemsByType(typeId: String): Flow<List<ModuleItemEntity>>

    @Query("SELECT * FROM module_items WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getModuleItemsByCategory(categoryId: String): Flow<List<ModuleItemEntity>>

    @Query("SELECT * FROM module_items WHERE id = :itemId")
    suspend fun getModuleItemById(itemId: String): ModuleItemEntity?

    @Query(
        """
        SELECT * FROM module_items 
        WHERE name LIKE '%' || :query || '%' 
        OR tags LIKE '%' || :query || '%'
        ORDER BY usageCount DESC
    """
    )
    fun searchModuleItems(query: String): Flow<List<ModuleItemEntity>>

    @Query("SELECT * FROM module_items WHERE isPremium = 1 ORDER BY createdAt DESC")
    fun getPremiumModuleItems(): Flow<List<ModuleItemEntity>>

    @Query(
        """
        SELECT mi.* FROM module_items mi
        LEFT JOIN unlocked_modules um ON mi.id = um.moduleItemId
        WHERE mi.isPremium = 0 OR um.moduleItemId IS NOT NULL
        ORDER BY mi.usageCount DESC
    """
    )
    fun getAvailableModuleItems(): Flow<List<ModuleItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModuleItem(item: ModuleItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModuleItems(items: List<ModuleItemEntity>)

    @Update
    suspend fun updateModuleItem(item: ModuleItemEntity)

    @Delete
    suspend fun deleteModuleItem(item: ModuleItemEntity)

    @Query("UPDATE module_items SET usageCount = usageCount + 1 WHERE id = :itemId")
    suspend fun incrementUsageCount(itemId: String)

    // ==================== UnlockedModule ====================

    @Query("SELECT * FROM unlocked_modules")
    fun getAllUnlockedModules(): Flow<List<UnlockedModuleEntity>>

    @Query("SELECT * FROM unlocked_modules WHERE moduleItemId = :itemId")
    suspend fun getUnlockedModule(itemId: String): UnlockedModuleEntity?

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM unlocked_modules WHERE moduleItemId = :itemId
        )
    """
    )
    suspend fun isModuleUnlocked(itemId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnlockedModule(unlockedModule: UnlockedModuleEntity)

    @Delete
    suspend fun deleteUnlockedModule(unlockedModule: UnlockedModuleEntity)

    @Query("DELETE FROM unlocked_modules WHERE unlockMethod = 'SUBSCRIPTION'")
    suspend fun clearSubscriptionUnlocks()

    // ==================== 복합 쿼리 ====================

    /**
     * 특정 타입의 카테고리와 각 카테고리의 모듈 아이템을 한 번에 조회
     */
    @Transaction
    @Query(
        """
        SELECT * FROM module_categories 
        WHERE typeId = :typeId 
        ORDER BY sortOrder ASC
    """
    )
    fun getCategoriesWithItems(typeId: String): Flow<List<CategoryWithItems>>

    /**
     * 통계: 가장 많이 사용된 모듈 Top N
     */
    @Query(
        """
        SELECT * FROM module_items 
        ORDER BY usageCount DESC 
        LIMIT :limit
    """
    )
    suspend fun getTopUsedModules(limit: Int = 10): List<ModuleItemEntity>
}

/**
 * 카테고리와 해당 카테고리의 모듈 아이템을 함께 가져오기 위한 데이터 클래스
 */
data class CategoryWithItems(
    @Embedded val category: ModuleCategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val items: List<ModuleItemEntity>
)
