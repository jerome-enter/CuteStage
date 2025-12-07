package com.example.cutestage.data.module

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 모듈 타입 (대사, 동작, 장면 등)
 * 개발자가 관리하는 최상위 분류
 */
@Entity(tableName = "module_types")
data class ModuleTypeEntity(
    @PrimaryKey
    val id: String,                      // "dialogue", "action", "scene", "background", "effect"
    val name: String,                    // "대사", "동작", "장면", "배경", "효과"
    val icon: String,                    // 아이콘 리소스 이름 (예: "ic_dialogue")
    val colorHex: String,                // "#FF5722"
    val isActive: Boolean = true,        // 활성화 여부 (false면 숨김)
    val sortOrder: Int = 0               // 표시 순서
)

/**
 * 모듈 카테고리 (모듈 타입 내의 하위 분류)
 */
@Entity(
    tableName = "module_categories",
    foreignKeys = [
        ForeignKey(
            entity = ModuleTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["typeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("typeId")]
)
data class ModuleCategoryEntity(
    @PrimaryKey
    val id: String,                      // "greeting", "conflict", "romance"
    val typeId: String,                  // "dialogue"
    val name: String,                    // "인사", "갈등", "로맨스"
    val sortOrder: Int = 0
)

/**
 * 개별 모듈 아이템 (실제 사용 가능한 모듈)
 */
@Entity(
    tableName = "module_items",
    foreignKeys = [
        ForeignKey(
            entity = ModuleTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["typeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ModuleCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("typeId"), Index("categoryId")]
)
data class ModuleItemEntity(
    @PrimaryKey
    val id: String,
    val typeId: String,
    val categoryId: String,
    val name: String,                    // "안녕하세요", "걷기", "첫 만남" 등
    val thumbnailUrl: String? = null,
    val isPremium: Boolean = false,      // 프리미엄 여부
    val unlockCost: Int = 0,             // 토큰 비용 (isPremium=true일 때)
    val contentJson: String,             // JSON 형태의 실제 콘텐츠
    val tags: String = "[]",             // JSON 배열 문자열 ["로맨스", "첫만남"]
    val usageCount: Int = 0,             // 사용 횟수 (인기도 측정용)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 언락된 프리미엄 모듈 (사용자가 토큰/구독으로 언락한 모듈)
 */
@Entity(
    tableName = "unlocked_modules",
    foreignKeys = [
        ForeignKey(
            entity = ModuleItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["moduleItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("moduleItemId")]
)
data class UnlockedModuleEntity(
    @PrimaryKey
    val moduleItemId: String,
    val unlockedAt: Long = System.currentTimeMillis(),
    val unlockMethod: UnlockMethod       // TOKEN, SUBSCRIPTION, EVENT, DEFAULT
)

enum class UnlockMethod {
    TOKEN,          // 토큰으로 구매
    SUBSCRIPTION,   // 구독으로 언락
    EVENT,          // 이벤트 보상
    DEFAULT         // 기본 제공
}

/**
 * Type Converters for Room
 */
class ModuleConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromUnlockMethod(value: UnlockMethod): String = value.name

    @TypeConverter
    fun toUnlockMethod(value: String): UnlockMethod = UnlockMethod.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}
