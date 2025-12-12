package com.example.cutestage.data.character

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cutestage.stage.CharacterGender

/**
 * 캐릭터 라이브러리 엔티티
 *
 * 사용자가 자주 사용하는 캐릭터를 저장하여 재사용할 수 있습니다.
 */
@Entity(tableName = "character_library")
data class CharacterLibraryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val gender: CharacterGender,
    val role: String? = null,           // 역할 (예: "학생", "선생님")
    val description: String? = null,    // 설명
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = System.currentTimeMillis(),
    val usageCount: Int = 0,            // 사용 횟수
    val isFavorite: Boolean = false     // 즐겨찾기
)
