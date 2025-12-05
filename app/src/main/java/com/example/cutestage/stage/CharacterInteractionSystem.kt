package com.example.cutestage.stage

/**
 * 캐릭터 상호작용 시스템
 *
 * 클릭 횟수에 따라 다양한 반응을 보이는 지능형 상호작용 시스템
 */
object CharacterInteractionSystem {
    /**
     * 감정 타입
     */
    enum class EmotionType {
        NORMAL, // 평범한 (SPEAK_NORMAL, IDLE)
        HAPPY, // 기쁜 (SPEAK_NORMAL)
        ANNOYED, // 짜증 (ANNOYED, IDLE_ANNOYED)
        ANGRY, // 화남 (SPEAK_ANGRY)
    }

    /**
     * 감정과 대사를 담는 데이터 클래스
     */
    data class EmotionalDialogue(
        val text: String,
        val emotion: EmotionType,
    )

    /**
     * 남자 캐릭터 감정별 대사
     */
    private val maleNormalDialogues = listOf(
        "안녕하세요!",
        "반갑습니다~",
        "오늘도 좋은 하루!",
        "만나서 반가워요!",
        "좋은 아침이에요!",
        "날씨 좋네요!",
        "오늘 기분 좋아요!",
        "환영합니다!",
        "뭐 하세요?",
        "요즘 어때요?",
        "커피 마실까?",
        "오늘 점심 뭐 먹지?",
        "주말이 기다려져요!",
        "힘내세요!",
        "화이팅!",
        "잘하고 있어요!",
        "괜찮아요!",
        "기분 좋아요!",
        "즐거워요 데헷~",
        "좋아요 좋아!",
        "고마워요!",
        "감사합니다!",
        "훗~ 나야 나!",
        "짜잔~ 등장!",
        "센스있죠?",
        "웃기죠? ㅋㅋ",
        "맞죠?",
        "당연하죠!",
        "백프로!",
        "궁금해요!",
    )
    private val maleHappyDialogues = listOf(
        "어서오세요 데헷~",
        "와~ 대박!",
        "오~ 멋지다!",
        "짱이네요!",
        "오예~!",
        "좋아 좋아 좋아!",
        "감동이에요!",
        "완전 좋아!",
        "신나요!",
        "재밌어요!",
        "최고예요!",
        "꺅~ 좋아!",
        "행복해요~",
        "놀랐지? 데헷~",
        "깜짝 선물!",
        "반전이지?",
        "대박이죠?",
        "쩐다!",
        "멋져요!",
        "진리!",
    )
    private val maleAnnoyedDialogues = listOf(
        "에휴... 또야?",
        "좀 쉬고 싶은데...",
        "또 클릭해요?",
        "그만 좀 눌러요 ㅠㅠ",
        "조금만 쉬면 안돼요?",
        "손가락 안 아파요?",
        "심심한가봐요...",
        "뭐가 그렇게 재밌어요?",
        "좀 피곤한데...",
        "쉬고 싶어요...",
        "또요? 진짜요?",
        "이제 그만할까요?",
        "좀 쉬어요...",
        "또 부르셨나요?",
        "무한 클릭인가요?",
    )
    private val maleAngryDialogues = listOf(
        "그러면 재밌니?",
        "진짜 화날 것 같아요!",
        "좀 그만 눌러요!",
        "화나요!",
        "짜증나!",
        "열받아!",
        "이제 정말 화났어요!",
        "그만하라고요!",
        "진짜 왜 그래!",
        "도대체 뭐야!",
        "화났어요! 진짜로!",
        "이제 안 말할 거예요!",
        "짜증나게 하지 마세요!",
        "화내기 싫은데...",
        "정말 화가 나요!",
    )

    /**
     * 여자 캐릭터 감정별 대사
     */
    private val femaleNormalDialogues = listOf(
        "안녕하세요~ 데헷!",
        "반가워요!",
        "오늘 날씨 좋죠?",
        "만나서 기뻐요!",
        "좋은 하루 되세요!",
        "오늘도 예쁘죠? 데헷~",
        "기분 좋은 날이에요!",
        "환영해요!",
        "뭐 하세요?",
        "요즘 어때요?",
        "커피 마실래요?",
        "점심 뭐 먹을까요?",
        "주말 기다려져요!",
        "힘내요!",
        "파이팅!",
        "잘하고 있어요!",
        "괜찮아요!",
        "기분 좋아요!",
        "즐거워요!",
        "좋아요 좋아!",
        "고마워요!",
        "감사해요!",
        "훗~ 접니다!",
        "짜잔~ 데헷!",
        "센스있죠?",
        "웃기죠? ㅋㅋ",
        "맞죠?",
        "당연하죠!",
        "백프로!",
        "궁금해요!",
    )
    private val femaleHappyDialogues = listOf(
        "어서오세요~ 데헷!",
        "와~ 대박이에요!",
        "오~ 멋있어요!",
        "짱이에요!",
        "오예~!",
        "좋아좋아!",
        "감동이에요!",
        "완전 좋아요!",
        "신나요!",
        "재밌어요!",
        "최고예요!",
        "꺅~ 좋아!",
        "행복해요 데헷~",
        "놀랐죠? 헤헤",
        "깜짝 이벤트!",
        "반전이죠?",
        "대박이죠?",
        "쩔어요!",
        "멋져요!",
        "진리!",
    )
    private val femaleAnnoyedDialogues = listOf(
        "에휴... 또예요?",
        "좀 쉬고 싶은데요...",
        "또 클릭하세요?",
        "그만 좀 눌러요 ㅠㅠ",
        "조금만 쉬면 안돼요?",
        "손가락 안 아파요?",
        "심심한가봐요...",
        "뭐가 그렇게 재밌어요?",
        "좀 피곤해요...",
        "쉬고 싶어요...",
        "또요? 진짜요?",
        "이제 그만할까요?",
        "좀 쉬어요...",
        "또 부르셨어요?",
        "무한 클릭인가요?",
    )
    private val femaleAngryDialogues = listOf(
        "그러면 재밌어요?",
        "진짜 화날 것 같아요!",
        "좀 그만 눌러요!",
        "화나요!",
        "짜증나요!",
        "열받아요!",
        "이제 정말 화났어요!",
        "그만하라구요!",
        "진짜 왜 그래요!",
        "도대체 뭐예요!",
        "화났어요! 진짜로!",
        "이제 안 말할 거예요!",
        "짜증나게 하지 마세요!",
        "화내기 싫은데...",
        "정말 화가 나요!",
    )

    /**
     * 클릭 횟수에 따라 적절한 감정과 대사 선택
     *
     * @param clickCount 연속 클릭 횟수
     * @param isMale 남자 캐릭터 여부
     * @return 감정과 대사
     */
    fun getEmotionalDialogue(
        clickCount: Int,
        isMale: Boolean,
    ): EmotionalDialogue { // 클릭 횟수에 따라 확률적으로 감정 결정
        val emotion = when {
            clickCount <= 2 -> { // 처음 2번은 주로 NORMAL/HAPPY
                if (Math.random() < 0.7) EmotionType.NORMAL else EmotionType.HAPPY
            }

            clickCount in 3..4 -> { // 3-4번: NORMAL이 줄고 HAPPY가 증가
                when {
                    Math.random() < 0.4 -> EmotionType.NORMAL
                    Math.random() < 0.8 -> EmotionType.HAPPY
                    else -> EmotionType.ANNOYED
                }
            }

            clickCount in 5..7 -> { // 5-7번: ANNOYED 등장
                when {
                    Math.random() < 0.2 -> EmotionType.HAPPY
                    Math.random() < 0.5 -> EmotionType.ANNOYED
                    else -> EmotionType.NORMAL
                }
            }

            clickCount in 8..10 -> { // 8-10번: ANNOYED가 주류
                when {
                    Math.random() < 0.7 -> EmotionType.ANNOYED
                    Math.random() < 0.9 -> EmotionType.ANGRY
                    else -> EmotionType.NORMAL
                }
            }

            else -> { // 11번 이상: ANGRY 확률 증가
                if (Math.random() < 0.6) EmotionType.ANGRY else EmotionType.ANNOYED
            }
        } // 감정에 맞는 대사 선택
        val text = when (emotion) {
            EmotionType.NORMAL -> if (isMale) maleNormalDialogues.random() else femaleNormalDialogues.random()
            EmotionType.HAPPY -> if (isMale) maleHappyDialogues.random() else femaleHappyDialogues.random()
            EmotionType.ANNOYED -> if (isMale) maleAnnoyedDialogues.random() else femaleAnnoyedDialogues.random()
            EmotionType.ANGRY -> if (isMale) maleAngryDialogues.random() else femaleAngryDialogues.random()
        }

        return EmotionalDialogue(text, emotion)
    }

    /**
     * 감정에 따른 애니메이션 타입 반환
     */
    fun getAnimationForEmotion(emotion: EmotionType): CharacterAnimationType =
        when (emotion) {
            EmotionType.NORMAL -> CharacterAnimationType.SPEAK_NORMAL
            EmotionType.HAPPY -> CharacterAnimationType.SPEAK_NORMAL
            EmotionType.ANNOYED -> CharacterAnimationType.ANNOYED
            EmotionType.ANGRY -> CharacterAnimationType.SPEAK_ANGRY
        }

    /**
     * 감정에 따른 음성 설정 반환 (남자)
     */
    fun getMaleVoiceForEmotion(emotion: EmotionType): CharacterVoice =
        when (emotion) {
            EmotionType.NORMAL -> CharacterVoice(
                enabled = true,
                pitch = 0.8f,
                speed = 40,
                duration = 80,
            )

            EmotionType.HAPPY -> CharacterVoice(
                enabled = true,
                pitch = 0.9f,
                speed = 35,
                duration = 75,
            )

            EmotionType.ANNOYED -> CharacterVoice(
                enabled = true,
                pitch = 0.7f,
                speed = 45,
                duration = 90,
            )

            EmotionType.ANGRY -> CharacterVoice(
                enabled = true,
                pitch = 0.6f,
                speed = 50,
                duration = 100,
            )
        }

    /**
     * 감정에 따른 음성 설정 반환 (여자)
     */
    fun getFemaleVoiceForEmotion(emotion: EmotionType): CharacterVoice =
        when (emotion) {
            EmotionType.NORMAL -> CharacterVoice(
                enabled = true,
                pitch = 1.7f, // 1.4f -> 1.7f (높게)
                speed = 35,
                duration = 70,
            )

            EmotionType.HAPPY -> CharacterVoice(
                enabled = true,
                pitch = 1.8f, // 1.5f -> 1.8f (높게)
                speed = 30,
                duration = 65,
            )

            EmotionType.ANNOYED -> CharacterVoice(
                enabled = true,
                pitch = 1.6f, // 1.3f -> 1.6f (높게)
                speed = 40,
                duration = 80,
            )

            EmotionType.ANGRY -> CharacterVoice(
                enabled = true,
                pitch = 1.5f, // 1.2f -> 1.5f (높게)
                speed = 45,
                duration = 90,
            )
        }
}
