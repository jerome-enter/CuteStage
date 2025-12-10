package com.example.cutestage.stage.beat

import com.example.cutestage.stage.CharacterGender

/**
 * Beat 템플릿 라이브러리
 *
 * 자주 사용되는 극적 상황들을 미리 정의한 Beat 템플릿입니다.
 * 연극 제작자들이 빠르게 프로토타입을 만들 수 있도록 돕습니다.
 */
object BeatTemplates {

    // ==================== 만남 관련 ====================

    /**
     * 첫 만남
     * 두 캐릭터가 무대 양쪽에서 등장하여 중앙에서 만남
     */
    fun firstMeeting(
        characterA: CharacterInfo,
        characterB: CharacterInfo,
        greetingA: String = "안녕하세요!",
        greetingB: String = "반갑습니다!"
    ): Beat {
        return Beat(
            id = "first_meeting_${System.currentTimeMillis()}",
            name = "첫 만남",
            description = "두 사람이 처음 만나는 순간",
            duration = 4f,
            layers = BeatLayers(
                characters = listOf(
                    CharacterAction(
                        characterId = characterA.id,
                        characterName = characterA.name,
                        gender = characterA.gender,
                        movement = Movement(
                            type = MovementType.ENTER,
                            from = Position.OFF_STAGE_LEFT,
                            to = Position.LEFT,
                            speed = Speed.NORMAL
                        ),
                        emotion = Emotion(EmotionType.EXCITED, 0.6f),
                        facingDirection = Direction.RIGHT
                    ),
                    CharacterAction(
                        characterId = characterB.id,
                        characterName = characterB.name,
                        gender = characterB.gender,
                        movement = Movement(
                            type = MovementType.ENTER,
                            from = Position.OFF_STAGE_RIGHT,
                            to = Position.RIGHT,
                            speed = Speed.NORMAL
                        ),
                        emotion = Emotion(EmotionType.HAPPY, 0.5f),
                        facingDirection = Direction.LEFT
                    )
                ),
                dialogues = listOf(
                    DialogueAction(
                        characterId = characterA.id,
                        text = greetingA,
                        emotion = EmotionType.HAPPY,
                        delay = 1.5f
                    ),
                    DialogueAction(
                        characterId = characterB.id,
                        text = greetingB,
                        emotion = EmotionType.HAPPY,
                        delay = 2.5f
                    )
                ),
                background = BackgroundLayer(BackgroundType.STAGE_FLOOR)
            )
        )
    }

    /**
     * 어색한 침묵
     * 두 캐릭터가 서로를 의식하며 어색한 침묵
     */
    fun awkwardSilence(
        characterA: CharacterInfo,
        characterB: CharacterInfo
    ): Beat {
        return Beat(
            id = "awkward_silence_${System.currentTimeMillis()}",
            name = "어색한 침묵",
            description = "서로를 의식하며 어색한 분위기",
            duration = 3f,
            layers = BeatLayers(
                characters = listOf(
                    CharacterAction(
                        characterId = characterA.id,
                        characterName = characterA.name,
                        gender = characterA.gender,
                        movement = Movement(type = MovementType.STAY, from = Position.LEFT),
                        emotion = Emotion(EmotionType.NERVOUS, 0.7f),
                        facingDirection = Direction.RIGHT
                    ),
                    CharacterAction(
                        characterId = characterB.id,
                        characterName = characterB.name,
                        gender = characterB.gender,
                        movement = Movement(type = MovementType.STAY, from = Position.RIGHT),
                        emotion = Emotion(EmotionType.SHY, 0.6f),
                        facingDirection = Direction.LEFT
                    )
                )
            )
        )
    }

    // ==================== 갈등 관련 ====================

    /**
     * 대립
     * 두 캐릭터가 서로 대립하는 상황
     */
    fun confrontation(
        characterA: CharacterInfo,
        characterB: CharacterInfo,
        lineA: String = "도대체 왜 그랬어?",
        lineB: String = "내가 그럴 수밖에 없었잖아!"
    ): Beat {
        return Beat(
            id = "confrontation_${System.currentTimeMillis()}",
            name = "대립",
            description = "두 사람이 서로 대립하는 긴장된 순간",
            duration = 5f,
            layers = BeatLayers(
                characters = listOf(
                    CharacterAction(
                        characterId = characterA.id,
                        characterName = characterA.name,
                        gender = characterA.gender,
                        movement = Movement(
                            type = MovementType.APPROACH,
                            from = Position.LEFT,
                            to = Position.LEFT_FRONT,
                            speed = Speed.FAST
                        ),
                        emotion = Emotion(EmotionType.ANGRY, 0.8f),
                        facingDirection = Direction.RIGHT
                    ),
                    CharacterAction(
                        characterId = characterB.id,
                        characterName = characterB.name,
                        gender = characterB.gender,
                        movement = Movement(
                            type = MovementType.RETREAT,
                            from = Position.RIGHT,
                            to = Position.RIGHT_FRONT,
                            speed = Speed.NORMAL
                        ),
                        emotion = Emotion(EmotionType.ANNOYED, 0.7f),
                        facingDirection = Direction.LEFT
                    )
                ),
                dialogues = listOf(
                    DialogueAction(
                        characterId = characterA.id,
                        text = lineA,
                        emotion = EmotionType.ANGRY,
                        delay = 0.5f
                    ),
                    DialogueAction(
                        characterId = characterB.id,
                        text = lineB,
                        emotion = EmotionType.ANNOYED,
                        delay = 2.5f
                    )
                )
            )
        )
    }

    /**
     * 물러서기
     * 한 캐릭터가 당황하여 물러남
     */
    fun stepBack(
        character: CharacterInfo,
        line: String = "잠깐만... 생각할 시간이 필요해"
    ): Beat {
        return Beat(
            id = "step_back_${System.currentTimeMillis()}",
            name = "물러서기",
            description = "당황하여 물러나는 순간",
            duration = 3f,
            layers = BeatLayers(
                characters = listOf(
                    CharacterAction(
                        characterId = character.id,
                        characterName = character.name,
                        gender = character.gender,
                        movement = Movement(
                            type = MovementType.RETREAT,
                            from = Position.CENTER,
                            to = Position.RIGHT,
                            speed = Speed.FAST
                        ),
                        emotion = Emotion(EmotionType.CONFUSED, 0.8f),
                        facingDirection = Direction.LEFT
                    )
                ),
                dialogues = listOf(
                    DialogueAction(
                        characterId = character.id,
                        text = line,
                        emotion = EmotionType.CONFUSED,
                        delay = 1f
                    )
                )
            )
        )
    }

    // ==================== 감정 표현 ====================

    /**
     * 고백
     * 한 캐릭터가 다른 캐릭터에게 고백
     */
    fun confession(
        confessor: CharacterInfo,
        listener: CharacterInfo,
        confessionLine: String = "사실은... 너를 좋아해"
    ): Beat {
        return Beat(
            id = "confession_${System.currentTimeMillis()}",
            name = "고백",
            description = "용기를 내어 고백하는 순간",
            duration = 5f,
            layers = BeatLayers(
                characters = listOf(
                    CharacterAction(
                        characterId = confessor.id,
                        characterName = confessor.name,
                        gender = confessor.gender,
                        movement = Movement(
                            type = MovementType.APPROACH,
                            from = Position.LEFT,
                            to = Position.CENTER_FRONT,
                            speed = Speed.SLOW
                        ),
                        emotion = Emotion(EmotionType.NERVOUS, 0.9f),
                        facingDirection = Direction.RIGHT
                    ),
                    CharacterAction(
                        characterId = listener.id,
                        characterName = listener.name,
                        gender = listener.gender,
                        movement = Movement(type = MovementType.STAY, from = Position.RIGHT),
                        emotion = Emotion(EmotionType.SURPRISED, 0.8f),
                        facingDirection = Direction.LEFT
                    )
                ),
                dialogues = listOf(
                    DialogueAction(
                        characterId = confessor.id,
                        text = confessionLine,
                        emotion = EmotionType.NERVOUS,
                        delay = 2f
                    )
                )
            )
        )
    }

    /**
     * 환호
     * 캐릭터들이 기뻐하며 박수
     */
    fun celebration(
        characters: List<CharacterInfo>,
        cheerLine: String = "와! 성공했어!"
    ): Beat {
        return Beat(
            id = "celebration_${System.currentTimeMillis()}",
            name = "환호",
            description = "기쁨을 표현하며 환호하는 순간",
            duration = 4f,
            layers = BeatLayers(
                characters = characters.mapIndexed { index, char ->
                    val position = when (index) {
                        0 -> Position.LEFT
                        1 -> Position.CENTER
                        else -> Position.RIGHT
                    }
                    CharacterAction(
                        characterId = char.id,
                        characterName = char.name,
                        gender = char.gender,
                        movement = Movement(type = MovementType.STAY, from = position),
                        emotion = Emotion(EmotionType.EXCITED, 1.0f),
                        gesture = Gesture(GestureType.CLAP),
                        facingDirection = Direction.FORWARD
                    )
                },
                dialogues = if (characters.isNotEmpty()) {
                    listOf(
                        DialogueAction(
                            characterId = characters[0].id,
                            text = cheerLine,
                            emotion = EmotionType.EXCITED,
                            delay = 1f
                        )
                    )
                } else emptyList()
            )
        )
    }

    // ==================== 작별 관련 ====================

    /**
     * 작별 인사
     * 두 캐릭터가 헤어지는 순간
     */
    fun farewell(
        characterA: CharacterInfo,
        characterB: CharacterInfo,
        farewellA: String = "잘 가...",
        farewellB: String = "다음에 또 봐"
    ): Beat {
        return Beat(
            id = "farewell_${System.currentTimeMillis()}",
            name = "작별 인사",
            description = "아쉬운 작별의 순간",
            duration = 5f,
            layers = BeatLayers(
                characters = listOf(
                    CharacterAction(
                        characterId = characterA.id,
                        characterName = characterA.name,
                        gender = characterA.gender,
                        movement = Movement(
                            type = MovementType.EXIT,
                            from = Position.CENTER,
                            to = Position.OFF_STAGE_LEFT,
                            speed = Speed.SLOW
                        ),
                        emotion = Emotion(EmotionType.SAD, 0.6f),
                        gesture = Gesture(GestureType.WAVE),
                        facingDirection = Direction.RIGHT
                    ),
                    CharacterAction(
                        characterId = characterB.id,
                        characterName = characterB.name,
                        gender = characterB.gender,
                        movement = Movement(
                            type = MovementType.EXIT,
                            from = Position.CENTER,
                            to = Position.OFF_STAGE_RIGHT,
                            speed = Speed.SLOW
                        ),
                        emotion = Emotion(EmotionType.SAD, 0.5f),
                        gesture = Gesture(GestureType.WAVE),
                        facingDirection = Direction.LEFT
                    )
                ),
                dialogues = listOf(
                    DialogueAction(
                        characterId = characterA.id,
                        text = farewellA,
                        emotion = EmotionType.SAD,
                        delay = 1f
                    ),
                    DialogueAction(
                        characterId = characterB.id,
                        text = farewellB,
                        emotion = EmotionType.SAD,
                        delay = 2.5f
                    )
                )
            )
        )
    }

    // ==================== 단독 액션 ====================

    /**
     * 독백
     * 혼자 생각에 잠긴 캐릭터
     */
    fun monologue(
        character: CharacterInfo,
        thought: String = "어떻게 해야 하지..."
    ): Beat {
        return Beat(
            id = "monologue_${System.currentTimeMillis()}",
            name = "독백",
            description = "혼자 생각에 잠긴 순간",
            duration = 4f,
            layers = BeatLayers(
                characters = listOf(
                    CharacterAction(
                        characterId = character.id,
                        characterName = character.name,
                        gender = character.gender,
                        movement = Movement(type = MovementType.STAY, from = Position.CENTER),
                        emotion = Emotion(EmotionType.CONFUSED, 0.7f),
                        facingDirection = Direction.FORWARD
                    )
                ),
                dialogues = listOf(
                    DialogueAction(
                        characterId = character.id,
                        text = thought,
                        emotion = EmotionType.CONFUSED,
                        delay = 1f
                    )
                )
            )
        )
    }

    /**
     * 등장
     * 캐릭터가 무대에 등장
     */
    fun entrance(
        character: CharacterInfo,
        from: Position = Position.OFF_STAGE_LEFT,
        to: Position = Position.CENTER,
        greeting: String? = null
    ): Beat {
        return Beat(
            id = "entrance_${System.currentTimeMillis()}",
            name = "등장",
            description = "${character.name} 등장",
            duration = 3f,
            layers = BeatLayers(
                characters = listOf(
                    CharacterAction(
                        characterId = character.id,
                        characterName = character.name,
                        gender = character.gender,
                        movement = Movement(
                            type = MovementType.ENTER,
                            from = from,
                            to = to,
                            speed = Speed.NORMAL
                        ),
                        emotion = Emotion(EmotionType.NEUTRAL, 0.5f),
                        facingDirection = Direction.FORWARD
                    )
                ),
                dialogues = if (greeting != null) {
                    listOf(
                        DialogueAction(
                            characterId = character.id,
                            text = greeting,
                            emotion = EmotionType.NEUTRAL,
                            delay = 1.5f
                        )
                    )
                } else emptyList()
            )
        )
    }
}

/**
 * 캐릭터 기본 정보
 * Beat 템플릿 생성 시 필요한 최소 정보
 */
data class CharacterInfo(
    val id: String,
    val name: String,
    val gender: CharacterGender
)
