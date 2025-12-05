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
     * 음표 데이터 클래스
     */
    data class Note(
        val pitch: Float,    // 음높이
        val duration: Int,   // 지속 시간 (ms)
        val lyric: String = "", // 가사 (선택)
    )

    /**
     * 노래 구절
     */
    data class Phrase(
        val notes: List<Note>,
        val lyric: String,
        val singer: CharacterGender, // 누가 부를지
        val animation: CharacterAnimationType = CharacterAnimationType.SING_NORMAL,
    )

    /**
     * 하얀 바다새 - 1절
     */
    private fun verse1(): List<Phrase> = listOf( // 어두운 바닷가 홀로 나는 새야
        Phrase(
            notes = listOf(
                Note(NoteFrequency.B, 400),      // 시
                Note(NoteFrequency.B, 400),      // 시
                Note(NoteFrequency.Cs, 400),     // 도#
                Note(NoteFrequency.D, 400),      // 레
                Note(NoteFrequency.E, 600),      // 미
                Note(NoteFrequency.D, 400),      // 레
                Note(NoteFrequency.Cs, 400),     // 도#
                Note(NoteFrequency.B, 400),      // 시
                Note(NoteFrequency.Fs, 400),     // 파#
                Note(NoteFrequency.B, 800),      // 시
            ),
            lyric = "어두운 바닷가 홀로 나는 새야",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_NORMAL
        ), // 갈 곳을 잃었나 하얀 바다새야
        Phrase(
            notes = listOf(
                Note(NoteFrequency.A, 400),      // 라
                Note(NoteFrequency.A, 400),      // 라
                Note(NoteFrequency.B, 400),      // 시
                Note(NoteFrequency.Cs, 400),     // 도#
                Note(NoteFrequency.D, 600),      // 레
                Note(NoteFrequency.Cs, 400),     // 도#
                Note(NoteFrequency.B, 400),      // 시
                Note(NoteFrequency.A, 400),      // 라
                Note(NoteFrequency.Fs, 400),     // 파#
                Note(0f, 200),                   // 쉼표
                Note(NoteFrequency.Fs, 400),     // 파#
                Note(NoteFrequency.Fs, 400),     // 파#
                Note(NoteFrequency.E, 400),      // 미
                Note(NoteFrequency.D, 400),      // 레
                Note(NoteFrequency.Cs, 400),     // 도#
                Note(NoteFrequency.B, 800),      // 시
            ),
            lyric = "갈 곳을 잃었나 하얀 바다새야",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_NORMAL
        )
    )

    /**
     * 하얀 바다새 - 2절
     */
    private fun verse2(): List<Phrase> = listOf( // 힘없는 소리로 홀로 우는 새야
        Phrase(
            notes = listOf(
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.D, 400),
                Note(NoteFrequency.E, 600),
                Note(NoteFrequency.D, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.B, 800),
            ),
            lyric = "힘없는 소리로 홀로 우는 새야",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_NORMAL
        ), // 네 짝을 잃었나 하얀 바다새야
        Phrase(
            notes = listOf(
                Note(NoteFrequency.A, 400),
                Note(NoteFrequency.A, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.D, 600),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.A, 400),
                Note(NoteFrequency.Fs, 400),
                Note(0f, 200),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.E, 400),
                Note(NoteFrequency.D, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.B, 800),
            ),
            lyric = "네 짝을 잃었나 하얀 바다새야",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_NORMAL
        )
    )

    /**
     * 브릿지 & 절정 부분
     */
    private fun bridge(): List<Phrase> = listOf( // 모두 다 가고 없는데
        Phrase(
            notes = listOf(
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.E, 400),
                Note(NoteFrequency.D, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.D, 800),
            ),
            lyric = "모두 다 가고 없는데",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_PITCHUP
        ), // 바다도 잠이 드는데
        Phrase(
            notes = listOf(
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.A, 400),
                Note(NoteFrequency.G, 400),
                Note(NoteFrequency.A, 800),
            ),
            lyric = "바다도 잠이 드는데",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_PITCHUP
        ), // 새는 왜 날개짓 하며
        Phrase(
            notes = listOf(
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.A, 400),
                Note(NoteFrequency.G, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.G, 800),
            ),
            lyric = "새는 왜 날개짓 하며",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 저렇게 날아만 다닐까
        Phrase(
            notes = listOf(
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.E, 400),
                Note(NoteFrequency.D, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.D, 800),
            ),
            lyric = "저렇게 날아만 다닐까",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 해지고 어두운데
        Phrase(
            notes = listOf(
                Note(NoteFrequency.E, 400),
                Note(NoteFrequency.E, 400),
                Note(NoteFrequency.E, 400),
                Note(NoteFrequency.E, 400),
                Note(NoteFrequency.D, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.Cs, 800),
            ),
            lyric = "새야 해지고 어두운데",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 어디로 떠나갈까
        Phrase(
            notes = listOf(
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.A, 400),
                Note(NoteFrequency.G, 400),
                Note(NoteFrequency.A, 800),
            ),
            lyric = "새야 어디로 떠나갈까",
            singer = CharacterGender.FEMALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 날마저 기우는데
        Phrase(
            notes = listOf(
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.B, 400),
                Note(NoteFrequency.A, 400),
                Note(NoteFrequency.G, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.G, 800),
            ),
            lyric = "새야 날마저 기우는데",
            singer = CharacterGender.MALE,
            animation = CharacterAnimationType.SING_CLIMAX
        ), // 새야 아픈 맘 어이하나
        Phrase(
            notes = listOf(
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.Fs, 400),
                Note(NoteFrequency.E, 400),
                Note(NoteFrequency.D, 400),
                Note(NoteFrequency.Cs, 400),
                Note(NoteFrequency.B, 1200),
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
            Note(NoteFrequency.E, 600),
            Note(NoteFrequency.D, 600),
            Note(NoteFrequency.Cs, 600),
            Note(NoteFrequency.B, 1200),
        ), lyric = "🎵 간주 - 함께 춤을 🎵", singer = CharacterGender.MALE, // 더미 (둘 다 춤)
        animation = CharacterAnimationType.DANCING_TYPE_A
    )

    /**
     * 엔딩
     */
    private fun ending(): List<Phrase> = listOf( // 아루 아루 아 새야 (함께 - 하모니)
        Phrase(
            notes = listOf(
                Note(NoteFrequency.B, 500),
                Note(NoteFrequency.B, 500),
                Note(NoteFrequency.A, 500),
                Note(NoteFrequency.A, 500),
                Note(NoteFrequency.G, 500),
                Note(NoteFrequency.Fs, 500),
                Note(NoteFrequency.Fs, 2000),
            ), lyric = "아루 아루 아 새야 ♥", singer = CharacterGender.MALE, // 함께 부르기 (하모니)
            animation = CharacterAnimationType.SING_CLIMAX
        )
    )

    /**
     * 하얀 바다새 전체 노래 시나리오 생성
     */
    fun createWhiteSeagullScenario() = theaterScript {
        debug(true) // 노래 전체 구성
        val allPhrases = verse1() + verse2() + bridge() + listOf(interlude()) + verse1() + ending()         // 각 구절을 씬으로 변환
        allPhrases.forEach { phrase -> // 총 지속 시간 계산 (음표 duration 합계 + 여유 시간)
            val totalDuration = phrase.notes.sumOf { it.duration.toLong() } + 500L // 간주인지 확인
            val isInterlude = phrase.lyric.contains("간주") // 엔딩(하모니)인지 확인
            val isHarmony = phrase.lyric.contains("♥")

            scene(
                backgroundRes = R.drawable.stage_floor,
                durationMillis = totalDuration,
            ) { // 남자 캐릭터 (왼쪽)
                // 노래 부를 때: 가운데 앞쪽으로 (y 증가, scale 증가)
                // 안 부를 때: 뒤로 (y 감소, scale 감소)
                val maleIsSinging = phrase.singer == CharacterGender.MALE
                val malePosY = when {
                    isInterlude || isHarmony -> 150.dp // 간주/하모니: 같은 위치
                    maleIsSinging -> 165.dp // 노래 부를 때: 앞으로
                    else -> 140.dp // 듣기: 뒤로
                }
                val maleScale = when {
                    isInterlude || isHarmony -> 1f
                    maleIsSinging -> 1.1f // 노래 부를 때: 강조
                    else -> 0.95f // 듣기: 약간 작게
                }

                character(
                    id = "male",
                    imageRes = R.drawable.stage_ch_m_1,
                    name = "영수",
                    x = 80.dp,
                    y = malePosY,
                    size = 100.dp,
                    scale = maleScale,
                    animationDuration = 600, // 부드럽게 이동
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.MALE,
                        currentAnimation = when {
                            isInterlude -> CharacterAnimationType.DANCING_TYPE_A // 간주: 춤
                            isHarmony -> CharacterAnimationType.SING_CLIMAX // 엔딩: 함께 노래
                            phrase.singer == CharacterGender.MALE -> phrase.animation // 본인 차례
                            else -> CharacterAnimationType.LISTENING // 듣기
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
                    isInterlude || isHarmony -> 150.dp // 간주/하모니: 같은 위치
                    femaleIsSinging -> 165.dp // 노래 부를 때: 앞으로
                    else -> 140.dp // 듣기: 뒤로
                }
                val femaleScale = when {
                    isInterlude || isHarmony -> 1f
                    femaleIsSinging -> 1.1f // 노래 부를 때: 강조
                    else -> 0.95f // 듣기: 약간 작게
                }

                character(
                    id = "female",
                    imageRes = R.drawable.stage_ch_f_1,
                    name = "영숙",
                    x = 220.dp,
                    y = femalePosY,
                    size = 100.dp,
                    scale = femaleScale,
                    flipX = true, // 왼쪽을 바라보도록 좌우 반전
                    animationDuration = 600, // 부드럽게 이동
                    spriteAnimation = CharacterAnimationState(
                        gender = CharacterGender.FEMALE,
                        currentAnimation = when {
                            isInterlude -> CharacterAnimationType.DANCING_TYPE_B // 간주: 춤 (다른 타입)
                            isHarmony -> CharacterAnimationType.SING_CLIMAX // 엔딩: 함께 노래
                            phrase.singer == CharacterGender.FEMALE -> phrase.animation // 본인 차례
                            else -> CharacterAnimationType.LISTENING // 듣기
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
                ) // 가사 표시
                val xPos = when {
                    isInterlude || isHarmony -> 130.dp // 중앙
                    phrase.singer == CharacterGender.MALE -> 100.dp
                    else -> 200.dp
                }
                val speakerName = when {
                    isInterlude || isHarmony -> null // 화자 없음
                    phrase.singer == CharacterGender.MALE -> "영수"
                    else -> "영숙"
                }
                dialogue(
                    text = phrase.lyric,
                    x = xPos,
                    y = 60.dp,
                    speakerName = speakerName,
                    delayMillis = 200L,
                    voice = if (phrase.singer == CharacterGender.MALE) {
                        CharacterVoice(pitch = 0.8f, speed = 90, duration = 55, volume = 0.6f)
                    } else {
                        CharacterVoice(pitch = 1.5f, speed = 65, duration = 48, volume = 0.5f)
                    },
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
