package com.example.cutestage.stage

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.cutestage.R
import kotlinx.coroutines.delay

/**
 * 노래 시나리오 - 하얀 바다새
 *
 * 남녀 듀엣곡으로, 동물의 숲 스타일 음성으로 멜로디를 표현합니다.
 */
object StageSongScenario {
    /**
     * 음계를 pitch 값으로 변환
     *
     * 기준음(시/B): 1.0
     * 각 반음씩 올라갈 때마다 약 1.059배 증가 (12평균율)
     */
    private object NoteFrequency {
        const val B = 1.0f      // 시
        const val Cs = 1.059f   // 도#
        const val D = 1.122f    // 레
        const val E = 1.260f    // 미
        const val Fs = 1.414f   // 파#
        const val G = 1.498f    // 솔
        const val A = 1.682f    // 라
        const val B_HIGH = 2.0f // 높은 시
    }

    /**
     * 노래 구절
     */
    data class Phrase(
        val notes: List<SongNote>,
        val lyric: String,
        val singer: CharacterGender, // 누가 부를지
        val animation: CharacterAnimationType = CharacterAnimationType.SING_NORMAL,
        val accompaniment: List<SongNote> = emptyList(), // 반주 (백그라운드, 옵션)
    )

    /**
     * 하얀 바다새 - 1절 (악보 기준 음길이 적용 + 가사 매칭)
     */
    private fun verse1(): List<Phrase> = listOf( // 어두운 바닷가 홀로 나는 새야
        Phrase(
            notes = listOf(
                SongNote("어", NoteFrequency.B, 250),
                SongNote("두", NoteFrequency.B, 250),
                SongNote("운", NoteFrequency.Cs, 250),
                SongNote("바", NoteFrequency.D, 500),
                SongNote("닷", NoteFrequency.E, 250),
                SongNote("가", NoteFrequency.D, 250),
                SongNote("홀", NoteFrequency.Cs, 250),
                SongNote("로", NoteFrequency.B, 500),
                SongNote("나", NoteFrequency.Fs, 250),
                SongNote("는", NoteFrequency.B, 1000),
                SongNote("새", NoteFrequency.A, 500),
                SongNote("야", NoteFrequency.B, 750),
            ),
            lyric = "어두운 바닷가 홀로 나는 새야",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_NORMAL,
            accompaniment = listOf(
                SongNote("", NoteFrequency.B * 0.5f, 500),
                SongNote("", NoteFrequency.D * 0.5f, 500),
                SongNote("", NoteFrequency.Fs * 0.5f, 500),
                SongNote("", NoteFrequency.A * 0.5f, 1000),
            )
        ), // 갈 곳을 잃었나 하얀 바다새야
        Phrase(
            notes = listOf(
                SongNote("갈", NoteFrequency.A, 250),
                SongNote("곳", NoteFrequency.A, 250),
                SongNote("을", NoteFrequency.B, 250),
                SongNote("잃", NoteFrequency.Cs, 500),
                SongNote("었", NoteFrequency.D, 250),
                SongNote("나", NoteFrequency.Cs, 250),
                SongNote("하", NoteFrequency.B, 250),
                SongNote("얀", NoteFrequency.A, 500),
                SongNote("바", NoteFrequency.Fs, 250),
                SongNote(" ", 0f, 100), // 쉼표
                SongNote("다", NoteFrequency.Fs, 250),
                SongNote("새", NoteFrequency.Fs, 250),
                SongNote("야", NoteFrequency.E, 250),
                SongNote("~", NoteFrequency.D, 500),
                SongNote("~", NoteFrequency.Cs, 250),
                SongNote("~", NoteFrequency.B, 1000),
            ),
            lyric = "갈 곳을 잃었나 하얀 바다새야",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_NORMAL,
            accompaniment = listOf(
                SongNote("", NoteFrequency.A * 0.5f, 500),
                SongNote("", NoteFrequency.Cs * 0.5f, 500),
                SongNote("", NoteFrequency.E * 0.5f, 500),
                SongNote("", NoteFrequency.A * 0.5f, 1000),
            )
        )
    )

    /**
     * 하얀 바다새 - 2절 (가사 매칭)
     */
    private fun verse2(): List<Phrase> = listOf( // 힘없는 소리로 홀로 우는 새야
        Phrase(
            notes = listOf(
                SongNote("힘", NoteFrequency.B, 400),
                SongNote("없", NoteFrequency.B, 400),
                SongNote("는", NoteFrequency.Cs, 400),
                SongNote("소", NoteFrequency.D, 400),
                SongNote("리", NoteFrequency.E, 600),
                SongNote("로", NoteFrequency.D, 400),
                SongNote("홀", NoteFrequency.Cs, 400),
                SongNote("로", NoteFrequency.B, 400),
                SongNote("우", NoteFrequency.Fs, 400),
                SongNote("는", NoteFrequency.B, 800),
                SongNote("새", NoteFrequency.A, 400),
                SongNote("야", NoteFrequency.B, 600),
            ),
            lyric = "힘없는 소리로 홀로 우는 새야",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_NORMAL
        ), // 네 짝을 잃었나 하얀 바다새야
        Phrase(
            notes = listOf(
                SongNote("네", NoteFrequency.A, 400),
                SongNote("짝", NoteFrequency.A, 400),
                SongNote("을", NoteFrequency.B, 400),
                SongNote("잃", NoteFrequency.Cs, 400),
                SongNote("었", NoteFrequency.D, 600),
                SongNote("나", NoteFrequency.Cs, 400),
                SongNote("하", NoteFrequency.B, 400),
                SongNote("얀", NoteFrequency.A, 400),
                SongNote("바", NoteFrequency.Fs, 400),
                SongNote(" ", 0f, 200),
                SongNote("다", NoteFrequency.Fs, 400),
                SongNote("새", NoteFrequency.Fs, 400),
                SongNote("야", NoteFrequency.E, 400),
                SongNote("~", NoteFrequency.D, 400),
                SongNote("~", NoteFrequency.Cs, 400),
                SongNote("~", NoteFrequency.B, 800),
            ),
            lyric = "네 짝을 잃었나 하얀 바다새야",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_NORMAL
        )
    )

    /**
     * 브릿지 & 절정 부분 (가사 매칭)
     */
    private fun bridge(): List<Phrase> = listOf( // 모두 다 가고 없는데
        Phrase(
            notes = listOf(
                SongNote("모", NoteFrequency.Fs, 400),
                SongNote("두", NoteFrequency.Fs, 400),
                SongNote("다", NoteFrequency.Fs, 400),
                SongNote("가", NoteFrequency.Fs, 400),
                SongNote("고", NoteFrequency.E, 400),
                SongNote("없", NoteFrequency.D, 400),
                SongNote("는", NoteFrequency.Cs, 400),
                SongNote("데", NoteFrequency.D, 800),
            ),
            lyric = "모두 다 가고 없는데",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_PITCHUP
        ), // 바다도 잠이 드는데
        Phrase(
            notes = listOf(
                SongNote("바", NoteFrequency.Cs, 400),
                SongNote("다", NoteFrequency.Cs, 400),
                SongNote("도", NoteFrequency.Cs, 400),
                SongNote("잠", NoteFrequency.Cs, 400),
                SongNote("이", NoteFrequency.B, 400),
                SongNote("드", NoteFrequency.A, 400),
                SongNote("는", NoteFrequency.G, 400),
                SongNote("데", NoteFrequency.A, 800),
            ),
            lyric = "바다도 잠이 드는데",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_PITCHUP
        ), // 새는 왜 날개짓 하며
        Phrase(
            notes = listOf(
                SongNote("새", NoteFrequency.B, 400),
                SongNote("는", NoteFrequency.B, 400),
                SongNote("왜", NoteFrequency.B, 400),
                SongNote("날", NoteFrequency.B, 400),
                SongNote("개", NoteFrequency.A, 400),
                SongNote("짓", NoteFrequency.G, 400),
                SongNote("하", NoteFrequency.Fs, 400),
                SongNote("며", NoteFrequency.G, 800),
            ),
            lyric = "새는 왜 날개짓 하며",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 저렇게 날아만 다닐까
        Phrase(
            notes = listOf(
                SongNote("저", NoteFrequency.Fs, 400),
                SongNote("렇", NoteFrequency.Fs, 400),
                SongNote("게", NoteFrequency.Fs, 400),
                SongNote("날", NoteFrequency.Fs, 400),
                SongNote("아", NoteFrequency.E, 400),
                SongNote("만", NoteFrequency.D, 400),
                SongNote("다", NoteFrequency.Cs, 400),
                SongNote("닐까", NoteFrequency.D, 800),
            ),
            lyric = "저렇게 날아만 다닐까",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 해지고 어두운데
        Phrase(
            notes = listOf(
                SongNote("새", NoteFrequency.E, 400),
                SongNote("야", NoteFrequency.E, 400),
                SongNote("해", NoteFrequency.E, 400),
                SongNote("지", NoteFrequency.E, 400),
                SongNote("고", NoteFrequency.D, 400),
                SongNote("어", NoteFrequency.Cs, 400),
                SongNote("두", NoteFrequency.B, 400),
                SongNote("운데", NoteFrequency.Cs, 800),
            ),
            lyric = "새야 해지고 어두운데",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 어디로 떠나갈까
        Phrase(
            notes = listOf(
                SongNote("새", NoteFrequency.Cs, 400),
                SongNote("야", NoteFrequency.Cs, 400),
                SongNote("어", NoteFrequency.Cs, 400),
                SongNote("디", NoteFrequency.Cs, 400),
                SongNote("로", NoteFrequency.B, 400),
                SongNote("떠", NoteFrequency.A, 400),
                SongNote("나", NoteFrequency.G, 400),
                SongNote("갈까", NoteFrequency.A, 800),
            ),
            lyric = "새야 어디로 떠나갈까",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 날마저 기우는데
        Phrase(
            notes = listOf(
                SongNote("새", NoteFrequency.B, 400),
                SongNote("야", NoteFrequency.B, 400),
                SongNote("날", NoteFrequency.B, 400),
                SongNote("마", NoteFrequency.B, 400),
                SongNote("저", NoteFrequency.A, 400),
                SongNote("기", NoteFrequency.G, 400),
                SongNote("우", NoteFrequency.Fs, 400),
                SongNote("는데", NoteFrequency.G, 800),
            ),
            lyric = "새야 날마저 기우는데",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 아픈 맘 어이하나
        Phrase(
            notes = listOf(
                SongNote("새", NoteFrequency.Fs, 400),
                SongNote("야", NoteFrequency.Fs, 400),
                SongNote("아", NoteFrequency.Fs, 400),
                SongNote("픈", NoteFrequency.Fs, 400),
                SongNote("맘", NoteFrequency.E, 400),
                SongNote("어", NoteFrequency.D, 400),
                SongNote("이", NoteFrequency.Cs, 400),
                SongNote("하나", NoteFrequency.B, 1200),
            ),
            lyric = "새야 아픈 맘 어이하나",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_CLIMAX
        )
    )

    /**
     * 간주 - 춤
     */
    private fun interlude(): Phrase = Phrase(
        notes = listOf(
            SongNote("", NoteFrequency.E, 600),
            SongNote("", NoteFrequency.D, 600),
            SongNote("", NoteFrequency.Cs, 600),
            SongNote("", NoteFrequency.B, 1200),
        ), lyric = "🎵 간주 - 함께 춤을 🎵", singer = CharacterGender.MALE, // 더미 (둘 다 춤)
        animation = CharacterAnimationType.DANCING_TYPE_A
    )

    /**
     * 엔딩 (가사 매칭)
     */
    private fun ending(): List<Phrase> = listOf( // 아루 아루 아 새야 (함께 - 하모니)
        Phrase(
            notes = listOf(
                SongNote("아", NoteFrequency.B, 500),
                SongNote("루", NoteFrequency.B, 500),
                SongNote("아", NoteFrequency.A, 500),
                SongNote("루", NoteFrequency.A, 500),
                SongNote("아", NoteFrequency.G, 500),
                SongNote("새", NoteFrequency.Fs, 500),
                SongNote("야~", NoteFrequency.Fs, 2000),
            ), lyric = "아루 아루 아 새야 ♥", singer = CharacterGender.MALE, // 함께 부르기 (하모니)
            animation = CharacterAnimationType.SING_CLIMAX
        )
    )

    /**
     * 하얀 바다새 전체 노래 시나리오 생성 (한 소절당 하나의 dialogue)
     */
    fun createWhiteSeagullScenario() = theaterScript {
        debug(true) // 노래 전체 구성
        val allPhrases =
            verse1() + verse2() + bridge() + listOf(interlude()) + verse1() + ending()         // 각 구절을 씬으로 변환
        allPhrases.forEach { phrase -> // 총 지속 시간 계산 (음표 duration 합계 + 여유 시간)
            val totalDuration = phrase.notes.sumOf { it.duration.toLong() } + 500L // 간주인지 확인
            val isInterlude = phrase.lyric.contains("간주") // 엔딩(하모니)인지 확인
            val isHarmony = phrase.lyric.contains("♥")

            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = totalDuration,
            ) { // 남자 캐릭터 (왼쪽)
                val maleIsSinging = phrase.singer == CharacterGender.MALE
                val malePosY = when {
                    isInterlude || isHarmony -> 150.dp
                    maleIsSinging -> 165.dp
                    else -> 140.dp
                }
                val maleScale = when {
                    isInterlude || isHarmony -> 1f
                    maleIsSinging -> 1.1f
                    else -> 0.95f
                }

                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "영수",
                    x = 80.dp,
                    y = malePosY,
                    size = 100.dp,
                    scale = maleScale,
                    animationDuration = 600,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = when {
                            isInterlude -> CharacterAnimationType.DANCING_TYPE_A
                            isHarmony -> CharacterAnimationType.SING_CLIMAX
                            phrase.singer == CharacterGender.MALE -> phrase.animation
                            else -> CharacterAnimationType.LISTENING
                        },
                        isAnimating = true,
                        frameDuration = if (isInterlude) 300 else 500,
                    ),
                    voice = CharacterVoice(
                        pitch = 0.8f,
                        speed = 90,
                        duration = 55,
                        volume = 0.6f,
                    ),
                ) // 여자 캐릭터 (오른쪽)
                val femaleIsSinging = phrase.singer == CharacterGender.FEMALE
                val femalePosY = when {
                    isInterlude || isHarmony -> 150.dp
                    femaleIsSinging -> 165.dp
                    else -> 140.dp
                }
                val femaleScale = when {
                    isInterlude || isHarmony -> 1f
                    femaleIsSinging -> 1.1f
                    else -> 0.95f
                }

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "영숙",
                    x = 220.dp,
                    y = femalePosY,
                    size = 100.dp,
                    scale = femaleScale,
                    flipX = true,
                    animationDuration = 600,
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = when {
                            isInterlude -> CharacterAnimationType.DANCING_TYPE_B
                            isHarmony -> CharacterAnimationType.SING_CLIMAX
                            phrase.singer == CharacterGender.FEMALE -> phrase.animation
                            else -> CharacterAnimationType.LISTENING
                        },
                        isAnimating = true,
                        frameDuration = if (isInterlude) 300 else 500,
                    ),
                    voice = CharacterVoice(
                        pitch = 1.5f,
                        speed = 65,
                        duration = 48,
                        volume = 0.5f,
                    ),
                ) // 노래 dialogue: 한 소절당 하나의 대화창 + notes 정보 전달
                val xPos = when {
                    isInterlude || isHarmony -> 130.dp
                    phrase.singer == CharacterGender.MALE -> 100.dp
                    else -> 200.dp
                }
                val speakerName = when {
                    isInterlude || isHarmony -> null
                    phrase.singer == CharacterGender.MALE -> "영수"
                    else -> "영숙"
                } // 한 소절 전체를 하나의 dialogue로 표시, notes로 각 글자의 음정 제어
                dialogue(
                    text = phrase.lyric,
                    x = xPos,
                    y = 60.dp,
                    speakerName = speakerName,
                    delayMillis = 200L,
                    typingSpeedMs = 0L, // 타이핑 효과 없이 바로 표시
                    voice = CharacterVoice(
                        pitch = 1.0f, // 기본값 (notes에서 개별 제어)
                        speed = 100,
                        duration = 50,
                        volume = if (phrase.singer == CharacterGender.MALE) 0.6f else 0.5f,
                    ),
                    notes = phrase.notes, // 각 글자의 음정과 음길이 정보
                )
            }
        } // 마지막 씬 - 함께 박수
        scene(
            backgroundRes = R.drawable.stage_floor,
            durationMillis = 3000L,
        ) {
            character(
                id = "male",
                imageRes = R.drawable.stage_ch_m_1,
                name = "영수",
                x = 80.dp,
                y = 150.dp,
                size = 100.dp,
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.MALE,
                    currentAnimation = CharacterAnimationType.CLAP,
                    isAnimating = true,
                    frameDuration = 300,
                ),
            )

            character(
                id = "female",
                imageRes = R.drawable.stage_ch_f_1,
                name = "영숙",
                x = 220.dp,
                y = 150.dp,
                size = 100.dp,
                flipX = true, // 왼쪽을 바라보도록 좌우 반전
                spriteAnimation = CharacterAnimationState(
                    gender = CharacterGender.FEMALE,
                    currentAnimation = CharacterAnimationType.CLAP,
                    isAnimating = true,
                    frameDuration = 300,
                ),
            )

            dialogue(
                text = "🎵 하얀 바다새 🎵",
                x = 130.dp,
                y = 60.dp,
                delayMillis = 500L,
            )
        }
    }
}
