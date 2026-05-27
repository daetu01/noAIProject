<template>
  <section id="projects" class="projects-section">
    <div class="section-wrap">
      <div class="section-header reveal">
        <p class="s-label">Selected Work</p>
        <h2 class="s-title">Projects</h2>
        <p class="s-sub">Systems and products built at the intersection of AI, design, and engineering.</p>
      </div>

      <!-- Featured: City Platform -->
      <div class="proj-featured reveal-s" @click="openProject(projects[0])">
        <div class="proj-preview city-preview">
          <!-- City grid background -->
          <div class="city-grid" />
          <!-- Heatmap blobs -->
          <div class="heat-zone" style="top:35%;left:28%;--c:#ff6450" />
          <div class="heat-zone" style="top:55%;left:60%;--c:#ffc832" />
          <div class="heat-zone" style="top:25%;left:65%;--c:#5b9cf6" />
          <!-- SVG connection lines -->
          <svg class="city-lines" viewBox="0 0 800 400" preserveAspectRatio="none">
            <line v-for="l in cityLines" :key="l.id" :x1="l.x1" :y1="l.y1" :x2="l.x2" :y2="l.y2" class="city-line" />
          </svg>
          <!-- Animated nodes -->
          <div v-for="n in cityNodes" :key="n.id" class="city-node"
            :style="`top:${n.y}%;left:${n.x}%;--delay:${n.d}s;background:${n.c}`" />
          <!-- Status bar overlay -->
          <div class="proj-hud">
            <div class="hud-badge">LIVE</div>
            <div class="hud-stats">
              <span>∿ WebSocket</span>
              <span>⚡ AI Active</span>
              <span>↻ Redis Hit: 94%</span>
            </div>
          </div>
          <!-- Scan line -->
          <div class="scan-line" />
        </div>

        <div class="proj-info">
          <div class="proj-tags">
            <span v-for="t in projects[0].tags" :key="t.label" :class="`tag tag-${t.color}`">{{ t.label }}</span>
            <span class="proj-year">{{ projects[0].year }}</span>
          </div>
          <h3 class="proj-title">{{ projects[0].title }}</h3>
          <p class="proj-sub">{{ projects[0].subtitle }}</p>
          <p class="proj-desc">{{ projects[0].desc }}</p>
          <div class="proj-tech">
            <span v-for="t in projects[0].techs" :key="t" class="tech-badge">{{ t }}</span>
          </div>
          <div class="proj-actions" @click.stop>
            <a href="/city" class="btn-pri proj-btn">Open Dashboard →</a>
            <button class="btn-ghost proj-btn" @click.stop="openProject(projects[0])">Details</button>
          </div>
        </div>
      </div>

      <!-- Grid of other projects -->
      <div class="proj-grid stagger">
        <div v-for="p in projects.slice(1)" :key="p.id"
          class="proj-card reveal-s"
          @click="openProject(p)">
          <div class="proj-card-preview" :class="`preview-${p.previewType}`">
            <div class="preview-inner">
              <!-- Virtudy preview -->
              <template v-if="p.previewType === 'virtudy'">
                <div class="virt-avatar">🤖</div>
                <div class="virt-chat">
                  <div class="virt-msg">What is recursion?</div>
                  <div class="virt-reply">A function calling itself to solve subproblems...</div>
                </div>
              </template>
              <!-- Mirror preview -->
              <template v-else-if="p.previewType === 'mirror'">
                <div class="mirror-world">
                  <div class="mirror-char left">◈</div>
                  <div class="mirror-divider" />
                  <div class="mirror-char right">◈</div>
                </div>
              </template>
              <!-- Fashion preview -->
              <template v-else-if="p.previewType === 'fashion'">
                <div class="fashion-grid">
                  <div class="fashion-color" style="background:#1a1a2e" />
                  <div class="fashion-color" style="background:#e8d5b7" />
                  <div class="fashion-color" style="background:#2d4a3e" />
                  <div class="fashion-color" style="background:#8b4513" />
                </div>
                <div class="fashion-label">Colour Palette · 92% match</div>
              </template>
            </div>
          </div>

          <div class="proj-card-info">
            <div class="proj-tags">
              <span v-for="t in p.tags" :key="t.label" :class="`tag tag-${t.color}`">{{ t.label }}</span>
              <span class="proj-status" :class="`status-${p.status.toLowerCase()}`">{{ p.status }}</span>
            </div>
            <h3 class="proj-card-title">{{ p.title }}</h3>
            <p class="proj-card-sub">{{ p.subtitle }}</p>
            <p class="proj-card-desc">{{ p.desc }}</p>
            <div class="proj-tech">
              <span v-for="t in p.techs" :key="t" class="tech-badge">{{ t }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { projects } from '@/data'

const emit = defineEmits<{ (e: 'open', p: typeof projects[0]): void }>()
function openProject(p: typeof projects[0]) { emit('open', p) }

const cityNodes = [
  { id: 1, x: 20, y: 30, d: 0, c: '#ff6450' },
  { id: 2, x: 45, y: 20, d: .6, c: '#5b9cf6' },
  { id: 3, x: 70, y: 45, d: 1.2, c: '#ffc832' },
  { id: 4, x: 30, y: 65, d: .4, c: '#a78bfa' },
  { id: 5, x: 60, y: 70, d: .9, c: '#34d399' },
  { id: 6, x: 80, y: 25, d: 1.5, c: '#5b9cf6' },
]

const cityLines = [
  { id: 1, x1: '20%', y1: '30%', x2: '45%', y2: '20%' },
  { id: 2, x1: '45%', y1: '20%', x2: '70%', y2: '45%' },
  { id: 3, x1: '30%', y1: '65%', x2: '60%', y2: '70%' },
  { id: 4, x1: '20%', y1: '30%', x2: '30%', y2: '65%' },
  { id: 5, x1: '70%', y1: '45%', x2: '80%', y2: '25%' },
  { id: 6, x1: '60%', y1: '70%', x2: '70%', y2: '45%' },
]
</script>

<style scoped>
.projects-section {
  background: linear-gradient(to bottom, var(--bg), var(--bg-2), var(--bg));
}
.section-header { margin-bottom: 56px; }

/* ─ Featured ─ */
.proj-featured {
  display: grid; grid-template-columns: 1fr 1fr;
  gap: 0;
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  border-radius: var(--r-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all .4s cubic-bezier(.4,0,.2,1);
  margin-bottom: 24px;
}
.proj-featured:hover {
  border-color: rgba(91,156,246,.4);
  transform: translateY(-6px);
  box-shadow: 0 32px 80px rgba(0,0,0,.45), 0 0 60px rgba(91,156,246,.07);
}

/* City preview */
.city-preview {
  position: relative; height: 440px; overflow: hidden;
  background: #040b14;
}
.city-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(91,156,246,.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(91,156,246,.04) 1px, transparent 1px);
  background-size: 40px 40px;
  animation: gridMove 14s linear infinite;
}
.heat-zone {
  position: absolute;
  width: 180px; height: 180px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--c) 0%, transparent 70%);
  transform: translate(-50%,-50%);
  animation: heatPulse 4s ease-in-out infinite;
  opacity: .35;
}
.heat-zone:nth-child(3) { animation-delay: 1.2s; }
.heat-zone:nth-child(4) { animation-delay: 2.4s; }

.city-lines {
  position: absolute; inset: 0; width: 100%; height: 100%;
}
.city-line {
  stroke: rgba(91,156,246,.2); stroke-width: .8;
}

.city-node {
  position: absolute;
  width: 8px; height: 8px;
  border-radius: 50%;
  transform: translate(-50%,-50%);
  animation: nodePop 3s ease-in-out infinite;
  animation-delay: var(--delay);
  box-shadow: 0 0 10px currentColor;
  filter: brightness(1.4);
}

.proj-hud {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: linear-gradient(to top, rgba(4,11,20,.95) 0%, transparent 100%);
  padding: 24px 20px 18px;
  display: flex; flex-direction: column; gap: 8px;
}
.hud-badge {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 4px 10px;
  background: rgba(52,211,153,.15);
  border: 1px solid rgba(52,211,153,.3);
  border-radius: 100px;
  font-size: 10px; font-weight: 700; letter-spacing: .1em;
  color: #34d399; width: fit-content;
}
.hud-badge::before {
  content: ''; width: 6px; height: 6px; border-radius: 50%;
  background: #34d399;
  animation: pulse 2s ease-in-out infinite;
}
.hud-stats { display: flex; gap: 16px; flex-wrap: wrap; }
.hud-stats span { font-size: 11px; color: rgba(255,255,255,.5); font-family: 'SF Mono','Fira Code',monospace; }

.scan-line {
  position: absolute; left: 0; right: 0; height: 2px;
  background: linear-gradient(90deg, transparent, rgba(91,156,246,.6), transparent);
  animation: scanLine 4s linear infinite;
  pointer-events: none;
}

.proj-info {
  padding: 40px 40px;
  display: flex; flex-direction: column; justify-content: center; gap: 12px;
}
.proj-tags { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.proj-year { font-size: 11px; color: var(--text-3); margin-left: auto; }
.proj-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 26px; font-weight: 700; letter-spacing: -.02em;
}
.proj-sub { font-size: 13px; color: var(--text-3); margin-top: -6px; }
.proj-desc { font-size: 14px; color: var(--text-2); line-height: 1.65; }
.proj-tech { display: flex; flex-wrap: wrap; gap: 7px; margin-top: 4px; }
.proj-actions { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 16px; }
.proj-btn { font-size: 13px; padding: 10px 22px; }

/* ─ Grid ─ */
.proj-grid {
  display: grid; grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.proj-card {
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  border-radius: var(--r);
  overflow: hidden; cursor: pointer;
  transition: all .4s cubic-bezier(.4,0,.2,1);
}
.proj-card:hover {
  border-color: rgba(167,139,250,.35);
  transform: translateY(-6px);
  box-shadow: 0 24px 60px rgba(0,0,0,.4), 0 0 40px rgba(167,139,250,.06);
}

.proj-card-preview {
  height: 180px; position: relative;
  overflow: hidden; background: #0d0d1a;
  display: flex; align-items: center; justify-content: center;
}
.preview-inner { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }

/* Virtudy preview */
.virt-avatar { font-size: 40px; filter: drop-shadow(0 0 16px rgba(167,139,250,.8)); animation: float 3s ease-in-out infinite; }
.virt-chat { position: absolute; bottom: 12px; left: 12px; right: 12px; display: flex; flex-direction: column; gap: 5px; }
.virt-msg, .virt-reply {
  padding: 6px 10px; border-radius: 8px; font-size: 10px; line-height: 1.4;
}
.virt-msg { background: rgba(167,139,250,.2); color: #a78bfa; align-self: flex-end; }
.virt-reply { background: rgba(255,255,255,.06); color: var(--text-2); }

/* Mirror preview */
.mirror-world { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; gap: 0; }
.mirror-char { font-size: 36px; padding: 16px; transition: all .3s ease; }
.mirror-char.left { color: #5b9cf6; text-shadow: 0 0 20px #5b9cf6; animation: float 2s ease-in-out infinite; }
.mirror-char.right { color: #f472b6; text-shadow: 0 0 20px #f472b6; animation: float 2s ease-in-out infinite reverse; }
.mirror-divider { width: 1px; height: 60%; background: linear-gradient(to bottom, transparent, rgba(255,255,255,.3), transparent); }

/* Fashion preview */
.fashion-grid { display: grid; grid-template-columns: repeat(2,1fr); grid-template-rows: repeat(2,1fr); gap: 2px; width: 100px; height: 80px; }
.fashion-color { border-radius: 6px; }
.fashion-label { position: absolute; bottom: 12px; font-size: 10px; color: var(--text-3); }

.proj-card-info { padding: 22px; display: flex; flex-direction: column; gap: 8px; }
.proj-card-title { font-family: 'Space Grotesk',sans-serif; font-size: 18px; font-weight: 700; letter-spacing: -.01em; }
.proj-card-sub { font-size: 12px; color: var(--text-3); margin-top: -4px; }
.proj-card-desc { font-size: 13px; color: var(--text-2); line-height: 1.6; }

.proj-status {
  font-size: 10px; font-weight: 600; letter-spacing: .08em; text-transform: uppercase;
  padding: 3px 9px; border-radius: 100px;
}
.status-live { background: rgba(52,211,153,.15); color: #34d399; border: 1px solid rgba(52,211,153,.25); }
.status-wip { background: rgba(251,146,60,.15); color: #fb923c; border: 1px solid rgba(251,146,60,.25); }
.status-concept { background: rgba(255,255,255,.05); color: var(--text-3); border: 1px solid rgba(255,255,255,.08); }

@media (max-width: 1024px) {
  .proj-featured { grid-template-columns: 1fr; }
  .city-preview { height: 280px; }
  .proj-grid { grid-template-columns: repeat(2,1fr); }
}
@media (max-width: 680px) {
  .proj-grid { grid-template-columns: 1fr; }
}
</style>
