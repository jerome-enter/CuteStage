package com.example.cutestage.data.character

import com.example.cutestage.stage.CharacterGender
import com.example.cutestage.stage.beat.CharacterInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 캐릭터 라이브러리 Repository
 */
@Singleton
class CharacterLibraryRepository @Inject constructor(
    private val characterDao: CharacterLibraryDao
) {

    fun getAllCharacters(): Flow<List<CharacterLibraryEntity>> =
        characterDao.getAllCharacters()

    fun getFavoriteCharacters(): Flow<List<CharacterLibraryEntity>> =
        characterDao.getFavoriteCharacters()

    fun getRecentCharacters(): Flow<List<CharacterLibraryEntity>> =
        characterDao.getRecentCharacters()

    fun getFrequentCharacters(): Flow<List<CharacterLibraryEntity>> =
        characterDao.getFrequentCharacters()

    suspend fun getCharacterById(id: String): CharacterLibraryEntity? =
        characterDao.getCharacterById(id)

    /**
     * 캐릭터 추가 또는 업데이트
     */
    suspend fun saveCharacter(
        name: String,
        gender: CharacterGender,
        role: String? = null,
        description: String? = null
    ): String {
        val characterId = UUID.randomUUID().toString()
        val character = CharacterLibraryEntity(
            id = characterId,
            name = name,
            gender = gender,
            role = role,
            description = description
        )
        characterDao.insertCharacter(character)
        return characterId
    }

    /**
     * 캐릭터 사용 기록
     */
    suspend fun recordUsage(id: String) {
        characterDao.incrementUsageCount(id)
    }

    /**
     * 즐겨찾기 토글
     */
    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        characterDao.toggleFavorite(id, isFavorite)
    }

    /**
     * 캐릭터 삭제
     */
    suspend fun deleteCharacter(character: CharacterLibraryEntity) {
        characterDao.deleteCharacter(character)
    }

    /**
     * CharacterLibraryEntity를 CharacterInfo로 변환
     */
    fun toCharacterInfo(entity: CharacterLibraryEntity): CharacterInfo {
        return CharacterInfo(
            id = entity.id,
            name = entity.name,
            gender = entity.gender
        )
    }

    /**
     * 기본 캐릭터 초기화
     */
    suspend fun initializeDefaultCharacters() {
        val existing = characterDao.getAllCharacters().first()
        if (existing.isEmpty()) {
            val defaultCharacters = listOf(
                CharacterLibraryEntity(
                    id = "default_male_student",
                    name = "철수",
                    gender = CharacterGender.MALE,
                    role = "학생",
                    isFavorite = true
                ),
                CharacterLibraryEntity(
                    id = "default_female_student",
                    name = "영희",
                    gender = CharacterGender.FEMALE,
                    role = "학생",
                    isFavorite = true
                ),
                CharacterLibraryEntity(
                    id = "default_male_teacher",
                    name = "김선생님",
                    gender = CharacterGender.MALE,
                    role = "선생님"
                ),
                CharacterLibraryEntity(
                    id = "default_female_teacher",
                    name = "이선생님",
                    gender = CharacterGender.FEMALE,
                    role = "선생님"
                ),
                CharacterLibraryEntity(
                    id = "default_father",
                    name = "아빠",
                    gender = CharacterGender.MALE,
                    role = "가장"
                ),
                CharacterLibraryEntity(
                    id = "default_mother",
                    name = "엄마",
                    gender = CharacterGender.FEMALE,
                    role = "주부"
                )
            )
            defaultCharacters.forEach { characterDao.insertCharacter(it) }
        }
    }
}
