<template>
  <div class="signup-root">
    <!-- Custom cursor -->
    <div class="ds-cursor" ref="cursor" :class="{ big: cursorBig }" />
    <div class="ds-cursor-ring" ref="ring" />

    <!-- Particle canvas -->
    <canvas ref="canvas" class="signup-canvas" />

    <!-- Back to studio -->
    <RouterLink to="/" class="back-link">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="15 18 9 12 15 6" />
      </svg>
      DAEAN STUDIO
    </RouterLink>

    <!-- Card -->
    <div class="signup-card" :class="{ shake: shaking }">
      <!-- Logo -->
      <div class="card-logo">
        <span class="logo-mark g-text">DS</span>
        <div class="logo-ring" />
      </div>

      <h1 class="card-title">Create account</h1>
      <p class="card-sub">Join the creative workspace</p>

      <!-- Error -->
      <Transition name="err">
        <div v-if="errorMsg" class="error-block">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
          {{ errorMsg }}
        </div>
      </Transition>

      <!-- Form -->
      <form class="signup-form" @submit.prevent="submit" novalidate>
        <div class="field-group">
          <div class="field">
            <label class="field-label">Nickname</label>
            <input
              v-model="nickName"
              type="text"
              placeholder="Your name"
              class="field-input"
              autocomplete="nickname"
              required
              @focus="focused = 'nick'"
              @blur="focused = null"
              :class="{ focused: focused === 'nick' }"
            />
          </div>

          <div class="field-sep" />

          <div class="field">
            <label class="field-label">Email</label>
            <input
              v-model="email"
              type="email"
              placeholder="you@example.com"
              class="field-input"
              autocomplete="email"
              required
              @focus="focused = 'email'"
              @blur="focused = null"
              :class="{ focused: focused === 'email' }"
            />
          </div>

          <div class="field-sep" />

          <div class="field">
            <label class="field-label">Password</label>
            <div class="input-wrap">
              <input
                v-model="password"
                :type="showPw ? 'text' : 'password'"
                placeholder="••••••••"
                class="field-input"
                autocomplete="new-password"
                required
                @focus="focused = 'password'"
                @blur="focused = null"
                :class="{ focused: focused === 'password' }"
              />
              <button type="button" class="pw-toggle" @click="showPw = !showPw" tabindex="-1">
                <svg v-if="!showPw" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                  <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24"/>
                  <line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="!loading">Create Account</span>
          <span v-else class="spinner" />
        </button>
      </form>

      <p class="card-footer">
        Already have an account?
        <RouterLink to="/login" class="footer-link">Sign in →</RouterLink>
      </p>

      <!-- Divider with service note -->
      <div class="service-note">
        <span />
        <p>Secure access to your creative workspace</p>
        <span />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

const store  = useAppStore()
const router = useRouter()

const nickName = ref('')
const email    = ref('')
const password = ref('')
const errorMsg = ref('')
const loading  = ref(false)
const showPw   = ref(false)
const focused  = ref<string | null>(null)
const shaking  = ref(false)
const canvas    = ref<HTMLCanvasElement | null>(null)
const cursor    = ref<HTMLElement | null>(null)
const ring      = ref<HTMLElement | null>(null)
const cursorBig = ref(false)

async function submit() {
  errorMsg.value = ''
  loading.value  = true
  try {
    await store.signup({ nickName: nickName.value, email: email.value, password: password.value })
    router.push('/login')
  } catch {
    errorMsg.value = '이미 사용 중인 이메일이거나 가입에 실패했습니다.'
    shaking.value  = true
    setTimeout(() => shaking.value = false, 600)
  } finally {
    loading.value = false
  }
}

// ─── Cursor ──────────────────────────────────────────────────────────────────
let rx = 0, ry = 0, tx = 0, ty = 0
let cursorRaf = 0

function onMouseMove(e: MouseEvent) {
  tx = e.clientX; ty = e.clientY
  if (cursor.value) { cursor.value.style.left = `${tx}px`; cursor.value.style.top = `${ty}px` }
}

function animateCursor() {
  rx += (tx - rx) * .13; ry += (ty - ry) * .13
  if (ring.value) { ring.value.style.left = `${rx}px`; ring.value.style.top = `${ry}px` }
  cursorRaf = requestAnimationFrame(animateCursor)
}

// ─── Particle background ─────────────────────────────────────────────────────
let particleRaf = 0
let cleanup: (() => void) | null = null

onMounted(() => {
  // cursor
  window.addEventListener('mousemove', onMouseMove, { passive: true })
  animateCursor()
  document.querySelectorAll('a, button, input').forEach(el => {
    el.addEventListener('mouseenter', () => { cursorBig.value = true })
    el.addEventListener('mouseleave', () => { cursorBig.value = false })
  })

  // particles
  const c = canvas.value!
  const ctx = c.getContext('2d')!
  let W = 0, H = 0

  type Pt = { x: number; y: number; vx: number; vy: number; r: number; o: number }
  let pts: Pt[] = []

  const resize = () => {
    W = c.width = c.offsetWidth
    H = c.height = c.offsetHeight
    pts = Array.from({ length: 60 }, () => ({
      x: Math.random() * W, y: Math.random() * H,
      vx: (Math.random() - .5) * .3, vy: (Math.random() - .5) * .3,
      r: Math.random() * 1.2 + .3, o: Math.random() * .3 + .05,
    }))
  }
  resize()
  window.addEventListener('resize', resize)

  const draw = () => {
    ctx.clearRect(0, 0, W, H)
    for (let i = 0; i < pts.length; i++) {
      const p = pts[i]
      p.x += p.vx; p.y += p.vy
      if (p.x < 0 || p.x > W) p.vx *= -1
      if (p.y < 0 || p.y > H) p.vy *= -1
      ctx.beginPath(); ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(255,255,255,${p.o})`; ctx.fill()
      for (let j = i + 1; j < pts.length; j++) {
        const q = pts[j], d = Math.hypot(p.x - q.x, p.y - q.y)
        if (d < 110) {
          ctx.beginPath(); ctx.moveTo(p.x, p.y); ctx.lineTo(q.x, q.y)
          ctx.strokeStyle = `rgba(91,156,246,${(1 - d / 110) * .08})`
          ctx.lineWidth = .6; ctx.stroke()
        }
      }
    }
    particleRaf = requestAnimationFrame(draw)
  }
  draw()

  cleanup = () => {
    cancelAnimationFrame(particleRaf)
    cancelAnimationFrame(cursorRaf)
    window.removeEventListener('resize', resize)
    window.removeEventListener('mousemove', onMouseMove)
  }
})

onUnmounted(() => cleanup?.())
</script>

<style scoped>
/* ─── Root ────────────────────────────────────────────── */
.signup-root {
  min-height: 100vh;
  background: var(--bg);
  display: flex; align-items: center; justify-content: center;
  padding: 24px;
  position: relative; overflow: hidden;
  font-family: var(--font);
}

/* ambient glow */
.signup-root::before {
  content: '';
  position: absolute; top: -20%; left: 50%;
  width: 700px; height: 500px;
  transform: translateX(-50%);
  background: radial-gradient(ellipse, rgba(91,156,246,.07) 0%, transparent 65%);
  pointer-events: none;
}
.signup-root::after {
  content: '';
  position: absolute; bottom: -10%; right: 20%;
  width: 400px; height: 400px;
  background: radial-gradient(ellipse, rgba(167,139,250,.05) 0%, transparent 65%);
  pointer-events: none;
}

.signup-canvas {
  position: absolute; inset: 0; width: 100%; height: 100%;
  pointer-events: none;
}

/* ─── Back link ───────────────────────────────────────── */
.back-link {
  position: absolute; top: 28px; left: 40px;
  display: inline-flex; align-items: center; gap: 7px;
  font-size: 12px; font-weight: 600; letter-spacing: .12em;
  color: #6e6e73; text-decoration: none;
  transition: color .2s ease;
}
.back-link:hover { color: #f5f5f7; }
.back-link svg { transition: transform .2s ease; }
.back-link:hover svg { transform: translateX(-3px); }

/* ─── Card ────────────────────────────────────────────── */
.signup-card {
  position: relative; z-index: 1;
  width: 100%; max-width: 400px;
  background: rgba(22,22,22,.85);
  border: 1px solid rgba(255,255,255,.09);
  border-radius: 24px;
  padding: 44px 40px 36px;
  backdrop-filter: blur(40px);
  -webkit-backdrop-filter: blur(40px);
  box-shadow: 0 32px 80px rgba(0,0,0,.5), 0 0 0 1px rgba(255,255,255,.04) inset;
  animation: fadeUp .7s cubic-bezier(.4,0,.2,1) both;
}

.signup-card.shake { animation: shake .5s ease; }

/* ─── Logo ────────────────────────────────────────────── */
.card-logo {
  width: 56px; height: 56px;
  margin: 0 auto 24px;
  display: flex; align-items: center; justify-content: center;
  position: relative;
}

.logo-mark {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 22px; font-weight: 700;
  letter-spacing: .05em;
  z-index: 1; position: relative;
}

.logo-ring {
  position: absolute; inset: 0;
  border-radius: 14px;
  background: var(--gradient);
  opacity: .15;
  filter: blur(2px);
}

/* text */
.card-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 26px; font-weight: 700;
  letter-spacing: -.02em; text-align: center;
  margin-bottom: 6px; color: #f5f5f7;
}
.card-sub { font-size: 14px; color: #6e6e73; text-align: center; margin-bottom: 28px; }

/* ─── Error ───────────────────────────────────────────── */
.error-block {
  display: flex; align-items: center; gap: 8px;
  background: rgba(255,59,48,.1);
  border: 1px solid rgba(255,59,48,.22);
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 13px; color: #ff6b6b;
  margin-bottom: 18px;
}

/* ─── Form ────────────────────────────────────────────── */
.signup-form { display: flex; flex-direction: column; gap: 14px; }

.field-group {
  background: rgba(255,255,255,.03);
  border: 1px solid rgba(255,255,255,.07);
  border-radius: 14px; overflow: hidden;
  transition: border-color .25s ease;
}
/* glow when child focused */
.field-group:focus-within {
  border-color: rgba(91,156,246,.35);
  box-shadow: 0 0 0 3px rgba(91,156,246,.07);
}

.field { padding: 0 16px; position: relative; }

.field-label {
  display: block;
  font-size: 10px; font-weight: 600; letter-spacing: .1em;
  text-transform: uppercase; color: #6e6e73;
  padding-top: 12px;
}

.field-input {
  width: 100%; padding: 6px 0 12px;
  background: transparent; border: none; outline: none;
  font-size: 15px; color: #f5f5f7;
  font-family: var(--font);
}
.field-input::placeholder { color: #3a3a3c; }
.field-input:-webkit-autofill { -webkit-text-fill-color: #f5f5f7; box-shadow: 0 0 0 100px transparent inset; }

.input-wrap { position: relative; display: flex; align-items: center; }
.input-wrap .field-input { padding-right: 32px; }

.pw-toggle {
  position: absolute; right: 0; top: 50%; transform: translateY(-50%);
  background: none; border: none; color: #6e6e73; cursor: pointer;
  padding: 4px; transition: color .2s ease; display: flex;
}
.pw-toggle:hover { color: #a1a1a6; }

.field-sep { height: 1px; background: rgba(255,255,255,.06); margin: 0 16px; }

/* ─── Submit ──────────────────────────────────────────── */
.submit-btn {
  width: 100%; padding: 15px;
  background: var(--gradient); color: #fff;
  border: none; border-radius: 14px;
  font-size: 15px; font-weight: 600; letter-spacing: .01em;
  cursor: pointer; font-family: var(--font);
  display: flex; align-items: center; justify-content: center; min-height: 52px;
  transition: all .25s ease;
  box-shadow: 0 4px 24px rgba(91,156,246,.25);
}
.submit-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 32px rgba(91,156,246,.4); }
.submit-btn:active:not(:disabled) { transform: translateY(0); }
.submit-btn:disabled { opacity: .55; cursor: not-allowed; }

.spinner {
  width: 18px; height: 18px;
  border: 2px solid rgba(255,255,255,.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: rotate .7s linear infinite;
}

/* ─── Footer ──────────────────────────────────────────── */
.card-footer {
  text-align: center; margin-top: 18px;
  font-size: 13px; color: #6e6e73;
}
.footer-link {
  color: #5b9cf6; font-weight: 500;
  text-decoration: none; transition: color .2s ease;
}
.footer-link:hover { color: #a78bfa; }

.service-note {
  display: flex; align-items: center; gap: 12px;
  margin-top: 24px;
}
.service-note span {
  flex: 1; height: 1px;
  background: rgba(255,255,255,.07);
}
.service-note p {
  font-size: 10px; letter-spacing: .08em;
  color: #3a3a3c; text-align: center; white-space: nowrap;
}

/* ─── Animations ──────────────────────────────────────── */
@keyframes shake {
  0%,100% { transform: translateX(0); }
  15%,45%,75% { transform: translateX(-8px); }
  30%,60%,90% { transform: translateX(8px); }
}

.err-enter-active { transition: all .25s ease; }
.err-leave-active { transition: all .2s ease; }
.err-enter-from   { opacity: 0; transform: translateY(-8px); }
.err-leave-to     { opacity: 0; transform: translateY(-4px); }

@media (max-width: 480px) {
  .signup-card { padding: 36px 24px 28px; }
  .back-link { top: 20px; left: 20px; }
}
</style>
