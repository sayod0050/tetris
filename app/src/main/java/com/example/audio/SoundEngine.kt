package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

/**
 * Procedural low-latency Audio Engine for Block Drop.
 * Generates custom synthesized SFX and an ambient melodic background music track
 * completely on-device with zero external audio assets or licensing constraints.
 */
class SoundEngine {

    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
    var soundVolume: Float = 0.8f // 0.0 to 1.0
    var musicVolume: Float = 0.5f // 0.0 to 1.0

    private val sampleRate = 44100
    private var musicJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    // Pre-rendered SFX byte buffers for instant zero-latency playback
    private var moveSfx: ByteArray? = null
    private var rotateSfx: ByteArray? = null
    private var softDropSfx: ByteArray? = null
    private var hardDropSfx: ByteArray? = null
    private var lineClearSfx: ByteArray? = null
    private var quadClearSfx: ByteArray? = null
    private var comboSfx: ByteArray? = null
    private var levelUpSfx: ByteArray? = null
    private var gameOverSfx: ByteArray? = null
    private var clickSfx: ByteArray? = null
    private var holdSfx: ByteArray? = null

    init {
        scope.launch {
            preRenderSounds()
        }
    }

    private fun preRenderSounds() {
        moveSfx = generateTone(pitchStart = 600.0, pitchEnd = 700.0, durationMs = 35, type = WaveType.SINE, decay = true)
        rotateSfx = generateTone(pitchStart = 450.0, pitchEnd = 850.0, durationMs = 55, type = WaveType.TRIANGLE, decay = true)
        softDropSfx = generateTone(pitchStart = 300.0, pitchEnd = 200.0, durationMs = 25, type = WaveType.SINE, decay = true)
        hardDropSfx = generateImpact(durationMs = 90)
        lineClearSfx = generateChord(listOf(523.25, 659.25, 783.99), durationMs = 180) // C5 major
        quadClearSfx = generateArpeggio(listOf(523.25, 659.25, 783.99, 1046.50), noteDurationMs = 60) // C5-E5-G5-C6
        comboSfx = generateTone(pitchStart = 500.0, pitchEnd = 1000.0, durationMs = 120, type = WaveType.SQUARE, decay = true)
        levelUpSfx = generateArpeggio(listOf(440.0, 554.37, 659.25, 880.0, 1108.73), noteDurationMs = 70)
        gameOverSfx = generateTone(pitchStart = 400.0, pitchEnd = 120.0, durationMs = 350, type = WaveType.SAWTOOTH, decay = true)
        clickSfx = generateTone(pitchStart = 800.0, pitchEnd = 950.0, durationMs = 25, type = WaveType.SINE, decay = true)
        holdSfx = generateTone(pitchStart = 350.0, pitchEnd = 550.0, durationMs = 60, type = WaveType.TRIANGLE, decay = true)
    }

    enum class WaveType { SINE, TRIANGLE, SQUARE, SAWTOOTH }

    private fun generateTone(
        pitchStart: Double,
        pitchEnd: Double,
        durationMs: Int,
        type: WaveType = WaveType.SINE,
        decay: Boolean = true
    ): ByteArray {
        val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteBuffer.allocate(totalSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        var phase = 0.0
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val currentFreq = pitchStart + (pitchEnd - pitchStart) * progress
            phase += 2.0 * PI * currentFreq / sampleRate

            var sample = when (type) {
                WaveType.SINE -> sin(phase)
                WaveType.TRIANGLE -> (2.0 / PI) * Math.asin(sin(phase))
                WaveType.SQUARE -> if (sin(phase) >= 0) 0.6 else -0.6
                WaveType.SAWTOOTH -> (2.0 * ((phase / (2 * PI)) % 1.0)) - 1.0
            }

            val envelope = if (decay) (1.0 - progress) * (1.0 - progress) else 1.0
            val volumeAdj = sample * envelope * 0.8
            val intSample = (volumeAdj.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
            buffer.putShort(intSample)
        }
        return buffer.array()
    }

    private fun generateImpact(durationMs: Int): ByteArray {
        val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteBuffer.allocate(totalSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        var phase = 0.0
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val freq = 160.0 * (1.0 - progress * 0.7)
            phase += 2.0 * PI * freq / sampleRate

            val sine = sin(phase)
            val noise = (Math.random() * 2.0 - 1.0) * (1.0 - progress) * 0.4
            val env = (1.0 - progress) * (1.0 - progress)
            val sample = (sine * 0.6 + noise) * env
            val intSample = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
            buffer.putShort(intSample)
        }
        return buffer.array()
    }

    private fun generateChord(freqs: List<Double>, durationMs: Int): ByteArray {
        val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteBuffer.allocate(totalSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        val phases = DoubleArray(freqs.size) { 0.0 }
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            var sample = 0.0
            for (f in freqs.indices) {
                phases[f] += 2.0 * PI * freqs[f] / sampleRate
                sample += sin(phases[f])
            }
            sample /= freqs.size
            val env = (1.0 - progress)
            val finalSample = (sample * env * 0.8).coerceIn(-1.0, 1.0) * Short.MAX_VALUE
            buffer.putShort(finalSample.toInt().toShort())
        }
        return buffer.array()
    }

    private fun generateArpeggio(notes: List<Double>, noteDurationMs: Int): ByteArray {
        val totalSamplesPerNote = (sampleRate * (noteDurationMs / 1000.0)).toInt()
        val totalSamples = totalSamplesPerNote * notes.size
        val buffer = ByteBuffer.allocate(totalSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        for (freq in notes) {
            var phase = 0.0
            for (i in 0 until totalSamplesPerNote) {
                val progress = i.toDouble() / totalSamplesPerNote
                phase += 2.0 * PI * freq / sampleRate
                val sample = sin(phase) * 0.6 + sin(phase * 2.0) * 0.2
                val env = 1.0 - (progress * 0.8)
                val finalSample = (sample * env * 0.8).coerceIn(-1.0, 1.0) * Short.MAX_VALUE
                buffer.putShort(finalSample.toInt().toShort())
            }
        }
        return buffer.array()
    }

    private fun playPcm(pcm: ByteArray?, relativeVol: Float = 1.0f) {
        if (!isSoundEnabled || pcm == null || soundVolume <= 0.01f) return
        scope.launch {
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(pcm.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.setVolume((soundVolume * relativeVol).coerceIn(0f, 1f))
                track.write(pcm, 0, pcm.size)
                track.play()
                delay((pcm.size / (sampleRate * 2) * 1000).toLong() + 50)
                track.stop()
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    fun playMove() = playPcm(moveSfx, 0.4f)
    fun playRotate() = playPcm(rotateSfx, 0.6f)
    fun playSoftDrop() = playPcm(softDropSfx, 0.35f)
    fun playHardDrop() = playPcm(hardDropSfx, 0.8f)
    fun playLineClear() = playPcm(lineClearSfx, 0.85f)
    fun playQuadClear() = playPcm(quadClearSfx, 1.0f)
    fun playHold() = playPcm(holdSfx, 0.5f)
    fun playLevelUp() = playPcm(levelUpSfx, 0.9f)
    fun playGameOver() = playPcm(gameOverSfx, 0.85f)
    fun playClick() = playPcm(clickSfx, 0.4f)

    fun playCombo(comboCount: Int) {
        if (!isSoundEnabled || soundVolume <= 0.01f) return
        scope.launch {
            val baseFreq = 440.0 + (comboCount * 70.0).coerceAtMost(600.0)
            val chord = listOf(baseFreq, baseFreq * 1.2599, baseFreq * 1.4983)
            val pcm = generateChord(chord, 140)
            playPcm(pcm, 0.8f)
        }
    }

    /**
     * Ambient electronic background music loop.
     * Plays a pleasant procedural chill-synth chord progression in the background.
     */
    fun startBackgroundMusic() {
        if (musicJob?.isActive == true) return
        musicJob = scope.launch {
            // Melodic progression: Am - F - C - G (modern synthwave progression)
            val chords = listOf(
                listOf(220.0, 261.63, 329.63), // Am
                listOf(174.61, 220.0, 261.63), // F
                listOf(261.63, 329.63, 392.0), // C
                listOf(196.0, 246.94, 293.66)  // G
            )
            while (isActive) {
                if (!isMusicEnabled || musicVolume <= 0.01f) {
                    delay(300)
                    continue
                }
                for (chord in chords) {
                    if (!isActive || !isMusicEnabled || musicVolume <= 0.01f) break
                    val chordPcm = generateChord(chord, 1800)
                    playMusicChord(chordPcm, musicVolume * 0.45f)
                    delay(1800)
                }
            }
        }
    }

    private fun playMusicChord(pcm: ByteArray, vol: Float) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcm.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.setVolume(vol.coerceIn(0f, 1f))
            track.write(pcm, 0, pcm.size)
            track.play()
            scope.launch {
                delay((pcm.size / (sampleRate * 2) * 1000).toLong() + 50)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
        }
    }

    fun stopBackgroundMusic() {
        musicJob?.cancel()
        musicJob = null
    }
}
