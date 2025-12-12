package com.example.cutestage.data.character

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 캐릭터 라이브러리 DAO
 */
@Dao
interface CharacterLibraryDao {

    @Query("SELECT * FROM character_library ORDER BY isFavorite DESC, usageCount DESC, lastUsedAt DESC")
    fun getAllCharacters(): Flow<List<CharacterLibraryEntity>>

    @Query("SELECT * FROM character_library WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteCharacters(): Flow<List<CharacterLibraryEntity>>

    @Query("SELECT * FROM character_library ORDER BY lastUsedAt DESC LIMIT 5")
    fun getRecentCharacters(): Flow<List<CharacterLibraryEntity>>

    @Query("SELECT * FROM character_library ORDER BY usageCount DESC LIMIT 10")
    fun getFrequentCharacters(): Flow<List<CharacterLibraryEntity>>

    @Query("SELECT * FROM character_library WHERE id = :id")
    suspend fun getCharacterById(id: String): CharacterLibraryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterLibraryEntity)

    @Update
    suspend fun updateCharacter(character: CharacterLibraryEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterLibraryEntity)

    @Query("UPDATE character_library SET usageCount = usageCount + 1, lastUsedAt = :timestamp WHERE id = :id")
    suspend fun incrementUsageCount(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE character_library SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)
}
