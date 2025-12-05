package com.example.cutestage.stage

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import kotlin.math.sin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 음성 엔진 타입
 */
enum class VoiceSoundType {
    /** AudioTrack 방식 - 부드럽고 자연스러운 소리 */
    AUDIO_TRACK,

    /** ToneGenerator 방식 - 단순하고 명확한 비프음 */
    TONE_GENERATOR,

    /** AnimalVoice 방식 - 동물의 숲 스타일 (Square Wave + Envelope + Random Pitch) */
    ANIMAL_VOICE,
}

/**
 * 음성 사운드 매니저 인터페이스
 */
interface IVoiceSoundManager {
    suspend fun playBeep(
        pitch: Float = 1.0f,
        duration: Int = 50,
        volume: Float = 0.5f,
    )

    fun release()
}

/**
 * AudioTrack 기반 음성 생성기
 * - 부드럽고 자연스러운 사인파 생성
 * - 페이드 인/아웃 적용
 * - 더 귀여운 소리
 */
class AudioTrackVoiceSoundManager : IVoiceSoundManager {
    private val sampleRate = 8000
    private var audioTrack: AudioTrack? = null

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )

        audioTrack = AudioTrack.Builder().setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build(),
        ).setAudioFormat(
            AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
        ).setBufferSizeInBytes(bufferSize).setTransferMode(AudioTrack.MODE_STREAM).build()

        audioTrack?.play()
    }

    override suspend fun playBeep(
        pitch: Float,
        duration: Int,
        volume: Float,
    ) {
        withContext(Dispatchers.Default) {
            try {
                val baseFrequency = 800f
                val frequency = baseFrequency * pitch
                val samples = generateBeepSamples(
                    frequency = frequency,
                    durationMs = duration,
                    volume = volume,
                )

                audioTrack?.write(samples, 0, samples.size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generateBeepSamples(
        frequency: Float,
        durationMs: Int,
        volume: Float,
    ): ShortArray {
        val samples = (durationMs * sampleRate / 1000)
        val buffer = ShortArray(samples)
        val fadeLength = samples / 4

        for (i in 0 until samples) {
            val angle = 2.0 * Math.PI * i / (sampleRate / frequency)
            var amplitude = sin(angle) // 페이드 인/아웃
            val envelope = when {
                i < fadeLength -> i.toFloat() / fadeLength
                i > samples - fadeLength -> (samples - i).toFloat() / fadeLength
                else -> 1f
            }

            amplitude *= envelope * volume

            buffer[i] = (amplitude * Short.MAX_VALUE * 0.8).toInt().toShort()
        }

        return buffer
    }

    override fun release() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
}

/**
 * AnimalVoice 기반 음성 생성기 (동물의 숲 스타일)
 * - Square Wave (사각파) 생성으로 8비트 게임 느낌
 * - Envelope (페이드 아웃) 적용으로 부드러운 끝맺음
 * - Random Pitch Variation으로 자연스러운 변화
 * - 가장 귀엽고 게임스러운 소리
 */
class AnimalVoiceSoundManager : IVoiceSoundManager {
    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )

        audioTrack = AudioTrack.Builder().setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build(),
        ).setAudioFormat(
            AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
        ).setBufferSizeInBytes(bufferSize).setTransferMode(AudioTrack.MODE_STREAM).build()

        audioTrack?.play()
    }

    override suspend fun playBeep(
        pitch: Float,
        duration: Int,
        volume: Float,
    ) {
        withContext(Dispatchers.Default) {
            try { // pitch 구간에 따라 다른 변화 범위 적용
                // 낮은 pitch (남자): 좁은 범위로 안정적인 목소리
                // 높은 pitch (여자): 넓은 범위로 생동감 있는 목소리
                val variationRange = when {
                    pitch < 1.0f -> 0.10f // 남자: ±10% (안정적)
                    else -> 0.18f // 여자: ±18% (다채로움)
                } // 랜덤 pitch 변화 적용
                val randomVariation = (Math.random().toFloat() - 0.5f) * 2f * variationRange
                val finalPitch = pitch + randomVariation // pitch를 Hz로 변환 (A4 = 440Hz 기준)
                val baseFrequency = 440f
                val frequency = baseFrequency * finalPitch
                val samples = generateSquareWave(
                    frequency = frequency,
                    durationMs = duration,
                    volume = volume,
                )

                audioTrack?.write(samples, 0, samples.size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Square Wave (사각파) 생성
     * - 동물의 숲 특유의 8비트 게임 느낌
     * - Envelope로 자연스러운 페이드 아웃
     */
    private fun generateSquareWave(
        frequency: Float,
        durationMs: Int,
        volume: Float,
    ): ShortArray {
        val numSamples = (sampleRate * durationMs / 1000f).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val time = i.toFloat() / sampleRate // Square wave: sin 값이 양수면 1, 음수면 -1
            val sineValue = sin(2.0 * Math.PI * frequency * time)
            val squareValue = if (sineValue >= 0) 1.0 else -1.0 // Envelope: 자연스러운 페이드 아웃 (70% 감쇄)
            val envelope = 1.0f - (i.toFloat() / numSamples) * 0.7f // 최종 샘플 값
            samples[i] = (squareValue * 3000 * envelope * volume).toInt().toShort()
        }

        return samples
    }

    override fun release() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
}

/**
 * ToneGenerator 기반 음성 생성기
 * - 단순하고 명확한 비프음
 * - 가볍고 빠른 처리
 * - 레트로 게임 느낌
 */
class ToneGeneratorVoiceSoundManager : IVoiceSoundManager {
    private var toneGenerator: ToneGenerator? = null

    init {
        initToneGenerator()
    }

    private fun initToneGenerator() {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun playBeep(
        pitch: Float,
        duration: Int,
        volume: Float,
    ) {
        withContext(Dispatchers.Default) {
            try { // ToneGenerator는 고정된 톤 타입만 지원하므로
                // pitch에 따라 다른 톤 선택
                val toneType = getToneTypeForPitch(pitch)

                toneGenerator?.startTone(toneType, duration)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * pitch 값에 따라 적절한 ToneGenerator 톤 타입 반환
     */
    private fun getToneTypeForPitch(pitch: Float): Int =
        when {
            pitch < 0.7f -> ToneGenerator.TONE_DTMF_1 // 낮은 음 (697Hz)
            pitch < 0.9f -> ToneGenerator.TONE_DTMF_4 // 중저음 (770Hz)
            pitch < 1.1f -> ToneGenerator.TONE_DTMF_7 // 중음 (852Hz)
            pitch < 1.3f -> ToneGenerator.TONE_DTMF_9 // 중고음 (941Hz)
            pitch < 1.5f -> ToneGenerator.TONE_DTMF_A // 고음 (1209Hz + 697Hz)
            else -> ToneGenerator.TONE_DTMF_D // 매우 높은 음 (1477Hz + 697Hz)
        }

    override fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}

/**
 * 음성 사운드 매니저 팩토리
 */
object VoiceSoundManagerFactory {
    /**
     * 현재 사용 중인 엔진 타입 (전역 설정)
     *
     * 변경 예시:
     * ```
     * VoiceSoundManagerFactory.currentEngineType = VoiceSoundType.TONE_GENERATOR
     * ```
     *
     * 기본값: 동물의 숲 스타일 🎮
     */
    var currentEngineType: VoiceSoundType = VoiceSoundType.ANIMAL_VOICE

    /**
     * 선택된 엔진 타입에 맞는 매니저 생성
     */
    fun create(engineType: VoiceSoundType = currentEngineType): IVoiceSoundManager =
        when (engineType) {
            VoiceSoundType.AUDIO_TRACK -> AudioTrackVoiceSoundManager()
            VoiceSoundType.TONE_GENERATOR -> ToneGeneratorVoiceSoundManager()
            VoiceSoundType.ANIMAL_VOICE -> AnimalVoiceSoundManager()
        }
}

/**
 * 캐릭터 음성 설정
 *
 * @param pitch 음높이 (0.5 = 매우 낮음, 1.0 = 기본, 2.0 = 매우 높음)
 * @param speed 말하는 속도 (밀리초 단위, 작을수록 빠름)
 * @param duration 각 글자 소리 지속 시간 (밀리초)
 * @param volume 볼륨 (0.0 ~ 1.0)
 * @param enabled 음성 켜기/끄기
 */
data class CharacterVoice(
    val pitch: Float = 1.0f,
    val speed: Int = 80,
    val duration: Int = 50,
    val volume: Float = 0.5f,
    val enabled: Boolean = true,
) {
    companion object { // ==================== 미리 정의된 프리셋 ====================
        /**
         * 남자 - 낮고 깊은 목소리
         */
        val MALE_DEEP = CharacterVoice(
            pitch = 0.7f,
            speed = 100,
            duration = 60,
            volume = 0.6f,
        )

        /**
         * 남자 - 보통 목소리
         */
        val MALE_NORMAL = CharacterVoice(
            pitch = 0.9f,
            speed = 85,
            duration = 55,
            volume = 0.5f,
        )

        /**
         * 여자 - 보통 목소리
         */
        val FEMALE_NORMAL = CharacterVoice(
            pitch = 1.2f,
            speed = 70,
            duration = 50,
            volume = 0.5f,
        )

        /**
         * 여자 - 높은 목소리
         */
        val FEMALE_HIGH = CharacterVoice(
            pitch = 1.5f,
            speed = 60,
            duration = 45,
            volume = 0.5f,
        )

        /**
         * 어린이 목소리
         */
        val CHILD = CharacterVoice(
            pitch = 1.8f,
            speed = 50,
            duration = 40,
            volume = 0.4f,
        )

        /**
         * 음성 없음
         */
        val SILENT = CharacterVoice(enabled = false)
    }
}
