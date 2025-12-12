package com.example.cutestage.stage.beat

import androidx.compose.ui.unit.dp

/**
 * LayeredBeat를 기존 Beat 시스템으로 변환
 *
 * 레이어 기반 Beat를 기존 BeatConverter와 TheaterScript로 변환 가능한 형태로 만듭니다.
 */
object LayeredBeatConverter {

    /**
     * LayeredBeat를 Beat로 변환
     */
    fun toClassicBeat(layeredBeat: LayeredBeat, allCharacters: List<CharacterInfo>): Beat {
        val duration = layeredBeat.calculateDuration()

        // 캐릭터별로 마지막 위치 추적
        val characterPositions = mutableMapOf<String, Position>()

        // 1. Movement Layer를 기반으로 각 캐릭터의 위치 추적
        layeredBeat.movementLayer.movements
            .sortedBy { it.startTime }
            .forEach { movement ->
                val position = movement.position.toPosition()
                characterPositions[movement.characterId] = position
            }

        // 2. 대사가 있는 캐릭터들을 추적
        val speakingCharacters = layeredBeat.dialogueLayer.dialogues.map { it.characterId }.toSet()

        // 3. 모든 등장 캐릭터 결정 (대사 또는 동작 또는 이동이 있는 캐릭터)
        val involvedCharacterIds = (
                layeredBeat.dialogueLayer.dialogues.map { it.characterId } +
                        layeredBeat.actionLayer.actions.map { it.characterId } +
                        layeredBeat.movementLayer.movements.map { it.characterId }
                ).toSet()

        // 4. 각 캐릭터에 대한 CharacterAction 생성
        val characterActions = involvedCharacterIds.mapNotNull { characterId ->
            val character = allCharacters.find { it.id == characterId } ?: return@mapNotNull null

            // 해당 캐릭터의 첫 대사 찾기 (감정 결정용)
            val firstDialogue =
                layeredBeat.dialogueLayer.dialogues.firstOrNull { it.characterId == characterId }

            // 해당 캐릭터의 동작 찾기
            val action =
                layeredBeat.actionLayer.actions.firstOrNull { it.characterId == characterId }

            // 이동 정보
            val movements = layeredBeat.movementLayer.movements
                .filter { it.characterId == characterId }
                .sortedBy { it.startTime }

            // 캐릭터 인덱스에 따라 기본 위치 결정 (좌, 중앙, 우)
            val characterIndex = involvedCharacterIds.toList().indexOf(characterId)
            val defaultPosition = when (characterIndex % 3) {
                0 -> Position.LEFT
                1 -> Position.CENTER
                else -> Position.RIGHT
            }

            val movement = if (movements.isEmpty()) {
                // 이동 정보 없음 - 인덱스 기반 기본 위치에 배치
                Movement(
                    type = MovementType.STAY,
                    from = defaultPosition,
                    to = defaultPosition,
                    speed = Speed.NORMAL
                )
            } else if (movements.size == 1) {
                // 한 곳에만 있음
                Movement(
                    type = MovementType.STAY,
                    from = movements[0].position.toPosition(),
                    to = movements[0].position.toPosition(),
                    speed = Speed.NORMAL
                )
            } else {
                // 이동함
                Movement(
                    type = MovementType.MOVE,
                    from = movements[0].position.toPosition(),
                    to = movements.last().position.toPosition(),
                    speed = Speed.NORMAL
                )
            }

            // 감정 결정
            val emotion = firstDialogue?.emotion?.toEmotionType() ?: EmotionType.NEUTRAL

            // 제스처 결정 (대사의 액션 또는 독립 액션)
            val gesture =
                if (firstDialogue?.action != null && firstDialogue.action != DialogueActionType.NONE) {
                    firstDialogue.action.toGestureType()?.let { Gesture(it) }
                } else {
                    action?.actionType?.toGestureType()?.let { Gesture(it) }
                }

            // 방향 결정 (이동 방향 기반)
            val facingDirection = if (movements.size >= 2) {
                val from = movements[0].position
                val to = movements[1].position
                when {
                    to.x > from.x -> Direction.RIGHT
                    to.x < from.x -> Direction.LEFT
                    else -> Direction.CENTER
                }
            } else {
                Direction.CENTER
            }

            CharacterAction(
                characterId = character.id,
                characterName = character.name,
                gender = character.gender,
                movement = movement,
                emotion = Emotion(emotion, 0.7f),
                gesture = gesture,
                facingDirection = facingDirection
            )
        }

        // 5. DialogueAction 생성
        val dialogueActions = layeredBeat.dialogueLayer.dialogues.map { entry ->
            DialogueAction(
                characterId = entry.characterId,
                text = entry.text,
                emotion = entry.emotion.toEmotionType(),
                delay = entry.startTime,
                typingSpeed = 50L
            )
        }

        // 6. BackgroundLayer 생성
        val backgroundLayer = BackgroundLayer(
            type = BackgroundType.STAGE_FLOOR, // 일단 기본 배경 사용
            resourceName = null
        )

        return Beat(
            id = layeredBeat.id,
            name = layeredBeat.name,
            description = "레이어 기반 Beat",
            duration = duration,
            layers = BeatLayers(
                characters = characterActions,
                background = backgroundLayer,
                dialogues = dialogueActions
            )
        )
    }

    /**
     * StagePosition을 Position으로 변환
     */
    private fun StagePosition.toPosition(): Position {
        return Position(
            x = this.x,
            y = this.y,
            zone = when {
                this.x < 0.3f -> PositionZone.LEFT
                this.x > 0.7f -> PositionZone.RIGHT
                else -> PositionZone.CENTER
            }
        )
    }

    /**
     * LayeredBeat 리스트를 Beat 리스트로 변환
     */
    fun toClassicBeats(
        layeredBeats: List<LayeredBeat>,
        allCharacters: List<CharacterInfo>
    ): List<Beat> {
        return layeredBeats.map { toClassicBeat(it, allCharacters) }
    }
}
