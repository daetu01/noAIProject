<template>
  <div class="studio-page">
    <!-- Custom cursor -->
    <div class="ds-cursor" ref="cursor" :class="{ big: cursorBig }" />
    <div class="ds-cursor-ring" ref="ring" />

    <!-- Navigation -->
    <StudioNav />

    <!-- Sections -->
    <HeroSection />
    <ProjectsSection @open="projectModal = $event" />
    <MusicSection />
    <DesignSection />
    <GamesSection />
    <LabSection />
    <AboutSection />

    <!-- Footer -->
    <footer class="studio-footer">
      <div class="footer-inner">
        <div class="footer-logo g-text">DAEAN STUDIO</div>
        <p class="footer-sub">Building worlds, one commit at a time.</p>
        <div class="footer-links">
          <a href="#projects">Projects</a>
          <a href="#music">Music</a>
          <a href="#design">Design</a>
          <a href="#games">Games</a>
          <a href="#lab">Lab</a>
          <a href="#about">About</a>
        </div>
        <p class="footer-copy">© 2026 Daean Studio. Crafted with intent.</p>
      </div>
    </footer>

    <!-- Project modal (shared) -->
    <div :class="['modal-overlay', { open: !!projectModal }]" @click.self="projectModal = null">
      <div class="modal-box" v-if="projectModal">
        <button class="modal-close" @click="projectModal = null">✕</button>

        <div class="pm-preview" :class="`pm-${projectModal.previewType}`">
          <div v-if="projectModal.previewType === 'city'" class="pm-city-inner">
            <div class="city-grid" />
            <div class="heat-zone" style="top:35%;left:28%;--c:#ff6450" />
            <div class="heat-zone" style="top:60%;left:65%;--c:#5b9cf6" />
            <div class="scan-line" />
          </div>
          <div class="pm-emoji" v-else>
            {{ projectModal.previewType === 'virtudy' ? '🤖' : projectModal.previewType === 'mirror' ? '🔮' : '✨' }}
          </div>
        </div>

        <div class="pm-body">
          <div class="pm-tags">
            <span v-for="t in projectModal.tags" :key="t.label" :class="`tag tag-${t.color}`">{{ t.label }}</span>
            <span class="tag tag-gray">{{ projectModal.year }}</span>
          </div>
          <h2 class="pm-title">{{ projectModal.title }}</h2>
          <p class="pm-sub">{{ projectModal.subtitle }}</p>
          <p class="pm-desc">{{ projectModal.desc }}</p>

          <div class="pm-tech-section">
            <h4>Tech Stack</h4>
            <div class="pm-techs">
              <span v-for="t in projectModal.techs" :key="t" class="tech-badge">{{ t }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import StudioNav from '@/components/StudioNav.vue'
import HeroSection from '@/components/sections/HeroSection.vue'
import ProjectsSection from '@/components/sections/ProjectsSection.vue'
import MusicSection from '@/components/sections/MusicSection.vue'
import DesignSection from '@/components/sections/DesignSection.vue'
import GamesSection from '@/components/sections/GamesSection.vue'
import LabSection from '@/components/sections/LabSection.vue'
import AboutSection from '@/components/sections/AboutSection.vue'
import { projects } from '@/data'

type Project = typeof projects[0]

// ─── Cursor ───────────────────────────────────────────────────────────────────
const cursor = ref<HTMLElement | null>(null)
const ring   = ref<HTMLElement | null>(null)
const cursorBig = ref(false)
let rx = 0, ry = 0, tx = 0, ty = 0
let raf = 0

function onMouseMove(e: MouseEvent) {
  tx = e.clientX; ty = e.clientY
  if (cursor.value) {
    cursor.value.style.left = `${tx}px`
    cursor.value.style.top  = `${ty}px`
  }
}

function animateRing() {
  rx += (tx - rx) * .13
  ry += (ty - ry) * .13
  if (ring.value) {
    ring.value.style.left = `${rx}px`
    ring.value.style.top  = `${ry}px`
  }
  raf = requestAnimationFrame(animateRing)
}

function onEnterInteractive() { cursorBig.value = true }
function onLeaveInteractive() { cursorBig.value = false }

// ─── Scroll reveal ────────────────────────────────────────────────────────────
let revealObs: IntersectionObserver | null = null

function setupReveal() {
  revealObs = new IntersectionObserver((entries) => {
    entries.forEach(e => { if (e.isIntersecting) { e.target.classList.add('visible'); revealObs?.unobserve(e.target) } })
  }, { threshold: .12, rootMargin: '0px 0px -40px 0px' })

  document.querySelectorAll('.reveal, .reveal-s, .reveal-l, .reveal-r').forEach(el => revealObs!.observe(el))
}

// ─── Project modal ────────────────────────────────────────────────────────────
const projectModal = ref<Project | null>(null)

onMounted(() => {
  window.addEventListener('mousemove', onMouseMove, { passive: true })
  animateRing()
  setTimeout(setupReveal, 120)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', onMouseMove)
  cancelAnimationFrame(raf)
  revealObs?.disconnect()
})
</script>

<style scoped>
.studio-page { background: var(--bg); }

/* Footer */
.studio-footer {
  border-top: 1px solid var(--glass-border);
  padding: 72px 48px;
  text-align: center;
}
.footer-inner { max-width: 1280px; margin: 0 auto; display: flex; flex-direction: column; align-items: center; gap: 16px; }

.footer-logo {
  font-family: 'Space Grotesk',sans-serif;
  font-size: 24px; font-weight: 700; letter-spacing: .12em;
}

.footer-sub { font-size: 14px; color: var(--text-3); }

.footer-links {
  display: flex; flex-wrap: wrap; gap: 24px; justify-content: center; margin: 8px 0;
}
.footer-links a { font-size: 13px; color: var(--text-3); transition: color .2s ease; }
.footer-links a:hover { color: var(--text); }

.footer-copy { font-size: 12px; color: #3a3a3c; }

/* Project modal shared styles */
.pm-preview {
  height: 240px; position: relative; overflow: hidden;
  background: #040b14;
  display: flex; align-items: center; justify-content: center;
}
.pm-city-inner { position: absolute; inset: 0; }

.city-grid {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(91,156,246,.04) 1px, transparent 1px), linear-gradient(90deg, rgba(91,156,246,.04) 1px, transparent 1px);
  background-size: 40px 40px;
  animation: gridMove 14s linear infinite;
}
.heat-zone {
  position: absolute; width: 200px; height: 200px; border-radius: 50%;
  background: radial-gradient(circle, var(--c) 0%, transparent 70%);
  transform: translate(-50%,-50%); opacity: .35;
  animation: heatPulse 4s ease-in-out infinite;
}
.scan-line {
  position: absolute; left: 0; right: 0; height: 2px;
  background: linear-gradient(90deg, transparent, rgba(91,156,246,.6), transparent);
  animation: scanLine 4s linear infinite;
}

.pm-emoji { font-size: 72px; filter: drop-shadow(0 0 30px rgba(0,0,0,.5)); animation: float 3s ease-in-out infinite; }

.pm-body { padding: 26px 30px 32px; }
.pm-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 14px; }
.pm-title { font-family: 'Space Grotesk',sans-serif; font-size: 26px; font-weight: 700; margin-bottom: 6px; }
.pm-sub { font-size: 13px; color: var(--text-3); margin-bottom: 14px; }
.pm-desc { font-size: 14px; color: var(--text-2); line-height: 1.7; margin-bottom: 22px; }
.pm-tech-section h4 { font-size: 11px; letter-spacing: .12em; text-transform: uppercase; color: var(--text-3); margin-bottom: 10px; }
.pm-techs { display: flex; flex-wrap: wrap; gap: 8px; }

@media (max-width: 768px) {
  .studio-footer { padding: 56px 24px; }
}
</style>
