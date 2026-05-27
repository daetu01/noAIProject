<template>
  <section id="hero" class="hero">
    <canvas ref="canvas" class="hero-canvas" />

    <div class="hero-content">
      <p class="hero-eye reveal" style="animation-delay:.2s">Welcome to my universe</p>

      <h1 class="hero-title">
        <span class="line reveal" style="animation-delay:.4s">DAEAN</span>
        <span class="line gradient reveal" style="animation-delay:.55s">STUDIO</span>
      </h1>

      <p class="hero-sub reveal" style="animation-delay:.7s">
        I build worlds, systems, music, and experiences.
      </p>

      <div class="hero-cta reveal" style="animation-delay:.9s">
        <a href="#projects" class="btn-pri">Explore Work</a>
        <a href="#about" class="btn-ghost">About Me</a>
      </div>
    </div>

    <div class="scroll-hint">
      <span>Scroll</span>
      <div class="scroll-dot" />
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const canvas = ref<HTMLCanvasElement | null>(null)
let raf = 0
let mouse = { x: 0, y: 0 }

interface Pt { x: number; y: number; vx: number; vy: number; r: number; o: number }

onMounted(() => {
  const c = canvas.value!
  const ctx = c.getContext('2d')!
  let pts: Pt[] = []
  let W = 0, H = 0

  const resize = () => {
    W = c.width = c.offsetWidth
    H = c.height = c.offsetHeight
    pts = Array.from({ length: 110 }, () => ({
      x: Math.random() * W,
      y: Math.random() * H,
      vx: (Math.random() - .5) * .35,
      vy: (Math.random() - .5) * .35,
      r: Math.random() * 1.4 + .4,
      o: Math.random() * .45 + .1,
    }))
  }
  resize()
  window.addEventListener('resize', resize)
  window.addEventListener('mousemove', e => { mouse.x = e.clientX; mouse.y = e.clientY })

  const draw = () => {
    ctx.clearRect(0, 0, W, H)
    for (let i = 0; i < pts.length; i++) {
      const p = pts[i]
      p.x += p.vx; p.y += p.vy
      if (p.x < 0 || p.x > W) p.vx *= -1
      if (p.y < 0 || p.y > H) p.vy *= -1
      const dx = mouse.x - p.x, dy = mouse.y - p.y
      const dist = Math.sqrt(dx * dx + dy * dy)
      if (dist < 200) { p.vx += dx / dist * .004; p.vy += dy / dist * .004 }
      const speed = Math.sqrt(p.vx * p.vx + p.vy * p.vy)
      if (speed > .8) { p.vx *= .97; p.vy *= .97 }
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(255,255,255,${p.o})`
      ctx.fill()
      for (let j = i + 1; j < pts.length; j++) {
        const q = pts[j]
        const d = Math.hypot(p.x - q.x, p.y - q.y)
        if (d < 130) {
          ctx.beginPath(); ctx.moveTo(p.x, p.y); ctx.lineTo(q.x, q.y)
          ctx.strokeStyle = `rgba(91,156,246,${(1 - d / 130) * .12})`
          ctx.lineWidth = .7; ctx.stroke()
        }
      }
    }
    raf = requestAnimationFrame(draw)
  }
  draw()

  cleanup = () => {
    cancelAnimationFrame(raf)
    window.removeEventListener('resize', resize)
  }
})

let cleanup: (() => void) | null = null

onUnmounted(() => { cleanup?.() })
</script>

<style scoped>
.hero {
  position: relative; height: 100vh; min-height: 640px;
  display: flex; align-items: center; justify-content: center;
  overflow: hidden;
}

.hero-canvas {
  position: absolute; inset: 0; width: 100%; height: 100%;
}

/* Radial dark gradient vignette */
.hero::after {
  content: '';
  position: absolute; inset: 0;
  background: radial-gradient(ellipse 80% 60% at 50% 50%, transparent 40%, rgba(11,11,11,.7) 100%);
  pointer-events: none;
}

.hero-content {
  position: relative; z-index: 1;
  text-align: center; padding: 0 24px;
  display: flex; flex-direction: column; align-items: center;
}

.hero-eye {
  font-size: 11px; letter-spacing: .28em; text-transform: uppercase;
  color: #6e6e73; margin-bottom: 28px;
}

.hero-title {
  font-family: 'Space Grotesk', sans-serif;
  display: flex; flex-direction: column; gap: 0;
  font-size: clamp(72px, 13vw, 160px);
  font-weight: 700;
  letter-spacing: -.04em;
  line-height: .9;
  margin-bottom: 28px;
  user-select: none;
}
.hero-title .line { display: block; }
.hero-title .gradient {
  background: linear-gradient(135deg, #5b9cf6 0%, #a78bfa 60%, #f472b6 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-sub {
  font-size: clamp(15px, 2.2vw, 20px);
  color: #a1a1a6; font-weight: 300;
  letter-spacing: .01em; max-width: 520px;
  margin: 0 auto 48px; line-height: 1.55;
}

.hero-cta { display: flex; gap: 14px; flex-wrap: wrap; justify-content: center; }

/* Scroll hint */
.scroll-hint {
  position: absolute; bottom: 36px; left: 50%;
  transform: translateX(-50%);
  display: flex; flex-direction: column; align-items: center; gap: 10px;
  animation: fadeIn .8s ease 1.4s both;
}
.scroll-hint span {
  font-size: 10px; letter-spacing: .22em; text-transform: uppercase; color: #6e6e73;
}
.scroll-dot {
  width: 4px; height: 4px;
  background: #6e6e73; border-radius: 50%;
  animation: scrollDot 2s ease-in-out infinite;
}

@media (max-width: 480px) {
  .hero-title { font-size: clamp(60px, 20vw, 90px); }
}
</style>
