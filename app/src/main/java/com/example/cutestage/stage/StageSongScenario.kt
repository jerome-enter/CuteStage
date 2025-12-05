package com.example.cutestage.stage

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.cutestage.R
import kotlinx.coroutines.delay

/**
 * 노래 시나리오 - 하얀 바다새
 *
 * 남녀 듀엣곡으로, 동물의 숲 스타일 음성으로 멜로디를 표현합니다.
 * 남성과 여성의 음역대를 구분하여 자연스러운 듀엣을 표현합니다.
 */
object StageSongScenario {
    /**
     * 남성 음계 (낮은 음역대)
     * 기준음(시/B): 0.8f (남성 음역대)
     * 각 반음씩 올라갈 때마다 약 1.059배 증가 (12평균율)
     */
    private object MaleNoteFrequency {
        const val B = 0.8f       // 시
        const val Cs = 0.847f    // 도# (0.8 * 1.059)
        const val D = 0.898f     // 레
        const val E = 1.008f     // 미
        const val Fs = 1.131f    // 파#
        const val G = 1.198f     // 솔
        const val A = 1.346f     // 라
        const val B_HIGH = 1.6f  // 높은 시
    }

    /**
     * 여성 음계 (높은 음역대)
     * 기준음(시/B): 1.5f (여성 음역대)
     * 각 반음씩 올라갈 때마다 약 1.059배 증가 (12평균율)
     */
    private object FemaleNoteFrequency {
        const val B = 1.5f       // 시
        const val Cs = 1.589f    // 도# (1.5 * 1.059)
        const val D = 1.683f     // 레
        const val E = 1.890f     // 미
        const val Fs = 2.121f    // 파#
        const val G = 2.247f     // 솔
        const val A = 2.523f     // 라
        const val B_HIGH = 3.0f  // 높은 시
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
     * 하얀 바다새 - 1절 (음길이 2배 빠르게, 성별 음역대 적용)
     */
    private fun verse1(): List<Phrase> = listOf( // 어두운 바닷가 홀로 나는 새야 (남성)
        Phrase(
            notes = listOf(
                SongNote("어", MaleNoteFrequency.B, 125),
                SongNote("두", MaleNoteFrequency.B, 125),
                SongNote("운", MaleNoteFrequency.Cs, 125),
                SongNote("바", MaleNoteFrequency.D, 250),
                SongNote("닷", MaleNoteFrequency.E, 125),
                SongNote("가", MaleNoteFrequency.D, 125),
                SongNote("홀", MaleNoteFrequency.Cs, 125),
                SongNote("로", MaleNoteFrequency.B, 250),
                SongNote("나", MaleNoteFrequency.Fs, 125),
                SongNote("는", MaleNoteFrequency.B, 500),
                SongNote("새", MaleNoteFrequency.A, 250),
                SongNote("야", MaleNoteFrequency.B, 375),
            ),
            lyric = "어두운 바닷가 홀로 나는 새야",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_NORMAL,
        ), // 갈 곳을 잃었나 하얀 바다새야 (여성)
        Phrase(
            notes = listOf(
                SongNote("갈", FemaleNoteFrequency.A, 125),
                SongNote("곳", FemaleNoteFrequency.A, 125),
                SongNote("을", FemaleNoteFrequency.B, 125),
                SongNote("잃", FemaleNoteFrequency.Cs, 250),
                SongNote("었", FemaleNoteFrequency.D, 125),
                SongNote("나", FemaleNoteFrequency.Cs, 125),
                SongNote("하", FemaleNoteFrequency.B, 125),
                SongNote("얀", FemaleNoteFrequency.A, 250),
                SongNote("바", FemaleNoteFrequency.Fs, 125),
                SongNote(" ", 0f, 50), // 쉼표
                SongNote("다", FemaleNoteFrequency.Fs, 125),
                SongNote("새", FemaleNoteFrequency.Fs, 125),
                SongNote("야", FemaleNoteFrequency.E, 125),
                SongNote("~", FemaleNoteFrequency.D, 250),
                SongNote("~", FemaleNoteFrequency.Cs, 125),
                SongNote("~", FemaleNoteFrequency.B, 500),
            ),
            lyric = "갈 곳을 잃었나 하얀 바다새야",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_NORMAL,
        )
    )

    /**
     * 하얀 바다새 - 2절 (음길이 2배 빠르게, 성별 음역대 적용)
     */
    private fun verse2(): List<Phrase> = listOf( // 힘없는 소리로 홀로 우는 새야 (여성)
        Phrase(
            notes = listOf(
                SongNote("힘", FemaleNoteFrequency.B, 200),
                SongNote("없", FemaleNoteFrequency.B, 200),
                SongNote("는", FemaleNoteFrequency.Cs, 200),
                SongNote("소", FemaleNoteFrequency.D, 200),
                SongNote("리", FemaleNoteFrequency.E, 300),
                SongNote("로", FemaleNoteFrequency.D, 200),
                SongNote("홀", FemaleNoteFrequency.Cs, 200),
                SongNote("로", FemaleNoteFrequency.B, 200),
                SongNote("우", FemaleNoteFrequency.Fs, 200),
                SongNote("는", FemaleNoteFrequency.B, 400),
                SongNote("새", FemaleNoteFrequency.A, 200),
                SongNote("야", FemaleNoteFrequency.B, 300),
            ),
            lyric = "힘없는 소리로 홀로 우는 새야",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_NORMAL
        ), // 네 짝을 잃었나 하얀 바다새야 (남성)
        Phrase(
            notes = listOf(
                SongNote("네", MaleNoteFrequency.A, 200),
                SongNote("짝", MaleNoteFrequency.A, 200),
                SongNote("을", MaleNoteFrequency.B, 200),
                SongNote("잃", MaleNoteFrequency.Cs, 200),
                SongNote("었", MaleNoteFrequency.D, 300),
                SongNote("나", MaleNoteFrequency.Cs, 200),
                SongNote("하", MaleNoteFrequency.B, 200),
                SongNote("얀", MaleNoteFrequency.A, 200),
                SongNote("바", MaleNoteFrequency.Fs, 200),
                SongNote(" ", 0f, 100),
                SongNote("다", MaleNoteFrequency.Fs, 200),
                SongNote("새", MaleNoteFrequency.Fs, 200),
                SongNote("야", MaleNoteFrequency.E, 200),
                SongNote("~", MaleNoteFrequency.D, 200),
                SongNote("~", MaleNoteFrequency.Cs, 200),
                SongNote("~", MaleNoteFrequency.B, 400),
            ),
            lyric = "네 짝을 잃었나 하얀 바다새야",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_NORMAL
        )
    )

    /**
     * 브릿지 & 절정 부분 (음길이 2배 빠르게, 성별 음역대 적용)
     */
    private fun bridge(): List<Phrase> = listOf( // 모두 다 가고 없는데 (남성)
        Phrase(
            notes = listOf(
                SongNote("모", MaleNoteFrequency.Fs, 200),
                SongNote("두", MaleNoteFrequency.Fs, 200),
                SongNote("다", MaleNoteFrequency.Fs, 200),
                SongNote("가", MaleNoteFrequency.Fs, 200),
                SongNote("고", MaleNoteFrequency.E, 200),
                SongNote("없", MaleNoteFrequency.D, 200),
                SongNote("는", MaleNoteFrequency.Cs, 200),
                SongNote("데", MaleNoteFrequency.D, 400),
            ),
            lyric = "모두 다 가고 없는데",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_PITCHUP
        ), // 바다도 잠이 드는데 (여성)
        Phrase(
            notes = listOf(
                SongNote("바", FemaleNoteFrequency.Cs, 200),
                SongNote("다", FemaleNoteFrequency.Cs, 200),
                SongNote("도", FemaleNoteFrequency.Cs, 200),
                SongNote("잠", FemaleNoteFrequency.Cs, 200),
                SongNote("이", FemaleNoteFrequency.B, 200),
                SongNote("드", FemaleNoteFrequency.A, 200),
                SongNote("는", FemaleNoteFrequency.G, 200),
                SongNote("데", FemaleNoteFrequency.A, 400),
            ),
            lyric = "바다도 잠이 드는데",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_PITCHUP
        ), // 새는 왜 날개짓 하며 (남성)
        Phrase(
            notes = listOf(
                SongNote("새", MaleNoteFrequency.B, 200),
                SongNote("는", MaleNoteFrequency.B, 200),
                SongNote("왜", MaleNoteFrequency.B, 200),
                SongNote("날", MaleNoteFrequency.B, 200),
                SongNote("개", MaleNoteFrequency.A, 200),
                SongNote("짓", MaleNoteFrequency.G, 200),
                SongNote("하", MaleNoteFrequency.Fs, 200),
                SongNote("며", MaleNoteFrequency.G, 400),
            ),
            lyric = "새는 왜 날개짓 하며",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 저렇게 날아만 다닐까 (여성)
        Phrase(
            notes = listOf(
                SongNote("저", FemaleNoteFrequency.Fs, 200),
                SongNote("렇", FemaleNoteFrequency.Fs, 200),
                SongNote("게", FemaleNoteFrequency.Fs, 200),
                SongNote("날", FemaleNoteFrequency.Fs, 200),
                SongNote("아", FemaleNoteFrequency.E, 200),
                SongNote("만", FemaleNoteFrequency.D, 200),
                SongNote("다", FemaleNoteFrequency.Cs, 200),
                SongNote("닐까", FemaleNoteFrequency.D, 400),
            ),
            lyric = "저렇게 날아만 다닐까",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 해지고 어두운데 (남성)
        Phrase(
            notes = listOf(
                SongNote("새", MaleNoteFrequency.E, 200),
                SongNote("야", MaleNoteFrequency.E, 200),
                SongNote("해", MaleNoteFrequency.E, 200),
                SongNote("지", MaleNoteFrequency.E, 200),
                SongNote("고", MaleNoteFrequency.D, 200),
                SongNote("어", MaleNoteFrequency.Cs, 200),
                SongNote("두", MaleNoteFrequency.B, 200),
                SongNote("운데", MaleNoteFrequency.Cs, 400),
            ),
            lyric = "새야 해지고 어두운데",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 어디로 떠나갈까 (여성)
        Phrase(
            notes = listOf(
                SongNote("새", FemaleNoteFrequency.Cs, 200),
                SongNote("야", FemaleNoteFrequency.Cs, 200),
                SongNote("어", FemaleNoteFrequency.Cs, 200),
                SongNote("디", FemaleNoteFrequency.Cs, 200),
                SongNote("로", FemaleNoteFrequency.B, 200),
                SongNote("떠", FemaleNoteFrequency.A, 200),
                SongNote("나", FemaleNoteFrequency.G, 200),
                SongNote("갈까", FemaleNoteFrequency.A, 400),
            ),
            lyric = "새야 어디로 떠나갈까",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 날마저 기우는데 (남성)
        Phrase(
            notes = listOf(
                SongNote("새", MaleNoteFrequency.B, 200),
                SongNote("야", MaleNoteFrequency.B, 200),
                SongNote("날", MaleNoteFrequency.B, 200),
                SongNote("마", MaleNoteFrequency.B, 200),
                SongNote("저", MaleNoteFrequency.A, 200),
                SongNote("기", MaleNoteFrequency.G, 200),
                SongNote("우", MaleNoteFrequency.Fs, 200),
                SongNote("는데", MaleNoteFrequency.G, 400),
            ),
            lyric = "새야 날마저 기우는데",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 아픈 맘 어이하나 (여성)
        Phrase(
            notes = listOf(
                SongNote("새", FemaleNoteFrequency.Fs, 200),
                SongNote("야", FemaleNoteFrequency.Fs, 200),
                SongNote("아", FemaleNoteFrequency.Fs, 200),
                SongNote("픈", FemaleNoteFrequency.Fs, 200),
                SongNote("맘", FemaleNoteFrequency.E, 200),
                SongNote("어", FemaleNoteFrequency.D, 200),
                SongNote("이", FemaleNoteFrequency.Cs, 200),
                SongNote("하나", FemaleNoteFrequency.B, 600),
            ),
            lyric = "새야 아픈 맘 어이하나",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_CLIMAX
        )
    )

    /**
     * 간주 - 춤 (음길이 2배 빠르게, 남성 음역대 사용)
     */
    private fun interlude(): Phrase = Phrase(
        notes = listOf(
            SongNote("", MaleNoteFrequency.E, 300),
            SongNote("", MaleNoteFrequency.D, 300),
            SongNote("", MaleNoteFrequency.Cs, 300),
            SongNote("", MaleNoteFrequency.B, 600),
        ), lyric = "🎵 간주 - 함께 춤을 🎵", singer = CharacterGender.MALE, // 더미 (둘 다 춤)
        animation = CharacterAnimationType.DANCING_TYPE_A
    )

    /**
     * 엔딩 (음길이 2배 빠르게, 하모니이므로 중간 음역대 사용)
     */
    private fun ending(): List<Phrase> = listOf( // 아루 아루 아 새야 (함께 - 하모니)
        Phrase(
            notes = listOf(
                SongNote("아", MaleNoteFrequency.B_HIGH, 250),
                SongNote("루", MaleNoteFrequency.B_HIGH, 250),
                SongNote("아", MaleNoteFrequency.A, 250),
                SongNote("루", MaleNoteFrequency.A, 250),
                SongNote("아", MaleNoteFrequency.G, 250),
                SongNote("새", MaleNoteFrequency.Fs, 250),
                SongNote("야~", MaleNoteFrequency.Fs, 1000),
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
                backgroundRes = R.drawable.stage_music_bank_bg,
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
            backgroundRes = R.drawable.stage_music_bank_bg,
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
