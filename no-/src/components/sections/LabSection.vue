<template>
  <section id="lab" class="lab-section">
    <div class="lab-glow-bg" />

    <div class="section-wrap">
      <div class="section-header reveal">
        <p class="s-label">Experimental Zone</p>
        <h2 class="s-title">Lab</h2>
        <p class="s-sub">Prototypes, demos, and ideas in motion — where curiosity runs the build system.</p>
      </div>

      <div class="lab-grid stagger">
        <div
          v-for="item in labItems" :key="item.id"
          :class="['lab-card', `reveal`, { 'status-active': item.status === 'active' }]"
          @mouseenter="hovered = item.id"
          @mouseleave="hovered = null"
        >
          <div class="lab-card-glow" :class="`gc-${item.id}`" />

          <div class="lab-icon-wrap">
            <span class="lab-icon">{{ item.icon }}</span>
          </div>

          <div class="lab-content">
            <h3 class="lab-title">{{ item.title }}</h3>
            <p class="lab-desc">{{ item.desc }}</p>

            <div class="lab-tags">
              <span v-for="t in item.tags" :key="t" class="tech-badge">{{ t }}</span>
            </div>
          </div>

          <div class="lab-footer">
            <div :class="['lab-status', `ls-${item.status}`]">
              <span class="ls-dot" />
              {{ statusLabel(item.status) }}
            </div>
            <span class="lab-arrow">↗</span>
          </div>
        </div>
      </div>

      <!-- Terminal-ish footer -->
      <div class="lab-terminal reveal">
        <div class="term-bar">
          <div class="term-dots">
            <span style="background:#ff5f57" />
            <span style="background:#ffbd2e" />
            <span style="background:#28c840" />
          </div>
          <span class="term-title">daean-lab ~ /experiments</span>
        </div>
        <div class="term-body">
          <p><span class="term-prompt">$</span> ls -la ./active</p>
          <p class="term-output">neural-style-engine &nbsp; particle-field &nbsp; procedural-city</p>
          <p><span class="term-prompt">$</span> git status</p>
          <p class="term-output">On branch <span class="term-green">main</span> · 3 experiments in progress · next release: when it's done</p>
          <p class="term-cursor"><span class="term-prompt">$</span> <span class="blink">█</span></p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { labItems } from '@/data'

const hovered = ref<number | null>(null)

function statusLabel(s: string) {
  return { active: 'Active', wip: 'In Progress', concept: 'Concept' }[s] ?? s
}
</script>

<style scoped>
.lab-section {
  background: var(--bg);
  position: relative; overflow: hidden;
}

.lab-glow-bg {
  position: absolute; top: 50%; left: 50%;
  width: 900px; height: 600px;
  transform: translate(-50%,-50%);
  background: radial-gradient(ellipse, rgba(167,139,250,.025) 0%, transparent 70%);
  pointer-events: none;
}

.section-header { margin-bottom: 56px; }

.lab-grid {
  display: grid; grid-template-columns: repeat(3,1fr);
  gap: 18px; margin-bottom: 40px;
}

.lab-card {
  background: var(--bg-3);
  border: 1px solid var(--glass-border);
  border-radius: var(--r);
  padding: 28px;
  cursor: default;
  transition: all .4s cubic-bezier(.4,0,.2,1);
  position: relative; overflow: hidden;
  display: flex; flex-direction: column; gap: 14px;
}
.lab-card:hover {
  border-color: rgba(167,139,250,.38);
  transform: translateY(-5px);
  box-shadow: 0 20px 60px rgba(0,0,0,.4);
}

.lab-card-glow {
  position: absolute; inset: 0;
  background: var(--gradient);
  opacity: 0;
  transition: opacity .4s ease;
  border-radius: inherit;
}
.lab-card:hover .lab-card-glow { opacity: .04; }

.lab-icon-wrap { position: relative; z-index: 1; }
.lab-icon { font-size: 34px; display: block; transition: transform .3s ease; }
.lab-card:hover .lab-icon { transform: scale(1.15) rotate(-5deg); }

.lab-content { position: relative; z-index: 1; flex: 1; }
.lab-title {
  font-family: 'Space Grotesk',sans-serif;
  font-size: 17px; font-weight: 700; margin-bottom: 8px; letter-spacing: -.01em;
}
.lab-desc { font-size: 13px; color: var(--text-2); line-height: 1.65; margin-bottom: 12px; }
.lab-tags { display: flex; flex-wrap: wrap; gap: 6px; }

.lab-footer {
  position: relative; z-index: 1;
  display: flex; justify-content: space-between; align-items: center;
  padding-top: 4px;
}

.lab-status {
  display: flex; align-items: center; gap: 6px;
  font-size: 11px; font-weight: 600; letter-spacing: .04em;
}
.ls-dot {
  width: 6px; height: 6px; border-radius: 50%;
  animation: glowPulse 2s ease-in-out infinite;
}
.ls-active { color: #34d399; }
.ls-active .ls-dot { background: #34d399; color: #34d399; }
.ls-wip { color: #fb923c; }
.ls-wip .ls-dot { background: #fb923c; color: #fb923c; }
.ls-concept { color: var(--text-3); }
.ls-concept .ls-dot { background: var(--text-3); color: var(--text-3); }

.lab-arrow {
  font-size: 16px; color: var(--text-3);
  opacity: 0; transform: translateX(-4px);
  transition: all .25s ease;
}
.lab-card:hover .lab-arrow { opacity: 1; transform: none; color: var(--purple); }

/* Terminal */
.lab-terminal {
  background: #0d0d0d;
  border: 1px solid rgba(255,255,255,.08);
  border-radius: var(--r);
  overflow: hidden;
}
.term-bar {
  height: 34px; background: rgba(255,255,255,.04);
  border-bottom: 1px solid rgba(255,255,255,.06);
  display: flex; align-items: center; gap: 12px;
  padding: 0 14px;
}
.term-dots { display: flex; gap: 6px; }
.term-dots span { width: 10px; height: 10px; border-radius: 50%; }
.term-title { font-size: 12px; color: var(--text-3); }

.term-body {
  padding: 20px 24px;
  font-family: 'SF Mono','Fira Code',monospace;
  font-size: 12px; line-height: 1.9;
  display: flex; flex-direction: column; gap: 2px;
}
.term-prompt { color: var(--blue); margin-right: 8px; }
.term-output { color: var(--text-3); padding-left: 16px; }
.term-green { color: #34d399; }
.blink { animation: pulse 1s step-end infinite; }

@media (max-width: 900px) { .lab-grid { grid-template-columns: repeat(2,1fr); } }
@media (max-width: 560px) { .lab-grid { grid-template-columns: 1fr; } }
</style>
