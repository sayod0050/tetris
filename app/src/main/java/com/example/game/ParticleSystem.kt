package com.example.game

import androidx.compose.ui.graphics.Color

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1f,
    var life: Float = 1f
)

data class FloatingBanner(
    val id: Long,
    val text: String,
    val color: Color,
    val startY: Float,
    var currentY: Float,
    var alpha: Float = 1f,
    var scale: Float = 0.5f,
    var life: Float = 1f
)

class ParticleManager {
    val particles = mutableListOf<Particle>()
    val banners = mutableListOf<FloatingBanner>()
    private var bannerIdSeq = 0L

    fun spawnLineClearParticles(rowY: Float, boardWidth: Float, cellHeight: Float, colors: List<Color>) {
        val count = 25
        val random = java.util.Random()
        for (i in 0 until count) {
            val px = random.nextFloat() * boardWidth
            val py = rowY + (random.nextFloat() * cellHeight)
            val angle = (random.nextFloat() * 2 * Math.PI).toFloat()
            val speed = 2f + random.nextFloat() * 6f
            val vx = (Math.cos(angle.toDouble()) * speed).toFloat()
            val vy = (Math.sin(angle.toDouble()) * speed).toFloat() - 2f
            val color = colors[random.nextInt(colors.size)]
            val size = 4f + random.nextFloat() * 6f
            particles.add(Particle(px, py, vx, vy, color, size))
        }
    }

    fun spawnBanner(text: String, color: Color, startY: Float) {
        banners.add(
            FloatingBanner(
                id = ++bannerIdSeq,
                text = text,
                color = color,
                startY = startY,
                currentY = startY
            )
        )
    }

    fun update(deltaTimeSec: Float) {
        // Update particles
        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.x += p.vx
            p.y += p.vy
            p.vy += 9.8f * deltaTimeSec * 2f // Gravity
            p.life -= deltaTimeSec * 1.8f
            p.alpha = p.life.coerceIn(0f, 1f)
            if (p.life <= 0f) {
                pIter.remove()
            }
        }

        // Update banners
        val bIter = banners.iterator()
        while (bIter.hasNext()) {
            val b = bIter.next()
            b.currentY -= 40f * deltaTimeSec
            b.life -= deltaTimeSec * 0.9f
            b.alpha = (b.life * 1.5f).coerceIn(0f, 1f)
            b.scale = (1f + (1f - b.life) * 0.2f).coerceIn(0.8f, 1.3f)
            if (b.life <= 0f) {
                bIter.remove()
            }
        }
    }

    fun clear() {
        particles.clear()
        banners.clear()
    }
}
