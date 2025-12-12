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

    /**
     * Beat를 LayeredBeat로 역변환 (편집 모드용)
     */
    fun fromClassicBeat(beat: Beat, allCharacters: List<CharacterInfo>): LayeredBeat {
        // 1. Location Layer 복원
        val locationLayer = LocationLayer(
            location = StageLocation.STAGE_FLOOR // 기본값, 추후 확장 가능
        )

        // 2. Dialogue Layer 복원
        val dialogues = beat.layers.dialogues.map { dialogueAction ->
            val character = allCharacters.find { it.id == dialogueAction.characterId }
            DialogueEntry(
                characterId = dialogueAction.characterId,
                characterName = character?.name ?: "Unknown",
                text = dialogueAction.text,
                emotion = dialogueAction.emotion.toDialogueEmotion(),
                startTime = dialogueAction.delay,
                action = null // 기존 Beat에는 action 정보가 없음
            )
        }
        val dialogueLayer = DialogueLayer(dialogues = dialogues)

        // 3. Movement Layer 복원
        val movements = beat.layers.characters.flatMap { characterAction ->
            val movementList = mutableListOf<MovementEntry>()

            when (characterAction.movement.type) {
                MovementType.STAY -> {
                    // 한 위치에 머무름
                    characterAction.movement.from?.let { from ->
                        movementList.add(
                            MovementEntry(
                                characterId = characterAction.characterId,
                                position = from.toStagePosition(),
                                startTime = 0f,
                                autoWalk = false
                            )
                        )
                    }
                }

                MovementType.MOVE -> {
                    // 이동
                    characterAction.movement.from?.let { from ->
                        movementList.add(
                            MovementEntry(
                                characterId = characterAction.characterId,
                                position = from.toStagePosition(),
                                startTime = 0f,
                                autoWalk = false
                            )
                        )
                    }
                    characterAction.movement.to?.let { to ->
                        movementList.add(
                            MovementEntry(
                                characterId = characterAction.characterId,
                                position = to.toStagePosition(),
                                startTime = beat.duration / 2, // 중간에 이동
                                autoWalk = true
                            )
                        )
                    }
                }

                MovementType.ENTER -> {
                    // 입장
                    characterAction.movement.to?.let { to ->
                        movementList.add(
                            MovementEntry(
                                characterId = characterAction.characterId,
                                position = to.toStagePosition(),
                                startTime = 0f,
                                autoWalk = true
                            )
                        )
                    }
                }

                MovementType.EXIT -> {
                    // 퇴장
                    characterAction.movement.from?.let { from ->
                        movementList.add(
                            MovementEntry(
                                characterId = characterAction.characterId,
                                position = from.toStagePosition(),
                                startTime = beat.duration * 0.7f, // 후반에 퇴장
                                autoWalk = true
                            )
                        )
                    }
                }

                MovementType.APPROACH, MovementType.RETREAT -> {
                    // 접근/후퇴는 MOVE와 동일하게 처리
                    characterAction.movement.from?.let { from ->
                        movementList.add(
                            MovementEntry(
                                characterId = characterAction.characterId,
                                position = from.toStagePosition(),
                                startTime = 0f,
                                autoWalk = false
                            )
                        )
                    }
                    characterAction.movement.to?.let { to ->
                        movementList.add(
                            MovementEntry(
                                characterId = characterAction.characterId,
                                position = to.toStagePosition(),
                                startTime = beat.duration / 2,
                                autoWalk = true
                            )
                        )
                    }
                }
            }
            movementList
        }
        val movementLayer = MovementLayer(movements = movements)

        // 4. Action Layer 복원 (제스처가 있는 경우)
        val actions = beat.layers.characters.mapNotNull { characterAction ->
            characterAction.gesture?.let { gesture ->
                ActionEntry(
                    characterId = characterAction.characterId,
                    actionType = gesture.type.toStageActionType()
                )
            }
        }
        val actionLayer = ActionLayer(actions = actions)

        return LayeredBeat(
            id = beat.id,
            name = beat.name,
            duration = beat.duration,
            locationLayer = locationLayer,
            dialogueLayer = dialogueLayer,
            actionLayer = actionLayer,
            movementLayer = movementLayer
        )
    }

    /**
     * Beat 리스트를 LayeredBeat 리스트로 역변환
     */
    fun fromClassicBeats(beats: List<Beat>, allCharacters: List<CharacterInfo>): List<LayeredBeat> {
        return beats.map { fromClassicBeat(it, allCharacters) }
    }

    /**
     * Position을 StagePosition으로 변환
     */
    private fun Position.toStagePosition(): StagePosition {
        return StagePosition(x = this.x, y = this.y)
    }

    /**
     * EmotionType을 DialogueEmotion으로 변환
     */
    private fun EmotionType.toDialogueEmotion(): DialogueEmotion {
        return when (this) {
            EmotionType.NEUTRAL -> DialogueEmotion.CALM
            EmotionType.HAPPY -> DialogueEmotion.HAPPY
            EmotionType.SAD -> DialogueEmotion.SAD
            EmotionType.ANGRY -> DialogueEmotion.ANGRY
            EmotionType.SURPRISED -> DialogueEmotion.SURPRISED
            EmotionType.SCARED -> DialogueEmotion.FEARFUL
            EmotionType.EXCITED -> DialogueEmotion.EXCITED
            EmotionType.NERVOUS -> DialogueEmotion.NERVOUS
            EmotionType.SHY -> DialogueEmotion.SHY
            EmotionType.CONFUSED, EmotionType.ANNOYED, EmotionType.DISGUSTED -> DialogueEmotion.ANNOYED
        }
    }

    /**
     * GestureType을 StageActionType으로 변환
     */
    private fun GestureType.toStageActionType(): StageActionType {
        return when (this) {
            GestureType.WAVE -> StageActionType.WAVE
            GestureType.CLAP -> StageActionType.CLAP
            GestureType.BOW -> StageActionType.BOW
            GestureType.SIT -> StageActionType.BOW // 매핑 없음
            GestureType.STAND -> StageActionType.WAVE // 매핑 없음
            GestureType.DANCE -> StageActionType.WAVE // 매핑 없음
            GestureType.SING -> StageActionType.WAVE // 매핑 없음
            GestureType.POINT -> StageActionType.WAVE // 매핑 없음
            GestureType.HUG -> StageActionType.WAVE // 매핑 없음
            GestureType.PUSH -> StageActionType.WAVE // 매핑 없음
            GestureType.PULL -> StageActionType.WAVE // 매핑 없음
        }
    }
}
